package com.syxgo.electrombile.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.application.MyApplication;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.IdentifyingCodeView;
import com.syxgo.electrombile.view.LoadingDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tangchujia on 2017/8/18.
 */

public class CodeActivity extends BaseActivity {
    private Button mReSendBtn;
    private IdentifyingCodeView mCodeView;
    private Dialog progDialog = null;
    private int delayTime = 60;
    private boolean isStart = false;
    private TextView mPhoneTv;
    private String phone;
    private String noncestr;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        initTop();
        initView();
    }

    private void initView() {
        noncestr = getIntent().getStringExtra("noncestr");
        phone = getIntent().getStringExtra("phone");
        setTimer();
        mTitletv.setText("手机验证");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mCodeView = (IdentifyingCodeView) findViewById(R.id.codeview);
        mReSendBtn = (Button) findViewById(R.id.resend_btn);
        mReSendBtn.setEnabled(false);
        mCodeView = (IdentifyingCodeView) findViewById(R.id.codeview);
        mPhoneTv = (TextView) findViewById(R.id.phone_tv);
        mPhoneTv.setText(getIntent().getStringExtra("phone"));
        mCodeView.setInputCompleteListener(new IdentifyingCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                if (mCodeView.getTextContent().length() == 6) {
                    login();

                }

            }

            @Override
            public void deleteContent() {

            }
        });
    }

    private void login() {
        NetUtil.checkNetwork(this);

        showProgressDialog("正在登陆...");
        final String phoneStr = getPhone(phone);
        String code = mCodeView.getTextContent();
        Map<String, String> params = new HashMap<String, String>();
        params.put("phone", phoneStr);
        params.put("code", code);
        params.put("noncestr", noncestr);
        NetRequest
                .post()
                .url(HttpUrl.REGISTER_LOGIN)
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
                                String token = new JSONObject(result).getJSONObject("staff").getString("token");
                                MyPreference myPreference = MyPreference.getInstance(CodeActivity.this);
                                myPreference.putToken(token);
                                myPreference.putProviderId(-100);
                                myPreference.putPhone(phoneStr);
                                Intent intent = new Intent(CodeActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                ToastUtil.showToast(CodeActivity.this, netResponse.getResult().toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(CodeActivity.this, R.string.error_msg);
                        dissmissProgressDialog();
                    }
                });
    }

    /**
     * 获取电话号码
     *
     * @return
     */
    public String getPhone(String phone) {
        return replaceBlank(phone);
    }

    /**
     * 去除字符串中的空格、回车、换行符、制表符
     *
     * @param str
     * @return
     */
    private String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 设置验证码倒计时
     */
    private void setTimer() {
        isStart = true;
        delayTime = 60;
        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    delayTime--;
                    handler.sendEmptyMessage(20001);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } while (isStart && delayTime > 0);
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 20001:
                    mReSendBtn.setText("重发验证码" + delayTime + "s");
                    mReSendBtn.setEnabled(false);
                    mReSendBtn.setBackground(ContextCompat.getDrawable(CodeActivity.this, R.color.colorGray));
                    if (delayTime == 0) {
                        isStart = false;
                        mReSendBtn.setText("重新发送");
                        mReSendBtn.setEnabled(true);
                        mReSendBtn.setBackground(ContextCompat.getDrawable(CodeActivity.this, R.drawable.selector_btn));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(CodeActivity.this, message);
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
