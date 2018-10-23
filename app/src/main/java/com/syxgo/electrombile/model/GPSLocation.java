package com.syxgo.electrombile.model;

import java.io.Serializable;

/**
 * Created by tangchujia on 2018/3/20.
 */

public class GPSLocation implements Serializable {
    double lat;
    double lng;
    int precision;
    int satel_count;//卫星数量
    String status;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getSatel_count() {
        return satel_count;
    }

    public void setSatel_count(int satel_count) {
        this.satel_count = satel_count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
