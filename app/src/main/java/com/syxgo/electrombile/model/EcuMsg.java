package com.syxgo.electrombile.model;

import java.io.Serializable;

/**
 * Created by tangchujia on 2018/3/20.
 */

public class EcuMsg implements Serializable {
    EcuInfo ecu_info;
    String time;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public EcuInfo getEcu_info() {
        return ecu_info;
    }

    public void setEcu_info(EcuInfo ecu_info) {
        this.ecu_info = ecu_info;
    }
}
