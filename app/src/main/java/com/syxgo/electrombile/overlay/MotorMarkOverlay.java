package com.syxgo.electrombile.overlay;

import android.content.Context;
import android.view.animation.LinearInterpolator;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.model.Bike;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vkl on 2016/12/28.
 */

public class MotorMarkOverlay {
    protected List<Marker> motorMarkers = new ArrayList<Marker>();
    protected AMap mAMap;
    private Context context;
    private List<Bike> markBikes = new ArrayList<Bike>();
    private CoordinateConverter converter;

    public MotorMarkOverlay(Context context, AMap amap, List<Bike> markBikes) {
        super();
        this.context = context;
        this.mAMap = amap;
        this.markBikes = markBikes;
        converter = new CoordinateConverter(context);
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
    }

    /**
     * 去掉MotorMarkOverlay上所有的Marker。
     */
    public void removeFromMap() {
        for (Marker marker : motorMarkers) {
            marker.remove();
        }
    }

    /**
     * 添加电摩点到地图中。
     */
    public void addToMap() {

        for (Bike bike : markBikes) {
            try {
                addMotorMarker(bike);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 增加motor marker
     */
    protected void addMotorMarker(Bike bike) {
        converter.coord(new LatLng(bike.getLat(), bike.getLng()));
        LatLng desLatLng = converter.convert();//坐标转换

        MarkerOptions options = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.motor_marker))
                .position(desLatLng)
                .title(String.format("编号：%06d", bike.getId()))
                .snippet("电量：" + bike.getBattery_level() + "%")
                .infoWindowEnable(true)
                .draggable(true);

//        if(bike.getStatus() == 0){
//            options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.motor_marker_outline));
//        }else if(bike.getStatus() == 1){
//            options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.motor_marker_online));
//            int tele_interval = DateUtil.getMarginMinutes(DateUtil.getDateTime(), bike.getLast_active());
//            if (tele_interval > 10) {
//                options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.motor_marker_online_communication));
//            }
//            if(bike.getBattery_status()==48||bike.getBattery_level()<5){
//                options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.motor_marker_online_battery));
//            }
//        }else if(bike.getStatus() == 2){
//            options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.motor_marker_riding));
//            int tele_interval = DateUtil.getMarginMinutes(DateUtil.getDateTime(), bike.getLast_active());
//            if (tele_interval > 10) {
//                options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.motor_marker_riding_communication));
//            }
//            if(bike.getBattery_status()==48||bike.getBattery_level()<5){
//                options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.motor_marker_riding_battery));
//            }
//        }else {
//            options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.motor_marker_exception));
//        }

        if (options == null) {
            return;
        }
        Marker marker = mAMap.addMarker(options);
        if (marker != null) {
            motorMarkers.add(marker);
            //添加动画效果
            startGrowAnimation(marker);
        }
    }

    /**
     * 地上生长的Marker
     */
    private void startGrowAnimation(Marker growMarker) {
        if (growMarker != null) {
            Animation animation = new ScaleAnimation(0, 1, 0, 1);
            animation.setInterpolator(new LinearInterpolator());
            //整个移动所需要的时间
            animation.setDuration(500);
            //设置动画
            growMarker.setAnimation(animation);
            //开始动画
            growMarker.startAnimation();
        }
    }
}
