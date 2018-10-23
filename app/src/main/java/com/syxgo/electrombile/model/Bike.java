package com.syxgo.electrombile.model;

import java.io.Serializable;

/**
 * Created by tangchujia on 2017/8/29.
 */

public class Bike implements Serializable {
    int id;
    String created;
    String updated;
    boolean visibly;
    int version;
    int provider_id;
    int btype;
    String name;
    String description;
    String serial;
    String qrcode;
    String last_active;
    String rom_version;
    String hardware_version;
    int ride_id;
    int last_ride_id;
    String last_ride_time;
    float lng;
    float lat;
    int battery_level;
    int backup_battery_level;
    int alarm;
    int motion;
    boolean acc;
    boolean is_lock;
    boolean is_backseat_lock;
    int ride_time;
    int ride_count;
    double ride_distance;
    int station_id;
    boolean is_rent;
    boolean is_offline;
    String comment;
    String bluetooth;
    int group_id;
    int task_id;


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

    public int getBtype() {
        return btype;
    }

    public void setBtype(int btype) {
        this.btype = btype;
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

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getLast_active() {
        return last_active;
    }

    public void setLast_active(String last_active) {
        this.last_active = last_active;
    }

    public String getRom_version() {
        return rom_version;
    }

    public void setRom_version(String rom_version) {
        this.rom_version = rom_version;
    }

    public String getHardware_version() {
        return hardware_version;
    }

    public void setHardware_version(String hardware_version) {
        this.hardware_version = hardware_version;
    }

    public int getRide_id() {
        return ride_id;
    }

    public void setRide_id(int ride_id) {
        this.ride_id = ride_id;
    }

    public int getLast_ride_id() {
        return last_ride_id;
    }

    public void setLast_ride_id(int last_ride_id) {
        this.last_ride_id = last_ride_id;
    }

    public String getLast_ride_time() {
        return last_ride_time;
    }

    public void setLast_ride_time(String last_ride_time) {
        this.last_ride_time = last_ride_time;
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

    public int getBattery_level() {
        return battery_level;
    }

    public void setBattery_level(int battery_level) {
        this.battery_level = battery_level;
    }

    public int getBackup_battery_level() {
        return backup_battery_level;
    }

    public void setBackup_battery_level(int backup_battery_level) {
        this.backup_battery_level = backup_battery_level;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public int getMotion() {
        return motion;
    }

    public void setMotion(int motion) {
        this.motion = motion;
    }

    public boolean isAcc() {
        return acc;
    }

    public void setAcc(boolean acc) {
        this.acc = acc;
    }

    public boolean is_lock() {
        return is_lock;
    }

    public void setIs_lock(boolean is_lock) {
        this.is_lock = is_lock;
    }

    public boolean is_backseat_lock() {
        return is_backseat_lock;
    }

    public void setIs_backseat_lock(boolean is_backseat_lock) {
        this.is_backseat_lock = is_backseat_lock;
    }

    public int getRide_time() {
        return ride_time;
    }

    public void setRide_time(int ride_time) {
        this.ride_time = ride_time;
    }

    public int getRide_count() {
        return ride_count;
    }

    public void setRide_count(int ride_count) {
        this.ride_count = ride_count;
    }

    public double getRide_distance() {
        return ride_distance;
    }

    public void setRide_distance(double ride_distance) {
        this.ride_distance = ride_distance;
    }

    public int getStation_id() {
        return station_id;
    }

    public void setStation_id(int station_id) {
        this.station_id = station_id;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getBluetooth() {
        return bluetooth;
    }

    public void setBluetooth(String bluetooth) {
        this.bluetooth = bluetooth;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }
}
