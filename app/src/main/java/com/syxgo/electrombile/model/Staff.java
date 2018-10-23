package com.syxgo.electrombile.model;

import java.io.Serializable;

/**
 * Created by tangchujia on 2017/8/30.
 */

public class Staff implements Serializable {
    int id;
    String created;
    String updated;
    boolean visibly;
    int version;
    int superior_id;
    int group_id;
    String phone;
    String name;
    int role;//用户类型
    double lng;
    double lat;
    boolean openim;
    String openim_password;//阿里旺旺账号密码
    String openim_userid;//阿里旺旺账号用户名
    int distributed_task_num;
    int accepted_task_num;
    int success_task_num;
    int fail_task_num;

    public int getDistributed_task_num() {
        return distributed_task_num;
    }

    public void setDistributed_task_num(int distributed_task_num) {
        this.distributed_task_num = distributed_task_num;
    }

    public int getAccepted_task_num() {
        return accepted_task_num;
    }

    public void setAccepted_task_num(int accepted_task_num) {
        this.accepted_task_num = accepted_task_num;
    }

    public int getSuccess_task_num() {
        return success_task_num;
    }

    public void setSuccess_task_num(int success_task_num) {
        this.success_task_num = success_task_num;
    }

    public int getFail_task_num() {
        return fail_task_num;
    }

    public void setFail_task_num(int fail_task_num) {
        this.fail_task_num = fail_task_num;
    }

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

    public int getSuperior_id() {
        return superior_id;
    }

    public void setSuperior_id(int superior_id) {
        this.superior_id = superior_id;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public boolean isOpenim() {
        return openim;
    }

    public void setOpenim(boolean openim) {
        this.openim = openim;
    }

    public String getOpenim_password() {
        return openim_password;
    }

    public void setOpenim_password(String openim_password) {
        this.openim_password = openim_password;
    }

    public String getOpenim_userid() {
        return openim_userid;
    }

    public void setOpenim_userid(String openim_userid) {
        this.openim_userid = openim_userid;
    }
}
