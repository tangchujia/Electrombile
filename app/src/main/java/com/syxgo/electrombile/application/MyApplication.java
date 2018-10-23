package com.syxgo.electrombile.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import org.litepal.LitePalApplication;

/**
 * Created by tangchujia on 2017/7/26.
 */

public class MyApplication extends LitePalApplication implements Application.ActivityLifecycleCallbacks {
    private static Context context;
    private boolean mIsAppForeground;//判断app是否在前台
    private int mAliveActivityCount;//记录存活的activity数量

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        if (Build.VERSION.SDK_INT >= 24) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }


    public static Context getContext() {
        return context;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mAliveActivityCount++;
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        mIsAppForeground = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mIsAppForeground = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mAliveActivityCount--;
    }
}
