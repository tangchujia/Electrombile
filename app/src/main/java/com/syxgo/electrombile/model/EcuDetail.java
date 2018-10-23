package com.syxgo.electrombile.model;

import java.io.Serializable;

/**
 * Created by tangchujia on 2018/3/20.
 */

public class EcuDetail implements Serializable {
    int id;
    EcuLocation location;
    EcuMsg ecu;
    int status;
    String ecu_uuid;
    String token;
    String ble_mac;
    Bike bike;

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    public String getBle_mac() {
        return ble_mac;
    }

    public void setBle_mac(String ble_mac) {
        this.ble_mac = ble_mac;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEcu_uuid() {
        return ecu_uuid;
    }

    public void setEcu_uuid(String ecu_uuid) {
        this.ecu_uuid = ecu_uuid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public EcuLocation getLocation() {
        return location;
    }

    public void setLocation(EcuLocation location) {
        this.location = location;
    }

    public EcuMsg getEcu() {
        return ecu;
    }

    public void setEcu(EcuMsg ecu) {
        this.ecu = ecu;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
