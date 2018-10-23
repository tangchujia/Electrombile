package com.syxgo.electrombile.model;

import org.litepal.crud.DataSupport;

/**
 * Created by tangchujia on 2017/10/19.
 */

public class OrderData extends DataSupport {
    String createdTime;
    int user_id;
    String order_no;//订单唯一标识

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }
}
