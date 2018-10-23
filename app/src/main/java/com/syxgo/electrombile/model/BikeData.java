package com.syxgo.electrombile.model;

import org.litepal.crud.DataSupport;

/**
 * Created by tangchujia on 2017/10/19.
 */

public class BikeData extends DataSupport {
    int bike_id;

    int battery_level;//电池百分比,-1代表三块子电池都异常
    boolean is_lock;//车锁状态

    boolean is_rent;//是否租借
    boolean is_offline;//是否被下线

    public int getBike_id() {
        return bike_id;
    }

    public void setBike_id(int bike_id) {
        this.bike_id = bike_id;
    }

    public int getBattery_level() {
        return battery_level;
    }

    public void setBattery_level(int battery_level) {
        this.battery_level = battery_level;
    }

    public boolean is_lock() {
        return is_lock;
    }

    public void setIs_lock(boolean is_lock) {
        this.is_lock = is_lock;
    }

    public boolean is_rent() {
        return is_rent;
    }

    public void setIs_rent(boolean is_rent) {
        this.is_rent = is_rent;
    }

    public boolean is_offline() {
        return is_offline;
    }

    public void setIs_offline(boolean is_offline) {
        this.is_offline = is_offline;
    }
}
