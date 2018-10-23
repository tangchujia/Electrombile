package com.syxgo.electrombile.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.manager.UIHelper;
import com.syxgo.electrombile.util.LoginUtil;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.StringUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tangchujia on 2017/12/18.
 */

public class LoginPwdActivity extends BaseActivity implements View.OnClickListener {
    private EditText mPhoneEt;
    private ImageView mDeleteImg;
    private Button mNextBtn;
    private Dialog progDialog = null;
    private long mExitTime;
    private EditText mPwdEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pwd);
        initTop();
        initView();
    }

    private void initView() {
        if (!StringUtil.isEmpty(MyPreference.getInstance(LoginPwdActivity.this).getToken())) {
            UIHelper.showMain(LoginPwdActivity.this);
            finish();
        }
        mTitletv.setText("登录");
        mBackImg.setVisibility(View.GONE);
        mPhoneEt = (EditText) findViewById(R.id.phone_et);
        mDeleteImg = (ImageView) findViewById(R.id.delete_img);
        mPwdEt = (EditText) findViewById(R.id.pwd_et);
        mNextBtn = (Button) findViewById(R.id.next_btn);
        mNextBtn.setOnClickListener(this);
        mNextBtn.setEnabled(false);
        findViewById(R.id.forget_pwd).setOnClickListener(this);
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
                if (s.length() == 13) {
                    mNextBtn.setEnabled(true);
                    mNextBtn.setBackground(getResources().getDrawable(R.drawable.selector_btn_orange_square));
                } else {
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
            case R.id.next_btn:
                login();
                break;
            case R.id.delete_img:
                mPhoneEt.setText("");
                break;
            case R.id.forget_pwd:
                UIHelper.showLoginPhone(LoginPwdActivity.this);
                break;
        }
    }

    private void login() {
        String phoneStr = getPhone();
        String pwd = mPwdEt.getText().toString().trim();
        if (pwd.equals("")) {
            ToastUtil.showToast(LoginPwdActivity.this, "请输入密码");
            return;
        }
        showProgressDialog("正在登陆...");
        Map<String, String> params = new HashMap<String, String>();
        params.put("phone", phoneStr);
        params.put("password", pwd);
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
                                JSONObject object = new JSONObject(result).getJSONObject("staff");
                                String token = object.getString("token");
                                int staffId = object.getInt("id");
                                String phone = object.getString("phone");
                                MyPreference myPreference = MyPreference.getInstance(LoginPwdActivity.this);
                                myPreference.putToken(token);
                                myPreference.putUserId(staffId);
                                myPreference.putPhone(phone);
                                myPreference.putProviderId(-100);
                                finish();
                                UIHelper.showMain(LoginPwdActivity.this);
                            } else {
                                LoginUtil.login(LoginPwdActivity.this, result);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(LoginPwdActivity.this, "数据请求失败！");
                        dissmissProgressDialog();
                    }
                });
    }


    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(LoginPwdActivity.this, message);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //1.点击返回键条件成立
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            //2.点击的时间差如果大于2000，则提示用户点击两次退出
            if (System.currentTimeMillis() - mExitTime > 2000) {
                //3.保存当前时间
                mExitTime = System.currentTimeMillis();
                //4.提示
                ToastUtil.showToast(LoginPwdActivity.this, "再按一次退出程序");
            } else {
                //5.点击的时间差小于2000，退出。
                finish();
                System.exit(0);
            }
            return true;
        }
        return false;
    }
}
