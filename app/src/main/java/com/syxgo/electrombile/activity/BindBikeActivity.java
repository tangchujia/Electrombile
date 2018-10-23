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
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.application.MyApplication;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.manager.UIHelper;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.StringUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangchujia on 2017/7/31.
 */

public class BindBikeActivity extends BaseActivity implements View.OnClickListener {
    private static final int ECU_ID = 0x07;
    private static final int Bike_ID = 0x08;
    private EditText mEcuIdTv;
    private EditText mBikeIdTv;
    private int mEcuID;
    private int mBikeID;
    private Dialog progDialog = null;
    private TextView mResultTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_bike);
        initTop();
        initView();
    }

    private void initView() {
        mEcuIdTv = (EditText) findViewById(R.id.ecu_id_tv);
        mBikeIdTv = (EditText) findViewById(R.id.bike_id_tv);
        findViewById(R.id.scan_ecu_id_btn).setOnClickListener(this);
        findViewById(R.id.scan_bike_id_btn).setOnClickListener(this);
        findViewById(R.id.bind_btn).setOnClickListener(this);
        mTitletv.setText("绑定车辆");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mResultTv = (TextView) findViewById(R.id.result_tv);
    }

    @Override
    public void onClick(View view) {
        Intent openCameraIntent = null;
        switch (view.getId()) {
            case R.id.scan_ecu_id_btn:
                openCameraIntent = new Intent(BindBikeActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, ECU_ID);
                break;
            case R.id.scan_bike_id_btn:
                openCameraIntent = new Intent(BindBikeActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, Bike_ID);
                break;
            case R.id.bind_btn:
                try {
//                    showDialog("测试","测试");
                    bind();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void doBind() {
        NetUtil.checkNetwork(this);
//        String[] strs = mEcuIdTv.getText().toString().split("：");
        mEcuID = Integer.parseInt(mEcuIdTv.getText().toString().trim());
//        String[] bike_strs = mBikeIdTv.getText().toString().split("：");
        mBikeID = Integer.parseInt(mBikeIdTv.getText().toString().trim());
        if (mEcuID == 0 || mBikeID == 0) {
            ToastUtil.showToast(this, "信息不完整");
            dissmissProgressDialog();
            return;
        } else {
            showProgressDialog("正在绑定...");
            Map<String, Integer> params = new HashMap<>();
            params.put("ecu_id", mEcuID);
            params.put("bike_id", mBikeID);
            Map<String, Object> p = new HashMap<>();
            Map[] maps = {params};
            p.put("bikes", maps);
            NetRequest
                    .post()
                    .url(HttpUrl.BIND_BIKE)
                    .addHeader("Authorization:Bear", MyPreference.getInstance(BindBikeActivity.this).getToken())
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
                                    org.json.JSONArray jsonArray = new org.json.JSONObject(result).getJSONArray("bikes");
                                    ToastUtil.showToast(BindBikeActivity.this, "绑定成功");
                                    mEcuIdTv.setText("");
                                    mBikeIdTv.setText("");
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                                    String bindStr = "绑定成功" + "\n" + "id：" + jsonObject.getString("id") + "\n"
                                            + "created：" + jsonObject.getString("created") + "\n"
                                            + "updated：" + jsonObject.getString("updated");
                                    showDialog("绑定成功", bindStr);
                                    mEcuID = 0;
                                    mBikeID = 0;
                                } else {
                                    String message = new org.json.JSONObject(result).getString("message");
                                    showDialog("绑定失败", message);
                                }
                            } catch (Exception e) {
                                showDialog("绑定失败", e.getMessage());
                                e.printStackTrace();
                            }
                            dissmissProgressDialog();
                        }

                        @Override
                        public void onFailed(NetResponse netResponse) {
                            showDialog("绑定失败", getString(R.string.error_msg));
                            dissmissProgressDialog();
                        }
                    });

        }
    }

    private void showDialog(String title, String msg) {
        new AlertDialog.Builder(BindBikeActivity.this).setTitle(title)
                .setMessage(msg)
                .setCancelable(true)
                .setIcon(R.mipmap.ic_logo_app)
                .setPositiveButton("写入NFC", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UIHelper.showNFCWrite(BindBikeActivity.this, 155);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            try {
                if (requestCode == ECU_ID) {
//                    mEcuID = Integer.parseInt(scanResult);
                    mEcuIdTv.setText(scanResult);
                } else if (requestCode == Bike_ID) {

                    String[] strings = scanResult.split("#");

//                    mBikeID = Integer.parseInt(strings[strings.length - 1]);
                    mBikeIdTv.setText(strings[strings.length - 1]);


                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToast(BindBikeActivity.this, "二维码信息不对应");

            }
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(BindBikeActivity.this, message);
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

    private void bind() {
        final EditText editText = new EditText(BindBikeActivity.this);
        editText.setHint("请输入密码");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//                editText.setInputType();
        new android.support.v7.app.AlertDialog.Builder(BindBikeActivity.this)
                .setTitle("绑定车辆")
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
                            Toast.makeText(BindBikeActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (str_id.equals("666666")) {
                            doBind();
                        } else {
                            Toast.makeText(BindBikeActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.dismiss();
                    }
                }).show();
    }
}
