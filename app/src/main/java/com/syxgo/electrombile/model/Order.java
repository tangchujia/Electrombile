package com.syxgo.electrombile.model;

import java.io.Serializable;

/**
 * Created by tangchujia on 2017/10/18.
 */

public class Order implements Serializable {
    int id;
    String created;
    String updated;
    boolean visibly;//是否被删除
    int user_id;
    String order_no;//订单唯一标识
    int order_type;//1:押金 2:充值 3:消费
    int order_status;//订单状态(0:预支付，1:支付成功)
    String channel;//渠道标识(weixin,alipay)
    String transaction_no;//渠道订单号
    int amount;//真实押金、充值金额
    int gift_amount;//赠送余额
    String subject;//订单内容
    String description;//订单详细内容
    String paid_time;//订单支付时间
    int ride_id;
    int bike_id;
    int provider_id;
    int refund_status;//退款状态(0:未退款 1:退款中 2:退款成功 3:退款失败)
    String refund_time;//发起退款时间
    String refund_no;//退款订单号
    String refund_trade_no;
    int refund_amount;//渠道退款金额
    int refund_gift_amount;//赠送余额退款金额
    String refund_fail_code;//退款失败的错误码
    String refund_fail_msg;//退款失败的描述信息
    String transfer_no;//退款失败，走转账接口给用户转账时订单号

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

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTransaction_no() {
        return transaction_no;
    }

    public void setTransaction_no(String transaction_no) {
        this.transaction_no = transaction_no;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getGift_amount() {
        return gift_amount;
    }

    public void setGift_amount(int gift_amount) {
        this.gift_amount = gift_amount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaid_time() {
        return paid_time;
    }

    public void setPaid_time(String paid_time) {
        this.paid_time = paid_time;
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

    public int getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(int provider_id) {
        this.provider_id = provider_id;
    }

    public int getRefund_status() {
        return refund_status;
    }

    public void setRefund_status(int refund_status) {
        this.refund_status = refund_status;
    }

    public String getRefund_time() {
        return refund_time;
    }

    public void setRefund_time(String refund_time) {
        this.refund_time = refund_time;
    }

    public String getRefund_no() {
        return refund_no;
    }

    public void setRefund_no(String refund_no) {
        this.refund_no = refund_no;
    }

    public String getRefund_trade_no() {
        return refund_trade_no;
    }

    public void setRefund_trade_no(String refund_trade_no) {
        this.refund_trade_no = refund_trade_no;
    }

    public int getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(int refund_amount) {
        this.refund_amount = refund_amount;
    }

    public int getRefund_gift_amount() {
        return refund_gift_amount;
    }

    public void setRefund_gift_amount(int refund_gift_amount) {
        this.refund_gift_amount = refund_gift_amount;
    }

    public String getRefund_fail_code() {
        return refund_fail_code;
    }

    public void setRefund_fail_code(String refund_fail_code) {
        this.refund_fail_code = refund_fail_code;
    }

    public String getRefund_fail_msg() {
        return refund_fail_msg;
    }

    public void setRefund_fail_msg(String refund_fail_msg) {
        this.refund_fail_msg = refund_fail_msg;
    }

    public String getTransfer_no() {
        return transfer_no;
    }

    public void setTransfer_no(String transfer_no) {
        this.transfer_no = transfer_no;
    }
}
