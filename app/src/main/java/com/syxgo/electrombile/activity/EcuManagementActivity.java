package com.syxgo.electrombile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.manager.UIHelper;

import static android.R.attr.id;

/**
 * Created by tangchujia on 2017/9/12.
 */
public class EcuManagementActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitu_ecu);
        initTop();
        iniView();
    }

    private void iniView() {
        mTitletv.setText("ECU管理");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.tv_bind_ecu).setOnClickListener(this);
        findViewById(R.id.tv_create_ecu).setOnClickListener(this);
        findViewById(R.id.tv_search_ecu).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_bind_ecu:
                UIHelper.showECUBind(EcuManagementActivity.this);
                break;

            case R.id.tv_create_ecu:
                UIHelper.showECUCreate(EcuManagementActivity.this);
                break;
            case R.id.tv_search_ecu:
                UIHelper.showECUSearch(EcuManagementActivity.this);
                break;

        }
    }
}
