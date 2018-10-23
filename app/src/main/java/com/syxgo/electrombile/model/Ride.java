package com.syxgo.electrombile.model;

import java.io.Serializable;

/**
 * Created by tangchujia on 2017/9/20.
 */

public class Ride implements Serializable {
    int id;
    String created;
    String updated;
    boolean visibly;
    int version;
    int provider_id;
    int user_id;
    int bike_id;
    int order_id;
    double lng_from;
    double lat_from;
    String time_from;
    String time_to;
    double distance;
    int real_fee;
    int gift_fee;
    int status;
    int fee;
    String track_points;

    public String getTrack_points() {
        return track_points;
    }

    public void setTrack_points(String track_points) {
        this.track_points = track_points;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public boolean isVisibly() {
        return visibly;
    }

    public void setVisibly(boolean visibly) {
        this.visibly = visibly;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(int provider_id) {
        this.provider_id = provider_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getBike_id() {
        return bike_id;
    }

    public void setBike_id(int bike_id) {
        this.bike_id = bike_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public double getLng_from() {
        return lng_from;
    }

    public void setLng_from(double lng_from) {
        this.lng_from = lng_from;
    }

    public double getLat_from() {
        return lat_from;
    }

    public void setLat_from(double lat_from) {
        this.lat_from = lat_from;
    }

    public String getTime_from() {
        return time_from;
    }

    public void setTime_from(String time_from) {
        this.time_from = time_from;
    }

    public String getTime_to() {
        return time_to;
    }

    public void setTime_to(String time_to) {
        this.time_to = time_to;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getReal_fee() {
        return real_fee;
    }

    public void setReal_fee(int real_fee) {
        this.real_fee = real_fee;
    }

    public int getGift_fee() {
        return gift_fee;
    }

    public void setGift_fee(int gift_fee) {
        this.gift_fee = gift_fee;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }
}
