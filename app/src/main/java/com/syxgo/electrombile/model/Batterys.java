package com.syxgo.electrombile.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tangchujia on 2017/9/15.
 */

public class Batterys implements Serializable {
    List<Battery> batterys;

    public List<Battery> getBatterys() {
        return batterys;
    }

    public void setBatterys(List<Battery> batterys) {
        this.batterys = batterys;
    }
}
