package com.syxgo.electrombile.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.adapter.ExpandableListViewaAdapter;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.manager.UIHelper;
import com.syxgo.electrombile.model.Station;
import com.syxgo.electrombile.model.StationGroup;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;
import com.syxgo.electrombile.view.SwipyRefreshLayout;
import com.syxgo.electrombile.view.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangchujia on 2017/9/13.
 */

public class StationListActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener, GeocodeSearch.OnGeocodeSearchListener {
    private ExpandableListView mStationEv;
    private SwipyRefreshLayout mSwipeLayout;
    private ExpandableListViewaAdapter expandableListViewaAdapter;
    private List<StationGroup> mStationGroups = new ArrayList<>();
    private Dialog progDialog = null;
    private GeocodeSearch geocoderSearch = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);
        initTop();
        initView();
        initData();
    }

    private void initView() {
        mTitletv.setText("停车点列表");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mSwipeLayout = (SwipyRefreshLayout) findViewById(R.id.swipeLayout);
        mStationEv = (ExpandableListView) findViewById(R.id.evdevice);
        expandableListViewaAdapter = new ExpandableListViewaAdapter(this);
        mStationEv.setAdapter(expandableListViewaAdapter);

        mStationEv.setGroupIndicator(null);
        mStationEv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (mStationGroups == null) {
                    return false;
                }
                Station station = mStationGroups.get(groupPosition).getStations().get(childPosition);
                Intent intent = new Intent(StationListActivity.this, StationSearchActivity.class);
                intent.putExtra("station", station);
                intent.putExtra("from", "stationlist");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return false;
            }
        });

        initSwipe();
    }

    private void initData() {

        getStations();

    }

    private void getStations() {
        LatLng latLng = getIntent().getParcelableExtra("location");
        LatLonPoint mEndPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        getAddress(mEndPoint);

    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        //逆地理编码（坐标转地址）
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 100,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
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

        } else if (SwipyRefreshLayoutDirection.BOTTOM == direction) {//上拉加载更多
            load();
            mSwipeLayout.setRefreshing(false);
        }
    }

    private void load() {
    }

    private void refresh() {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String addressName = result.getRegeocodeAddress().getFormatAddress();
                String json = getIntent().getStringExtra("stations");
                StationGroup group = new StationGroup();
                group.setName(addressName);
                List<Station> stations = JSONObject.parseArray(json, Station.class);
                if (stations.size() == 0) {
                    ToastUtil.showToast(StationListActivity.this, "附近没有停车点");
                }
                group.setStations(stations);
                mStationGroups.add(group);
                expandableListViewaAdapter.setDataChanged(mStationGroups);
                mStationEv.expandGroup(0);
//                }
            } else {
                ToastUtil.showToast(StationListActivity.this, "未知位置");
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
