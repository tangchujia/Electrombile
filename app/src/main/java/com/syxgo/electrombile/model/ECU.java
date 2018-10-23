package com.syxgo.electrombile.model;

import java.io.Serializable;

/**
 * Created by tangchujia on 2017/8/29.
 */

public class ECU implements Serializable{
    String ble_mac;
    String ecu_uuid;
    String token;
    String secret;
    String gsm_imei;
    String cdma_imei;
    String ct_phone_number;
    String cm_phone_number;
    String cu_phone_number;
    int id;
    String created;
    String updated;
    boolean visibly;
    int version;


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

    public String getBle_mac() {
        return ble_mac;
    }

    public void setBle_mac(String ble_mac) {
        this.ble_mac = ble_mac;
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

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getGsm_imei() {
        return gsm_imei;
    }

    public void setGsm_imei(String gsm_imei) {
        this.gsm_imei = gsm_imei;
    }

    public String getCdma_imei() {
        return cdma_imei;
    }

    public void setCdma_imei(String cdma_imei) {
        this.cdma_imei = cdma_imei;
    }

    public String getCt_phone_number() {
        return ct_phone_number;
    }

    public void setCt_phone_number(String ct_phone_number) {
        this.ct_phone_number = ct_phone_number;
    }

    public String getCm_phone_number() {
        return cm_phone_number;
    }

    public void setCm_phone_number(String cm_phone_number) {
        this.cm_phone_number = cm_phone_number;
    }

    public String getCu_phone_number() {
        return cu_phone_number;
    }

    public void setCu_phone_number(String cu_phone_number) {
        this.cu_phone_number = cu_phone_number;
    }
}
