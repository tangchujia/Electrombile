package com.syxgo.electrombile.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.manager.UIHelper;

/**
 * Created by tangchujia on 2017/9/15.
 */

public class BatteryManagementActivity extends BaseActivity implements View.OnClickListener {
    private TextView mBindTv;
    private TextView mCreateTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        initTop();
        initView();
    }

    private void initView() {
        mTitletv.setText("电池管理");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBindTv = (TextView) findViewById(R.id.tv_bind_battery);
        mCreateTv = (TextView) findViewById(R.id.tv_create_battery);

        mBindTv.setOnClickListener(this);
        mCreateTv.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_bind_battery:
                UIHelper.showBatteryBind(BatteryManagementActivity.this);
                break;
            case R.id.tv_create_battery:
                UIHelper.showBatteryCreate(BatteryManagementActivity.this);
                break;

        }

    }
}
