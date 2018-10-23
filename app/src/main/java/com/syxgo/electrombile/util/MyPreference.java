package com.syxgo.electrombile.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

@SuppressLint("WorldReadableFiles")
public class MyPreference {
    public static final String USER_PREFERENCES = "merchant_preferences";

    private static MyPreference instance;
    private SharedPreferences userSetting;

    @SuppressWarnings("static-access")
    private MyPreference(Context context) {
        this.userSetting = context.getSharedPreferences(USER_PREFERENCES, context.MODE_APPEND);
    }

    public static MyPreference getInstance(Context context) {
        if (instance == null) {
            synchronized (MyPreference.class) {
                if (instance == null) {
                    instance = new MyPreference(context);
                }
            }
        }
        return instance;
    }

    /**
     * 设置登录用户token
     */
    public void putToken(String token) {
        userSetting.edit().putString("token", token).commit();
    }

    /**
     * 获取登录用户token
     */
    public String getToken() {
        return userSetting.getString("token", "");
    }

    /**
     * 设置当前登录ID
     */
    public void putUserId(int id) {
        userSetting.edit().putInt("id", id).commit();
    }

    /**
     * 获取当前登录ID
     */
    public int getUserId() {
        return userSetting.getInt("id", -100);
    }


    /**
     * 设置provider ID
     */
    public void putProviderId(int id) {
        userSetting.edit().putInt("provider_id", id).commit();
    }

    /**
     * 获取provider ID
     */
    public int getProviderId() {
        return userSetting.getInt("provider_id", -100);
    }

    /**
     * 设置是否为第一次登录
     */
    public void putFirstLogin(boolean firstLogin) {
        userSetting.edit().putBoolean("first-time-use", firstLogin).commit();
    }

    /**
     * 获取是否为第一次登录
     */
    public boolean getFirstLogin() {
        return userSetting.getBoolean("first-time-use", true);
    }

    /**
     * 设置phone
     */
    public void putPhone(String token) {
        userSetting.edit().putString("phone", token).commit();
    }

    /**
     * 获取phone
     */
    public String getPhone() {
        return userSetting.getString("phone", "");
    }
}
