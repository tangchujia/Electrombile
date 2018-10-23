package com.syxgo.electrombile.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.adapter.UserAdapter;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.manager.UIHelper;
import com.syxgo.electrombile.model.User;
import com.syxgo.electrombile.model.UserData;
import com.syxgo.electrombile.util.LoginUtil;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;
import com.syxgo.electrombile.view.RecyclerItemClickListener;
import com.syxgo.electrombile.view.SwipyRefreshLayout;
import com.syxgo.electrombile.view.SwipyRefreshLayoutDirection;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangchujia on 2017/10/18.
 */

public class UserManagementActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private RecyclerView mUserRv;
    private SwipyRefreshLayout mSwipeLayout;
    private List<UserData> mUsers = new ArrayList<>();
    private UserAdapter mAdapter;
    private Dialog progDialog = null;
    private int mOffset = 1;
    private int mCount = 20;
    private EditText user_id_et;
    private boolean isSearch = false;
    private LinearLayout mSearchLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initTop();
        initView();
        getUsers("");

    }

    private void initView() {
        mTitletv.setText("用户管理");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        user_id_et = (EditText) findViewById(R.id.user_id_et);
        mMenuImg.setVisibility(View.VISIBLE);
        mSearchLayout = (LinearLayout) findViewById(R.id.layout_search);

        mMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearch = !isSearch;
                if (isSearch) {
                    mSearchLayout.setVisibility(View.VISIBLE);
                } else {
                    mSearchLayout.setVisibility(View.GONE);
                }
            }
        });
        mSwipeLayout = (SwipyRefreshLayout) findViewById(R.id.swipeLayout);
        mUserRv = (RecyclerView) findViewById(R.id.user_rv);

        initSwipe();
        mUserRv.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new UserAdapter(mUsers);
        mUserRv.setAdapter(mAdapter);

        mUserRv.addOnItemTouchListener(new RecyclerItemClickListener(UserManagementActivity.this, mUserRv, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (mUsers == null) {
                    return;
                }
                UIHelper.showUserDetail(UserManagementActivity.this, mUsers.get(position).getUser_id());
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        user_id_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    mUsers = DataSupport.where(" phone like ?", "%" + charSequence + "%").find(UserData.class);
                } else {
                    mUsers = DataSupport.findAll(UserData.class);
                }
                mAdapter.setDateChanged(mUsers);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userid = user_id_et.getText().toString().trim();
                getUsers(userid);
            }
        });
    }

    private void getUsers(String phone) {
        NetUtil.checkNetwork(UserManagementActivity.this);
        String page = "?page=" + mOffset;
        String per_page = "&per_page=" + mCount;
        String url = "";
        if (phone.equals(""))
            url = HttpUrl.GET_USERS + page + per_page;
        else
            url = HttpUrl.GET_USERS + "?phone=" + phone;

        if (url.equals("")) return;
        showProgressDialog("正在查询...");
        NetRequest
                .get()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(UserManagementActivity.this).getToken())
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
                                org.json.JSONArray jsonArray = new org.json.JSONObject(result).getJSONArray("users");

                                List<User> users = JSONObject.parseArray(jsonArray.toString(), User.class);
                                if (mOffset == 1) {
                                    mUsers = new ArrayList<>();
                                    DataSupport.deleteAll(UserData.class);

                                }
                                for (User user : users) {
                                    UserData userData = new UserData();
                                    userData.setBike_id(user.getBike_id());
                                    userData.setPhone(user.getPhone());
                                    userData.setReal_name(user.getReal_name());
                                    userData.setUser_id(user.getId());
                                    mUsers.add(userData);
                                }
                                DataSupport.saveAll(mUsers);
                                if (mUsers.size() == 0) {
                                    ToastUtil.showToast(UserManagementActivity.this, "没有了");
                                }
                                String id = user_id_et.getText().toString().trim();
                                if (!TextUtils.isEmpty(id)) {
                                    List<UserData> list = DataSupport.where("user_id like ? or phone like ?", "%" + id + "%", "%" + id + "%").find(UserData.class);
                                    mAdapter.setDateChanged(list);
                                } else {
                                    mAdapter.setDateChanged(mUsers);
                                }

                            } else {
                                LoginUtil.login(UserManagementActivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(UserManagementActivity.this, e.getMessage());

                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(UserManagementActivity.this, "查询失败");
                        dissmissProgressDialog();
                    }
                });
    }

    private void initSwipe() {
        mSwipeLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeLayout.setEnabled(true);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);

    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (SwipyRefreshLayoutDirection.TOP == direction) {//下拉刷新
            refresh();
            mSwipeLayout.setRefreshing(false);
        } else if (SwipyRefreshLayoutDirection.BOTTOM == direction) {//下拉加载更多
            loadMore();

            mSwipeLayout.setRefreshing(false);
        }
    }

    private void loadMore() {
        mOffset++;
        getUsers("");
    }

    private void refresh() {
        mOffset = 1;
        getUsers("");
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(UserManagementActivity.this, message);
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
