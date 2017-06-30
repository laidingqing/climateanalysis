package com.gmos.lab.access;

import com.gmos.lab.model.ClimateAnalysis;
import com.gmos.lab.search.SearchClimateAnalysis;
import com.gmos.lab.search.SearchService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHBase {

    public static String salt(String key, int modulus) {
        int saltAsInt = Math.abs(key.hashCode()) % modulus;
        int charsInSalt = (int)digitsRequired(modulus);
        return String.format("%0" + charsInSalt + "d", saltAsInt) + ":" + key;
    }

    public static double digitsRequired(int modulus){
        return (Math.log10(modulus-1)+1);
    }

    public static List<ClimateAnalysis> query(SearchService searchService, String latitude, String longitude) throws IOException {
        List<ClimateAnalysis> rets = new ArrayList<ClimateAnalysis>();
        List<SearchClimateAnalysis> byGEO = searchService.findByGEO(latitude,longitude);
        Map<String, ClimateAnalysis> ca = new HashMap<String, ClimateAnalysis>();
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "sandbox.hortonworks.com");
        config.set("zookeeper.znode.parent", "/hbase-unsecure");
        HTable table = new HTable(config, "climate_data");
        System.out.println("size: " + byGEO.size());

        for (SearchClimateAnalysis vo:byGEO){
            String station = vo.getStation();
            String date = vo.getDate();
            Get g = new Get(Bytes.toBytes(salt(station,5)+":"+date));
            Result r = table.get(g);
            String year = Bytes.toString(r.getValue(Bytes.toBytes("climate"), Bytes.toBytes("c4")));
            String season = Bytes.toString(r.getValue(Bytes.toBytes("climate"), Bytes.toBytes("c5")));
            if (r.getValue(Bytes.toBytes("climate"), Bytes.toBytes("c8")) != null){
                double tempeature = Bytes.toDouble(r.getValue(Bytes.toBytes("climate"), Bytes.toBytes("c8")));
                ClimateAnalysis obj = ca.get(year + ":" + season);
                if (obj == null) {
                    obj = new ClimateAnalysis(tempeature, 1, year, season);
                    ca.put(year + ":" + season, obj);
                } else {
                    obj.setDatapoints(obj.getDatapoints() + 1);
                    obj.setTempeature(obj.getTempeature() + tempeature);
                }
            }
        }
        for (Map.Entry<String, ClimateAnalysis> obj:ca.entrySet()) {
            ClimateAnalysis vo = obj.getValue();
            vo.setTempeature(vo.getTempeature()/vo.getDatapoints());
            rets.add(vo);
        }
        return rets;
    }

}
