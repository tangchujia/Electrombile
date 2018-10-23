package com.syxgo.electrombile.model;

import java.io.Serializable;

/**
 * Created by tangchujia on 2018/3/20.
 */

public class EcuInfo implements Serializable {
    Status status;
    int gprs_rssi;

    public int getGprs_rssi() {
        return gprs_rssi;
    }

    public void setGprs_rssi(int gprs_rssi) {
        this.gprs_rssi = gprs_rssi;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
