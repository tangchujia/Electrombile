package com.syxgo.electrombile.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tangchujia on 2017/8/29.
 */

public class Bikes implements Serializable {
    List<Bike> bikes;

    public List<Bike> getBikes() {
        return bikes;
    }

    public void setBikes(List<Bike> bikes) {
        this.bikes = bikes;
    }
}
