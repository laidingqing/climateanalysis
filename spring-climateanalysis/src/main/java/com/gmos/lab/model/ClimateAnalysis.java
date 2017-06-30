package com.gmos.lab.model;

public class ClimateAnalysis implements java.io.Serializable{

    public void setTempeature(double tempeature) {
        this.tempeature = tempeature;
    }

    public void setDatapoints(long datapoints) {
        this.datapoints = datapoints;
    }

    private double tempeature;
    private long datapoints;
    private final String year;
    private final String season;

    public ClimateAnalysis(double tempeature, long datapoints, String year, String season) {
        this.tempeature = tempeature;
        this.datapoints = datapoints;
        this.year = year;
        this.season = season;
    }

    public long getDatapoints() {
        return this.datapoints;
    }

    public double getTempeature() {
        return this.tempeature;
    }

    public String getYear() {
        return this.year;
    }

    public String getSeason() {
        return this.season;
    }

}
