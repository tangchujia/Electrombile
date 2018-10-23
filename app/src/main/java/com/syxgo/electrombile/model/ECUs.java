package com.syxgo.electrombile.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tangchujia on 2017/8/29.
 */

public class ECUs implements Serializable {
    List<ECU> ecus;

    public List<ECU> getEcus() {
        return ecus;
    }

    public void setEcus(List<ECU> ecus) {
        this.ecus = ecus;
    }
}
