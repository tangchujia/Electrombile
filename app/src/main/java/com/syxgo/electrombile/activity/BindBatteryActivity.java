package com.syxgo.electrombile.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * Created by tangchujia on 2017/9/15.
 */
public class BindBatteryActivity extends BaseActivity implements View.OnClickListener {
    private static final int ECU_ID = 0x07;
    private static final int Bike_ID = 0x08;
    private EditText mBatteryTv;
    private EditText mBmsTv;
    private int mBatteryID;
    private int mBmsID;
    private Dialog progDialog = null;
    private TextView mResultTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_battery);
        initTop();
        initView();
    }

    private void initView() {
        mBatteryTv = (EditText) findViewById(R.id.ecu_id_tv);
        mBmsTv = (EditText) findViewById(R.id.bike_id_tv);

        findViewById(R.id.scan_ecu_id_btn).setOnClickListener(this);
        findViewById(R.id.scan_bike_id_btn).setOnClickListener(this);
        findViewById(R.id.bind_btn).setOnClickListener(this);
        mTitletv.setText("绑定电池");
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
                openCameraIntent = new Intent(BindBatteryActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, ECU_ID);
                break;
            case R.id.scan_bike_id_btn:
                openCameraIntent = new Intent(BindBatteryActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, Bike_ID);
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
        mBatteryID= Integer.parseInt(mBatteryTv.getText().toString().trim());
        mBmsID=Integer.parseInt(mBmsTv.getText().toString().trim());
        if (mBatteryID == 0 || mBmsID == 0) {
            ToastUtil.showToast(this, "信息不完整");
            dissmissProgressDialog();
            return;
        } else {
            showProgressDialog("正在绑定...");
            Map<String, Integer> params = new HashMap<>();
            params.put("battery_id", mBatteryID);
            params.put("bms_id", mBmsID);
            Map<String, Object> p = new HashMap<>();
            Map[] maps = {params};
            p.put("batterys", maps);
            NetRequest
                    .put()
                    .url(HttpUrl.BIND_BATTERY)
                    .addHeader("Authorization:Bear", MyPreference.getInstance(BindBatteryActivity.this).getToken())
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
                                    org.json.JSONArray jsonArray = new org.json.JSONObject(result).getJSONArray("batterys");
                                    ToastUtil.showToast(BindBatteryActivity.this, "绑定成功");
                                    mBatteryTv.setText("电池ID（二维码位于电池外壳）");
                                    mBmsTv.setText("BMS ID（二维码位于散热片）");
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                                    String bindStr = "绑定成功" + "\n" + "id：" + jsonObject.getString("id") + "\n"
                                            + "created：" + jsonObject.getString("created") + "\n"
                                            + "updated：" + jsonObject.getString("updated");
                                    showDialog("绑定成功", bindStr);
                                    mBatteryID = 0;
                                    mBmsID = 0;
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
        new AlertDialog.Builder(BindBatteryActivity.this).setTitle(title)
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
                    mBatteryID = Integer.parseInt(scanResult);
                    mBatteryTv.setText(scanResult);
                } else if (requestCode == Bike_ID) {
                    String bmsid = new org.json.JSONObject(scanResult).getString("BMS");
                    mBmsID = Integer.parseInt(bmsid, 16);
                    mBmsTv.setText(mBmsID+"");


                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToast(BindBatteryActivity.this, "二维码信息不对应");

            }
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(BindBatteryActivity.this, message);
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
        final EditText editText = new EditText(BindBatteryActivity.this);
        editText.setHint("请输入密码");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//                editText.setInputType();
        new android.support.v7.app.AlertDialog.Builder(BindBatteryActivity.this)
                .setTitle("绑定电池")
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
                            Toast.makeText(BindBatteryActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (str_id.equals("222222")) {
                            doBind();
                        } else {
                            Toast.makeText(BindBatteryActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.dismiss();
                    }
                }).show();
    }
}
