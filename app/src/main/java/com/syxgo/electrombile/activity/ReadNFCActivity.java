package com.syxgo.electrombile.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.util.UriPrefix;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by tangchujia on 2017/11/16.
 */

public class ReadNFCActivity extends BaseActivity {
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private TextView mNFCInfo;
    private String mTagText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_read);
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

    private void initView() {
        mTitletv.setText("NFC读取");
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mNFCInfo = (TextView) findViewById(R.id.tv_nfctext);

    }

    @Override
    public void onNewIntent(Intent intent) {
        //1.获取Tag对象
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //2.获取Ndef的实例
        Ndef ndef = Ndef.get(detectedTag);
        mTagText = ndef.getType() + "\n";
        readNfcTag(intent);
        mNFCInfo.setText(mTagText);
    }

    /**
     * 读取NFC标签文本数据
     */
    private void readNfcTag(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msgs[] = null;
            int contentSize = 0;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    contentSize += msgs[i].toByteArray().length;
                }
            }
            try {
                if (msgs != null) {
                    NdefRecord record1 = msgs[0].getRecords()[0];
                    NdefRecord record2 = msgs[0].getRecords()[1];
                    NdefRecord record3 = msgs[0].getRecords()[2];
                    NdefRecord record4 = msgs[0].getRecords()[3];
                    Uri uri1 = parse(record3);
                    Uri uri2 = parse(record4);
                    String textRecord1 = parseTextRecord(record1);
//                    String textRecord2 = parseTextRecord(record2);
//                    String textRecord3 = uri1.toString();
//                    String textRecord4 = uri2.toString();
                    String pkgname = "com.syxgo.motor";
                    String uri = "https://www.syxgo.com";
                    String load = "http://a.app.qq.com/o/simple.jsp?pkgname=com.syxgo.motor";
                    mTagText += "1.车辆编号：\n  " + textRecord1 + "\n\n" + "2.包名：\n   " + pkgname + "\n\n" + "3.下载地址：\n   " + load + "\n\n" + "4.网址：\n    " + uri;
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 解析NDEF文本数据，从第三个字节开始，后面的文本数据
     *
     * @param ndefRecord
     * @return
     */
    public static String parseTextRecord(NdefRecord ndefRecord) {
        /**
         * 判断数据是否为NDEF格式
         */
        //判断TNF
        if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
            return null;
        }
        //判断可变的长度的类型
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
            return null;
        }
        try {
            //获得字节数组，然后进行分析
            byte[] payload = ndefRecord.getPayload();
            //下面开始NDEF文本数据第一个字节，状态字节
            //判断文本是基于UTF-8还是UTF-16的，取第一个字节"位与"上16进制的80，16进制的80也就是最高位是1，
            //其他位都是0，所以进行"位与"运算后就会保留最高位
            String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
            //3f最高两位是0，第六位是1，所以进行"位与"运算后获得第六位
            int languageCodeLength = payload[0] & 0x3f;
            //下面开始NDEF文本数据第二个字节，语言编码
            //获得语言编码
            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            //下面开始NDEF文本数据后面的字节，解析出文本
            String textRecord = new String(payload, languageCodeLength + 1,
                    payload.length - languageCodeLength - 1, textEncoding);
            return textRecord;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 解析NdefRecord中Uri数据
     *
     * @param record
     * @return
     */
    public static Uri parse(NdefRecord record) {
        short tnf = record.getTnf();
        if (tnf == NdefRecord.TNF_WELL_KNOWN) {
            return parseWellKnown(record);
        } else if (tnf == NdefRecord.TNF_ABSOLUTE_URI) {
            return parseAbsolute(record);
        }
        throw new IllegalArgumentException("Unknown TNF " + tnf);
    }

    /**
     * 处理绝对的Uri
     * 没有Uri识别码，也就是没有Uri前缀，存储的全部是字符串
     *
     * @param ndefRecord 描述NDEF信息的一个信息段，一个NdefMessage可能包含一个或者多个NdefRecord
     * @return
     */
    private static Uri parseAbsolute(NdefRecord ndefRecord) {
        //获取所有的字节数据
        byte[] payload = ndefRecord.getPayload();
        Uri uri = Uri.parse(new String(payload, Charset.forName("UTF-8")));
        return uri;
    }

    /**
     * 处理已知类型的Uri
     *
     * @param ndefRecord
     * @return
     */
    private static Uri parseWellKnown(NdefRecord ndefRecord) {
        //判断数据是否是Uri类型的
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_URI))
            return null;
        //获取所有的字节数据
        byte[] payload = ndefRecord.getPayload();
        String prefix = UriPrefix.URI_PREFIX_MAP.get(payload[0]);
        byte[] prefixBytes = prefix.getBytes(Charset.forName("UTF-8"));
        byte[] fullUri = new byte[prefixBytes.length + payload.length - 1];
        System.arraycopy(prefixBytes, 0, fullUri, 0, prefixBytes.length);
        System.arraycopy(payload, 1, fullUri, prefixBytes.length, payload.length - 1);
        Uri uri = Uri.parse(new String(fullUri, Charset.forName("UTF-8")));
        return uri;
    }
}
