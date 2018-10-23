package com.syxgo.electrombile.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.util.UriPrefix;

import java.nio.charset.Charset;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by tangchujia on 2017/11/16.
 */

public class WriteNFCActivity extends BaseActivity {
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private EditText mBikeEt;
    private TextView mPkgnameEt;
    private TextView mUriEt;
    private TextView mLoadEt;
    private CheckBox mIsReadOnlyCb;
    private static boolean mIsChecked = false;
    private String pkgname = "com.syxgo.motor";
    private String uri = "https://www.syxgo.com";
    private String load = "http://a.app.qq.com/o/simple.jsp?pkgname=com.syxgo.motor";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_write);
        initTop();
        initView();
    }

    /**
     * 启动Activity，界面可见时
     */
    @Override
    protected void onStart() {
        super.onStart();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //一旦截获NFC消息，就会通过PendingIntent调用窗口
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
    }

    /**
     * 获得焦点，按钮可以点击
     */
    @Override
    public void onResume() {
        super.onResume();
        //设置处理优于所有其他NFC的处理
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    /**
     * 暂停Activity，界面获取焦点，按钮可以点击
     */
    @Override
    public void onPause() {
        super.onPause();
        //恢复默认状态
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String bikeID = mBikeEt.getText().toString().trim();
//        String pkgName = mPkgnameEt.getText().toString().trim();
//        String load = mLoadEt.getText().toString().trim();
//        String uri = mUriEt.getText().toString().trim();

        if (bikeID == null)
            return;
        //获取Tag对象
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefMessage ndefMessage = new NdefMessage(
                new NdefRecord[]{createTextRecord(bikeID), NdefRecord.createApplicationRecord(pkgname), createUriRecord(load), createUriRecord(uri)});
        boolean result = writeTag(ndefMessage, detectedTag);
        if (result) {
            Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "写入失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        int bikeid = getIntent().getIntExtra("bike_id", -1);
        mTitletv.setText("NFC写入");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mBikeEt = (EditText) findViewById(R.id.bike_id_tv);
        mPkgnameEt = (TextView) findViewById(R.id.pkgname_tv);
        mLoadEt = (TextView) findViewById(R.id.load_tv);
        mUriEt = (TextView) findViewById(R.id.uri_tv);
        mIsReadOnlyCb = (CheckBox) findViewById(R.id.is_readonly_cb);

        mPkgnameEt.setText("包名：" + pkgname);
        mUriEt.setText("网址：" + uri);
        mLoadEt.setText("下载地址：" + load);
        if (bikeid != -1) {
            String str = String.format("%06d", bikeid);
            mBikeEt.setText(str);
        }
        mIsReadOnlyCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mIsChecked = isChecked;
                String str = "";
                if (isChecked) {
                    str = "写入后将不可修改";
                } else {
                    str = "信息可改写";

                }
                Toast.makeText(WriteNFCActivity.this, str, Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * 创建NDEF文本数据
     *
     * @param text
     * @return
     */
    public static NdefRecord createTextRecord(String text) {
        byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = Charset.forName("UTF-8");
        //将文本转换为UTF-8格式
        byte[] textBytes = text.getBytes(utfEncoding);
        //设置状态字节编码最高位数为0
        int utfBit = 0;
        //定义状态字节
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        //设置第一个状态字节，先将状态码转换成字节
        data[0] = (byte) status;
        //设置语言编码，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1到langBytes.length的位置
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        //设置文本字节，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1 + langBytes.length
        //到textBytes.length的位置
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        //通过字节传入NdefRecord对象
        //NdefRecord.RTD_TEXT：传入类型 读写
        NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return ndefRecord;
    }

    /**
     * 将Uri转成NdefRecord
     *
     * @param uriStr
     * @return
     */
    public static NdefRecord createUriRecord(String uriStr) {
        byte prefix = 0;
        for (Byte b : UriPrefix.URI_PREFIX_MAP.keySet()) {
            String prefixStr = UriPrefix.URI_PREFIX_MAP.get(b).toLowerCase();
            if ("".equals(prefixStr))
                continue;
            if (uriStr.toLowerCase().startsWith(prefixStr)) {
                prefix = b;
                uriStr = uriStr.substring(prefixStr.length());
                break;
            }
        }
        byte[] data = new byte[1 + uriStr.length()];
        data[0] = prefix;
        System.arraycopy(uriStr.getBytes(), 0, data, 1, uriStr.length());
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], data);
        return record;
    }

    /**
     * 写数据
     *
     * @param ndefMessage 创建好的NDEF文本数据
     * @param tag         标签
     * @return
     */
    public boolean writeTag(NdefMessage ndefMessage, Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            if (!ndef.isWritable()) {
                Toast.makeText(WriteNFCActivity.this, "标签已被锁定，不能改写！", Toast.LENGTH_SHORT).show();
            }
            ndef.connect();
            ndef.writeNdefMessage(ndefMessage);
            if (mIsChecked) {
                ndef.makeReadOnly();

            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
