package com.syxgo.electrombile.model;

import java.io.Serializable;

/**
 * Created by tangchujia on 2017/9/7.
 */

public class Lnglat implements Serializable {
    float lat;
    float lng;

    public double getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }
}
