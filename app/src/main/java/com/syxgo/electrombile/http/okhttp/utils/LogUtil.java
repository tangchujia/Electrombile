package com.syxgo.electrombile.http.okhttp.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tangchujia on 2017/7/20.
 */

public class LogUtil {
    static String className;//类名
    static String methodName;//方法名
    static int lineNumber;//行数

    /**
     * 单个日志文件大小，单位MB
     */
    private static int m_ilogFileSize = 5;

    private static String m_strSDAdress = "";

    /**
     * 默认java的文件后缀名
     */
    public final static String STR_DEFAULT_JAVA_FILE_EXT = ".java";

    /**
     * 日志文件名
     */
    public final static String STR_LOG_FILE_NAME = "wimoLog.txt";

    private LogUtil() {
        /* Protect from instantiations */
    }

    public static void init() {
        m_strSDAdress = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "wimo" + File.separator;
    }

    public static boolean isDebuggable() {
        return true;
    }

    private static String createLog(String log) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(methodName);
        buffer.append("(").append(className).append(":").append(lineNumber).append(")");
        buffer.append(log);
        return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }


    public static void e(String message) {
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.e(className, createLog(message));
    }


    public static void i(String message) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.i(className, createLog(message));
    }

    public static void d(String message) {
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.d(className, createLog(message));
    }

    public static void v(String message) {
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.v(className, createLog(message));
    }

    public static void w(String message) {
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.w(className, createLog(message));
    }

    public static String writeI(String message) {
        String i = formatLogString("I", message);
        try {
            saveLogToLocalFileOriginal(i);
            return i;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return i;
    }

    public static String writeW(String message) {
        String i = formatLogString("W", message);
        try {
            saveLogToLocalFileOriginal(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return i;
    }

    public static void writeE(String message) {
        try {
            String i = formatLogString("E", message);
            saveLogToLocalFileOriginal(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void saveLogToLocalFileOriginal(String strLogInfo) throws IOException {

        if (TextUtils.isEmpty(strLogInfo)) {
            return;
        }

        int iReturn = 0;
        // 日志创建时间
        String strLogFileCreateTime = null;
        Date dateLogFileCreate = null;
        // Calendar类的日志创建时间及当前时间
        Calendar calOldLogFileCreateTime = null;
        Calendar calNewLogFileCreateTime = null;

        // 日志文件、备份文件
        File fileLog = null;
        // 文件输出流
        FileOutputStream outputStream = null;
        // 读文件
        BufferedReader bufReader = null;

        long lDaysBetween = 0;
        fileLog = new File(m_strSDAdress + STR_LOG_FILE_NAME);

        iReturn = FileUtil.append2File(fileLog,
                DateUtil.getCurrentDay(DateUtil.P13) + "\n");
        if (FileUtil.FILE_UTIL_STATUS_FILE_EXIST == iReturn) {
            // 日志文件存在时，检查文件大小,超过5M则删除文件，重新创建
            if ((fileLog.length() / (1024.0 * 1024.0)) > m_ilogFileSize) {
//                fileLog.renameTo(new File(m_strSDAdress + STR_BAK_LOG_FILE_NAME));

                fileLog = new File(m_strSDAdress + STR_LOG_FILE_NAME);

                FileUtil.appendContent2File(fileLog,
                        DateUtil.getCurrentDay(DateUtil.P13) + "\n");

            }
            // 检查日志存在时间，超过7天则备份文件，删除上一次的备份文件（如果存在的话）。
            FileReader fileReader = new FileReader(m_strSDAdress + STR_LOG_FILE_NAME);
            bufReader = new BufferedReader(fileReader);
            strLogFileCreateTime = bufReader.readLine();
            if (null == strLogFileCreateTime) {
                bufReader.close();
                fileReader.close();
                return;
            }

            SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

            try {
                dateLogFileCreate = sdfDateFormat.parse(strLogFileCreateTime);
            } catch (ParseException e) {
                e.printStackTrace();
                bufReader.close();
                fileReader.close();
                return;
            }

            calOldLogFileCreateTime = Calendar.getInstance();
            calOldLogFileCreateTime.set(dateLogFileCreate.getYear() + 1900,
                    dateLogFileCreate.getMonth(), dateLogFileCreate.getDate(),
                    dateLogFileCreate.getHours(), dateLogFileCreate.getMinutes(),
                    dateLogFileCreate.getSeconds());

            bufReader.close();
            fileReader.close();
            calNewLogFileCreateTime = Calendar.getInstance();
            while (calOldLogFileCreateTime.before(calNewLogFileCreateTime)
                    && lDaysBetween <= 7) {
                calOldLogFileCreateTime.add(Calendar.DAY_OF_MONTH, 1);
                lDaysBetween++;
            }
            if (lDaysBetween > 7) {
                fileLog = new File(m_strSDAdress + STR_LOG_FILE_NAME);

                FileUtil.appendContent2File(fileLog,
                        DateUtil.getCurrentDay(DateUtil.P13) + "\n");

            }
        }

        // 创建文件输出流对象
        outputStream = new FileOutputStream(fileLog, true);
        // 写日志到文件中
        outputStream.write((strLogInfo + "\n").getBytes("utf-8"));
        // 关闭文件流
        outputStream.close();
    }


    private static String formatLogString(String strLevel,
                                          String strInfo) {
        if (null == strInfo) {
            return null;
        }

        String strReturnInfo = null;

        strReturnInfo = String.format("[%s][%s]  %s", strLevel,
                dateToString(Calendar.getInstance().getTime()), strInfo);
        return strReturnInfo;
        // return String.format("%23s", TimeUtil.getFormattedLocalTimeStr());

    }

    public static String dateToString(Date time) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ctime = formatter.format(time);
        return ctime;
    }

}
