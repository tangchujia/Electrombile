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
import com.syxgo.electrombile.model.Bike;
import com.syxgo.electrombile.model.OrderData;
import com.syxgo.electrombile.util.LoginUtil;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;

import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tangchujia on 2017/10/18.
 */

public class BikeDetailActivity extends BaseActivity implements View.OnClickListener {
    private Dialog progDialog = null;
    private static String[] text = new String[]{"开锁", "关锁", "响铃", "开坐垫锁", "上线", "下线"};
    private int mBikeId = -1;
    private boolean isOperate = false;
    private LinearLayout mOperateLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_detail);
        initTop();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBikeInfo();
    }

    private void initView() {
        mTitletv.setText("车辆详情");
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
                getBikeInfo();
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

        findViewById(R.id.unlock_tv).setOnClickListener(this);
        findViewById(R.id.lock_tv).setOnClickListener(this);
        findViewById(R.id.backlock_tv).setOnClickListener(this);
        findViewById(R.id.beep_tv).setOnClickListener(this);
        findViewById(R.id.online_tv).setOnClickListener(this);
        findViewById(R.id.offline_tv).setOnClickListener(this);
    }

    private void getBikeInfo() {
        int bikeId = getIntent().getIntExtra(Common.BIKE_ID, -1);
        if (bikeId == -1) {
            return;
        }
        NetUtil.checkNetwork(BikeDetailActivity.this);

        String url = HttpUrl.GET_BIKES + "?bike_id=" + bikeId;
        showProgressDialog("正在查询...");
        NetRequest
                .get()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(BikeDetailActivity.this).getToken())
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
                                org.json.JSONArray jsonArray = new org.json.JSONObject(result).getJSONArray("bikes");

                                List<Bike> bikes = JSONObject.parseArray(jsonArray.toString(), Bike.class);

                                if (bikes.size() == 0) {
                                    ToastUtil.showToast(BikeDetailActivity.this, "没有信息");
                                } else {
                                    setData(bikes.get(0));

                                }


                            } else {
                                LoginUtil.login(BikeDetailActivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(BikeDetailActivity.this, e.getMessage());

                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(BikeDetailActivity.this, "查询失败");
                        dissmissProgressDialog();
                    }
                });
    }

    private void setData(final Bike bike) {
        TextView bike_id = (TextView) findViewById(R.id.bike_id);
        TextView created_time = (TextView) findViewById(R.id.created_time);
        TextView update_time = (TextView) findViewById(R.id.update_time);
        TextView last_active_time = (TextView) findViewById(R.id.last_active_time);
        TextView bike_isrent = (TextView) findViewById(R.id.bike_isrent);
        TextView bike_battery = (TextView) findViewById(R.id.bike_battery);
        TextView bike_backup_battery = (TextView) findViewById(R.id.bike_backup_battery);
        TextView bike_islock = (TextView) findViewById(R.id.bike_islock);
        TextView bike_isonline = (TextView) findViewById(R.id.bike_isonline);
        TextView station_id = (TextView) findViewById(R.id.station_id);
        TextView ride_id = (TextView) findViewById(R.id.ride_id);
        TextView last_ride_id = (TextView) findViewById(R.id.last_ride_id);
        TextView last_ride_time = (TextView) findViewById(R.id.last_ride_time);
        TextView bike_des = (TextView) findViewById(R.id.bike_des);

        LinearLayout layout_ride_id = (LinearLayout) findViewById(R.id.layout_ride_id);
        layout_ride_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bike.getRide_id() == 0) {
                    ToastUtil.showToast(BikeDetailActivity.this, "车辆当前没有行程");
                } else {
                    UIHelper.showRideDetail(BikeDetailActivity.this, bike.getRide_id());
                }
            }
        });

        LinearLayout layout_last_ride_id = (LinearLayout) findViewById(R.id.layout_last_ride_id);
        layout_last_ride_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bike.getLast_ride_id() == 0) {
                    ToastUtil.showToast(BikeDetailActivity.this, "车辆没有最后一次行程");
                } else {
                    UIHelper.showRideDetail(BikeDetailActivity.this, bike.getLast_ride_id());
                }

            }
        });

        LinearLayout layout_bike_location = (LinearLayout) findViewById(R.id.layout_bike_location);
        layout_bike_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showBikeLocation(BikeDetailActivity.this, bike.getId());
            }
        });
        LinearLayout layout_bike_orbit = (LinearLayout) findViewById(R.id.layout_bike_orbit);
        layout_bike_orbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showBikeOrbit(BikeDetailActivity.this, bike.getId());
            }
        });
        mBikeId = bike.getId();
        bike_id.setText("车辆编号：" + bike.getId());
        created_time.setText("创建时间：" + bike.getCreated());
        update_time.setText("更新时间：" + bike.getUpdated());
        last_active_time.setText("上一次与服务器通信时间：" + bike.getLast_active());
        if (bike.is_rent()) {
            bike_isrent.setText("是否租借：是");
        } else {
            bike_isrent.setText("是否租借：否");
        }
        bike_battery.setText("车辆电池电量：" + bike.getBattery_level() + "%");
        bike_backup_battery.setText("备用电池电量：" + bike.getBackup_battery_level() + "%");
        if (bike.is_lock()) {
            bike_islock.setText("车锁状态：已上锁");
        } else {
            bike_islock.setText("车锁状态：未上锁");
        }
        if (bike.is_offline()) {
            bike_isonline.setText("是否已上线：否");
        } else {
            bike_isonline.setText("是否已上线：是");
        }
        station_id.setText("最后停放的停车点ID：" + bike.getStation_id());
        ride_id.setText("当前行程ID：" + bike.getRide_id());
        last_ride_id.setText("最后一次行程ID：" + bike.getLast_ride_id());
        last_ride_time.setText("最后一次行程时间：" + bike.getLast_ride_time());
        bike_des.setText("车辆描述：" + bike.getDescription());
    }


    private void operate(final int index, final String action, String title) {
        //0"开锁", 1"关锁", 2"响铃", 3"开坐垫锁", 4"上线", 5"下线"

        if (mBikeId == -1 || index == -1 || title.equals("") || action.equals("")) {
            return;
        }

        final EditText editText = new EditText(BikeDetailActivity.this);
        if (index == 4 || index == 5) {
            editText.setHint("请输入上下线原因");
        } else {
            editText.setHint("请输入123456");
        }

        new AlertDialog.Builder(BikeDetailActivity.this)
                .setTitle("车辆" + mBikeId + "确定" + title + "？")
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
                        switch (index) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                                if (str_id.equals("123456")) {
                                    bikeRpc(mBikeId, action);
                                } else {
                                    ToastUtil.showToast(BikeDetailActivity.this, "输入错误");
                                }
                                break;
                            case 4:
                                bikeLine(mBikeId, 4, str_id);
                                break;
                            case 5:
                                bikeLine(mBikeId, 5, str_id);
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();

                    }
                }).show();


    }

    private void bikeLine(int bikeid, int action, String comment) {
        if (comment.equals("")) {
            ToastUtil.showToast(BikeDetailActivity.this, "上下线理由不能为空");
            return;
        }
        String url = "";
        if (action == 4) {
            url = HttpUrl.BIKE_ONLINE;

        } else if (action == 5) {
            url = HttpUrl.BIKE_OFFLINE;
        }
        if (url.equals("")) return;
        Map<String, Object> params = new HashMap<>();
        params.put("bike_id", bikeid);
        params.put("comment", comment);
        Map<String, Object> p = new HashMap<>();
        Map[] maps = {params};
        p.put("bikes", maps);
        NetRequest
                .post()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(BikeDetailActivity.this).getToken())
                .jsonObject(p)
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
                                ToastUtil.showToast(BikeDetailActivity.this, "操作成功");
                            } else {
                                LoginUtil.login(BikeDetailActivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(BikeDetailActivity.this, "失败");
                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(BikeDetailActivity.this, "失败");
                        dissmissProgressDialog();
                    }
                });
    }


    private void bikeRpc(int bikeid, String action) {
        if (action.equals("")) return;
        NetUtil.checkNetwork(BikeDetailActivity.this);
        showProgressDialog("正在执行...");
        Map<String, Object> params = new HashMap<>();
        params.put("bike_id", bikeid);
        params.put("action", action);
        Map<String, Object> p = new HashMap<>();
        Map[] maps = {params};
        p.put("bikes", maps);
        NetRequest
                .post()
                .url(HttpUrl.BIKE_RPC)
                .addHeader("Authorization:Bear", MyPreference.getInstance(BikeDetailActivity.this).getToken())
                .jsonObject(p)
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
                                ToastUtil.showToast(BikeDetailActivity.this, "操作成功");
                            } else {
                                LoginUtil.login(BikeDetailActivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(BikeDetailActivity.this, "失败");
                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(BikeDetailActivity.this, "失败");
                        dissmissProgressDialog();
                    }
                });
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(BikeDetailActivity.this, message);
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

    @Override
    public void onClick(View view) {
        String action = "";
        String title = "";
        int index = -1;
        switch (view.getId()) {
            case R.id.unlock_tv:
                index = 0;
                action = "unlock";
                title = "开锁";
                break;
            case R.id.lock_tv:
                index = 1;
                action = "lock";
                title = "关锁";
                break;
            case R.id.beep_tv:
                index = 2;
                action = "beep";
                title = "响铃";
                break;
            case R.id.backlock_tv:
                index = 3;
                action = "unlock_backseat";
                title = "开坐垫锁";
                break;
            case R.id.online_tv:
                index = 4;
                title = "上线";
                action = "online";
                break;
            case R.id.offline_tv:
                index = 5;
                title = "下线";
                action = "offline";

                break;
        }
        operate(index, action, title);
    }
}
