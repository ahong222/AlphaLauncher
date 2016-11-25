package com.ifnoif.launcher.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by syh on 2016/11/24.
 */

public class DeviceAndOSUtils {
    public static int sOSVersion = Build.VERSION.SDK_INT;
    private static int sStatusBarHeight = 0;
    private static int sNavBarHeight = -1;
    private static int sCurrentNavBarHeight = -1;

    public static void setNaviAndStatusBackground(Activity activity) {
        int flags =
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        if (Build.VERSION.SDK_INT == 19 || Build.VERSION.SDK_INT == 20) {
            flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        } else if (Build.VERSION.SDK_INT >= 21) {
            flags |= WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
        }
        activity.getWindow().addFlags(flags);
    }

    public static float dp2px(Context context, int dp) {
        return context.getResources().getDisplayMetrics().density * dp;
    }

    public static int getStatusBarHeight(Context context) {
        if (sStatusBarHeight <= 0) {
            int statusHeight = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (statusHeight > 0) {
                sStatusBarHeight = context.getResources().getDimensionPixelSize(statusHeight);
            }
        }
        if (sStatusBarHeight <= 0) {
            sStatusBarHeight = (int) dp2px(context, 48);
        }
        return sStatusBarHeight;

    }

    public static boolean checkDeviceHasNavigationBar(Context activity) {
        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
        boolean hasMenuKey = ViewConfiguration.get(activity)
                .hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
                .deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            // 做任何你需要做的,这个设备有一个导航栏
            return true;
        }
        return false;
    }

    public static int getCurrentNavBarHeight(Context context) {
        if (sCurrentNavBarHeight < 0) {
            boolean hasNav = checkDeviceHasNavigationBar(context);
            if (hasNav) {
                sCurrentNavBarHeight = getStaticNavBarHeight(context);
            } else {
                sCurrentNavBarHeight = 0;
            }
        }
        return sCurrentNavBarHeight;
    }

    public static int getStaticNavBarHeight(Context context) {
        if (sNavBarHeight < 0) {
            int navigationHeight = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (navigationHeight > 0) {
                sNavBarHeight = context.getResources().getDimensionPixelSize(navigationHeight);
            } else {
                sNavBarHeight = 0;
            }

        }
        return sNavBarHeight;
    }
}
