package com.syxgo.electrombile.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.iflytek.autoupdate.IFlytekUpdate;
import com.iflytek.autoupdate.IFlytekUpdateListener;
import com.iflytek.autoupdate.UpdateConstants;
import com.iflytek.autoupdate.UpdateErrorCode;
import com.iflytek.autoupdate.UpdateInfo;
import com.iflytek.autoupdate.UpdateType;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.adapter.MainGridAdapter;
import com.syxgo.electrombile.manager.ActivityManager;
import com.syxgo.electrombile.manager.UIHelper;
import com.syxgo.electrombile.model.Item;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private List<Item> list;
    private MainGridAdapter adapter;
    private PullToRefreshGridView home_pull_refresh_grid;
    private long mExitTime;
    private IFlytekUpdate updManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTop();
        initUpdate();
        mTitletv.setText("正式版");
        mMenutv.setText("版本更新");
        mMenutv.setVisibility(View.GONE);
//        mMenutv.setVisibility(View.VISIBLE);
        mBackImg.setVisibility(View.GONE);
        initData();
        initView();
//        addListener();
        loadData();
    }

    private void initView() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        home_pull_refresh_grid = (PullToRefreshGridView) findViewById(R.id.home_pull_refresh_grid);
        //设置可上拉刷新和下拉刷新
        home_pull_refresh_grid.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        //设置刷新时显示的文本
        ILoadingLayout startLayout = home_pull_refresh_grid.getLoadingLayoutProxy(true, false);
        startLayout.setPullLabel("正在下拉刷新...");
        startLayout.setRefreshingLabel("正在玩命加载中...");
        startLayout.setReleaseLabel("放开以刷新");

        final ILoadingLayout endLayout = home_pull_refresh_grid.getLoadingLayoutProxy(false, true);
        endLayout.setPullLabel("正在上拉刷新...");
        endLayout.setRefreshingLabel("正在玩命加载中...");
        endLayout.setReleaseLabel("放开以刷新");


        home_pull_refresh_grid.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                home_pull_refresh_grid.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {

            }
        });

        home_pull_refresh_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        UIHelper.showBattery(MainActivity.this);
                        break;
                    case 1:
                        UIHelper.showEcu(MainActivity.this);
                        break;
                    case 2:
                        UIHelper.showNFC(MainActivity.this);
                        break;
                    case 3:
                        UIHelper.showBike(MainActivity.this);
                        break;
                    case 4:
                        UIHelper.showRide(MainActivity.this);
                        break;
                    case 5:
                        UIHelper.showUser(MainActivity.this);
                        break;
                    case 6:
                        UIHelper.showOrder(MainActivity.this);
                        break;
                    case 7:
                        UIHelper.showUserCenter(MainActivity.this);
                        break;

                    default:
                        break;
                }


            }
        });

        adapter = new MainGridAdapter(MainActivity.this, list);
        home_pull_refresh_grid.setAdapter(adapter);
    }

    private void exit() {
        new AlertDialog.Builder(MainActivity.this).setTitle("闪影行管理端")
                .setMessage("您确定要退出登录吗？")
                .setCancelable(true)
                .setIcon(R.mipmap.ic_logo_app)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyPreference.getInstance(MainActivity.this).putToken("");
                        ActivityManager.getScreenManager().popAllActivity();
                    }
                })
                .show();
    }

//    private void addListener() {
//        mMenutv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updManager.forceUpdate(MainActivity.this, updateListener);
//
//            }
//        });
//    }

    private IFlytekUpdateListener updateListener = new IFlytekUpdateListener() {

        @Override
        public void onResult(int errorcode, UpdateInfo result) {

            if (errorcode == UpdateErrorCode.OK && result != null) {
                if (result.getUpdateType() == UpdateType.NoNeed) {
                    handler.sendEmptyMessage(20001);
                    return;
                }
                updManager.showUpdateInfo(MainActivity.this, result);
            } else {
                handler.sendEmptyMessage(20002);
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 20001:
//                    ToastUtil.showToast(MainActivity.this, "已经是最新版本！");
                    break;
                case 20002:
                    ToastUtil.showToast(MainActivity.this, "请求更新失败！");
                    break;
                default:
                    break;
            }
        }
    };

    private void initUpdate() {
        updManager = IFlytekUpdate.getInstance(MainActivity.this);
        updManager.setDebugMode(false);
        updManager.setParameter(UpdateConstants.EXTRA_WIFIONLY, "false");
        // 设置通知栏icon，默认使用SDK默认
        updManager.setParameter(UpdateConstants.EXTRA_NOTI_ICON, "false");
        updManager.setParameter(UpdateConstants.EXTRA_STYLE, UpdateConstants.UPDATE_UI_DIALOG);
        updManager.autoUpdate(MainActivity.this, updateListener);
    }

    private void initData() {
        list = new ArrayList<>();
        list.add(new Item(0, "电池管理", R.mipmap.ic_battery));
        list.add(new Item(1, "ECU管理", R.mipmap.ic_ecu));
        list.add(new Item(2, "NFC操作", R.mipmap.ic_nfc));

        list.add(new Item(3, "车辆管理", R.mipmap.ic_bike));
        list.add(new Item(4, "行程管理", R.mipmap.ic_ride));
        list.add(new Item(5, "用户管理", R.mipmap.ic_users));
        list.add(new Item(6, "订单管理", R.mipmap.ic_order));
        list.add(new Item(7, "个人中心", R.mipmap.ic_mine));

    }


    private void loadData() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //1.点击返回键条件成立
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            //2.点击的时间差如果大于2000，则提示用户点击两次退出
            if (System.currentTimeMillis() - mExitTime > 2000) {
                //3.保存当前时间
                mExitTime = System.currentTimeMillis();
                //4.提示
                ToastUtil.showToast(MainActivity.this, "再按一次退出程序");
            } else {
                //5.点击的时间差小于2000，退出。
                finish();
                System.exit(0);
            }
            return true;
        }
        return false;
    }
}
