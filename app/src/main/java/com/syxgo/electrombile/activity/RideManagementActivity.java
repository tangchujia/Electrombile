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
import com.syxgo.electrombile.adapter.RideAdapter;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.manager.UIHelper;
import com.syxgo.electrombile.model.Ride;
import com.syxgo.electrombile.model.RideData;
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

public class RideManagementActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private RecyclerView mRideRv;
    private SwipyRefreshLayout mSwipeLayout;
    private List<RideData> mRides = new ArrayList<>();
    private RideAdapter mAdapter;
    private Dialog progDialog = null;
    private int mOffset = 1;
    private int mCount = 20;
    private EditText ride_et;
    private boolean isSearch = false;
    private LinearLayout mSearchLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);
        initTop();
        initView();
        getRides("");
    }

    private void initView() {
        mTitletv.setText("行程管理");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ride_et = (EditText) findViewById(R.id.ride_et);
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
        mRideRv = (RecyclerView) findViewById(R.id.ride_rv);

        initSwipe();
        mRideRv.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new RideAdapter(mRides);
        mRideRv.setAdapter(mAdapter);

        mRideRv.addOnItemTouchListener(new RecyclerItemClickListener(RideManagementActivity.this, mRideRv, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (mRides == null) {
                    return;
                }
                UIHelper.showRideDetail(RideManagementActivity.this, mRides.get(position).getRideId());
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        ride_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    mRides = DataSupport.where("user_id like ? or bike_id like ? or rideId like ?", "%" + charSequence + "%", "%" + charSequence + "%", "%" + charSequence + "%").find(RideData.class);
                } else {
                    mRides = DataSupport.findAll(RideData.class);
                }
                mAdapter.setDateChanged(mRides);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rideid = ride_et.getText().toString().trim();
                getRides(rideid);
            }
        });
    }

    private void getRides(String rideid) {
        NetUtil.checkNetwork(RideManagementActivity.this);
        String page = "?page=" + mOffset;
        String per_page = "&per_page=" + mCount;
        String url = "";

        if (rideid.equals("")) {
            url = HttpUrl.GET_RIDES + page + per_page;

        } else {
            url = HttpUrl.GET_RIDES + "?ride_id=" + rideid;

        }
        if (url.equals("")) return;

        showProgressDialog("正在查询...");
        NetRequest
                .get()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(RideManagementActivity.this).getToken())
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
                                org.json.JSONArray jsonArray = new org.json.JSONObject(result).getJSONArray("rides");

                                List<Ride> rides = JSONObject.parseArray(jsonArray.toString(), Ride.class);
                                if (mOffset == 1) {
                                    mRides = new ArrayList<>();
                                    DataSupport.deleteAll(RideData.class);

                                }
                                for (Ride ride : rides) {
                                    RideData rideData = new RideData();
                                    rideData.setBike_id(ride.getBike_id());
                                    rideData.setUser_id(ride.getUser_id());
                                    rideData.setRideId(ride.getId());
                                    rideData.setCreatedTime(ride.getCreated());
                                    rideData.setStatus(ride.getStatus());
                                    mRides.add(rideData);
                                }
                                DataSupport.saveAll(mRides);
                                if (mRides.size() == 0) {
                                    ToastUtil.showToast(RideManagementActivity.this, "没有了");
                                }
                                String id = ride_et.getText().toString().trim();
                                if (!TextUtils.isEmpty(id)) {
                                    List<RideData> list = DataSupport.where("user_id like ? or bike_id like ? or rideId like ?", "%" + id + "%", "%" + id + "%", "%" + id + "%").find(RideData.class);
                                    mAdapter.setDateChanged(list);
                                } else {
                                    mAdapter.setDateChanged(mRides);
                                }

                            } else {
                                LoginUtil.login(RideManagementActivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(RideManagementActivity.this, e.getMessage());

                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(RideManagementActivity.this, "查询失败");
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
        getRides("");
    }

    private void refresh() {
        mOffset = 1;
        getRides("");
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(RideManagementActivity.this, message);
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
