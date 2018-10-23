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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
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
import com.amap.api.maps.LocationSource;
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
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.application.MyApplication;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.manager.ActivityManager;
import com.syxgo.electrombile.manager.UIHelper;
import com.syxgo.electrombile.model.Lnglat;
import com.syxgo.electrombile.model.Station;
import com.syxgo.electrombile.util.AMapUtil;
import com.syxgo.electrombile.util.DensityUtil;
import com.syxgo.electrombile.util.DeviceUtil;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.AEADBadTagException;

import static android.R.id.progress;

/**
 * Created by tangchujia on 2017/9/12.
 */

public class StationSearchActivity extends BaseActivity implements AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener, AMap.OnMarkerClickListener, AMap.CancelableCallback {
    private MapView mMapView = null;
    private AMap mAMap;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private boolean isFirst = true;
    private MyLocationStyle myLocationStyle;
    private Dialog progDialog = null;
    private List<Circle> mCircles = new ArrayList<>();
    private List<Polygon> mPolygon = new ArrayList<>();
    /**
     * 坐标搜索位置
     */
    private GeocodeSearch geocoderSearch = null;
    private TextView mNameTv;
    private TextView mLocationTv;
    private TextView mBikeNumTv;
    private AMapLocation myMapLocation;
    private List<Station> stations = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();
    private LatLng mLatLng;
    private Marker mCenterMarker;
    private Button mDeleteBtn;
    private org.json.JSONArray jsonArray;
    private String flag;
    private LatLng mLocation;//搜索点坐标

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_search);
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

        flag = getIntent().getStringExtra("from");

        mTitletv.setText("查看停车点");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mMapView = (MapView) findViewById(R.id.station_map);
        mNameTv = (TextView) findViewById(R.id.station_name_tv);
        mLocationTv = (TextView) findViewById(R.id.station_location_tv);
        mBikeNumTv = (TextView) findViewById(R.id.station_bike_num_tv);
        mDeleteBtn = (Button) findViewById(R.id.delete_station_btn);
        mNameTv.setVisibility(View.GONE);
        mLocationTv.setVisibility(View.GONE);
        mBikeNumTv.setVisibility(View.GONE);
        mDeleteBtn.setText("点击停车点查看具体信息");
        mMenutv.setVisibility(View.VISIBLE);
        mMenutv.setText("查看列表");
        mMenutv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StationSearchActivity.this, StationListActivity.class);
                if (jsonArray == null) {
                    ToastUtil.showToast(StationSearchActivity.this, "查询停车点失败");
                    return;
                }
                intent.putExtra("stations", jsonArray.toString());
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("location", mLocation);
                startActivity(intent);
            }
        });
    }

    private void initMap() {
        Location location = getLocation(StationSearchActivity.this);
        if (location == null) {
            Toast.makeText(getApplicationContext(), "未获取到当前定位，请确保打开GPS和数据网络！", Toast.LENGTH_LONG).show();
        } else if (location.getLatitude() == 0 && location.getLongitude() == 0) {
            Toast.makeText(getApplicationContext(), "请确保打开该应用的定位权限和数据网络权限！", Toast.LENGTH_LONG).show();
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
        setBlueMap(StationSearchActivity.this, "mystyle_sdk_1501143118_0100.data");
        //增加地图监听
        aMapListener();

        findViewById(R.id.location_img).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                changeCamera(
                        CameraUpdateFactory.newCameraPosition(
                                new CameraPosition(mAMap.getCameraPosition().target, 19, 90, 0)), StationSearchActivity.this);
                return true;
            }
        });

        findViewById(R.id.location_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foundMyLocation();
            }
        });

        findViewById(R.id.refresh_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myMapLocation != null) {
//                    if (flag.equals("stationsearch")) {
                    mLatLng = mAMap.getCameraPosition().target;
                    getStations(mLatLng);
//                    } else if (flag.equals("stationlist")) {
//                        Station station = (Station) getIntent().getSerializableExtra("station");
//                        showStation(station);
//                    }
                }
            }
        });

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
                mAMap.setOnMarkerClickListener(StationSearchActivity.this);
            }
        });
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
            progDialog = new LoadingDialog().createLoadingDialog(StationSearchActivity.this, message);
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
                                    new CameraPosition(latLng, 17, 0, 0)), StationSearchActivity.this); //经纬度，缩放比例， 水平面夹角（3D效果），方向夹角（旋转）
                }
            }
        } catch (Exception e) {
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

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                myMapLocation = aMapLocation;
                //定位成功回调信息，设置相关消息
                if (isFirst) {
                    isFirst = false;
                    LatLng latlng = null;
                    //将地图移动到定位点
                    latlng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    changeCamera(
                            CameraUpdateFactory.newCameraPosition(
                                    new CameraPosition(latlng, 17, 0, 0)), StationSearchActivity.this);
                    getStations(latlng);

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

    private void getStations(LatLng latlng) {
        NetUtil.checkNetwork(this);
        if (latlng == null) {
            return;
        }
        showProgressDialog("正在查询停车点...");

        mLocation = latlng;
        LatLonPoint point = AMapUtil.toWGS84Point(latlng.latitude, latlng.longitude);
        String url = String.format(HttpUrl.SEARCH_STATION_NEARBY, point.getLongitude(), point.getLatitude(), 10000);
        NetRequest
                .get()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(StationSearchActivity.this).getToken())
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
                                jsonArray = new org.json.JSONObject(result).getJSONArray("stations");
                                stations = JSONObject.parseArray(jsonArray.toString(), Station.class);
                                clearMap();
                                showStations(stations);
                            } else {
                                ToastUtil.showToast(StationSearchActivity.this, netResponse.getResult().toString());
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(StationSearchActivity.this, "停车点查看失败");

                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(StationSearchActivity.this, "停车点查看失败");
                        dissmissProgressDialog();
                    }
                });

    }

    private void showStations(List<Station> stations) {
        if (stations.size() == 0) {
            ToastUtil.showToast(StationSearchActivity.this, "附近10km内无停车点");
            return;
        }
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
        for (Station station : stations) {
            LatLng latLng = AMapUtil.GpsConvertToGD(StationSearchActivity.this, new LatLng(station.getLat(), station.getLng()));

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
                        lats[i] = AMapUtil.GpsConvertToGD(StationSearchActivity.this, new LatLng(lnglat.get(i).getLat(), lnglat.get(i).getLng()));
                    }
                    polygonOptions.add(lats);
                    polygonOptions.strokeWidth(15) // 多边形的边框
                            .strokeColor(R.color.colorOrange) // 边框颜色
                            .fillColor(R.color.colorBlue);   // 多边形的填充色

                    Polygon polygon = mAMap.addPolygon(polygonOptions);
                    mPolygon.add(polygon);
                }

            } else if (station.getStype() == 3) {
                //圆形
                Circle circle = mAMap.addCircle(new CircleOptions().
                        center(latLng).
                        radius(station.getRadius())
                        .strokeColor(R.color.colorOrange) // 边框颜色
                        .fillColor(R.color.colorBlue)  // 多边形的填充色
                        .strokeWidth(15));
                mCircles.add(circle);
            }
        }
        mAMap.animateCamera(CameraUpdateFactory
                .newLatLngBoundsRect(boundsBuilder.build(),
                        DeviceUtil.dp2px(StationSearchActivity.this, 50),
                        DeviceUtil.dp2px(StationSearchActivity.this, 50),
                        DeviceUtil.dp2px(StationSearchActivity.this, 50),
                        DeviceUtil.dp2px(StationSearchActivity.this, 50)));
        if (flag.equals("stationlist")) {
            Station station = (Station) getIntent().getSerializableExtra("station");
            LatLng latLng = AMapUtil.GpsConvertToGD(StationSearchActivity.this, new LatLng(station.getLat(), station.getLng()));
            LatLonPoint mEndPoint = new LatLonPoint(station.getLat(), station.getLng());
            mCenterMarker.setObject(station);
            getAddress(mEndPoint);
            changeCamera(
                    CameraUpdateFactory.newCameraPosition(
                            new CameraPosition(latLng, 17, 0, 0)), StationSearchActivity.this);
//            showPop(station);
        }

    }

    private void clearMap() {
        if (mPolygon != null) {
            for (Polygon p :
                    mPolygon) {
                p.remove();
            }
        }
        if (mCircles != null) {
            for (Circle c :
                    mCircles) {
                c.remove();
            }
        }
        for (Marker m : markers) {
            mLatLng = mAMap.getCameraPosition().target;
            if (!m.getPosition().equals(mLatLng))
                m.remove();
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String addressName = result.getRegeocodeAddress().getFormatAddress();
                mLocationTv.setText(addressName);

//                if (flag.equals("stationsearch")) {
                Station station = (Station) mCenterMarker.getObject();
                showPop(station);
//                }
            } else {
                ToastUtil.showToast(StationSearchActivity.this, "未知位置");
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mAMap != null) {
            mCenterMarker = marker;
            changeCamera(
                    CameraUpdateFactory.newCameraPosition(
                            new CameraPosition(marker.getPosition(), 15, 0, 0)), StationSearchActivity.this);
            LatLonPoint mEndPoint = AMapUtil.convertToLatLonPoint(marker.getPosition());
            getAddress(mEndPoint);
            return true;
        } else {
            return true;
        }
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

    private void showPop(final Station s) {
        for (Marker m : markers) {
            if (!m.equals(mCenterMarker))
                m.remove();
        }

        for (Station station : stations) {
            MarkerOptions centerOption = new MarkerOptions();
            LatLng latLng = AMapUtil.GpsConvertToGD(StationSearchActivity.this, new LatLng(station.getLat(), station.getLng()));
            if (station.getId() == s.getId()) {
                centerOption = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.search_center_ic))
                        .visible(true);
            }
            Marker marker = mAMap.addMarker(centerOption.position(latLng));
            marker.setObject(station);
            markers.add(marker);
        }

        mNameTv.setVisibility(View.VISIBLE);
        mLocationTv.setVisibility(View.VISIBLE);
        mBikeNumTv.setVisibility(View.VISIBLE);
//        mStationMsgTv.setVisibility(View.VISIBLE);

        mDeleteBtn.setText("删除停车点");
        String distance = "";
        if (s.getDistance() < 1000) {
            distance = String.format("%.2fm", s.getDistance());
        } else {
            distance = String.format("%.2fkm", s.getDistance() / 1000);
        }
        mNameTv.setText("停车点编号：" + s.getId() + "（" + s.getName() + "）" + "    " + distance);
        mBikeNumTv.setText("车辆数量：" + s.getBikes_count());
        findViewById(R.id.delete_station_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(StationSearchActivity.this).setTitle("确定删除停车点：" + +s.getId() + "（" + s.getName() + "）" + "?")
                        .setMessage("位置：" + mLocationTv.getText().toString() + "\n" + "车辆数量：" + s.getBikes_count())
                        .setCancelable(true)
                        .setIcon(R.mipmap.ic_logo_app)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })

                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteStation(s);

                            }
                        })
                        .show();
            }
        });
    }

    private void deleteStation(Station station) {
        NetUtil.checkNetwork(this);

        showProgressDialog("正在删除停车点...");
        Map<String, Integer> params = new HashMap<>();
        params.put("station_id", station.getId());
        NetRequest
                .delete()
                .url(HttpUrl.DELETE_STATION + "?station_id=" + station.getId())
                .addHeader("Authorization:Bear", MyPreference.getInstance(StationSearchActivity.this).getToken())
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
                                ToastUtil.showToast(StationSearchActivity.this, "停车点删除成功");
                                LatLng latlng = new LatLng(myMapLocation.getLatitude(), myMapLocation.getLongitude());

                                getStations(latlng);
                            } else {
                                ToastUtil.showToast(StationSearchActivity.this, netResponse.getResult().toString());
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(StationSearchActivity.this, "停车点删除失败");

                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(StationSearchActivity.this, "停车点删除失败");
                        dissmissProgressDialog();
                    }
                });

    }

    private int getSp(int i) {
        return (int) DensityUtil.px2sp(this, DensityUtil.dip2px(this, i));
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onCancel() {

    }
}
