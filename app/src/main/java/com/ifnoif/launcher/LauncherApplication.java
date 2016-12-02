package com.ifnoif.launcher;

import android.app.Application;
import android.content.Context;

/**
 * Created by syh on 2016/12/1.
 */

public class LauncherApplication extends Application {
    private static LauncherApplication instance;

    public static LauncherApplication getApplication() {
        return instance;
    }

    public static Context getContext() {
        if (instance != null) {
            return instance.getApplicationContext();
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }

}
