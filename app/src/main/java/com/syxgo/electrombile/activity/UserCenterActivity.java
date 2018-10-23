package com.syxgo.electrombile.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iflytek.autoupdate.IFlytekUpdate;
import com.iflytek.autoupdate.IFlytekUpdateListener;
import com.iflytek.autoupdate.UpdateConstants;
import com.iflytek.autoupdate.UpdateErrorCode;
import com.iflytek.autoupdate.UpdateInfo;
import com.iflytek.autoupdate.UpdateType;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.manager.ActivityManager;
import com.syxgo.electrombile.manager.UIHelper;
import com.syxgo.electrombile.model.Staff;
import com.syxgo.electrombile.util.DeviceUtil;
import com.syxgo.electrombile.util.LoginUtil;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.StringUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.ActionSheetDialog;
import com.syxgo.electrombile.view.LoadingDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tangchujia on 2017/8/17.
 */

public class UserCenterActivity extends BaseActivity {
    private Dialog progDialog = null;
    private TextView mNameTv;
    private TextView mPhoneTv;
    private TextView mVersionTv;
    private IFlytekUpdate updManager;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private AlertDialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercenter);
        initTop();
        initUpdate();
        initView();
        initData();
    }

    private void initView() {
        mTitletv.setText("个人中心");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mNameTv = (TextView) findViewById(R.id.name_tv);
        mPhoneTv = (TextView) findViewById(R.id.phone_tv);

        mVersionTv = (TextView) findViewById(R.id.version_tv);
        findViewById(R.id.change_pwd_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        findViewById(R.id.update_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyStoragePermissions(UserCenterActivity.this);
            }
        });
        mMenutv.setVisibility(View.VISIBLE);
        mMenutv.setText("退出");
        mMenutv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Setting", "logout");
                new ActionSheetDialog(UserCenterActivity.this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .setTitle("确定退出当前登录账号？")
                        .addSheetItem("确定", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                MyPreference.getInstance(UserCenterActivity.this).putToken("");
                                ActivityManager.getScreenManager().popAllActivity();
                            }
                        }).show();

            }
        });

        mVersionTv.setText("当前版本 V" + DeviceUtil.GetVersion(UserCenterActivity.this));

    }


    public void verifyStoragePermissions(Activity activity) {
// Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
// We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        } else {
            updManager.forceUpdate(activity, updateListener);

        }
    }


    private void initUpdate() {
        updManager = IFlytekUpdate.getInstance(UserCenterActivity.this);
        updManager.setDebugMode(false);
        updManager.setParameter(UpdateConstants.EXTRA_WIFIONLY, "false");
        // 设置通知栏icon，默认使用SDK默认
        updManager.setParameter(UpdateConstants.EXTRA_NOTI_ICON, "false");
        updManager.setParameter(UpdateConstants.EXTRA_STYLE, UpdateConstants.UPDATE_UI_DIALOG);
    }

    private IFlytekUpdateListener updateListener = new IFlytekUpdateListener() {

        @Override
        public void onResult(int errorcode, UpdateInfo result) {

            if (errorcode == UpdateErrorCode.OK && result != null) {
                if (result.getUpdateType() == UpdateType.NoNeed) {
                    handler.sendEmptyMessage(20001);
                    return;
                }
                updManager.showUpdateInfo(UserCenterActivity.this, result);
            } else {
                handler.sendEmptyMessage(20002);
            }
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 20001:
                    ToastUtil.showToast(UserCenterActivity.this, "已经是最新版本！");
                    break;
                case 20002:
                    ToastUtil.showToast(UserCenterActivity.this, "请求更新失败！");
                    break;
                default:
                    break;
            }
        }
    };

    private void initData() {
        String phone = MyPreference.getInstance(UserCenterActivity.this).getPhone();
        if (phone.equals("")) {
            MyPreference.getInstance(UserCenterActivity.this).putToken("");
            UIHelper.showLogin(UserCenterActivity.this);
        };

        showProgressDialog("正在加载...");
        NetRequest
                .get()
                .url(HttpUrl.GET_STAFF + "?phone=" + phone)
                .addHeader("Authorization:Bear", MyPreference.getInstance(UserCenterActivity.this).getToken())
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
                                org.json.JSONArray jsonArray = new JSONObject(result).getJSONArray("staffs");
                                List<Staff> staffs = com.alibaba.fastjson.JSONObject.parseArray(jsonArray.toString(), Staff.class);

//                                Staff staff = com.alibaba.fastjson.JSONObject.parseObject(jsonArray.toString(), Staff.class);
                                mPhoneTv.setText(staffs.get(0).getPhone());
                                mNameTv.setText(staffs.get(0).getName());
                            } else {
//                                UIHelper.showLogin(UserCenterActivity.this);
                                LoginUtil.login(UserCenterActivity.this, result);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(UserCenterActivity.this, R.string.error_msg);
                        dissmissProgressDialog();
                    }
                });

    }


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserCenterActivity.this);
        View view = View
                .inflate(this, R.layout.pwd_dialog, null);
        builder.setView(view);
        builder.setCancelable(true);
        final EditText pwd_et = (EditText) view
                .findViewById(R.id.pwd_et);//输入密码
        final EditText pwd_re_et = (EditText) view
                .findViewById(R.id.pwd_re_et);//确认密码
        Button btn_cancel = (Button) view
                .findViewById(R.id.btn_cancel);//取消按钮
        Button btn_comfirm = (Button) view
                .findViewById(R.id.btn_comfirm);//确定按钮
        //取消或确定按钮监听事件处理
        dialog = builder.create();
        dialog.show();

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_pwd = pwd_et.getText().toString().trim();
                String str_pwd_re = pwd_re_et.getText().toString().trim();
                if (StringUtil.isBlank(str_pwd)) {
                    ToastUtil.showToast(UserCenterActivity.this, "请输入密码");
                    return;
                }
                if (!str_pwd.equals(str_pwd_re)) {
                    ToastUtil.showToast(UserCenterActivity.this, "密码不一致");
                    return;
                }
                changePwd(str_pwd);

            }
        });
    }

    private void changePwd(String pwd) {
        showProgressDialog("正在修改密码...");
        Map<String, String> params = new HashMap<String, String>();
        params.put("password", pwd);
        NetRequest
                .post()
                .url(HttpUrl.CHANGE_PWD)
                .addHeader("Authorization:Bear", MyPreference.getInstance(UserCenterActivity.this).getToken())
                .jsonObject(params)
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
                                MyPreference.getInstance(UserCenterActivity.this).putToken("");
                                UIHelper.showLogin(UserCenterActivity.this);

                            } else {
                                LoginUtil.login(UserCenterActivity.this, result);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(UserCenterActivity.this, R.string.error_msg);
                        dialog.dismiss();
                        dissmissProgressDialog();
                    }
                });

    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(UserCenterActivity.this, message);
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
