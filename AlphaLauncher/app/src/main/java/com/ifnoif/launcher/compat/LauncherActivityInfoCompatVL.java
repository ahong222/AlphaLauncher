/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ifnoif.launcher.compat;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import com.ifnoif.launcher.util.BitmapUtils;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class LauncherActivityInfoCompatVL extends LauncherActivityInfoCompat {
    private LauncherActivityInfo mLauncherActivityInfo;

    LauncherActivityInfoCompatVL(PackageManager pm, LauncherActivityInfo launcherActivityInfo) {
        super(pm);
        mLauncherActivityInfo = launcherActivityInfo;
    }

    public ComponentName getComponentName() {
        return mLauncherActivityInfo.getComponentName();
    }

    public UserHandleCompat getUser() {
        return UserHandleCompat.fromUser(mLauncherActivityInfo.getUser());
    }

    public CharSequence getLabel() {
        return mLauncherActivityInfo.getLabel();
    }

    public Drawable getIcon(int density) {
        Drawable drawable = null;
        try {
            PackageManager packageManager = getPackageManager();
            drawable = packageManager.getActivityIcon(mLauncherActivityInfo.getComponentName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (drawable == null) {
            drawable = mLauncherActivityInfo.getIcon(density);
        }

        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap newBitmap = BitmapUtils.getNoTransparentBitmap(bitmap);
            return new BitmapDrawable(newBitmap);
        }
        return drawable;
    }

    public ApplicationInfo getApplicationInfo() {
        return mLauncherActivityInfo.getApplicationInfo();
    }

    public long getFirstInstallTime() {
        return mLauncherActivityInfo.getFirstInstallTime();
    }
}
