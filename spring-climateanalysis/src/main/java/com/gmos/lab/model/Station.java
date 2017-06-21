package com.gmos.lab.model;

public class Station implements java.io.Serializable{

    private double latitude;
    private double longitude;
    private String country;
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public void setCountry(String country) {

        this.country = country;
    }

    public void setLongitude(double longitude) {

        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {

        this.latitude = latitude;
    }

    public Station(){
    }

    public Station(double latitude, double longitude, String country, String id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.id = id;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String getCountry() {
        return this.country;
    }

    public String getId() {
        return this.id;
    }

}
