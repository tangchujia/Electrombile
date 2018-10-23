package com.syxgo.electrombile.util;

import android.app.Activity;

import com.syxgo.electrombile.manager.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tangchujia on 2017/10/11.
 */

public class LoginUtil {

    public static void login(Activity activity, String result) {
        String msg = "";
        try {
            msg = new JSONObject(result).getString("message");
            if (msg.equals("需要先登录")) {
                MyPreference.getInstance(activity).putToken("");
                UIHelper.showLogin(activity);
            }
            ToastUtil.showToast(activity, msg);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
