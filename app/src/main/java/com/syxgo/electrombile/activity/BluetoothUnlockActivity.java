package com.syxgo.electrombile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.http.HttpUrl;
import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.http.okhttp.utils.LogUtil;
import com.syxgo.electrombile.manager.ClientManager;
import com.syxgo.electrombile.util.MyPreference;
import com.syxgo.electrombile.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;

/**
 * Created by tangchujia on 2017/7/26.
 */

public class BluetoothUnlockActivity extends BaseActivity {
    private static final int SCAN_BIKE = 0x07;

    private BluetoothClient mClient;
    private String MAC = "";
    private String cmd = "";
    private TextView mResultTv;
    private List<String> macList = new ArrayList<>();
    private String str = "";
    public static String Service_uuid = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid_TX = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private boolean mConnected;
    private boolean isnotify;
    //    private int mBikeID = 3;
    private EditText mBikeIdEt;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_unlock);
        initTop();
        initView();
        initBle();
    }

    private void getRpc(String bikeid) {
        Map<String, Object> params = new HashMap<>();
        params.put("bike_id", Integer.parseInt(bikeid));
        params.put("action", "unlock_backseat");
        NetRequest
                .post()
                .url(HttpUrl.GET_RPC)
                .addHeader("Authorization:Bear", MyPreference.getInstance(BluetoothUnlockActivity.this).getToken())
                .jsonObject(params)
                .build()
                .connTimeOut(10 * 1000)
                .readTimeOut(10 * 1000)
                .execute(new NetResponseListener() {
                    @Override
                    public void onSuccess(NetResponse netResponse) {
                        String result = netResponse.getResult().toString();
                        try {
                            int status = new JSONObject(result).getInt("status");
                            if (status == 200) {
                                JSONObject json = new JSONObject(result).getJSONObject("bike");
                                String mac = json.getString("bluetooth");
                                StringBuffer s1 = new StringBuffer(mac);
                                int index;
                                for (index = 2; index < s1.length(); index += 3) {
                                    s1.insert(index, ':');
                                }
                                MAC = s1.toString();
                                cmd = json.getString("cmd");
                                connectDeviceIfNeeded();
                            } else {
                                ToastUtil.showToast(BluetoothUnlockActivity.this, netResponse.getResult().toString());
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(BluetoothUnlockActivity.this, "开锁失败");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(NetResponse netResponse) {
                        ToastUtil.showToast(BluetoothUnlockActivity.this, "开锁失败");
                    }
                });
    }

    private void initView() {
        mTitletv.setText("开坐垫锁");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mResultTv = (TextView) findViewById(R.id.search_result_tv);
        mBikeIdEt = (EditText) findViewById(R.id.bike_no_et);
        findViewById(R.id.unlock_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bikeid = mBikeIdEt.getText().toString().trim();
                if (bikeid == null || bikeid.equals("")) {
                    ToastUtil.showToast(BluetoothUnlockActivity.this, "请输入车辆编号");
                    return;
                }
                getRpc(bikeid);

            }
        });
    }

    private void initBle() {
        mClient = ClientManager.getClient();
        LogUtil.d("initBle蓝牙连接状态：" + mClient.getConnectStatus(MAC));

        if (!mClient.isBluetoothOpened()) {
            ToastUtil.showToast(BluetoothUnlockActivity.this, "蓝牙未打开");
            mClient.openBluetooth();
        }

        mClient.registerBluetoothStateListener(mBluetoothStateListener);
        mClient.registerBluetoothBondListener(mBluetoothBondListener);
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
            }
        }
    };

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
                    mClient.registerConnectStatusListener(MAC, mConnectStatusListener);
                } else {
                    LogUtil.d("reconnect蓝牙连接失败");

                }
            }
        });
    }

    private void connectDeviceIfNeeded() {
        if (!mConnected) {
            connect();
        } else {
            writeCharacteristic();
        }
    }

    /**
     * 监听蓝牙状态
     * true：蓝牙打开
     * falset：蓝牙关闭
     */
    private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            Toast.makeText(BluetoothUnlockActivity.this, "蓝牙状态：" + openOrClosed, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 监听设备配对状态变化
     */
    private final BluetoothBondListener mBluetoothBondListener = new BluetoothBondListener() {
        @Override
        public void onBondStateChanged(String mac, int bondState) {

        }
    };


    /**
     * 连接
     */
    private void connect() {
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
                    LogUtil.d("connect蓝牙连接状态：" + mClient.getConnectStatus(MAC));
                    mConnected = true;
                    mClient.registerConnectStatusListener(MAC, mConnectStatusListener);
                    connectDeviceIfNeeded();
                } else {
                    LogUtil.d("connect蓝牙连接失败");

                }
            }
        });
    }

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
     * 读Characteristic
     *
     * @param mac
     * @param serviceUUID
     * @param characterUUID
     */
    private void readCharacteristic(String mac, UUID serviceUUID, UUID characterUUID) {
        mClient.read(mac, serviceUUID, characterUUID, new BleReadResponse() {
            @Override
            public void onResponse(int code, byte[] data) {
                if (code == REQUEST_SUCCESS) {
                    mResultTv.setText(String.format("readCharacteristic: %s", ByteUtils.byteToString(data)));
                    ToastUtil.showToast(BluetoothUnlockActivity.this, "success");
                } else {
                    ToastUtil.showToast(BluetoothUnlockActivity.this, "failed");
                }
            }
        });
    }

    /**
     * 写Characteristic
     *
     * @param mac
     * @param serviceUUID
     * @param characterUUID
     * @param bytes
     */
    private void writeCharacteristic(String mac, UUID serviceUUID, UUID characterUUID, byte[] bytes) {
        mClient.write(mac, serviceUUID, characterUUID, bytes, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    LogUtil.d("写入成功");
                }
            }
        });
    }


    /**
     * 读Descriptor
     *
     * @param mac
     * @param serviceUUID
     * @param characterUUID
     * @param descriptorUUID
     */
    private void readDescriptor(String mac, UUID serviceUUID, UUID characterUUID, UUID descriptorUUID) {
        mClient.readDescriptor(mac, serviceUUID, characterUUID, descriptorUUID, new BleReadResponse() {
            @Override
            public void onResponse(int code, byte[] data) {

            }
        });
    }

    /**
     * 打开设备通知
     *
     * @param mac
     * @param serviceUUID
     * @param characterUUID
     */
    private void setNotify(String mac, UUID serviceUUID, UUID characterUUID) {
        mClient.notify(mac, serviceUUID, characterUUID, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                LogUtil.d(String.format("notify:%s", ByteUtils.byteToString(value)));
                mResultTv.setText(String.format("notify:%s", ByteUtils.byteToString(value)));
            }

            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    LogUtil.d("打开通知");

                }
            }
        });

    }

    /**
     * 关闭通知
     *
     * @param mac
     * @param serviceUUID
     * @param characterUUID
     */
    private void unNotify(String mac, UUID serviceUUID, UUID characterUUID) {
        mClient.unnotify(mac, serviceUUID, characterUUID, new BleUnnotifyResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {

                }
            }
        });
    }

    /**
     * 写Descriptor
     *
     * @param mac
     * @param serviceUUID
     * @param characterUUID
     * @param descriptorUUID
     * @param bytes
     */
    private void writeDescriptor(String mac, UUID serviceUUID, UUID characterUUID, UUID descriptorUUID, byte[] bytes) {
        mClient.writeDescriptor(mac, serviceUUID, characterUUID, descriptorUUID, bytes, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    LogUtil.d("writeDescriptor成功");
                }
            }
        });
    }

//    public void setGattProfile(BleGattProfile profile) {
//
//        List<BleGattService> services = profile.getServices();
//
//        for (BleGattService service : services) {
//            mDataList.add(new DetailItem(DetailItem.TYPE_SERVICE, service.getUUID(), null));
//            List<BleGattCharacter> characters = service.getCharacters();
//            for (BleGattCharacter character : characters) {
//                mDataList.add(new DetailItem(DetailItem.TYPE_CHARACTER, character.getUuid(), service.getUUID()));
//            }
//        }
//
//    }

    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        byte[] byteArray = str.getBytes();
        return byteArray;
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnected) {
            mClient.disconnect(MAC);
//            unNotify(MAC, UUID.fromString(Service_uuid), UUID.fromString(Characteristic_uuid_TX));
            mClient.unregisterConnectStatusListener(MAC, mConnectStatusListener);
            mClient.unregisterBluetoothStateListener(mBluetoothStateListener);
            mClient.unregisterBluetoothBondListener(mBluetoothBondListener);
            LogUtil.d("onDestroy蓝牙连接状态：" + mClient.getConnectStatus(MAC));
        }

    }

}
