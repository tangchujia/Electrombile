package com.syxgo.electrombile.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.manager.Common;
import com.syxgo.electrombile.manager.UIHelper;
import com.syxgo.electrombile.model.OrderData;
import com.syxgo.electrombile.model.User;
import com.syxgo.electrombile.util.LoginUtil;
import com.syxgo.electrombile.util.MoneyUtils;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;

import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tangchujia on 2017/10/18.
 */

public class UserDetailActivity extends BaseActivity implements View.OnClickListener {
    private Dialog progDialog = null;
    private boolean isOperate = false;
    private LinearLayout mOperateLayout;
    private int mUserId;
    private TextView add_balance_tv;
    private TextView sub_balance_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        initTop();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
    }

    private void initView() {
        mTitletv.setText("用户详情");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mOperateLayout = (LinearLayout) findViewById(R.id.layout_operate);

        mMenuImg.setVisibility(View.VISIBLE);
        mMenuImg.setBackgroundResource(R.drawable.refresh_btn);
        mMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserInfo();
            }
        });

        mMenutv.setVisibility(View.VISIBLE);
        mMenutv.setText("操作");
        mMenutv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOperate = !isOperate;
                if (isOperate) {
                    mOperateLayout.setVisibility(View.VISIBLE);
                } else {
                    mOperateLayout.setVisibility(View.GONE);

                }
            }
        });

        findViewById(R.id.block_tv).setOnClickListener(this);
        findViewById(R.id.unblock_tv).setOnClickListener(this);
        findViewById(R.id.refund_balance_tv).setOnClickListener(this);
        findViewById(R.id.refund_deposit_tv).setOnClickListener(this);
        findViewById(R.id.add_balance_tv).setOnClickListener(this);
        findViewById(R.id.sub_balance_tv).setOnClickListener(this);
    }

    private void getUserInfo() {
        int userId = getIntent().getIntExtra(Common.USER_ID, -1);
        if (userId == -1) {
            return;
        }
        NetUtil.checkNetwork(UserDetailActivity.this);

        String url = HttpUrl.GET_USERS + "?user_id=" + userId;
        showProgressDialog("正在查询...");
        NetRequest
                .get()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(UserDetailActivity.this).getToken())
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
                                DataSupport.deleteAll(OrderData.class);
                                org.json.JSONArray jsonArray = new org.json.JSONObject(result).getJSONArray("users");

                                List<User> users = JSONObject.parseArray(jsonArray.toString(), User.class);

                                if (users.size() == 0) {
                                    ToastUtil.showToast(UserDetailActivity.this, "没有信息");
                                } else {
                                    setData(users.get(0));

                                }


                            } else {
                                ToastUtil.showToast(UserDetailActivity.this, netResponse.getResult().toString());
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(UserDetailActivity.this, e.getMessage());

                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(UserDetailActivity.this, "查询失败");
                        dissmissProgressDialog();
                    }
                });
    }

    private void setData(final User user) {
        TextView user_id = (TextView) findViewById(R.id.user_id);
        TextView created_time = (TextView) findViewById(R.id.created_time);
        TextView update_time = (TextView) findViewById(R.id.update_time);
        TextView user_real_name = (TextView) findViewById(R.id.user_real_name);
        TextView user_phone = (TextView) findViewById(R.id.user_phone);
        TextView user_name = (TextView) findViewById(R.id.user_name);
        TextView user_invite = (TextView) findViewById(R.id.user_invite);
        TextView user_is_identify_verified = (TextView) findViewById(R.id.user_is_identify_verified);
        TextView user_is_blocked = (TextView) findViewById(R.id.user_is_blocked);
        TextView user_deposit = (TextView) findViewById(R.id.user_deposit);
        TextView real_balance = (TextView) findViewById(R.id.real_balance);
        TextView gift_balance = (TextView) findViewById(R.id.gift_balance);
        TextView consume_real = (TextView) findViewById(R.id.consume_real);
        TextView consume_gift = (TextView) findViewById(R.id.consume_gift);
        TextView ride_id = (TextView) findViewById(R.id.ride_id);
        TextView bike_id = (TextView) findViewById(R.id.bike_id);

        LinearLayout layout_ride = (LinearLayout) findViewById(R.id.layout_ride);
        layout_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getRide_id() == 0) {
                    ToastUtil.showToast(UserDetailActivity.this, "该用户当前没有骑行订单");
                } else {
                    UIHelper.showRideDetail(UserDetailActivity.this, user.getRide_id());
                }
            }
        });

        LinearLayout layout_bike = (LinearLayout) findViewById(R.id.layout_bike);
        layout_bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getRide_id() == 0) {
                    ToastUtil.showToast(UserDetailActivity.this, "该用户当前没有骑行车辆");
                } else {
                    UIHelper.showBikeDetail(UserDetailActivity.this, user.getBike_id());
                }

            }
        });
        mUserId = user.getId();
        user_id.setText("用户ID：" + user.getId());
        created_time.setText("创建时间：" + user.getCreated());
        update_time.setText("更新时间：" + user.getUpdated());
        user_real_name.setText("真实姓名：" + user.getReal_name());
        user_phone.setText("手机号码：" + user.getPhone());
        user_name.setText("昵称：" + user.getName());
        user_invite.setText("邀请码：" + user.getInvite());
        if (user.is_identify_verified()) {
            user_is_identify_verified.setText("是否实名认证：是");
        } else {
            user_is_identify_verified.setText("是否实名认证：否");
        }
        if (user.is_blocked()) {
            user_is_blocked.setText("是否列入黑名单：是");

        } else {
            user_is_blocked.setText("是否列入黑名单：否");

        }
        user_deposit.setText("用户押金（元）：" + MoneyUtils.fenToYuan(user.getDeposit()));
        real_balance.setText("用户当前真实余额（元）：" + MoneyUtils.fenToYuan(user.getReal_balance()));
        gift_balance.setText("用户当前赠送余额（元）：" + MoneyUtils.fenToYuan(user.getGift_balance()));
        consume_real.setText("用户消费真实余额（元）：" + MoneyUtils.fenToYuan(user.getConsume_real()));
        consume_gift.setText("用户消费赠送余额（元）：" + MoneyUtils.fenToYuan(user.getConsume_gift()));
        ride_id.setText("骑行订单：" + user.getRide_id());
        bike_id.setText("骑行车辆：" + user.getBike_id());
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(UserDetailActivity.this, message);
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
    public void onClick(View view) {
        String url = "";
        String title = "";
        try {
            switch (view.getId()) {
                case R.id.block_tv:
                    String block = "block";
                    title = "加入黑名单";
                    block(block, title);
                    break;
                case R.id.unblock_tv:
                    String unblock = "unblock";
                    title = "移出黑名单";
                    block(unblock, title);
                    break;
                case R.id.refund_balance_tv:
                    title = "退余额";
                    url = HttpUrl.REFUND_BALANCE;
                    refund(url, title);
                    break;
                case R.id.refund_deposit_tv:
                    title = "退押金";
                    url = HttpUrl.REFUND_DEPOSIT;
                    refund(url, title);
                    break;
                case R.id.add_balance_tv:
                    title = "赠送余额";
                    url = HttpUrl.ADD_BALANCE;
                    balance(url, title);
                    break;
                case R.id.sub_balance_tv:
                    title = "用户扣费";
                    url = HttpUrl.SUB_BALANCE;
                    balance(url, title);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void balance(final String url, String title) {
        if (mUserId == -1) return;
        if (title.equals("")) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailActivity.this);
        View view = View
                .inflate(this, R.layout.custom_dialog, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView dialog_title = (TextView) view
                .findViewById(R.id.title);//设置标题
        final EditText dialog_money = (EditText) view
                .findViewById(R.id.money);//输入内容
        final EditText dialog_subject = (EditText) view
                .findViewById(R.id.subject);//输入内容
        Button btn_cancel = (Button) view
                .findViewById(R.id.btn_cancel);//取消按钮
        Button btn_comfirm = (Button) view
                .findViewById(R.id.btn_comfirm);//确定按钮
        dialog_title.setText("用户ID" + mUserId + "确定" + title + "？");
        //取消或确定按钮监听事件处理
        final AlertDialog dialog = builder.create();
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
                String str_money = dialog_money.getText().toString().trim();
                String str_subject = dialog_subject.getText().toString().trim();
                doBalance(url, str_money, str_subject);
                dialog.dismiss();
            }
        });
    }


    private void doBalance(String url, String value, String subject) {
        String money = MoneyUtils.yuanToFen(value);
        NetUtil.checkNetwork(UserDetailActivity.this);
        showProgressDialog("正在执行...");
        if (url.equals("")) return;
        Map<String, Object> p = new HashMap<>();
        if (url.equals(HttpUrl.ADD_BALANCE)) {
            Map<String, Object> params = new HashMap<>();
            params.put("user_id", mUserId);
            params.put("gift_balance", Integer.parseInt(money));
            params.put("subject", subject);

            Map[] maps = {params};
            p.put("users", maps);
        } else if (url.equals(HttpUrl.SUB_BALANCE)) {
            Map<String, Object> params = new HashMap<>();
            params.put("user_id", mUserId);
            params.put("real_balance", Integer.parseInt(money));
            params.put("subject", subject);

            Map[] maps = {params};
            p.put("users", maps);
        }

        NetRequest
                .post()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(UserDetailActivity.this).getToken())
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
                                ToastUtil.showToast(UserDetailActivity.this, "操作成功");
                                getUserInfo();
                            } else {
                                LoginUtil.login(UserDetailActivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(UserDetailActivity.this, "失败");
                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(UserDetailActivity.this, "失败");
                        dissmissProgressDialog();
                    }
                });
    }

    private void refund(final String url, String title) {
        if (mUserId == -1) return;
        if (title.equals("")) return;
        final EditText editText = new EditText(UserDetailActivity.this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setHint("请输入123456");

        new AlertDialog.Builder(UserDetailActivity.this)
                .setTitle("用户ID" + mUserId + "确定" + title + "？")
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
                        if (str_id.equals("123456")) {
                            doRefund(url);
                        } else {
                            ToastUtil.showToast(UserDetailActivity.this, "输入错误");
                        }
                        dialog.dismiss();

                    }
                }).show();

    }

    private void doRefund(String url) {
        NetUtil.checkNetwork(UserDetailActivity.this);
        showProgressDialog("正在执行...");
        if (url.equals("")) return;
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", mUserId);
        NetRequest
                .post()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(UserDetailActivity.this).getToken())
                .jsonObject(params)
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
                                ToastUtil.showToast(UserDetailActivity.this, "操作成功");
                                getUserInfo();
                            } else {
                                LoginUtil.login(UserDetailActivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(UserDetailActivity.this, "失败");
                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(UserDetailActivity.this, "失败");
                        dissmissProgressDialog();
                    }
                });
    }

    private void block(final String action, String title) {
        if (action.equals("")) return;
        if (title.equals("")) return;
        final EditText editText = new EditText(UserDetailActivity.this);
        editText.setHint("请输入123456");

        new AlertDialog.Builder(UserDetailActivity.this)
                .setTitle("用户ID" + mUserId + "确定" + title + "？")
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

                        if (str_id.equals("123456")) {
                            doBlock(action);
                        } else {
                            ToastUtil.showToast(UserDetailActivity.this, "输入错误");
                        }
                        dialog.dismiss();

                    }
                }).show();

    }

    private void doBlock(String action) {
        NetUtil.checkNetwork(UserDetailActivity.this);
        showProgressDialog("正在执行...");
        String url = HttpUrl.USER_BLOCK;
        if (url.equals("")) return;
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", mUserId);
        params.put("action", action);
        Map<String, Object> p = new HashMap<>();
        Map[] maps = {params};
        p.put("users", maps);
        NetRequest
                .post()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(UserDetailActivity.this).getToken())
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
                                ToastUtil.showToast(UserDetailActivity.this, "操作成功");
                                getUserInfo();
                            } else {
                                LoginUtil.login(UserDetailActivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(UserDetailActivity.this, "失败");
                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(UserDetailActivity.this, "失败");
                        dissmissProgressDialog();
                    }
                });
    }
}
