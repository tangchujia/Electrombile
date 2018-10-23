package com.syxgo.electrombile.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
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
import com.syxgo.electrombile.model.Ride;
import com.syxgo.electrombile.util.LoginUtil;
import com.syxgo.electrombile.util.MoneyUtils;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tangchujia on 2017/10/18.
 */

public class RideDetailactivity extends BaseActivity {
    private Dialog progDialog = null;
    private boolean isOperate = false;
    private LinearLayout mOperateLayout;
    private int mUserId = -1;
    private int mRideId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail);
        initTop();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRideInfo();
    }

    private void initView() {
        mTitletv.setText("行程详情");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mOperateLayout = (LinearLayout) findViewById(R.id.layout_operate);
        mMenuImg.setVisibility(View.VISIBLE);
        mMenuImg.setBackgroundResource(R.drawable.refresh_btn);
        mMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRideInfo();
            }
        });

        mMenutv.setVisibility(View.VISIBLE);
        mMenutv.setText("操作");
        mMenutv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOperate = !isOperate;
                if (isOperate) {
                    mOperateLayout.setVisibility(View.VISIBLE);
                } else {
                    mOperateLayout.setVisibility(View.GONE);

                }
            }
        });
        findViewById(R.id.finish_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishRide();
            }
        });
    }

    private void finishRide() {
        if (mUserId == -1) return;
        if (mRideId == -1) return;
        final EditText editText = new EditText(RideDetailactivity.this);
        editText.setHint("请输入骑行费用（输入0自动计费）");

        new AlertDialog.Builder(RideDetailactivity.this)
                .setTitle("确定结束行程" + mRideId + "？")
                .setCancelable(true)
                .setView(editText)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str_id = editText.getText().toString().trim();
                        if (str_id.equals("")) {
                            ToastUtil.showToast(RideDetailactivity.this, "请输入骑行费用");
                            return;
                        }
                        doFinish(str_id);

                        dialog.dismiss();

                    }
                }).show();
    }

    private void doFinish(String money) {
        NetUtil.checkNetwork(RideDetailactivity.this);
        int fee = Integer.parseInt(MoneyUtils.yuanToFen(money));

        showProgressDialog("正在执行...");
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", mUserId);
        params.put("ride_id", mRideId);
        if (fee != 0) {
            params.put("fee", fee);
        }

        NetRequest
                .post()
                .url(HttpUrl.RIDE_FINISH)
                .addHeader("Authorization:Bear", MyPreference.getInstance(RideDetailactivity.this).getToken())
                .jsonObject(params)
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
                                ToastUtil.showToast(RideDetailactivity.this, "操作成功");
                            } else {
                                LoginUtil.login(RideDetailactivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(RideDetailactivity.this, "失败");
                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(RideDetailactivity.this, "失败");
                        dissmissProgressDialog();
                    }
                });
    }

    private void getRideInfo() {
        int rideId = getIntent().getIntExtra(Common.RIDE_ID, -1);
        if (rideId == -1) {
            return;
        }
        NetUtil.checkNetwork(RideDetailactivity.this);

        String url = HttpUrl.GET_RIDES + "?ride_id=" + rideId;
        showProgressDialog("正在查询...");
        NetRequest
                .get()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(RideDetailactivity.this).getToken())
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
                                org.json.JSONArray jsonArray = new org.json.JSONObject(result).getJSONArray("rides");

                                List<Ride> rides = JSONObject.parseArray(jsonArray.toString(), Ride.class);

                                if (rides.size() == 0) {
                                    ToastUtil.showToast(RideDetailactivity.this, "没有信息");
                                } else {
                                    setData(rides.get(0));

                                }


                            } else {
                                LoginUtil.login(RideDetailactivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(RideDetailactivity.this, e.getMessage());

                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(RideDetailactivity.this, "查询失败");
                        dissmissProgressDialog();
                    }
                });
    }

    private void setData(final Ride ride) {
        TextView ride_id = (TextView) findViewById(R.id.ride_id);
        TextView created_time = (TextView) findViewById(R.id.created_time);
        TextView update_time = (TextView) findViewById(R.id.update_time);
        TextView ride_status = (TextView) findViewById(R.id.ride_status);
        TextView ride_distance = (TextView) findViewById(R.id.ride_distance);
        TextView ride_time = (TextView) findViewById(R.id.ride_time);
        TextView order_id = (TextView) findViewById(R.id.order_id);
        TextView real_fee = (TextView) findViewById(R.id.real_fee);
        TextView fee = (TextView) findViewById(R.id.fee);
        TextView gift_fee = (TextView) findViewById(R.id.gift_fee);
        TextView user_id = (TextView) findViewById(R.id.user_id);
        TextView bike_id = (TextView) findViewById(R.id.bike_id);

        LinearLayout layout_order = (LinearLayout) findViewById(R.id.layout_order);
        layout_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showOrderDetail(RideDetailactivity.this, ride.getOrder_id() + "");

            }
        });

        LinearLayout layout_user = (LinearLayout) findViewById(R.id.layout_user);
        layout_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showUserDetail(RideDetailactivity.this, ride.getUser_id());
            }
        });

        LinearLayout layout_bike = (LinearLayout) findViewById(R.id.layout_bike);
        layout_bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showBikeDetail(RideDetailactivity.this, ride.getBike_id());
            }
        });

        LinearLayout layout_orbit = (LinearLayout) findViewById(R.id.layout_orbit);
        layout_orbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showRideOrbit(RideDetailactivity.this, ride.getId());
            }
        });
        mRideId = ride.getId();
        mUserId = ride.getUser_id();
        ride_id.setText("行程ID：" + ride.getId());
        created_time.setText("开始时间：" + ride.getTime_from());
        update_time.setText("更新时间：" + ride.getUpdated());

        int status = ride.getStatus();
        if (status == 1) {
            ride_status.setText("骑行状态：骑行中");

        } else if (status == 2) {
            ride_status.setText("骑行状态：已结束");

        }
        ride_distance.setText("骑行距离：" + ride.getDistance());
        ride_time.setText("行程时间：");
        order_id.setText("骑行相关订单：" + ride.getOrder_id());
        fee.setText("骑行中实时总金额（元）：" + MoneyUtils.fenToYuan(ride.getFee()));
        real_fee.setText("骑行结束实际金额（元）：" + MoneyUtils.fenToYuan(ride.getReal_fee()));
        gift_fee.setText("骑行结束赠送金额（元）：" + MoneyUtils.fenToYuan(ride.getGift_fee()));
        user_id.setText("用户ID：" + ride.getUser_id());
        bike_id.setText("车辆ID：" + ride.getBike_id());
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(RideDetailactivity.this, message);
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
