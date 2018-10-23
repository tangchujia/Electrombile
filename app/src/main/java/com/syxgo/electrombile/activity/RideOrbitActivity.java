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
import com.syxgo.electrombile.model.Ride;
import com.syxgo.electrombile.util.AMapUtil;
import com.syxgo.electrombile.util.LoginUtil;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangchujia on 2017/10/20.
 */

public class RideOrbitActivity extends BaseActivity {
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
        mTitletv.setText("行程轨迹");
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
        int rideId = getIntent().getIntExtra(Common.RIDE_ID, -1);
        if (rideId == -1) {
            return;
        }
        NetUtil.checkNetwork(RideOrbitActivity.this);

        String url = HttpUrl.GET_RIDES + "?ride_id=" + rideId;
        showProgressDialog("正在查询...");
        NetRequest
                .get()
                .url(url)
                .addHeader("Authorization:Bear", MyPreference.getInstance(RideOrbitActivity.this).getToken())
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
                                getOrbit(rides.get(0));

                            } else {
                                LoginUtil.login(RideOrbitActivity.this, result);
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(RideOrbitActivity.this, e.getMessage());
                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(RideOrbitActivity.this, "查询失败");
                        dissmissProgressDialog();
                    }
                });
    }

    private void getOrbit(Ride ride) {
        String track = ride.getTrack_points();
        String[] orbits = track.split(";");
        List<LatLng> latLngs = new ArrayList<>();
        for (String orbit : orbits) {
            String[] ll = orbit.split(",");
            LatLng latLng = new LatLng(Double.parseDouble(ll[0]), Double.parseDouble(ll[1]));
            latLngs.add(latLng);
        }

        LatLng[] lats = new LatLng[latLngs.size()];
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
        for (int i = 0; i < latLngs.size(); i++) {
            lats[i] = AMapUtil.GpsConvertToGD(RideOrbitActivity.this, new LatLng(latLngs.get(i).latitude, latLngs.get(i).longitude));
            boundsBuilder.include(new LatLng(latLngs.get(i).latitude, latLngs.get(i).longitude));//把所有点都include进去（LatLng类型）
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
            boundsBuilder.include(AMapUtil.GpsConvertToGD(RideOrbitActivity.this, new LatLng(lats[i].latitude, lats[i].longitude)));//把所有点都include进去（LatLng类型）
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
            progDialog = new LoadingDialog().createLoadingDialog(RideOrbitActivity.this, message);
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

    //绘制一条纹理线
//    private void addPolylinesWithTexture() {
//        //四个点
//        LatLng A = new LatLng(Lat_A+1, Lon_A+1);
//        LatLng B = new LatLng(Lat_B+1, Lon_B+1);
//        LatLng C = new LatLng(Lat_C+1, Lon_C+1);
//        LatLng D = new LatLng(Lat_D+1, Lon_D+1);
//
//        //用一个数组来存放纹理
//        List<BitmapDescriptor> texTuresList = new ArrayList<BitmapDescriptor>();
////        texTuresList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_alr));
////        texTuresList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture));
//        texTuresList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_alr_night));
//
//        //指定某一段用某个纹理，对应texTuresList的index即可, 四个点对应三段颜色
//        List<Integer> texIndexList = new ArrayList<Integer>();
////        texIndexList.add(0);//对应上面的第0个纹理
////        texIndexList.add(2);
//        texIndexList.add(1);
//
//
//        PolylineOptions options = new PolylineOptions();
//        options.width(20);//设置宽度
//
//        //加入四个点
//        options.add(A,B,C,D);
//
//        //加入对应的颜色,使用setCustomTextureList 即表示使用多纹理；
//        options.setCustomTextureList(texTuresList);
//
//        //设置纹理对应的Index
//        options.setCustomTextureIndex(texIndexList);
//
//        aMap.addPolyline(options);
//    }
}
