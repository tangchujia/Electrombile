package com.syxgo.electrombile.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.model.Battery;
import com.syxgo.electrombile.model.Batterys;
import com.syxgo.electrombile.model.Bike;
import com.syxgo.electrombile.model.Bikes;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.StringUtil;
import com.syxgo.electrombile.view.LoadingDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangchujia on 2017/9/15.
 */

public class CreateBatteryActivity extends BaseActivity {
    private EditText mNumTv;
    private Dialog progDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ecu);
        initTop();
        initView();
    }

    private void initView() {
        mTitletv.setText("创建电池");
//        mBikeRv = (RecyclerView) findViewById(R.id.ecu_rv);
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        mBikeRv.setLayoutManager(new LinearLayoutManager(this));

        mNumTv = (EditText) findViewById(R.id.num_et);
        findViewById(R.id.create_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create();
            }
        });
    }

    private void createBike() {
        NetUtil.checkNetwork(this);

        String numTv = mNumTv.getText().toString().trim();
        if (numTv.equals(""))
            return;
        showProgressDialog("正在创建...");
        int num = Integer.parseInt(numTv);
        List<Battery> data = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Battery bike = new Battery();
            data.add(bike);
        }
        Batterys batterys = new Batterys();
        batterys.setBatterys(data);
        NetRequest
                .post()
                .url(HttpUrl.CREATE_BATTERY)
                .addHeader("Authorization:Bear", MyPreference.getInstance(CreateBatteryActivity.this).getToken())
                .jsonObject(batterys)
                .build()
                .connTimeOut(10 * 1000)
                .readTimeOut(10 * 1000)
                .execute(new NetResponseListener() {
                    @Override
                    public void onSuccess(NetResponse netResponse) {
                        String result = netResponse.getResult().toString();
                        try {
                            int status = new JSONObject(result).getInt("status");
                            if (status == 200) {
                                mNumTv.setText("");
                                String str = new JSONObject(result).getString("battery");
                                List<Battery> list = com.alibaba.fastjson.JSONObject.parseArray(str, Battery.class);
                                StringBuffer buffer = new StringBuffer("");
                                for (Battery battery :
                                        list) {
                                    buffer.append("id:" + battery.getId() + "\n");
                                }
                                showDialog("创建成功", buffer.toString());
//                                mAdapter = new BikeAdapter(list);
//                                mBikeRv.setAdapter(mAdapter);
                                dissmissProgressDialog();
                            } else {
                                String message = new JSONObject(result).getString("message");
                                showDialog("创建失败", message);
                                dissmissProgressDialog();
                            }
                        } catch (Exception e) {
                            showDialog("创建失败", e.getMessage());
                            dissmissProgressDialog();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        showDialog("绑定失败", getString(R.string.error_msg));
                        dissmissProgressDialog();
                    }
                });
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(CreateBatteryActivity.this, message);
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

    private void showDialog(String title, String msg) {
        new AlertDialog.Builder(CreateBatteryActivity.this).setTitle(title)
                .setMessage(msg)
                .setCancelable(true)
                .setIcon(R.mipmap.ic_logo_app)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void create() {
        final EditText editText = new EditText(CreateBatteryActivity.this);
        editText.setHint("请输入密码");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//                editText.setInputType();
        new AlertDialog.Builder(CreateBatteryActivity.this)
                .setTitle("创建电池")
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
                        if (StringUtil.isBlank(str_id)) {
                            Toast.makeText(CreateBatteryActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (str_id.equals("111111")){
                            createBike();
                        }else {
                            Toast.makeText(CreateBatteryActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.dismiss();
                    }
                }).show();
    }
}
