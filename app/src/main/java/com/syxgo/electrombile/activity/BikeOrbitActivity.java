package com.syxgo.electrombile.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.manager.Common;
import com.syxgo.electrombile.model.Location;
import com.syxgo.electrombile.util.AMapUtil;
import com.syxgo.electrombile.util.LoginUtil;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangchujia on 2017/10/23.
 */

public class BikeOrbitActivity extends BaseActivity {
    private Dialog progDialog = null;
    private AMap mAMap;
    private MapView mapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_orbit);
        mapView = (MapView) findViewById(R.id.bike_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initTop();
        initView();
        initMap();
        getRideInfo();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private void initView() {
        mTitletv.setText("车辆轨迹");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initMap() {
        if (mAMap == null) {
            mAMap = mapView.getMap();
        }

        UiSettings settings = mAMap.getUiSettings();
        settings.setZoomControlsEnabled(false);// 是否显示放大缩小按钮
        settings.setMyLocationButtonEnabled(false);// 是否显示定位按钮
        settings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
        mAMap.setMaxZoomLevel(19);
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(17));

        findViewById(R.id.refresh_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRideInfo();
            }
        });

    }

    private void getRideInfo() {
        int bikeId = getIntent().getIntExtra(Common.BIKE_ID, -1);

        if (bikeId == -1) {
            return;
        }
        NetUtil.checkNetwork(BikeOrbitActivity.this);

        String url = HttpUrl.GET_ORBIT + "?bike_id=" + bikeId;
        showProgressDialog("正在查询...");
        NetRequest
                .get()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(BikeOrbitActivity.this).getToken())
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
                                org.json.JSONArray jsonArray = new org.json.JSONObject(result).getJSONArray("locations");
                                List<Location> locations = JSONObject.parseArray(jsonArray.toString(), Location.class);
                                getOrbit(locations);

                            } else {
                                LoginUtil.login(BikeOrbitActivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(BikeOrbitActivity.this, e.getMessage());
                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(BikeOrbitActivity.this, "查询失败");
                        dissmissProgressDialog();
                    }
                });
    }

    private void getOrbit(List<Location> locations) {

        LatLng[] lats = new LatLng[locations.size()];
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
        for (int i = 0; i < locations.size(); i++) {
            lats[i] = AMapUtil.GpsConvertToGD(BikeOrbitActivity.this, new LatLng(locations.get(i).getLat(), locations.get(i).getLng()));
            boundsBuilder.include(new LatLng(locations.get(i).getLat(), locations.get(i).getLng()));//把所有点都include进去（LatLng类型）
        }
        //用一个数组来存放纹理
        List<BitmapDescriptor> texTuresList = new ArrayList<BitmapDescriptor>();
        texTuresList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_alr_night));

        //指定某一段用某个纹理，对应texTuresList的index即可, 四个点对应三段颜色
        List<Integer> texIndexList = new ArrayList<Integer>();
        texIndexList.add(1);

        PolylineOptions options = new PolylineOptions();
        options.width(20);//设置宽度

        //加入点
        for (int start = 0, end = lats.length - 1; start < end; start++, end--) {
            LatLng temp = lats[end];
            lats[end] = lats[start];
            lats[start] = temp;
        }
        options.add(lats);

        //加入对应的颜色,使用setCustomTextureList 即表示使用多纹理；
        options.setCustomTextureList(texTuresList);

        //设置纹理对应的Index
        options.setCustomTextureIndex(texIndexList);

        mAMap.addPolyline(options);

        for (int i = 0; i < lats.length; i++) {
            boundsBuilder.include(AMapUtil.GpsConvertToGD(BikeOrbitActivity.this, new LatLng(lats[i].latitude, lats[i].longitude)));//把所有点都include进去（LatLng类型）
        }
        mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 50));
        LatLng from_latlng = new LatLng(lats[lats.length - 1].latitude, lats[lats.length - 1].longitude);
        MarkerOptions fromOption = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_from))
                .visible(true);
        mAMap.addMarker(fromOption.position(from_latlng));

        LatLng to_latlng = new LatLng(lats[0].latitude, lats[0].longitude);
        MarkerOptions toOption = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_to))
                .visible(true);
        mAMap.addMarker(toOption.position(to_latlng));

    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(BikeOrbitActivity.this, message);
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
