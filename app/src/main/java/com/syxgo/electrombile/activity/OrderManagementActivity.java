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
import com.syxgo.electrombile.adapter.OrderAdapter;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.manager.UIHelper;
import com.syxgo.electrombile.model.Order;
import com.syxgo.electrombile.model.OrderData;
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

public class OrderManagementActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener, View.OnClickListener {
    private RecyclerView mOrderRv;
    private SwipyRefreshLayout mSwipeLayout;
    private List<OrderData> mOrders = new ArrayList<>();
    private OrderAdapter mAdapter;
    private Dialog progDialog = null;
    private int mOffset = 1;
    private int mCount = 20;
    private TextView type_1;
    private TextView type_2;
    private TextView type_3;
    private int mOrderType = 1;
    private EditText user_id_et;
    private boolean isSearch = false;
    private LinearLayout mSearchLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initTop();
        initView();
        getOrders("");
    }

    private void initView() {
        mTitletv.setText("订单管理");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        user_id_et = (EditText) findViewById(R.id.user_id_et);
        mSearchLayout = (LinearLayout) findViewById(R.id.layout_search);

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
        type_1 = (TextView) findViewById(R.id.type_1);
        type_2 = (TextView) findViewById(R.id.type_2);
        type_3 = (TextView) findViewById(R.id.type_3);

        type_1.setOnClickListener(this);
        type_2.setOnClickListener(this);
        type_3.setOnClickListener(this);

        mSwipeLayout = (SwipyRefreshLayout) findViewById(R.id.swipeLayout);
        mOrderRv = (RecyclerView) findViewById(R.id.order_rv);

        initSwipe();
        mOrderRv.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new OrderAdapter(this, mOrders);
        mOrderRv.setAdapter(mAdapter);

        mOrderRv.addOnItemTouchListener(new RecyclerItemClickListener(OrderManagementActivity.this, mOrderRv, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (mOrders == null) {
                    return;
                }
                UIHelper.showOrderDetail(OrderManagementActivity.this, mOrders.get(position).getOrder_no());
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
                    mOrders = DataSupport.where("user_id like ?", "%" + charSequence + "%").find(OrderData.class);
                } else {
                    mOrders = DataSupport.findAll(OrderData.class);
                }
                mAdapter.setDateChanged(mOrders);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userid = user_id_et.getText().toString().trim();
                getOrders(userid);
            }
        });
    }

    private void getOrders(String userid) {
        NetUtil.checkNetwork(OrderManagementActivity.this);
        String str_orderType = getType(mOrderType);
        String page = "&page=" + mOffset;
        String per_page = "&per_page=" + mCount;
        String url = "";
        if (userid.equals(""))
            url = HttpUrl.GET_ORDERS + str_orderType + page + per_page;
        else
            url = HttpUrl.GET_ORDERS + "?user_id=" + userid;
        if (url.equals("")) return;

        showProgressDialog("正在查询...");
        NetRequest
                .get()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(OrderManagementActivity.this).getToken())
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
                                org.json.JSONArray jsonArray = new org.json.JSONObject(result).getJSONArray("orders");

                                List<Order> orders = JSONObject.parseArray(jsonArray.toString(), Order.class);
                                if (mOffset == 1) {
                                    mOrders = new ArrayList<>();
                                    DataSupport.deleteAll(OrderData.class);

                                }
                                for (Order order : orders) {
                                    OrderData orderData = new OrderData();
                                    orderData.setCreatedTime(order.getCreated());
                                    orderData.setOrder_no(order.getOrder_no());
                                    orderData.setUser_id(order.getUser_id());
                                    mOrders.add(orderData);
                                }
                                DataSupport.saveAll(mOrders);
                                if (mOrders.size() == 0) {
                                    ToastUtil.showToast(OrderManagementActivity.this, "没有了");
                                }
                                String id = user_id_et.getText().toString().trim();
                                if (!TextUtils.isEmpty(id)) {
                                    List<OrderData> list = DataSupport.where("user_id like ?", "%" + id + "%").find(OrderData.class);
                                    mAdapter.setDateChanged(list);
                                } else {
                                    mAdapter.setDateChanged(mOrders);

                                }

                            } else {
                                LoginUtil.login(OrderManagementActivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(OrderManagementActivity.this, e.getMessage());

                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(OrderManagementActivity.this, "查询失败");
                        dissmissProgressDialog();
                    }
                });
    }

    private String getType(int orderType) {
        String result = "?order_type=" + orderType;
        return result;
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
            case R.id.type_1:
                mOrderType = 1;
                type_1.setBackgroundResource(R.drawable.shape_type_orange);
                type_2.setBackgroundResource(R.drawable.shape_type_grey);
                type_3.setBackgroundResource(R.drawable.shape_type_grey);
                getOrders("");
                break;
            case R.id.type_2:
                mOrderType = 2;
                type_1.setBackgroundResource(R.drawable.shape_type_grey);
                type_2.setBackgroundResource(R.drawable.shape_type_orange);
                type_3.setBackgroundResource(R.drawable.shape_type_grey);
                getOrders("");

                break;
            case R.id.type_3:
                mOrderType = 3;
                type_1.setBackgroundResource(R.drawable.shape_type_grey);
                type_2.setBackgroundResource(R.drawable.shape_type_grey);
                type_3.setBackgroundResource(R.drawable.shape_type_orange);
                getOrders("");

                break;
        }
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
        getOrders("");
    }

    private void refresh() {
        mOffset = 1;
        getOrders("");
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(OrderManagementActivity.this, message);
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
