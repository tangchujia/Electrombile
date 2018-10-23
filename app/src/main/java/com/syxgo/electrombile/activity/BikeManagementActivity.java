package com.syxgo.electrombile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.manager.UIHelper;

/**
 * Created by tangchujia on 2017/9/12.
 */

public class BikeManagementActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike);
        initTop();
        initView();
    }

    private void initView() {
        mTitletv.setText("车辆管理");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.tv_bind_bike).setOnClickListener(this);
        findViewById(R.id.tv_create_bike).setOnClickListener(this);
//        findViewById(R.id.tv_unlock_bike).setOnClickListener(this);
        findViewById(R.id.tv_search_bike).setOnClickListener(this);
        findViewById(R.id.tv_unbind_bike).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_bind_bike:
                UIHelper.showBikeBind(BikeManagementActivity.this);
                break;
            case R.id.tv_create_bike:
                UIHelper.showBikeCreate(BikeManagementActivity.this);
                break;
//            case R.id.tv_unlock_bike:
//                UIHelper.showBikeUnlock(BikeManagementActivity.this);
//                break;
            case R.id.tv_search_bike:
                UIHelper.showBikeSearch(BikeManagementActivity.this);
                break;
            case R.id.tv_unbind_bike:
                UIHelper.showBikeUnbindActivity(BikeManagementActivity.this);
            default:
                break;
        }

    }
}
