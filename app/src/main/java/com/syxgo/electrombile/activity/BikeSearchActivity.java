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
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.adapter.BikeAdapter;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.manager.UIHelper;
import com.syxgo.electrombile.model.Bike;
import com.syxgo.electrombile.model.BikeData;
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

public class BikeSearchActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener, View.OnClickListener {
    private RecyclerView mBikeRv;
    private SwipyRefreshLayout mSwipeLayout;
    private List<BikeData> mBikes = new ArrayList<>();
    private BikeAdapter mAdapter;
    private Dialog progDialog = null;
    private int mOffset = 1;
    private int mCount = 20;
    private TextView type_0;
    private TextView type_1;
    private TextView type_2;
    private TextView type_4;
    private TextView type_5;
    private TextView type_6;
    private int mBikeType = 0;
    private EditText user_id_et;
    private LinearLayout mSearchLayout;
    private boolean isSearch = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_search);
        initTop();
        initView();
        getBikes("");
    }

    private void initView() {
        mTitletv.setText("车辆管理");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        user_id_et = (EditText) findViewById(R.id.user_id_et);
        mSearchLayout= (LinearLayout) findViewById(R.id.layout_search);
        mMenuImg.setVisibility(View.VISIBLE);
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
        type_0 = (TextView) findViewById(R.id.type_0);
        type_1 = (TextView) findViewById(R.id.type_1);
        type_2 = (TextView) findViewById(R.id.type_2);
        type_4 = (TextView) findViewById(R.id.type_4);
        type_5 = (TextView) findViewById(R.id.type_5);
        type_6 = (TextView) findViewById(R.id.type_6);

        type_0.setOnClickListener(this);
        type_1.setOnClickListener(this);
        type_2.setOnClickListener(this);
        type_4.setOnClickListener(this);
        type_5.setOnClickListener(this);
        type_6.setOnClickListener(this);

        findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bikeid = user_id_et.getText().toString().trim();
                getBikes(bikeid);
            }
        });

        mSwipeLayout = (SwipyRefreshLayout) findViewById(R.id.swipeLayout);
        mBikeRv = (RecyclerView) findViewById(R.id.order_rv);

        initSwipe();
        mBikeRv.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new BikeAdapter(mBikes);
        mBikeRv.setAdapter(mAdapter);

        mBikeRv.addOnItemTouchListener(new RecyclerItemClickListener(BikeSearchActivity.this, mBikeRv, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (mBikes == null) {
                    return;
                }
                UIHelper.showBikeDetail(BikeSearchActivity.this, mBikes.get(position).getBike_id());
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
                    mBikes = DataSupport.where("bike_id like ?", "%" + charSequence + "%").find(BikeData.class);
                } else {
                    mBikes = DataSupport.findAll(BikeData.class);
                }
                mAdapter.setDateChanged(mBikes);

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void getBikes(String bikeid) {
        NetUtil.checkNetwork(BikeSearchActivity.this);
        String status = "";
        if (mBikeType != 0) {
            status = "&status=" + mBikeType;

        }
        String page = "?page=" + mOffset;
        String per_page = "&per_page=" + mCount;
        String url = "";
        if (bikeid.equals(""))
            url = HttpUrl.GET_BIKES + page + per_page + status;
        else
            url = HttpUrl.GET_BIKES + "?bike_id=" + bikeid;
        if (url.equals("")) return;

        showProgressDialog("正在查询...");
        NetRequest
                .get()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(BikeSearchActivity.this).getToken())
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

                                List<Bike> bikes = JSONObject.parseArray(jsonArray.toString(), Bike.class);
                                if (mOffset == 1) {
                                    mBikes = new ArrayList<>();
                                    DataSupport.deleteAll(BikeData.class);

                                }
                                for (Bike bike : bikes) {
                                    BikeData bikeData = new BikeData();
                                    bikeData.setBike_id(bike.getId());
                                    bikeData.setBattery_level(bike.getBattery_level());
                                    bikeData.setIs_lock(bike.is_lock());
                                    bikeData.setIs_offline(bike.is_offline());
                                    bikeData.setIs_rent(bike.is_rent());
                                    mBikes.add(bikeData);
                                }
                                DataSupport.saveAll(mBikes);
                                if (bikes.size() == 0) {
                                    ToastUtil.showToast(BikeSearchActivity.this, "没有了");
                                }
                                String id = user_id_et.getText().toString().trim();
                                if (!TextUtils.isEmpty(id)) {

                                    List<BikeData> list = DataSupport.where("bike_id like ?", "%" + id + "%").find(BikeData.class);
                                    mAdapter.setDateChanged(list);
                                } else {
                                    mAdapter.setDateChanged(mBikes);

                                }

                            } else {
                                LoginUtil.login(BikeSearchActivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(BikeSearchActivity.this, e.getMessage());

                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(BikeSearchActivity.this, "查询失败");
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.type_0:
                mBikeType = 0;
                type_0.setBackgroundResource(R.drawable.shape_type_orange);
                type_1.setBackgroundResource(R.drawable.shape_type_grey);
                type_2.setBackgroundResource(R.drawable.shape_type_grey);
                type_4.setBackgroundResource(R.drawable.shape_type_grey);
                type_5.setBackgroundResource(R.drawable.shape_type_grey);
                type_6.setBackgroundResource(R.drawable.shape_type_grey);
                break;
            case R.id.type_1:
                mBikeType = 1;
                type_1.setBackgroundResource(R.drawable.shape_type_orange);
                type_0.setBackgroundResource(R.drawable.shape_type_grey);
                type_2.setBackgroundResource(R.drawable.shape_type_grey);
                type_4.setBackgroundResource(R.drawable.shape_type_grey);
                type_5.setBackgroundResource(R.drawable.shape_type_grey);
                type_6.setBackgroundResource(R.drawable.shape_type_grey);
                break;
            case R.id.type_2:
                mBikeType = 2;
                type_0.setBackgroundResource(R.drawable.shape_type_grey);
                type_1.setBackgroundResource(R.drawable.shape_type_grey);
                type_2.setBackgroundResource(R.drawable.shape_type_orange);
                type_4.setBackgroundResource(R.drawable.shape_type_grey);
                type_5.setBackgroundResource(R.drawable.shape_type_grey);
                type_6.setBackgroundResource(R.drawable.shape_type_grey);

                break;
            case R.id.type_4:
                mBikeType = 4;
                type_4.setBackgroundResource(R.drawable.shape_type_orange);
                type_0.setBackgroundResource(R.drawable.shape_type_grey);
                type_2.setBackgroundResource(R.drawable.shape_type_grey);
                type_1.setBackgroundResource(R.drawable.shape_type_grey);
                type_5.setBackgroundResource(R.drawable.shape_type_grey);
                type_6.setBackgroundResource(R.drawable.shape_type_grey);
                break;
            case R.id.type_5:
                mBikeType = 5;
                type_5.setBackgroundResource(R.drawable.shape_type_orange);
                type_0.setBackgroundResource(R.drawable.shape_type_grey);
                type_2.setBackgroundResource(R.drawable.shape_type_grey);
                type_4.setBackgroundResource(R.drawable.shape_type_grey);
                type_1.setBackgroundResource(R.drawable.shape_type_grey);
                type_6.setBackgroundResource(R.drawable.shape_type_grey);
                break;
            case R.id.type_6:
                mBikeType = 6;
                type_6.setBackgroundResource(R.drawable.shape_type_orange);
                type_0.setBackgroundResource(R.drawable.shape_type_grey);
                type_2.setBackgroundResource(R.drawable.shape_type_grey);
                type_4.setBackgroundResource(R.drawable.shape_type_grey);
                type_5.setBackgroundResource(R.drawable.shape_type_grey);
                type_1.setBackgroundResource(R.drawable.shape_type_grey);
                break;
        }
        user_id_et.setText("");
        mSearchLayout.setVisibility(View.GONE);
        getBikes("");
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
        getBikes("");
    }

    private void refresh() {
        mOffset = 1;
        getBikes("");
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(BikeSearchActivity.this, message);
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
