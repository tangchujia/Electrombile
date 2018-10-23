package com.syxgo.electrombile.model;

import org.litepal.crud.DataSupport;

/**
 * Created by tangchujia on 2017/10/19.
 */

public class RideData extends DataSupport {
    int rideId;
    String createdTime;
    int user_id;
    int bike_id;
    int status;//行程状态 1:骑行中 2:已结束

    public int getRideId() {
        return rideId;
    }

    public void setRideId(int rideId) {
        this.rideId = rideId;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
