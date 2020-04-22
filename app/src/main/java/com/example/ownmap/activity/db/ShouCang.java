package com.example.ownmap.activity.db;

import com.amap.api.services.core.PoiItem;

import java.io.Serializable;

public class ShouCang implements Serializable {
    private String url;
    private String title;
    private String address;
    private String time;
    private String rand;
    private String lat;
    private String lon;
    private String number;

    public ShouCang(String url,String title,String address,String time, String rand,String lat,String lon,String number){
        this.url = url;
        this.title = title;
        this.address = address;
        this.time = time;
        this.rand = rand;
        this.lat = lat;
        this.lon = lon;
        this.number = number;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getTime() {
        return time;
    }

    public String getRand() {
        return rand;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getNumber() {
        return number;
    }
}
