package com.syxgo.electrombile.model;

import java.io.Serializable;

/**
 * Created by tangchujia on 2018/3/20.
 */

public class EcuLocation implements Serializable{
    GPSLocation gps_location;
    String time;

    public GPSLocation getGps_location() {
        return gps_location;
    }

    public void setGps_location(GPSLocation gps_location) {
        this.gps_location = gps_location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
