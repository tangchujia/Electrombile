package com.syxgo.electrombile.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.http.okhttp.utils.LogUtil;
import com.syxgo.electrombile.manager.ClientManager;
import com.syxgo.electrombile.model.ECU;
import com.syxgo.electrombile.model.EcuDetail;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.NetUtil;
import com.syxgo.electrombile.util.StringUtil;
import com.syxgo.electrombile.util.ToastUtil;
import com.syxgo.electrombile.view.LoadingDialog;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.R.id.message;
import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static java.security.CryptoPrimitive.MAC;

/**
 * Created by tangchujia on 2018/3/20.
 */

public class SearchECUActivity extends BaseActivity implements View.OnClickListener {
    private static final int ECU_ID = 0x09;
    private static final int ECU_INFO = 0x10;
    private EditText mEcuIdTv;
    private EditText mEcuInfoTv;
    private int mEcuID = 0;
    private String mEcuToken = "";
    private Dialog progDialog = null;
    private TextView mResultTv;
    public static String Service_uuid = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid_TX = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private BluetoothClient mClient;
    private boolean mConnected;
    private String MAC = "";
    private String cmd = "";
    private TextView mBleTv;
    private String ID = "ID";
    private String TOKEN = "TOKEN";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ecu);
        initBle();
        initTop();
        initView();

    }

    private void initBle() {
        mClient = ClientManager.getClient();

        if (!mClient.isBluetoothOpened()) {
            ToastUtil.showToast(SearchECUActivity.this, "蓝牙未打开");
            mClient.openBluetooth();
        }

        mClient.registerBluetoothStateListener(mBluetoothStateListener);
        mClient.registerBluetoothBondListener(mBluetoothBondListener);


    }

    /**
     * 监听设备配对状态变化
     */
    private final BluetoothBondListener mBluetoothBondListener = new BluetoothBondListener() {
        @Override
        public void onBondStateChanged(String mac, int bondState) {

        }
    };
    /**
     * 监听蓝牙状态
     * true：蓝牙打开
     * falset：蓝牙关闭
     */
    private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            Toast.makeText(SearchECUActivity.this, "蓝牙状态：" + openOrClosed, Toast.LENGTH_SHORT).show();
        }
    };

    private void connectDeviceIfNeeded() {
        if (!mConnected) {
            connect();
        }
    }

    /**
     * 写Characteristic
     */
    private void writeCharacteristic() {
        int sendTimes = cmd.length() / 20;
        for (int i = 0; i < sendTimes; i++) {
            final String s = cmd.substring(i * 20, (i + 1) * 20);
            mClient.write(MAC, UUID.fromString(Service_uuid), UUID.fromString(Characteristic_uuid_TX), s.getBytes(), new BleWriteResponse() {
                @Override
                public void onResponse(int code) {
                    if (code == REQUEST_SUCCESS) {
                        LogUtil.e("蓝牙发送数据" + s);
                    }
                }
            });
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        final String s = cmd.substring(sendTimes * 20, cmd.length());
        mClient.write(MAC, UUID.fromString(Service_uuid), UUID.fromString(Characteristic_uuid_TX), s.getBytes(), new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    LogUtil.e("蓝牙发送数据：" + s);
                }
            }
        });
    }

    /**
     * 连接
     */
    private void connect() {
        mClient.registerConnectStatusListener(MAC, mConnectStatusListener);

        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)   // 连接如果失败重试3次
                .setConnectTimeout(30000)   // 连接超时30s
                .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
                .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
                .build();
        mClient.connect(MAC, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {
                //BleGattProfile括了所有的service和characteristic的uuid
                if (code == REQUEST_SUCCESS) {
                    LogUtil.d("connect蓝牙连接状态：" + ClientManager.getClient().getConnectStatus(MAC));
                    mConnected = true;
                    String status = "蓝牙状态：" + "<font color=\"#7CD514\">" + "成功" + "</font>";
                    CharSequence charSequence;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        charSequence = Html.fromHtml(status, Html.FROM_HTML_MODE_LEGACY);
                    } else {
                        charSequence = Html.fromHtml(status);
                    }
                    mBleTv.setText(charSequence);
//                    connectDeviceIfNeeded();
                    if (mConnected) {
                        disconnectBle();
                        LogUtil.d("onDestroy蓝牙连接状态：" + mClient.getConnectStatus(MAC));
                    }
                } else {
                    String status = "蓝牙状态：" + "<font color=\"#ED4B28\">" + "失败" + "</font>";
                    CharSequence charSequence;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        charSequence = Html.fromHtml(status, Html.FROM_HTML_MODE_LEGACY);
                    } else {
                        charSequence = Html.fromHtml(status);
                    }
                    mBleTv.setText(charSequence);
                    LogUtil.d("connect蓝牙连接失败");

                }
            }
        });
    }

    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            BluetoothLog.w(String.format("Task onConnectStatusChanged %d in %s",
                    status, Thread.currentThread().getName()));

            mConnected = (status == STATUS_CONNECTED);
            if (!mConnected) {
                mClient.registerConnectStatusListener(MAC, mConnectStatusListener);
                LogUtil.e("Task 蓝牙重连");
                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                reconnect();
            } else {
                String s = "蓝牙状态：" + "<font color=\"#7CD514\">" + "成功" + "</font>";
                CharSequence charSequence;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    charSequence = Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY);
                } else {
                    charSequence = Html.fromHtml(s);
                }
                mBleTv.setText(charSequence);
                disconnectBle();

            }
        }
    };

    private void disconnectBle() {
        mClient.disconnect(MAC);
        mClient.unregisterConnectStatusListener(MAC, mConnectStatusListener);
        mClient.unregisterBluetoothStateListener(mBluetoothStateListener);
        mClient.unregisterBluetoothBondListener(mBluetoothBondListener);
        if (mClient.getConnectStatus(MAC) != REQUEST_SUCCESS) {
            ToastUtil.showToast(this, "蓝牙已断开");

        }

    }

    private void reconnect() {
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)   // 连接如果失败重试3次
                .setConnectTimeout(30000)   // 连接超时30s
                .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
                .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
                .build();
        mClient.connect(MAC, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {
                //BleGattProfile括了所有的service和characteristic的uuid
                if (code == REQUEST_SUCCESS) {
                    LogUtil.d("reconnect蓝牙连接状态：" + mClient.getConnectStatus(MAC));
                    mConnected = true;
//                    mClient.registerConnectStatusListener(MAC, mConnectStatusListener);
                    String s = "蓝牙状态：" + "<font color=\"#7CD514\">" + "成功" + "</font>";
                    CharSequence charSequence;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        charSequence = Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY);
                    } else {
                        charSequence = Html.fromHtml(s);
                    }
                    mBleTv.setText(charSequence);
                    if (mConnected) {
                        disconnectBle();
                        LogUtil.d("onDestroy蓝牙连接状态：" + mClient.getConnectStatus(MAC));
                    }
                } else {
                    LogUtil.d("reconnect蓝牙连接失败");
                    String status = "蓝牙状态：" + "<font color=\"#ED4B28\">" + "失败" + "</font>";
                    CharSequence charSequence;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        charSequence = Html.fromHtml(status, Html.FROM_HTML_MODE_LEGACY);
                    } else {
                        charSequence = Html.fromHtml(status);
                    }
                    mBleTv.setText(charSequence);
                }
            }
        });
    }

    private void initView() {
        mEcuIdTv = (EditText) findViewById(R.id.ecu_id_tv);
        mEcuInfoTv = (EditText) findViewById(R.id.ecu_info_tv);
        findViewById(R.id.scan_ecu_id_btn).setOnClickListener(this);
        findViewById(R.id.scan_ecu_token_btn).setOnClickListener(this);
        findViewById(R.id.search_id_btn).setOnClickListener(this);
        findViewById(R.id.search_token_btn).setOnClickListener(this);

        mTitletv.setText("查询ECU");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mResultTv = (TextView) findViewById(R.id.result_tv);
        mBleTv = (TextView) findViewById(R.id.ble_tv);

    }

    @Override
    public void onClick(View view) {
        Intent openCameraIntent = null;
        switch (view.getId()) {
            case R.id.scan_ecu_id_btn:
                openCameraIntent = new Intent(SearchECUActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, ECU_ID);
                break;
            case R.id.scan_ecu_token_btn:
                openCameraIntent = new Intent(SearchECUActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, ECU_INFO);
                break;
            case R.id.search_id_btn:
                try {
                    search(ID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.search_token_btn:
                try {
                    search(TOKEN);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void search(String s) {
        NetUtil.checkNetwork(this);

        String url = "";
        if (s.equals(ID)) {
            if (mEcuIdTv.getText().toString().equals("")) {
                ToastUtil.showToast(this, "请扫描或输入ecu id");
                return;
            } else {
                mEcuID = Integer.parseInt(mEcuIdTv.getText().toString().trim());
                url = HttpUrl.SEARCH_ECU + "?ecu_id=" + mEcuID;
            }
        } else if (s.equals(TOKEN)) {
            if (mEcuToken.equals("")) {
                ToastUtil.showToast(this, "请扫描或输入ecu info");
                return;
            } else {
                url = HttpUrl.SEARCH_ECU + "?ecu_token=" + mEcuToken;
            }
        }
        if (url.equals("")) {
            return;
        } else {
            showProgressDialog("正在查询ECU...");

            NetRequest
                    .get()
                    .url(url)
                    .addHeader("Authorization:Bear", MyPreference.getInstance(SearchECUActivity.this).getToken())
                    .build()
                    .connTimeOut(10 * 1000)
                    .readTimeOut(10 * 1000)
                    .execute(new NetResponseListener() {
                        @Override
                        public void onSuccess(NetResponse netResponse) {
                            String result = netResponse.getResult().toString();
                            try {
                                int status = new org.json.JSONObject(result).getInt("status");
                                if (status == 200) {
                                    mEcuIdTv.setText("");
                                    mEcuInfoTv.setText("");
                                    mEcuID = 0;
                                    mEcuToken = "";
                                    String str = new org.json.JSONObject(result).getString("ecu");
                                    EcuDetail ecu1 = JSONObject.parseObject(str, EcuDetail.class);
                                    int index;
                                    StringBuffer s1 = new StringBuffer(ecu1.getBle_mac());
                                    for (index = 2; index < s1.length(); index += 3) {
                                        s1.insert(index, ':');
                                    }
                                    MAC = s1.toString();
                                    mBleTv.setVisibility(View.VISIBLE);
                                    connectDeviceIfNeeded();
                                    showText(ecu1);
                                } else {
                                    ToastUtil.showToast(SearchECUActivity.this, "查询失败");
                                    mResultTv.setVisibility(View.VISIBLE);
                                    mResultTv.setText("查询失败");
                                    String message = new org.json.JSONObject(result).getString("message");
                                }
                            } catch (Exception e) {
                                mResultTv.setVisibility(View.VISIBLE);
                                mResultTv.setText("查询失败");
                                ToastUtil.showToast(SearchECUActivity.this, "查询失败");
                                e.printStackTrace();
                            }
                            dissmissProgressDialog();
                        }

                        @Override
                        public void onFailed(NetResponse netResponse) {
                            mResultTv.setVisibility(View.VISIBLE);
                            mResultTv.setText("查询失败");
                            ToastUtil.showToast(SearchECUActivity.this, "查询失败");
                            dissmissProgressDialog();
                        }
                    });
        }
    }

    private void showText(EcuDetail ecu) {
//        boolean isbike = false;
        boolean iscount = false;
        boolean isgps = false;
        boolean isecu = false;
        boolean islevel = false;
        String bike = "";
        if (ecu.getBike() == null) {
            bike = "<font color=\"#ED4B28\">" + "绑定失败" + "</font>" + "<br />";
            islevel = true;
//            isbike = false;
        } else {
//            isbike = true;
            int level = ecu.getBike().getBackup_battery_level();
            String levelStr = "";
            if (level <= 100 && level > 0) {
                islevel = true;
                levelStr = "<font color=\"#7CD514\">" + level + "%" + "</font>";
            } else if (level <= 0) {
                islevel = false;
                levelStr = "<font color=\"#ED4B28\">" + level + "</font>";
            } else if (level < 128 && level > 100) {
                islevel = false;
                levelStr = "<font color=\"#ED4B28\">" + level + "</font>";
            } else if (level >= 128 && level <= 253) {
                islevel = true;
                levelStr = "<font color=\"#7CD514\">" + (2250 + (level - 128) * 16) + "mv" + "</font>";
            } else if (level > 253) {
                islevel = false;
                levelStr = "<font color=\"#ED4B28\">" + level + "</font>";
            }
            bike = "<font color=\"#7CD514\">" + "绑定成功" + "</font>" + "<br />" + "车辆编号：" + ecu.getBike().getId() + "<br />" + "备用电池电量：" + levelStr + "<br />";
        }
        String searchStr = "ecu id：" + ecu.getId() + "<br />"
                + "ble_mac：" + ecu.getBle_mac() + "<br />"
//                + "ecu_uuid：" + ecu.getEcu_uuid() + "<br />"
                + "token：" + ecu.getToken() + "<br />";
        String gpstime = "";
        String ecutime = "";
        String gpsStr = "";
        String gpsDura = "";
        String ecuDura = "";
        String rssiStr = "";
        int rssi = 0;
        try {
            int gps = ecu.getLocation().getGps_location().getSatel_count();
            gpstime = ecu.getLocation().getTime();
            ecutime = ecu.getEcu().getTime();
            rssi = ecu.getEcu().getEcu_info().getGprs_rssi();
            if (rssi <= 15) {
                rssiStr = "gprs信号强度：" + "<font color=\"#ED4B28\">" + "弱（" + rssi + "）" + "</font>" + "<br />";
            } else {
                if (rssi <= 25) {
                    rssiStr = "gprs信号强度：" + "<font color=\"#FD9137\">" + "中（" + rssi + "）" + "</font>" + "<br />";
                } else {
                    rssiStr = "gprs信号强度：" + "<font color=\"#7CD514\">" + "强（" + rssi + "）" + "</font>" + "<br />";
                }
            }
            if (gps > 8) {
                iscount = true;
                gpsStr = "卫星数量：" + "<font color=\"#7CD514\">" + "强（" + gps + "）" + "</font>" + "<br />";
            } else if (gps <= 4) {
                iscount = false;
                gpsStr = "卫星数量：" + "<font color=\"#ED4B28\">" + "弱（" + gps + "）" + "</font>" + "<br />";
            } else {
                iscount = true;
                gpsStr = "卫星数量：" + "<font color=\"#FD9137\">" + "中（" + gps + "</font>" + "）" + "<br />";
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            java.util.Date now = new Date();
            Date gpsdate = null;
            Date ecudate = null;
            gpsdate = df.parse(gpstime);
            ecudate = df.parse(ecutime);
            long gpsl = now.getTime() - gpsdate.getTime();
            long ecul = now.getTime() - ecudate.getTime();
            long gpsday = gpsl / (24 * 60 * 60 * 1000);
            long ecuday = ecul / (24 * 60 * 60 * 1000);
            long gpshour = (gpsl / (60 * 60 * 1000) - gpsday * 24);
            long ecuhour = (ecul / (60 * 60 * 1000) - ecuday * 24);
            long gpsmin = ((gpsl / (60 * 1000)) - gpsday * 24 * 60 - gpshour * 60);
            long ecumin = ((ecul / (60 * 1000)) - ecuday * 24 * 60 - ecuhour * 60);
            long gpss = (gpsl / 1000 - gpsday * 24 * 60 * 60 - gpshour * 60 * 60 - gpsmin * 60);
            long ecus = (ecul / 1000 - ecuday * 24 * 60 * 60 - ecuhour * 60 * 60 - ecumin * 60);
            String gtime = gpsday + "天" + gpshour + ":" + gpsmin;
            String etime = ecuday + "天" + ecuhour + ":" + ecumin;
            gpstime = gpstime.replace("T", " ");
            ecutime = ecutime.replace("T", " ");
            if (gpsl > (5 * 60 * 1000)) {
                isgps = false;
                gpsDura = "GPS最近一次上报时间：" + "<font color=\"#ED4B28\">" + gpstime + "<br />" + "间隔：" + gtime + "</font>" + "<br />";
            } else {
                isgps = true;
                gpsDura = "GPS最近一次上报时间：" + "<font color=\"#7CD514\">" + gpstime + "<br />" + "间隔：" + gtime + "</font>" + "<br />";
            }
            if (gpsl > (5 * 60 * 1000)) {
                isecu = false;
                ecuDura = "ECU最近一次上报时间：" + "<font color=\"#ED4B28\">" + ecutime + "<br />" + "间隔：" + etime + "</font>" + "<br />";
            } else {
                isecu = true;
                ecuDura = "ECU最近一次上报时间：" + "<font color=\"#7CD514\">" + ecutime + "<br />" + "间隔：" + etime + "</font>" + "<br />";
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        String result = "";
        if (iscount && isgps && isecu && islevel && (rssi > 15)) {
            result = "<font color=\"#7CD514\">" + "<big>" + "通过" + "</big></font>" + "<br />" + "<br />";
        } else {
            result = "<font color=\"#ED4B28\">" + "<big>" + "不通过" + "</big></font>" + "<br />" + "<br />";
        }
        final String content = result + bike + searchStr + rssiStr + gpsStr + gpsDura + ecuDura;
        CharSequence charSequence;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            charSequence = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY);
        } else {
            charSequence = Html.fromHtml(content);
        }
        mResultTv.setText(charSequence);
        mResultTv.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            try {
                if (requestCode == ECU_ID) {
                    int id = Integer.parseInt(scanResult);
                    mEcuIdTv.setText(id + "");
                } else if (requestCode == ECU_INFO) {
                    mEcuToken = new org.json.JSONObject(scanResult).getString("TOKEN");
                    String uuid = new org.json.JSONObject(scanResult).getString("UUID");
                    mEcuInfoTv.setText("UUID：" + uuid + "/" + "TOKEN：" + mEcuToken);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToast(SearchECUActivity.this, "二维码信息不对应");
            }
        }
    }

    private void ecuRpc(int ecuid, String action) {
        showProgressDialog("正在绑定...");
        Map<String, Object> params = new HashMap<>();
        params.put("ecu_id", ecuid);
        params.put("action", action);

        NetRequest
                .post()
                .url(HttpUrl.ECU_RPC)
                .addHeader("Authorization:Bear", MyPreference.getInstance(SearchECUActivity.this).getToken())
                .jsonObject(params)
                .build()
                .connTimeOut(10 * 1000)
                .readTimeOut(10 * 1000)
                .execute(new NetResponseListener() {
                    @Override
                    public void onSuccess(NetResponse netResponse) {
                        String result = netResponse.getResult().toString();
                        try {
                            int status = new org.json.JSONObject(result).getInt("status");
                            if (status == 200) {
                                org.json.JSONArray jsonArray = new org.json.JSONObject(result).getJSONArray("ecus");
                            } else {
                                String message = new org.json.JSONObject(result).getString("message");
                                ToastUtil.showToast(SearchECUActivity.this, message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dissmissProgressDialog();
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        dissmissProgressDialog();
                    }
                });
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new LoadingDialog().createLoadingDialog(SearchECUActivity.this, message);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
            progDialog = null;
        }
    }
}
