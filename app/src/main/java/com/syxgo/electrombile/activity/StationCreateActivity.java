package com.syxgo.electrombile.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.RouteSearch;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.application.MyApplication;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.manager.UIHelper;
import com.syxgo.electrombile.model.Lnglat;
import com.syxgo.electrombile.model.Station;
import com.syxgo.electrombile.util.AMapUtil;
import com.syxgo.electrombile.util.DensityUtil;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.StringUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tangchujia on 2017/7/24.
 */

public class StationCreateActivity extends BaseActivity implements View.OnClickListener, LocationSource, AMap.OnMarkerClickListener, AMapLocationListener, AMap.CancelableCallback, GeocodeSearch.OnGeocodeSearchListener {
    private MapView mMapView = null;
    private AMap mAMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private boolean isFirst = true;
    private Marker mCenterMarker;
    private LatLng mLatLng;
    private PopupWindow mPop;
    private List<Marker> mMarkers = new ArrayList<>();
    private EditText mNameEt;
    private EditText mDescriptionEt;
    private Polygon mPolygon;
    private LatLng mLocation;
    private RouteSearch mRouteSearch = null;
    private MyLocationStyle myLocationStyle;
    private LatLng mParkCenterLatlng;
    private Marker mParkCenterMarker;

    private Dialog progDialog = null;

    /**
     * 坐标搜索位置
     */
    private GeocodeSearch geocoderSearch = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setparking);
        initTop();
        initView();
        mMapView.onCreate(savedInstanceState);
        initMap();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
        isFirst = true;
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

    private void initView() {
        NetUtil.checkNetwork(this);

        mMapView = (MapView) findViewById(R.id.bike_map);
        mTitletv.setText("创建停车点");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.add_marker_btn).setOnClickListener(this);
        findViewById(R.id.add_parking_btn).setOnClickListener(this);
        findViewById(R.id.reset_marker_btn).setOnClickListener(this);
        findViewById(R.id.center_marker_btn).setOnClickListener(this);
        mNameEt = (EditText) findViewById(R.id.name_et);
        mDescriptionEt= (EditText) findViewById(R.id.description_et);


    }

    private void initMap() {
        Location location = getLocation(StationCreateActivity.this);
        if (location == null) {
            Toast.makeText(getApplicationContext(), "未获取到当前定位，请确保打开GPS和数据网络！", Toast.LENGTH_LONG).show();
        } else if (location.getLatitude() == 0 && location.getLongitude() == 0) {
            Toast.makeText(getApplicationContext(), "请确保打开该应用的定位权限和数据网络权限！", Toast.LENGTH_LONG).show();
        } else {
            mLocation = new LatLng(location.getLatitude(), location.getLongitude());
//            getBikeNearBy(latLng, 3000, 1, 20);
        }

        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }

        UiSettings settings = mAMap.getUiSettings();
        settings.setZoomControlsEnabled(false);// 是否显示放大缩小按钮
        settings.setMyLocationButtonEnabled(false);// 是否显示定位按钮
        settings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
        mAMap.setMaxZoomLevel(19);
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        //设置定位样式
        setLocationStyle();
        //自定义地图样式
//        setBlueMap(StationCreateActivity.this, "mystyle_sdk_1501143118_0100.data");
        //增加地图监听
        aMapListener();

        //逆地理编码（坐标转地址）
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        findViewById(R.id.location_img).setOnClickListener(this);
        findViewById(R.id.location_img).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                changeCamera(
                        CameraUpdateFactory.newCameraPosition(
                                new CameraPosition(mAMap.getCameraPosition().target, 19, 90, 0)), StationCreateActivity.this);
                return true;
            }
        });

    }

    private void setLocationStyle() {
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.location_marker));
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
        myLocationStyle.strokeColor(Color.TRANSPARENT);
        myLocationStyle.strokeWidth(0f);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        mAMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
        mAMap.setMyLocationStyle(myLocationStyle);// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
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
        mAMap.setCustomMapStylePath(cacheFile.getAbsolutePath());
        mAMap.setMapCustomEnable(true);
    }

    /**
     * 注册AMap监听
     */
    private void aMapListener() {
        /**
         * 地图加载完毕监听
         */
        mAMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                initLocation();
                addCenterToMap();
                mAMap.setOnMarkerClickListener(StationCreateActivity.this);
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!marker.getPosition().equals(mParkCenterMarker)) {
            showPop(marker);
        }
        return false;
    }

    private void addCenterToMap() {
        try {
            MarkerOptions centerOption = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pin))
                    .anchor(0.5f, 0.5f)
                    .visible(true);
            mLatLng = mAMap.getCameraPosition().target;
            Point screenPosition = mAMap.getProjection().toScreenLocation(mLatLng);
            mCenterMarker = mAMap.addMarker(centerOption);
            //设置Marker在屏幕上,不跟随地图移动
            mCenterMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
            mCenterMarker.setClickable(true);

        } catch (Throwable e) {
            e.printStackTrace();
        }
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
        //设置是否强制刷新WIFI，默认为强制刷新
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


    //激活定位
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                AMapLocation myMapLocation = aMapLocation;
                //定位成功回调信息，设置相关消息
                LatLng mLatlng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                if (isFirst) {
                    //将地图移动到定位点
                    changeCamera(
                            CameraUpdateFactory.newCameraPosition(
                                    new CameraPosition(mLatlng, 17, 0, 0)), StationCreateActivity.this);
                    isFirst = false;
                } else {
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }


    private void showPop(final Marker marker) {
        View popView = getLayoutInflater().inflate(R.layout.view_pop, null);
        mPop = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPop.setBackgroundDrawable(new BitmapDrawable());
        mPop.setFocusable(true);
        mPop.setOutsideTouchable(true);
        mPop.update();
        mPop.showAtLocation(mMapView, Gravity.CENTER, getSp(90), -getSp(130));
        popView.findViewById(R.id.add_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (marker.equals(mCenterMarker))
                    addMarker();
            }
        });
        popView.findViewById(R.id.delete_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!marker.equals(mCenterMarker))
                    deleteMarker(marker);
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_marker_btn:
                showParking();
                break;
            case R.id.add_parking_btn:
                create();
                break;
            case R.id.reset_marker_btn:
                reSet();
                break;
            case R.id.center_marker_btn:
                if (mPolygon == null) {
                    ToastUtil.showToast(StationCreateActivity.this, "请先设置停车区域");
                    return;
                }
                addCenterMarker();
                break;
            case R.id.location_img:
                foundMyLocation();
                break;
            default:
                break;
        }
    }

    private void create() {
        NetUtil.checkNetwork(this);

        String name = mNameEt.getText().toString().trim();
        if (StringUtil.isEmpty(name)) {
            ToastUtil.showToast(this, "请填写停车点名称");
            return;
        } else if (mParkCenterLatlng == null) {
            ToastUtil.showToast(this, "请创建中心点");
            return;
        }
        final EditText editText = new EditText(StationCreateActivity.this);
        editText.setHint("请输入密码");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//                editText.setInputType();
        new android.support.v7.app.AlertDialog.Builder(StationCreateActivity.this)
                .setTitle("创建停车点")
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
                        if (StringUtil.isBlank(str_id)) {
                            Toast.makeText(StationCreateActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (str_id.equals("777777")){
                            CreateStation(mDescriptionEt.getText().toString().trim());
                        }else {
                            Toast.makeText(StationCreateActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.dismiss();
                    }
                }).show();
    }
    private void addCenterMarker() {
        mParkCenterLatlng = mCenterMarker.getPosition();
        if (mParkCenterMarker != null) {
            mParkCenterMarker.remove();
        }
        MarkerOptions centerOption = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.search_center_ic))
                .visible(true);
        mParkCenterMarker = mAMap.addMarker(centerOption.position(mParkCenterLatlng));
        LatLonPoint mEndPoint = AMapUtil.convertToLatLonPoint(mParkCenterLatlng);
        getAddress(mEndPoint);
    }

    private void reSet() {
        if (mPolygon != null)
            mPolygon.remove();
        for (Marker marker : mMarkers) {
            marker.remove();
        }
        if (mParkCenterMarker != null) {
            mParkCenterMarker.remove();
            mParkCenterLatlng = null;
        }
        mMarkers = new ArrayList<>();
    }



    private void showParking() {
        LatLng[] lats = new LatLng[mMarkers.size()];
        if (mPolygon != null) {
            mPolygon.remove();
        }

        if (mMarkers.size() > 2) {
            // 声明 多边形参数对象
            PolygonOptions polygonOptions = new PolygonOptions();
            // 添加 多边形的每个顶点（顺序添加）
            for (int i = 0; i < mMarkers.size(); i++) {
                lats[i] = mMarkers.get(i).getPosition();
            }
            polygonOptions.add(lats);
            polygonOptions.strokeWidth(15) // 多边形的边框
                    .strokeColor(R.color.colorOrange) // 边框颜色
                    .fillColor(R.color.colorBlue);   // 多边形的填充色

            mPolygon = mAMap.addPolygon(polygonOptions);
        } else {
            ToastUtil.showToast(this, "停车边界点不足");
        }
    }

    private void addMarker() {
        if (mPop.isShowing()) {
            mPop.dismiss();
        }
        LatLng lat = mCenterMarker.getPosition();
        Marker marker = mAMap.addMarker(new MarkerOptions().position(lat));
        mMarkers.add(marker);

    }

    private void deleteMarker(Marker marker) {
        if (mPop.isShowing()) {
            mPop.dismiss();
        }
        marker.remove();
        mMarkers.remove(marker);

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String addressName = result.getRegeocodeAddress().getFormatAddress();
                mDescriptionEt.setText(addressName);

//                if (flag.equals("stationsearch")) {

//                }
            } else {
                ToastUtil.showToast(StationCreateActivity.this, "未知位置,创建停车点失败");
            }
        }
    }

    private void CreateStation(String addressName) {
        showProgressDialog("正在创建停车点...");
        String name = mNameEt.getText().toString().trim();
        LatLonPoint point = AMapUtil.toWGS84Point(mParkCenterLatlng.latitude, mParkCenterLatlng.longitude);
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("description", addressName);
        params.put("stype", 2);//2:多边形 3:圆形
        params.put("lng", point.getLongitude());
        params.put("lat", point.getLatitude());
//        params.put("coordinate", "GCJ-02");
        params.put("radius", "0");

        List<Lnglat> list = new ArrayList<>();
        for (Marker marker : mMarkers
                ) {
            Lnglat lnglat = new Lnglat();
            LatLng l = marker.getPosition();
            LatLonPoint p = AMapUtil.toWGS84Point(l.latitude, l.longitude);
            lnglat.setLat((float) p.getLatitude());
            lnglat.setLng((float) p.getLongitude());
            list.add(lnglat);
        }
        String lnglats = JSON.toJSONString(list);
        params.put("lnglats", lnglats);

        Map<String, Object> p = new HashMap<>();
        Map[] maps = {params};
        p.put("stations", maps);
        NetRequest
                .post()
                .url(HttpUrl.SET_STATION)
                .addHeader("Authorization:Bear", MyPreference.getInstance(StationCreateActivity.this).getToken())
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
//                                org.json.JSONArray jsonArray = new org.json.JSONObject(result).getJSONArray("stations");
                                ToastUtil.showToast(StationCreateActivity.this, "创建成功");
                                UIHelper.showMain(StationCreateActivity.this);
                            } else {
                                ToastUtil.showToast(StationCreateActivity.this, netResponse.getResult().toString());
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(StationCreateActivity.this, "停车点创建失败");

                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(StationCreateActivity.this, "停车点创建失败");
                        dissmissProgressDialog();
                    }
                });
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    /**
     * GPS 坐标转高德
     *
     * @param context
     * @param lat
     * @param lng
     * @return
     */
    public static LatLng GpsConvertToGD(Context context, double lat, double lng) {
        CoordinateConverter converter = new CoordinateConverter(context);
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(new LatLng(lat, lng));
        return converter.convert();
    }

    /**
     * Camera移动动画
     * 调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update, AMap.CancelableCallback callback) {
        mAMap.animateCamera(update, 600, callback);
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(StationCreateActivity.this, message);
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
     * 获取当前位置
     */
    private void foundMyLocation() {
        try {
            if (mAMap != null) {
                Location location = mAMap.getMyLocation();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (latLng != null) {
                    //将地图移动到定位点
                    changeCamera(
                            CameraUpdateFactory.newCameraPosition(
                                    new CameraPosition(latLng, 17, 0, 0)), StationCreateActivity.this); //经纬度，缩放比例， 水平面夹角（3D效果），方向夹角（旋转）
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int getSp(int i) {
        return (int) DensityUtil.px2sp(this, DensityUtil.dip2px(this, i));
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
}