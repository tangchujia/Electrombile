package com.syxgo.electrombile.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.manager.Common;
import com.syxgo.electrombile.model.Bike;
import com.syxgo.electrombile.model.Lnglat;
import com.syxgo.electrombile.model.OrderData;
import com.syxgo.electrombile.model.Station;
import com.syxgo.electrombile.util.AMapUtil;
import com.syxgo.electrombile.util.DeviceUtil;
import com.syxgo.electrombile.util.LoginUtil;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangchujia on 2017/10/20.
 */

public class BikeLocationActivity extends BaseActivity implements AMap.CancelableCallback {
    private MapView mMapView = null;
    private AMap mAMap;
    private Dialog progDialog = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_location);
        initTop();
        initView();
        mMapView.onCreate(savedInstanceState);
        initMap();
        getBikeInfo();
    }

    private void initMap() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
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
                getBikeInfo();
            }
        });

    }

    private void getStationNearBy(LatLng latLng) {
        NetUtil.checkNetwork(BikeLocationActivity.this);

        String url = HttpUrl.STATION_NEARBY + "?lng=" + latLng.longitude + "&lat=" + latLng.latitude + "&distance=" + 3000;
        showProgressDialog("正在查询...");
        NetRequest
                .get()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(BikeLocationActivity.this).getToken())
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
                                org.json.JSONArray jsonArray = new org.json.JSONObject(result).getJSONArray("stations");
                                List<Station> stations = JSONObject.parseArray(jsonArray.toString(), Station.class);
                                showStations(stations);


                            } else {
                                LoginUtil.login(BikeLocationActivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(BikeLocationActivity.this, e.getMessage());

                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(BikeLocationActivity.this, "查询失败");
                        dissmissProgressDialog();
                    }
                });
    }

    private void showStations(List<Station> stations) {
        List<Marker> markers = new ArrayList<>();
        List<Polygon> mPolygon = new ArrayList<>();
        List<Circle> mCircles = new ArrayList<>();

        if (stations.size() == 0) {
            ToastUtil.showToast(BikeLocationActivity.this, "附近3km内无停车点");
            return;
        }
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
        for (Station station : stations) {
            LatLng latLng = AMapUtil.GpsConvertToGD(BikeLocationActivity.this, new LatLng(station.getLat(), station.getLng()));

            boundsBuilder.include(latLng);//把所有点都include进去（LatLng类型）

            Marker marker = mAMap.addMarker(new MarkerOptions().position(latLng));
            marker.setObject(station);
            markers.add(marker);
            if (station.getStype() == 2) {
                //多边形
                PolygonOptions polygonOptions = new PolygonOptions();
                // 添加 多边形的每个顶点（顺序添加）
                List<Lnglat> lnglat = JSONObject.parseArray(station.getLnglats(), Lnglat.class);
                if (lnglat != null && lnglat.size() > 0) {
                    LatLng[] lats = new LatLng[lnglat.size()];
                    for (int i = 0; i < lnglat.size(); i++) {
                        lats[i] = AMapUtil.GpsConvertToGD(BikeLocationActivity.this, new LatLng(lnglat.get(i).getLat(), lnglat.get(i).getLng()));
                    }
                    polygonOptions.add(lats);
                    polygonOptions.strokeWidth(15) // 多边形的边框
                            .strokeColor(getResources().getColor(R.color.color_Orange_station)) // 边框颜色
                            .fillColor(getResources().getColor(R.color.color_Orange_station));   // 多边形的填充色

                    Polygon polygon = mAMap.addPolygon(polygonOptions);
                    mPolygon.add(polygon);
                }

            } else if (station.getStype() == 3) {
                //圆形
                Circle circle = mAMap.addCircle(new CircleOptions().
                        center(latLng).
                        radius(station.getRadius())
                        .strokeColor(getResources().getColor(R.color.color_Orange_station)) // 边框颜色
                        .fillColor(getResources().getColor(R.color.color_Orange_station))  // 多边形的填充色
                        .strokeWidth(15));
                mCircles.add(circle);
            }
        }
//        mAMap.animateCamera(CameraUpdateFactory
//                .newLatLngBoundsRect(boundsBuilder.build(),
//                        DeviceUtil.dp2px(BikeLocationActivity.this, 50),
//                        DeviceUtil.dp2px(BikeLocationActivity.this, 50),
//                        DeviceUtil.dp2px(BikeLocationActivity.this, 50),
//                        DeviceUtil.dp2px(BikeLocationActivity.this, 50)));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    private void getBikeInfo() {
        int bikeId = getIntent().getIntExtra(Common.BIKE_ID, -1);
        if (bikeId == -1) {
            return;
        }
        NetUtil.checkNetwork(BikeLocationActivity.this);

        String url = HttpUrl.GET_BIKES + "?bike_id=" + bikeId;
        showProgressDialog("正在查询...");
        NetRequest
                .get()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(BikeLocationActivity.this).getToken())
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
                                org.json.JSONArray jsonArray = new org.json.JSONObject(result).getJSONArray("bikes");

                                List<Bike> bikes = JSONObject.parseArray(jsonArray.toString(), Bike.class);
                                LatLng latlng = AMapUtil.GpsConvertToGD(BikeLocationActivity.this, bikes.get(0).getLat(), bikes.get(0).getLng());

                                changeCamera(
                                        CameraUpdateFactory.newCameraPosition(
                                                new CameraPosition(latlng, 17, 0, 0)), BikeLocationActivity.this);
                                if (bikes.size() == 0) {
                                    ToastUtil.showToast(BikeLocationActivity.this, "没有了");
                                } else {
                                    showLocation(bikes.get(0));

                                }


                            } else {
                                LoginUtil.login(BikeLocationActivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(BikeLocationActivity.this, e.getMessage());

                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(BikeLocationActivity.this, "查询失败");
                        dissmissProgressDialog();
                    }
                });
    }

    private void showLocation(Bike bike) {
        LatLng latLng = AMapUtil.GpsConvertToGD(BikeLocationActivity.this, new LatLng(bike.getLat(), bike.getLng()));
        MarkerOptions bikeOption = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.search_center_ic))
                .visible(true);
        Marker marker = mAMap.addMarker(bikeOption.position(latLng));
        getStationNearBy(latLng);

    }

    private void initView() {
        mTitletv.setText("车辆位置");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mMapView = (MapView) findViewById(R.id.bike_map);

    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(BikeLocationActivity.this, message);
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
    /**
     * Camera移动动画
     * 调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update, AMap.CancelableCallback callback) {
        mAMap.animateCamera(update, 600, callback);
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onCancel() {

    }
}
