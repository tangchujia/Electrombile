package com.syxgo.electrombile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.syxgo.electrombile.application.MyApplication;
import com.syxgo.electrombile.util.ToastUtil;


/**
 * Created by tangchujia on 16/12/1.
 */
public class NetReceiver extends BroadcastReceiver {
    ConnectivityManager mConnectivityManager;
    NetworkInfo netInfo;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = mConnectivityManager.getActiveNetworkInfo();
            NetworkInfo activeInfo = mConnectivityManager.getActiveNetworkInfo();

            if (activeInfo != null) { //网络连接
                Log.d("Receiver", "isNetStatusOk: " + true);
                ToastUtil.showToast(context, "网络连接");
            } else { //网络断开
                Log.d("Receiver", "isNetStatusOk: " + false);
                ToastUtil.showToast(context, "网络断开");
            }
        }
    }

}
