package com.syxgo.electrombile.manager;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by tangchujia on 2017/8/15.
 */

public class ActivityManager {
    private static final String TAG = "ScreenManager";
    private static Stack<Activity> activityStack;

    private static ActivityManager instance;

    /**
     * <单例方法>
     * <功能详细描述>
     * @return 该对象的实例
     * @see [类、类#方法、类#成员]
     */
    public static ActivityManager getScreenManager()
    {
        if (instance == null)
        {
            instance = new ActivityManager();
        }
        return instance;
    }

    /**
     * <获取当前栈顶Activity>
     * <功能详细描述>
     * @return
     * @see [类、类#方法、类#成员]
     */
    public Activity currentActivity()
    {
        if (activityStack == null || activityStack.size() == 0)
        {
            return null;
        }
        Activity activity = activityStack.lastElement();

        return activity;
    }

    /**
     * <将Activity入栈>
     * <功能详细描述>
     * @param activity
     * @see [类、类#方法、类#成员]
     */
    public void pushActivity(Activity activity)
    {
        if (activityStack == null)
        {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * <退出栈顶Activity>
     * <功能详细描述>
     * @param activity
     * @see [类、类#方法、类#成员]
     */
    public void popActivity(Activity activity)
    {
        if (activity != null)
        {
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * <退出栈中所有Activity,>
     * <功能详细描述>
     * @see [类、类#方法、类#成员]
     */
    public void popAllActivity()
    {
        for (Activity activity : activityStack) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
