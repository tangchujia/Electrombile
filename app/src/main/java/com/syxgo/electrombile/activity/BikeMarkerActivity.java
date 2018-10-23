package com.syxgo.electrombile.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.route.RouteSearch;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.model.Bike;
import com.syxgo.electrombile.overlay.MotorMarkOverlay;
import com.syxgo.electrombile.util.AMapUtil;
import com.syxgo.electrombile.util.DeviceUtil;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangchujia on 2017/9/19.
 */

public class BikeMarkerActivity extends BaseActivity implements AMap.CancelableCallback, AMapLocationListener {
    private MapView mapView;
    private AMap aMap;
    private MotorMarkOverlay motorMarkOverlay = null;
    private List<Bike> list;
    private boolean isFirstLoc = true;
    private LatLng mLatlng;
    private ImageView mLocationImg;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private Dialog progDialog = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_marker);
        initTop();
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        initView();
        initMap();
    }

    private void initView() {
        mLocationImg = (ImageView) findViewById(R.id.location_img);
        mLocationImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                changeCamera(
                        CameraUpdateFactory.newCameraPosition(
                                new CameraPosition(aMap.getCameraPosition().target, 19, 90, 0)), BikeMarkerActivity.this);
                return true;

            }
        });

        mLocationImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foundMyLocation();

            }
        });
        findViewById(R.id.refresh_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBike();
            }
        });
        mTitletv.setText("车辆查询");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void initMap() {
        Location location = getLocation(BikeMarkerActivity.this);
        if (location == null) {
            Toast.makeText(getApplicationContext(), "未获取到当前定位，请确保打开GPS和数据网络！", Toast.LENGTH_LONG).show();
        } else if (location.getLatitude() == 0 && location.getLongitude() == 0) {
            Toast.makeText(getApplicationContext(), "请确保打开该应用的定位权限和数据网络权限！", Toast.LENGTH_LONG).show();
        }

        if (aMap == null) {
            aMap = mapView.getMap();
        }

        UiSettings settings = aMap.getUiSettings();
        settings.setZoomControlsEnabled(false);// 是否显示放大缩小按钮
        settings.setMyLocationButtonEnabled(false);// 是否显示定位按钮
        settings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
        aMap.setMaxZoomLevel(19);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        //设置定位样式
        setLocationStyle();
        //自定义地图样式
        setBlueMap(BikeMarkerActivity.this, "mystyle_sdk_1501143118_0100.data");
        /**
         * 地图加载完毕监听
         */
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                initLocation();
            }
        });
    }

    private void setLocationStyle() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.location_marker));
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
        myLocationStyle.strokeColor(Color.TRANSPARENT);
        myLocationStyle.strokeWidth(0f);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
        aMap.setMyLocationStyle(myLocationStyle);// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
    }

    private void setBlueMap(Context context, String fileName) {
        File cacheFile = new File(context.getCacheDir(), fileName);
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        aMap.setCustomMapStylePath(cacheFile.getAbsolutePath());
        aMap.setMapCustomEnable(true);
    }

    private Location getLocation(Context context) {
        //You do not instantiate this class directly;
        //instead, retrieve it through:
        //Context.getSystemService(Context.LOCATION_SERVICE).LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        //检查权限设置
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getApplicationContext(), "权限设置->打开定位权限和数据网络权限", Toast.LENGTH_LONG).show();
            return null;
        }
        //获取GPS支持
        Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        if (location == null) {
            //获取NETWORK支持
            location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        }
        return location;
    }

    private void getBike() {
        NetUtil.checkNetwork(BikeMarkerActivity.this);
        String url = getIntent().getStringExtra("url");

        if (url == null || url.equals("")) {
            ToastUtil.showToast(BikeMarkerActivity.this, "刷新失败");
            return;
        }
        showProgressDialog("正在刷新...");
        NetRequest
                .get()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(BikeMarkerActivity.this).getToken())
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
                                list = JSONObject.parseArray(jsonArray.toString(), Bike.class);
                                if (list.size() == 0) {
                                    ToastUtil.showToast(BikeMarkerActivity.this, "刷新失败");
//                                    finish();
                                }
                                setMotorMarkers(list);

                            } else {
                                ToastUtil.showToast(BikeMarkerActivity.this, "刷新失败");
//                                finish();
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(BikeMarkerActivity.this, e.getMessage());

                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(BikeMarkerActivity.this, "刷新失败");
                        dissmissProgressDialog();
                    }
                });
    }

    /**
     * 获取当前位置
     */
    private void foundMyLocation() {
        try {
            if (aMap != null) {
                Location location = aMap.getMyLocation();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (latLng != null) {
                    //将地图移动到定位点
                    changeCamera(
                            CameraUpdateFactory.newCameraPosition(
                                    new CameraPosition(latLng, 17, 0, 0)), BikeMarkerActivity.this); //经纬度，缩放比例， 水平面夹角（3D效果），方向夹角（旋转）
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置自定义定位蓝点
     */
    private void setupLocationStyle() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 设置定位模式
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
    }

    void initData() {
        list = new ArrayList<>();
        try {
            list = (List<Bike>) getIntent().getSerializableExtra("list");
            setMotorMarkers(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置车辆marker图层
     */
    private void setMotorMarkers(List<Bike> markBikes) {
        if (motorMarkOverlay != null) {
            motorMarkOverlay.removeFromMap();
        }
        if (aMap != null) {
            motorMarkOverlay = new MotorMarkOverlay(BikeMarkerActivity.this, aMap, markBikes);
            motorMarkOverlay.addToMap();

//            if (markBikes.size() == 1) {
//                CoordinateConverter converter = new CoordinateConverter(BikeMarkerActivity.this);
//                // CoordType.GPS 待转换坐标类型
//                converter.from(CoordinateConverter.CoordType.GPS);
//                converter.coord(new LatLng(markBikes.get(0).getLat(), markBikes.get(0).getLng()));
//                LatLng desLatLng = converter.convert();//坐标转换
////                LatLng latlng = AMapUtil.GpsConvertToGD(BikeMarkerActivity.this, markBikes.get(0).getLat(), markBikes.get(0).getLng());
//                changeCamera(
//                        CameraUpdateFactory.newCameraPosition(
//                                new CameraPosition(desLatLng, 15, 0, 0)), BikeMarkerActivity.this);
//            } else {
//                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
//                for (Bike bike :
//                        markBikes) {
//                    boundsBuilder.include(new LatLng(bike.getLat(), bike.getLng()));//把所有点都include进去（LatLng类型）
//                }
//                aMap.animateCamera(CameraUpdateFactory
//                        .newLatLngBoundsRect(boundsBuilder.build(),
//                                DeviceUtil.dp2px(BikeMarkerActivity.this, 150),
//                                DeviceUtil.dp2px(BikeMarkerActivity.this, 150),
//                                DeviceUtil.dp2px(BikeMarkerActivity.this, 150),
//                                DeviceUtil.dp2px(BikeMarkerActivity.this, 150)));
//            }
            List<LatLng> list = new ArrayList<>();
            for (Bike bike :
                    markBikes) {
                list.add(AMapUtil.GpsConvertToGD(BikeMarkerActivity.this, new LatLng(bike.getLat(), bike.getLng())));
            }
            zoomToSpan(list);
        }

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
        if (mLocationClient != null) {
            mLocationClient.stopLocation();//停止定位
            mLocationClient.onDestroy();//销毁定位客户端。
        }
    }


    /**
     * Camera移动动画
     * 调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update, AMap.CancelableCallback callback) {
        aMap.animateCamera(update, 600, callback);
    }


    @Override
    public void onFinish() {

    }

    @Override
    public void onCancel() {

    }

    /**
     * 初始化定位坐标
     */
    public void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听，这里要实现AMapLocationListener接口，AMapLocationListener接口只有onLocationChanged方法可以实现，用于接收异步返回的定位结果，参数是AMapLocation类型。
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新initLoca
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(true);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                mLatlng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                if (isFirstLoc) {
                    //将地图移动到定位点
//                    LatLng latlng = AMapUtil.GpsConvertToGD(TaskDetailActivity.this, mBikes.get(0).getLat(), mBikes.get(0).getLng());
//                    changeCamera(
//                            CameraUpdateFactory.newCameraPosition(
//                                    new CameraPosition(mLatlng, 17, 0, 0)), BikeMarkerActivity.this);
                    initData();

                    isFirstLoc = false;

                } else {
//                    handler.sendEmptyMessage(20019);
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                ToastUtil.showToast(BikeMarkerActivity.this, "AmapError" + " location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    /**
     * 缩放移动地图，保证所有自定义marker在可视范围中。
     */
    public void zoomToSpan(List<LatLng> pointList) {
        if (pointList != null && pointList.size() > 0) {
            if (aMap == null)
                return;
//            centerMarker.setVisible(false);
            LatLngBounds bounds = getLatLngBounds(pointList);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));


        }
    }

    /**
     * 根据自定义内容获取缩放bounds
     */
    private LatLngBounds getLatLngBounds(List<LatLng> pointList) {
        LatLngBounds.Builder b = LatLngBounds.builder();
        for (int i = 0; i < pointList.size(); i++) {
            LatLng p = pointList.get(i);
            b.include(p);
        }
        return b.build();
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(BikeMarkerActivity.this, message);
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
