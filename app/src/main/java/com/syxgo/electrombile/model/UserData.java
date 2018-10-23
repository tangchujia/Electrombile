package com.syxgo.electrombile.model;

import org.litepal.crud.DataSupport;

/**
 * Created by tangchujia on 2017/10/19.
 */

public class UserData extends DataSupport {
    int user_id;
    String phone;//	用户手机号
    String real_name;//	用户真实姓名
    int bike_id;//	正在骑行的车辆id

    public int getBike_id() {
        return bike_id;
    }

    public void setBike_id(int bike_id) {
        this.bike_id = bike_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }
}
