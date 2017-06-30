package com.gmos.lab.search;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "sparkgmos", type = "gmos_metrics")
public class SearchClimateAnalysis {

    public String getStation() {
        return station;
    }

    public String getCountry() {
        return country;
    }

    public String getDate() {
        return date;
    }

    public String getYear() {
        return year;
    }

    public String getSeason() {
        return season;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Double getTavg() {
        return tavg;
    }

    public String getId() {
        return id;
    }

    @Id
    private String id;

    private String station;
    private String country;
    private String date;
    private String year;
    private String season;
    private String latitude;
    private String longitude;
    private Double tavg;

    public SearchClimateAnalysis() {
    }

    public SearchClimateAnalysis(String station, String country, String date, String year, String season, String latitude, String longitude, Double tavg) {
        this.station = station;
        this.country = country;
        this.date = date;
        this.year = year;
        this.season = season;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tavg = tavg;
    }

    @Override
    public String toString() {
        return "SearchClimateAnalysis{" +
                "station='" + station + '\'' +
                ", country='" + country + '\'' +
                ", date='" + date + '\'' +
                ", year='" + year + '\'' +
                ", season='" + season + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", tavg='" + tavg + '\'' +
                '}';
    }
}