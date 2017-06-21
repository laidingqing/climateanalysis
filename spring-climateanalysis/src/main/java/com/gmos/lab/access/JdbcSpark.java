package com.gmos.lab.access;

import com.gmos.lab.model.ClimateAnalysis;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;


/******************************************************************************
 * Spark Thrift Server
 * hive.server2.thrift.port = 10015
 */
public class JdbcSpark {
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";

    public static List<ClimateAnalysis> query(double latitude, double longitude) {
        List<ClimateAnalysis> rets = new ArrayList<ClimateAnalysis>();
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return rets;
        }
        Connection con = null;
        Statement stmt = null;
        ResultSet res = null;
        try {
            con = DriverManager.getConnection("jdbc:hive2://localhost:10015/default", "hive", "");
            stmt = con.createStatement();
            String sql = "select year, season, sum(case when length(gtavg) = 0 then 0.0 else cast(gtavg as double) end)/sum(case when length(gtavg) = 0 then 0 else 1 end) as average_tempeture, sum(case when length(gtavg) = 0 then 0 else 1 end) as available_tavg FROM gmos_etl_external_hive where glatitude = " + latitude + " and glongitude = " + longitude + " group by year, season";
            res = stmt.executeQuery(sql);
            while (res.next()) {
                String year = res.getString(1);
                String season = res.getString(2);
                double tempeature = res.getDouble(3);
                long datapoints = res.getLong(4);
                rets.add(new ClimateAnalysis(tempeature, datapoints, year, season));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (res != null) res.close();
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            }catch(Exception ex){}
        }

        return rets;
    }

    public static List<ClimateAnalysis> query(String start, String end, double latitude_left, double latitude_right, double longitude_left, double longitude_right) {
        List<ClimateAnalysis> rets = new ArrayList<ClimateAnalysis>();
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return rets;
        }
        Connection con = null;
        Statement stmt = null;
        ResultSet res = null;
        try {
            con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "hive", "");
            stmt = con.createStatement();
            String sql = "select year, season, sum(case when length(gtavg) = 0 then 0.0 else cast(gtavg as double) end)/sum(case when length(gtavg) = 0 then 0 else 1 end) as average_tempeture, sum(case when length(gtavg) = 0 then 0 else 1 end) as available_tavg FROM gmos_etl_external_hive where year >= '" + start + "' and year <= '" + end + "' and glatitude >= " + latitude_left + " and glatitude <= " + latitude_right + " and glongitude >= " + longitude_left + " and glongitude <= " + longitude_right + " group by year, season";
            res = stmt.executeQuery(sql);
            while (res.next()) {
                String year = res.getString(1);
                String season = res.getString(2);
                double tempeature = res.getDouble(3);
                long datapoints = res.getLong(4);
                rets.add(new ClimateAnalysis(tempeature, datapoints, year, season));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (res != null) res.close();
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            }catch(Exception ex){}
        }

        return rets;
    }



}
