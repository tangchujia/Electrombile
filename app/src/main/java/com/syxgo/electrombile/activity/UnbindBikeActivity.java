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

import com.syxgo.electrombile.R;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangchujia on 2018/1/8.
 */

public class UnbindBikeActivity extends BaseActivity implements View.OnClickListener {
    private EditText mBikeIdTv;
    private static final int Bike_ID = 0x08;
    private TextView mResultTv;
    private Dialog progDialog = null;
    private int mBikeID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_unbind);
        initTop();
        initView();
    }

    private void initView() {
        mTitletv.setText("绑定车辆");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mResultTv = (TextView) findViewById(R.id.result_tv);
        mBikeIdTv = (EditText) findViewById(R.id.bike_id_tv);
        findViewById(R.id.unbind_btn).setOnClickListener(this);
        findViewById(R.id.scan_bike_id_btn).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.unbind_btn:
                try {
                    unbind();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.scan_bike_id_btn:
                Intent openCameraIntent = new Intent(UnbindBikeActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, Bike_ID);
                break;
        }
    }

    private void unbind() {
        final EditText editText = new EditText(UnbindBikeActivity.this);
        editText.setHint("请输入密码");

        new android.support.v7.app.AlertDialog.Builder(UnbindBikeActivity.this)
                .setTitle("解绑车辆")
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
                            Toast.makeText(UnbindBikeActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (str_id.equals("syxgo2017")) {
                            doUnBind();
                        } else {
                            Toast.makeText(UnbindBikeActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    private void doUnBind() {
        NetUtil.checkNetwork(this);
        mBikeID = Integer.parseInt(mBikeIdTv.getText().toString().trim());
        if (mBikeID == 0) {
            ToastUtil.showToast(this, "信息不完整");
            dissmissProgressDialog();
            return;
        } else {
            showProgressDialog("正在绑定...");
            Map<String, Integer> params = new HashMap<>();
            params.put("bike_id", mBikeID);
            Map<String, Object> p = new HashMap<>();
            Map[] maps = {params};
            p.put("bikes", maps);
            NetRequest
                    .post()
                    .url(HttpUrl.UNBIND_BIKE)
                    .addHeader("Authorization:Bear", MyPreference.getInstance(UnbindBikeActivity.this).getToken())
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
                                    ToastUtil.showToast(UnbindBikeActivity.this, "绑定成功");
                                    mBikeIdTv.setText("");
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                                    showDialog("解绑成功", "车辆编号：" + mBikeID);
                                    mBikeID = 0;
                                } else {
                                    String message = new org.json.JSONObject(result).getString("message");
                                    showDialog("解绑失败", message);
                                }
                            } catch (Exception e) {
                                showDialog("解绑失败", e.getMessage());
                                e.printStackTrace();
                            }
                            dissmissProgressDialog();
                        }

                        @Override
                        public void onFailed(NetResponse netResponse) {
                            showDialog("解绑失败", getString(R.string.error_msg));
                            dissmissProgressDialog();
                        }
                    });
        }
    }

    private void showDialog(String title, String msg) {
        new AlertDialog.Builder(UnbindBikeActivity.this).setTitle(title)
                .setMessage(msg)
                .setCancelable(true)
                .setIcon(R.mipmap.ic_logo_app)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
                if (requestCode == Bike_ID) {

                    String[] strings = scanResult.split("#");

//                    mBikeID = Integer.parseInt(strings[strings.length - 1]);
                    mBikeIdTv.setText(strings[strings.length - 1]);


                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToast(UnbindBikeActivity.this, "二维码信息不对应");

            }
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(UnbindBikeActivity.this, message);
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
