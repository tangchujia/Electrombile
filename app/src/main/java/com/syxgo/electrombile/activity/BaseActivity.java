package com.syxgo.electrombile.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.manager.ActivityManager;

import kr.co.namee.permissiongen.PermissionGen;

/**
 * Created by tangchujia on 2017/7/21.
 */

public class BaseActivity extends AppCompatActivity {
    TextView mTitletv;
    TextView mMenutv;
    ImageView mBackImg;
    ImageView mMenuImg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 修改状态栏颜色，4.4+生效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus();
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_black);//通知栏所需颜色

        ActivityManager.getScreenManager().pushActivity(this);

        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request();
    }

    public void initTop() {
        mTitletv = (TextView) findViewById(R.id.tv_title_dd);
        mBackImg = (ImageView) findViewById(R.id.iv_back_dd);
//        mBackImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                BaseActivity.this.finish();
//            }
//        });
        mMenutv = (TextView) findViewById(R.id.tv_menu);
        mMenuImg = (ImageView) findViewById(R.id.img_menu);

    }

    @TargetApi(19)
    protected void setTranslucentStatus() {
        Window window = getWindow();
        // 头部状态栏
        window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 底部状态栏
//        window.setFlags(
//                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    @Override
    protected void onDestroy() {
        ActivityManager.getScreenManager().popActivity(this);
        super.onDestroy();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
