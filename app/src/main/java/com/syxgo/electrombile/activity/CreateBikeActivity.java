package com.syxgo.electrombile.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.adapter.BikeAdapter;
import com.syxgo.electrombile.adapter.EcuAdapter;
import com.syxgo.electrombile.application.MyApplication;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.model.Bike;
import com.syxgo.electrombile.model.Bikes;
import com.syxgo.electrombile.model.ECU;
import com.syxgo.electrombile.model.ECUs;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.StringUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangchujia on 2017/8/29.
 */
public class CreateBikeActivity extends BaseActivity {
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
        mTitletv.setText("创建车辆");
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
        List<Bike> data = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Bike bike = new Bike();
            data.add(bike);
        }
        Bikes bikes = new Bikes();
        bikes.setBikes(data);
        NetRequest
                .post()
                .url(HttpUrl.CREATE_BIKE)
                .addHeader("Authorization:Bear", MyPreference.getInstance(CreateBikeActivity.this).getToken())
                .jsonObject(bikes)
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
                                String str = new JSONObject(result).getString("bikes");
                                List<Bike> list = com.alibaba.fastjson.JSONObject.parseArray(str, Bike.class);
                                StringBuffer buffer = new StringBuffer("");
                                for (Bike bike :
                                        list) {
                                    buffer.append("id:" + bike.getId() + "\n");
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
            progDialog = new LoadingDialog().createLoadingDialog(CreateBikeActivity.this, message);
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
        new AlertDialog.Builder(CreateBikeActivity.this).setTitle(title)
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
        final EditText editText = new EditText(CreateBikeActivity.this);
        editText.setHint("请输入密码");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//                editText.setInputType();
        new android.support.v7.app.AlertDialog.Builder(CreateBikeActivity.this)
                .setTitle("创建车辆")
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
                            Toast.makeText(CreateBikeActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (str_id.equals("555555")){
                            createBike();
                        }else {
                            Toast.makeText(CreateBikeActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

}
