package com.syxgo.electrombile.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.manager.Common;
import com.syxgo.electrombile.manager.UIHelper;
import com.syxgo.electrombile.model.Order;
import com.syxgo.electrombile.model.OrderData;
import com.syxgo.electrombile.util.LoginUtil;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by tangchujia on 2017/10/18.
 */
public class OrderDetailActivity extends BaseActivity {
    private Dialog progDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initTop();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOrderInfo();
    }

    private void initView() {
        mTitletv.setText("订单详情");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mMenuImg.setBackgroundResource(R.drawable.refresh_btn);
        mMenuImg.setVisibility(View.VISIBLE);
        mMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOrderInfo();
            }
        });
    }

    private void getOrderInfo() {
        String orderNo = getIntent().getStringExtra(Common.ORDER_NO);
        if (orderNo.equals("")) {
            return;
        }
        NetUtil.checkNetwork(OrderDetailActivity.this);

        String url = HttpUrl.GET_ORDERS + "?order_no=" + orderNo;
        showProgressDialog("正在查询...");
        NetRequest
                .get()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(OrderDetailActivity.this).getToken())
                .build()
                .connTimeOut(10 * 1000)
                .readTimeOut(10 * 1000)
                .execute(new NetResponseListener() {
                    @Override
                    public void onSuccess(NetResponse netResponse) {
                        String result = netResponse.getResult().toString();
                        try {
                            int status = new org.json.JSONObject(result).getInt("status");
                            if (status == 200) {
                                DataSupport.deleteAll(OrderData.class);
                                org.json.JSONArray jsonArray = new org.json.JSONObject(result).getJSONArray("orders");

                                List<Order> orders = JSONObject.parseArray(jsonArray.toString(), Order.class);

                                if (orders.size() == 0) {
                                    ToastUtil.showToast(OrderDetailActivity.this, "没有信息");
                                } else {
                                    setData(orders.get(0));

                                }


                            } else {
                                LoginUtil.login(OrderDetailActivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(OrderDetailActivity.this, e.getMessage());

                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(OrderDetailActivity.this, "查询失败");
                        dissmissProgressDialog();
                    }
                });
    }

    private void setData(final Order order) {
        TextView order_id = (TextView) findViewById(R.id.order_id);
        TextView order_created_time = (TextView) findViewById(R.id.order_created_time);
        TextView order_update_time = (TextView) findViewById(R.id.order_update_time);
        TextView order_paid_time = (TextView) findViewById(R.id.order_paid_time);
        TextView order_no = (TextView) findViewById(R.id.order_no);
        TextView order_status = (TextView) findViewById(R.id.order_status);
        TextView order_subject = (TextView) findViewById(R.id.order_subject);
//        TextView order_description = (TextView) findViewById(R.id.order_description);
        TextView provider_id = (TextView) findViewById(R.id.provider_id);
        TextView user_id = (TextView) findViewById(R.id.user_id);
        TextView bike_id = (TextView) findViewById(R.id.bike_id);
        TextView ride_id = (TextView) findViewById(R.id.ride_id);
        LinearLayout layout_user_id = (LinearLayout) findViewById(R.id.layout_user_id);
        LinearLayout layout_ride_id = (LinearLayout) findViewById(R.id.layout_ride_id);
        LinearLayout layout_bike_id = (LinearLayout) findViewById(R.id.layout_bike_id);
        layout_user_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showUserDetail(OrderDetailActivity.this, order.getUser_id());
            }
        });
        layout_bike_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order.getBike_id() == 0) {
                    ToastUtil.showToast(OrderDetailActivity.this, "该笔订单没有相关车辆");
                } else {
                    UIHelper.showBikeDetail(OrderDetailActivity.this, order.getBike_id());
                }
            }
        });
        layout_ride_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order.getRide_id() == 0) {
                    ToastUtil.showToast(OrderDetailActivity.this, "该笔订单没有相关行程");
                } else {
                    UIHelper.showRideDetail(OrderDetailActivity.this, order.getRide_id());
                }
            }
        });

        order_id.setText("订单ID：" + order.getId());
        order_created_time.setText("创建时间：" + order.getCreated());
        order_update_time.setText("更新时间：" + order.getUpdated());
        order_paid_time.setText("支付时间：" + order.getPaid_time());
        order_no.setText("订单编号：" + order.getOrder_no());
        int status = order.getOrder_status();
        if (status == 0) {
            order_status.setText("订单状态：预支付");
        } else if (status == 1) {
            order_status.setText("订单状态：支付成功");

        }
        order_subject.setText("订单内容：" + order.getSubject());
//        order_description.setText("订单详细内容：" + order.getDescription());
        provider_id.setText("消费订单车辆所属供应商唯一标识：" + order.getProvider_id());
        user_id.setText("用户ID：" + order.getUser_id());
        bike_id.setText("消费订单关联的车辆id：" + order.getBike_id());
        ride_id.setText("消费订单关联的骑行id：" + order.getRide_id());
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(OrderDetailActivity.this, message);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
            progDialog = null;
        }
    }

}
