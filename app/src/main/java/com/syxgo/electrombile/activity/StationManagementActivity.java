package com.syxgo.electrombile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.manager.UIHelper;

/**
 * Created by tangchujia on 2017/9/12.
 */

public class StationManagementActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);
        initTop();
        initView();
    }

    private void initView() {
        mTitletv.setText("停车点管理");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showMain(StationManagementActivity.this);
            }
        });
        findViewById(R.id.tv_create_station).setOnClickListener(this);
        findViewById(R.id.tv_search_station).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_create_station:
                UIHelper.showStationCreate(StationManagementActivity.this);
                finish();
                break;
            case R.id.tv_search_station:
                UIHelper.showStationSearch(StationManagementActivity.this, "stationsearch");
                break;
        }
    }
}
