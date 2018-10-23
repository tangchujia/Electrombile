package com.syxgo.electrombile.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.manager.UIHelper;

/**
 * Created by tangchujia on 2017/11/16.
 */

public class NFCManagementActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        initTop();
        initView();
    }

    private void initView() {
        mTitletv.setText("NFC操作");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.tv_write_nfc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showNFCWrite(NFCManagementActivity.this, -1);

            }
        });
        findViewById(R.id.tv_read_nfc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showNFCRead(NFCManagementActivity.this);

            }
        });

    }
}
