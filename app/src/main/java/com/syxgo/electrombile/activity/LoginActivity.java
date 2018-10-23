package com.syxgo.electrombile.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import com.syxgo.electrombile.R;
import com.syxgo.electrombile.application.MyApplication;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.StringUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tangchujia on 2017/7/20.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_READ_CONTACTS = 0;
    private EditText mPhoneEt;
    private ImageView mDeleteImg;
    private Button mNextBtn;
    private Dialog progDialog = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initTop();
        initView();
    }

    private void initView() {
        if (!StringUtil.isEmpty(MyPreference.getInstance(LoginActivity.this).getToken())) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        mTitletv.setText("手机验证");
        mBackImg.setVisibility(View.GONE);
        mPhoneEt = (EditText) findViewById(R.id.phone_et);
        mDeleteImg = (ImageView) findViewById(R.id.delete_img);
        mNextBtn= (Button) findViewById(R.id.next_btn);
        mNextBtn.setOnClickListener(this);
        mNextBtn.setEnabled(false);

        mDeleteImg.setOnClickListener(this);
        mPhoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s == null || s.length() == 0) return;
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < s.length(); j++) {
                    if (j != 3 && j != 8 && s.charAt(j) == ' ') {
                        continue;
                    } else {
                        sb.append(s.charAt(j));
                        if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                            sb.insert(sb.length() - 1, ' ');
                        }
                    }
                }
                if (!TextUtils.isEmpty(sb.toString().trim())) {
                    mDeleteImg.setVisibility(View.VISIBLE);
                } else {
                    mDeleteImg.setVisibility(View.INVISIBLE);
                }
                if (!TextUtils.isEmpty(sb.toString().trim()) && !sb.toString().equals(s.toString())) {
                    mPhoneEt.setText(sb.toString());
                    mPhoneEt.setSelection(sb.length());
                }
                if(s.length() == 13){
                    mNextBtn.setEnabled(true);
                    mNextBtn.setBackground(getResources().getDrawable(R.drawable.selector_btn_orange_square));
                }else{
                    mNextBtn.setEnabled(false);
                    mNextBtn.setBackground(getResources().getDrawable(R.color.colorGray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * 获取电话号码
     *
     * @return
     */
    public String getPhone() {
        String str = mPhoneEt.getText().toString();
        return replaceBlank(str);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.delete_img:
                mPhoneEt.setText("");
                break;
            case R.id.next_btn:
                sendCode();
                break;
        }
    }



    private void sendCode() {
        NetUtil.checkNetwork(this);

        showProgressDialog("验证码发送中");
        Map<String, String> params = new HashMap<String, String>();
        params.put("phone", getPhone());
        NetRequest
                .post()
                .url(HttpUrl.GET_CODE)
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
                                String noncestr = new JSONObject(result).getString("noncestr");
                                Intent intent = new Intent(LoginActivity.this, CodeActivity.class);
                                intent.putExtra("phone", mPhoneEt.getText().toString());
                                intent.putExtra("noncestr", noncestr);
                                startActivity(intent);
                            } else {
                                ToastUtil.showToast(LoginActivity.this, netResponse.getResult().toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dissmissProgressDialog();

                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(LoginActivity.this, R.string.error_msg);
                        dissmissProgressDialog();

                    }
                });
    }
    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(this, message);
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
