package com.syxgo.electrombile.util;

import android.content.Context;

/**
 * Created by tangchujia on 2017/8/17.
 */

public class DensityUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * px转sp
     *
     * @param pxVal
     * @return
     */

    public static float px2sp(Context context, float pxVal)

    {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);

    }
}
