package com.gmos.lab.controller;

import com.gmos.lab.access.JdbcHive;
import com.gmos.lab.access.JdbcMySQL;
import com.gmos.lab.access.JdbcPresto;
import com.gmos.lab.access.JdbcSpark;
import com.gmos.lab.model.ClimateAnalysis;
import com.gmos.lab.model.Station;
import com.gmos.lab.util.AddressParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
public class GmosController {

    @RequestMapping("/searchbygeo")
    public ClimateAnalysis[] searchByGeo(@RequestParam(value="latitude") String latitude,
                                      @RequestParam(value="longitude") String longitude,
                                      @RequestParam(value="datasource") String datasource) {

        List<ClimateAnalysis> rets = new ArrayList<ClimateAnalysis>();

        double dlatitude = Double.parseDouble(latitude);
        double dlongitude = Double.parseDouble(longitude);

        if (datasource.equalsIgnoreCase("hive")){
            rets = JdbcHive.query(dlatitude, dlongitude);
        }else if(datasource.equalsIgnoreCase("spark")){
            rets = JdbcSpark.query(dlatitude, dlongitude);
        }else if(datasource.equalsIgnoreCase("presto")){
            rets = JdbcPresto.query(dlatitude, dlongitude);
        }

        return rets.toArray(new ClimateAnalysis[0]);
    }

    @RequestMapping("/searchbygrid")
    public ClimateAnalysis[] searchByGrid(@RequestParam(value="start") String start,
                                          @RequestParam(value="end") String end,
                                          @RequestParam(value="latitude_left") String latitude_left,
                                          @RequestParam(value="latitude_right") String latitude_right,
                                          @RequestParam(value="longitude_left") String longitude_left,
                                          @RequestParam(value="longitude_right") String longitude_right,
                                          @RequestParam(value="datasource") String datasource) {

        List<ClimateAnalysis> rets = new ArrayList<ClimateAnalysis>();

        double dlatitude_left = Double.parseDouble(latitude_left);
        double dlatitude_right = Double.parseDouble(latitude_right);
        double dlongitude_left = Double.parseDouble(longitude_left);
        double dlongitude_right = Double.parseDouble(longitude_right);

        if (datasource.equalsIgnoreCase("hive")){
            rets = JdbcHive.query(start, end, dlatitude_left, dlatitude_right, dlongitude_left, dlongitude_right);
        }else if (datasource.equalsIgnoreCase("spark")){
            rets = JdbcSpark.query(start, end, dlatitude_left, dlatitude_right, dlongitude_left, dlongitude_right);
        }else if (datasource.equalsIgnoreCase("presto")){
            rets = JdbcPresto.query(start, end, dlatitude_left, dlatitude_right, dlongitude_left, dlongitude_right);
        }

        return rets.toArray(new ClimateAnalysis[0]);
    }

    @RequestMapping("/retrievecountry")
    public Station retrieveCountry(@RequestParam(value="id") String id) {

        Station ret = JdbcMySQL.query(id);
        if (ret.getId() == null || ret.getId().isEmpty()) return ret;
        RestTemplate restTemplate = new RestTemplate();
        String json = restTemplate.getForObject("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + ret.getLatitude() + "," + ret.getLongitude(), String.class);
        String country = AddressParser.getCountry(json);
        ret.setCountry(country);
        return ret;
    }

}
