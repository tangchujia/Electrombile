package com.syxgo.electrombile.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tangchujia on 2017/9/12.
 */

public class Station implements Serializable {
    int id;
    String name;
    String description;
    float lng;
    float lat;
    int stype;//2.多边形 3.圆形
    int radius;
    String lnglats;
    int bikes_count;
    double distance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public int getStype() {
        return stype;
    }

    public void setStype(int stype) {
        this.stype = stype;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getLnglats() {
        return lnglats;
    }

    public void setLnglats(String lnglats) {
        this.lnglats = lnglats;
    }

    public int getBikes_count() {
        return bikes_count;
    }

    public void setBikes_count(int bikes_count) {
        this.bikes_count = bikes_count;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
