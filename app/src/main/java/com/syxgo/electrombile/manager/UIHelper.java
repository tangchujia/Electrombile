package com.syxgo.electrombile.manager;

import android.app.Activity;
import android.content.Intent;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.activity.BatteryManagementActivity;
import com.syxgo.electrombile.activity.BikeDetailActivity;
import com.syxgo.electrombile.activity.BikeLocationActivity;
import com.syxgo.electrombile.activity.BikeManagementActivity;
import com.syxgo.electrombile.activity.BikeOrbitActivity;
import com.syxgo.electrombile.activity.BikeSearchActivity;
import com.syxgo.electrombile.activity.BindBatteryActivity;
import com.syxgo.electrombile.activity.BindBikeActivity;
import com.syxgo.electrombile.activity.BindECUActivity;
import com.syxgo.electrombile.activity.BluetoothUnlockActivity;
import com.syxgo.electrombile.activity.CreateBatteryActivity;
import com.syxgo.electrombile.activity.CreateBikeActivity;
import com.syxgo.electrombile.activity.CreateECUActivity;
import com.syxgo.electrombile.activity.EcuManagementActivity;
import com.syxgo.electrombile.activity.LoginActivity;
import com.syxgo.electrombile.activity.LoginPwdActivity;
import com.syxgo.electrombile.activity.MainActivity;
import com.syxgo.electrombile.activity.NFCManagementActivity;
import com.syxgo.electrombile.activity.OrderDetailActivity;
import com.syxgo.electrombile.activity.OrderManagementActivity;
import com.syxgo.electrombile.activity.ReadNFCActivity;
import com.syxgo.electrombile.activity.RideDetailactivity;
import com.syxgo.electrombile.activity.RideManagementActivity;
import com.syxgo.electrombile.activity.RideOrbitActivity;
import com.syxgo.electrombile.activity.SearchECUActivity;
import com.syxgo.electrombile.activity.StationCreateActivity;
import com.syxgo.electrombile.activity.StationManagementActivity;
import com.syxgo.electrombile.activity.StationSearchActivity;
import com.syxgo.electrombile.activity.UnbindBikeActivity;
import com.syxgo.electrombile.activity.UserCenterActivity;
import com.syxgo.electrombile.activity.UserDetailActivity;
import com.syxgo.electrombile.activity.UserManagementActivity;
import com.syxgo.electrombile.activity.WriteNFCActivity;


/**
 * Created by tangchujia on 2017/8/19.
 */

public class UIHelper {
    public static void showLogin(Activity context) {
        Intent intent = new Intent(context, LoginPwdActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void showLoginPhone(Activity context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void showMain(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void showEcu(Activity context) {
        Intent intent = new Intent(context, EcuManagementActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showBattery(Activity context) {
        Intent intent = new Intent(context, BatteryManagementActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showBike(Activity context) {
        Intent intent = new Intent(context, BikeManagementActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showStation(Activity context) {
        Intent intent = new Intent(context, StationManagementActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showNFC(Activity context) {
        Intent intent = new Intent(context, NFCManagementActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showBikeCreate(Activity context) {
        Intent intent = new Intent(context, CreateBikeActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showBikeSearch(Activity context) {
        Intent intent = new Intent(context, BikeSearchActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showBikeBind(Activity context) {
        Intent intent = new Intent(context, BindBikeActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showBikeUnbindActivity(Activity context) {
        Intent intent = new Intent(context, UnbindBikeActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showBikeUnlock(Activity context) {
        Intent intent = new Intent(context, BluetoothUnlockActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showECUCreate(Activity context) {
        Intent intent = new Intent(context, CreateECUActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showECUSearch(Activity context) {
        Intent intent = new Intent(context, SearchECUActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showECUBind(Activity context) {
        Intent intent = new Intent(context, BindECUActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showStationCreate(Activity context) {
        Intent intent = new Intent(context, StationCreateActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showStationSearch(Activity context, String str) {
        Intent intent = new Intent(context, StationSearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", str);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showBatteryBind(Activity context) {
        Intent intent = new Intent(context, BindBatteryActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showBatteryCreate(Activity context) {
        Intent intent = new Intent(context, CreateBatteryActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showNFCWrite(Activity context, int bikeid) {
        Intent intent = new Intent(context, WriteNFCActivity.class);
        intent.putExtra("bike_id", bikeid);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showNFCRead(Activity context) {
        Intent intent = new Intent(context, ReadNFCActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.aliwx_slide_right_in, R.anim.aliwx_slide_left_out);
    }

    public static void showRide(Activity context) {
        Intent intent = new Intent(context, RideManagementActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void showUser(Activity context) {
        Intent intent = new Intent(context, UserManagementActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void showOrder(Activity context) {
        Intent intent = new Intent(context, OrderManagementActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void showUserCenter(Activity context) {
        Intent intent = new Intent(context, UserCenterActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void showOrderDetail(Activity context, String orderno) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(Common.ORDER_NO, orderno);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void showUserDetail(Activity context, int userid) {
        Intent intent = new Intent(context, UserDetailActivity.class);
        intent.putExtra(Common.USER_ID, userid);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void showRideDetail(Activity context, int rideid) {
        Intent intent = new Intent(context, RideDetailactivity.class);
        intent.putExtra(Common.RIDE_ID, rideid);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void showBikeDetail(Activity context, int bikeid) {
        Intent intent = new Intent(context, BikeDetailActivity.class);
        intent.putExtra(Common.BIKE_ID, bikeid);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void showBikeLocation(Activity context, int bikeid) {
        Intent intent = new Intent(context, BikeLocationActivity.class);
        intent.putExtra(Common.BIKE_ID, bikeid);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void showRideOrbit(Activity context, int rideid) {
        Intent intent = new Intent(context, RideOrbitActivity.class);
        intent.putExtra(Common.RIDE_ID, rideid);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void showBikeOrbit(Activity context, int bikeid) {
        Intent intent = new Intent(context, BikeOrbitActivity.class);
        intent.putExtra(Common.BIKE_ID, bikeid);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
