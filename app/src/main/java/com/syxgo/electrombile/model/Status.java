package com.syxgo.electrombile.model;

import java.io.Serializable;

/**
 * Created by tangchujia on 2018/3/20.
 */

public class Status implements Serializable {
    boolean SideLock;
    boolean BikeLock;
    boolean BackseatLock;
    boolean Acc;
    boolean Beep;
    boolean Vibrate;
    boolean Wheel;

    public boolean isSideLock() {
        return SideLock;
    }

    public void setSideLock(boolean sideLock) {
        SideLock = sideLock;
    }

    public boolean isBikeLock() {
        return BikeLock;
    }

    public void setBikeLock(boolean bikeLock) {
        BikeLock = bikeLock;
    }

    public boolean isBackseatLock() {
        return BackseatLock;
    }

    public void setBackseatLock(boolean backseatLock) {
        BackseatLock = backseatLock;
    }

    public boolean isAcc() {
        return Acc;
    }

    public void setAcc(boolean acc) {
        Acc = acc;
    }

    public boolean isBeep() {
        return Beep;
    }

    public void setBeep(boolean beep) {
        Beep = beep;
    }

    public boolean isVibrate() {
        return Vibrate;
    }

    public void setVibrate(boolean vibrate) {
        Vibrate = vibrate;
    }

    public boolean isWheel() {
        return Wheel;
    }

    public void setWheel(boolean wheel) {
        Wheel = wheel;
    }
}
