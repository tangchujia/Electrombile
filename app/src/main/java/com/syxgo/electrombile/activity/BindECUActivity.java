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

import com.alibaba.fastjson.JSONObject;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.application.MyApplication;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.model.ECU;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.StringUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tangchujia on 2017/7/31.
 */
public class BindECUActivity extends BaseActivity implements View.OnClickListener {
    private static final int ECU_ID = 0x05;
    private static final int ECU_INFO = 0x06;
    private EditText mEcuIdTv;
    private EditText mEcuInfoTv;
    private String mEcuUUID;
    private String mEcuToken;
    private int mEcuID;
    private Dialog progDialog = null;
    private TextView mResultTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_ecu);
        initTop();
        initView();
    }

    private void initView() {
        mEcuIdTv = (EditText) findViewById(R.id.ecu_id_tv);
        mEcuInfoTv = (EditText) findViewById(R.id.ecu_info_tv);
        findViewById(R.id.scan_ecu_id_btn).setOnClickListener(this);
        findViewById(R.id.scan_ecu_info_btn).setOnClickListener(this);
        findViewById(R.id.bind_btn).setOnClickListener(this);
        mTitletv.setText("绑定ECU");
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
                openCameraIntent = new Intent(BindECUActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, ECU_ID);
                break;
            case R.id.scan_ecu_info_btn:
                openCameraIntent = new Intent(BindECUActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, ECU_INFO);
                break;
            case R.id.bind_btn:
                try {
                    bind();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void doBind() {
        NetUtil.checkNetwork(this);
        mEcuID = Integer.parseInt(mEcuIdTv.getText().toString().trim());
        String[] strings = mEcuInfoTv.getText().toString().trim().split("/");
        String[] uuid = strings[0].split("：");
        String[] token = strings[1].split("：");
        mEcuToken = token[1];
        mEcuUUID = uuid[1];
        if (mEcuID == 0 || StringUtil.isEmpty(mEcuToken) || StringUtil.isEmpty(mEcuUUID)) {
            ToastUtil.showToast(this, "信息不完整");
            dissmissProgressDialog();
            return;
        } else {
            showProgressDialog("正在绑定ECU...");
            Map<String, Object> params = new HashMap<>();
            params.put("ecu_id", mEcuID);
            params.put("token", mEcuToken);
            params.put("secret", "5359582D" + mEcuUUID);
            params.put("ble_mac", mEcuToken);
            params.put("ecu_uuid", mEcuUUID);

            Map<String, Object> p = new HashMap<>();
            Map[] maps = {params};
            p.put("ecus", maps);
            NetRequest
                    .put()
                    .url(HttpUrl.BIND_ECU)
                    .addHeader("Authorization:Bear", MyPreference.getInstance(BindECUActivity.this).getToken())
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
                                    mEcuIdTv.setText("");
                                    mEcuInfoTv.setText("");
                                    mEcuID = 0;
                                    mEcuToken = "";
                                    mEcuUUID = "";

                                    String str = new org.json.JSONObject(result).getString("ecus");
                                    List<ECU> eculist = JSONObject.parseArray(str, ECU.class);
                                    ECU ecu1 = eculist.get(0);
                                    String bindStr = "绑定成功" + "\n" + "id：" + ecu1.getId() + "\n"
                                            + "created：" + ecu1.getCreated() + "\n"
                                            + "updated：" + ecu1.getUpdated() + "\n"
                                            + "version：" + ecu1.getVersion() + "\n"
                                            + "token：" + ecu1.getToken() + "\n"
                                            + "secret：" + ecu1.getSecret() + "\n";
                                    showDialog("绑定成功", bindStr);


                                } else {
                                    String message = new org.json.JSONObject(result).getString("message");
                                    showDialog("绑定失败", message);
                                }
                            } catch (Exception e) {
                                showDialog("绑定失败", "请重试！");
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
        new AlertDialog.Builder(BindECUActivity.this).setTitle(title)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            try {
                if (requestCode == ECU_ID) {

                    mEcuIdTv.setText(scanResult);


                } else if (requestCode == ECU_INFO) {

                    mEcuToken = new org.json.JSONObject(scanResult).getString("TOKEN");
                    mEcuUUID = new org.json.JSONObject(scanResult).getString("UUID");
                    mEcuInfoTv.setText("UUID：" + mEcuUUID + "/" + "TOKEN：" + mEcuToken);


                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToast(BindECUActivity.this, "二维码信息不对应");
            }
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(BindECUActivity.this, message);
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
        final EditText editText = new EditText(BindECUActivity.this);
        editText.setHint("绑定ECU");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//                editText.setInputType();
        new android.support.v7.app.AlertDialog.Builder(BindECUActivity.this)
                .setTitle("绑定ECU")
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
                            Toast.makeText(BindECUActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (str_id.equals("444444")) {
                            doBind();
                        } else {
                            Toast.makeText(BindECUActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.dismiss();
                    }
                }).show();
    }
}
