package com.syxgo.electrombile.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tangchujia on 2017/9/13.
 */

public class StationGroup implements Serializable {
    String name;
    List<Station> stations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }
}
