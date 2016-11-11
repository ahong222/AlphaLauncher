package com.ifnoif.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver {

    static final String SYSTEM_READY = "com.ifnoif.launcher.SYSTEM_READY";

    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendStickyBroadcast(new Intent(SYSTEM_READY));
    }
}
