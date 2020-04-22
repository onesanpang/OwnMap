package com.example.ownmap.activity.java;

public class Daily {
    private String date;
    private String txt;
    private String max;
    private String min;

    public Daily(String date,String txt,String max,String min){
        this.date = date;
        this.txt = txt;
        this.max = max;
        this.min = min;
    }

    public String getDate() {
        return date;
    }

    public String getTxt() {
        return txt;
    }

    public String getMax() {
        return max;
    }

    public String getMin() {
        return min;
    }
}
