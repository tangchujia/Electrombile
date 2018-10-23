package com.syxgo.electrombile.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by tangchujia on 2017/10/18.
 */

public class User extends DataSupport implements Serializable {
    int id;//	用户的id,唯一标识,自增字段
    String created;//用户创建时间
    String updated;//	更新时间
    boolean visibly;//	是否被删除
    String phone;//	用户手机号
    String password;//	用户登录密码
    String name;//	用户昵称
    String real_name;//	用户真实姓名
    String identify;//	用户身份证号~~~~
    String imgurl;//	用户头像信息
    String invite;//	用户邀请码
    boolean is_staff;//	是否是公司员工
    boolean is_identify_verified;//	是否实名认证
    boolean is_blocked;//	是否被列入黑名单
    String last_login;//	最后一次登录时间
    String read_message_time;//	最后阅读消息时间
    int unread_message_count;//	未读消息数量
    int ride_id;//	正在骑行订单id
    int bike_id;//	正在骑行的车辆id
    int ride_time;//	骑行总时间
    int ride_count;//	总骑行次数
    int ride_distince;//	总骑行距离
    boolean openim;//	是否创建了openim账号
    String openim_userid;//	openim账号的密码
    String openim_password;//	openim账号的密码
    int deposit;//	用户押金
    int real_balance;//	用户当前真实余额
    int gift_balance;//	用户当前赠送余额
    int consume_real;//	用户消费真实金额
    int consume_gift;//	用户消费赠送余额

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getInvite() {
        return invite;
    }

    public void setInvite(String invite) {
        this.invite = invite;
    }

    public boolean is_staff() {
        return is_staff;
    }

    public void setIs_staff(boolean is_staff) {
        this.is_staff = is_staff;
    }

    public boolean is_identify_verified() {
        return is_identify_verified;
    }

    public void setIs_identify_verified(boolean is_identify_verified) {
        this.is_identify_verified = is_identify_verified;
    }

    public boolean is_blocked() {
        return is_blocked;
    }

    public void setIs_blocked(boolean is_blocked) {
        this.is_blocked = is_blocked;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public String getRead_message_time() {
        return read_message_time;
    }

    public void setRead_message_time(String read_message_time) {
        this.read_message_time = read_message_time;
    }

    public int getUnread_message_count() {
        return unread_message_count;
    }

    public void setUnread_message_count(int unread_message_count) {
        this.unread_message_count = unread_message_count;
    }

    public int getRide_id() {
        return ride_id;
    }

    public void setRide_id(int ride_id) {
        this.ride_id = ride_id;
    }

    public int getBike_id() {
        return bike_id;
    }

    public void setBike_id(int bike_id) {
        this.bike_id = bike_id;
    }

    public int getRide_time() {
        return ride_time;
    }

    public void setRide_time(int ride_time) {
        this.ride_time = ride_time;
    }

    public int getRide_count() {
        return ride_count;
    }

    public void setRide_count(int ride_count) {
        this.ride_count = ride_count;
    }

    public int getRide_distince() {
        return ride_distince;
    }

    public void setRide_distince(int ride_distince) {
        this.ride_distince = ride_distince;
    }

    public boolean isOpenim() {
        return openim;
    }

    public void setOpenim(boolean openim) {
        this.openim = openim;
    }

    public String getOpenim_userid() {
        return openim_userid;
    }

    public void setOpenim_userid(String openim_userid) {
        this.openim_userid = openim_userid;
    }

    public String getOpenim_password() {
        return openim_password;
    }

    public void setOpenim_password(String openim_password) {
        this.openim_password = openim_password;
    }

    public int getDeposit() {
        return deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public int getReal_balance() {
        return real_balance;
    }

    public void setReal_balance(int real_balance) {
        this.real_balance = real_balance;
    }

    public int getGift_balance() {
        return gift_balance;
    }

    public void setGift_balance(int gift_balance) {
        this.gift_balance = gift_balance;
    }

    public int getConsume_real() {
        return consume_real;
    }

    public void setConsume_real(int consume_real) {
        this.consume_real = consume_real;
    }

    public int getConsume_gift() {
        return consume_gift;
    }

    public void setConsume_gift(int consume_gift) {
        this.consume_gift = consume_gift;
    }
}
