package com.ifnoif.launcher;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class used to help set lockscreen wallpapers on N+.
 */
public class NycWallpaperUtils {

    public static int FLAG_SYSTEM = 0x00000001;
    public static int FLAG_LOCK = 0x00000002;
    /**
     * Calls cropTask.execute(), once the user has selected which wallpaper to set. On pre-N
     * devices, the prompt is not displayed since there is no API to set the lockscreen wallpaper.
     */
    public static void executeCropTaskAfterPrompt(
            Context context, final AsyncTask<Integer, ?, ?> cropTask,
            DialogInterface.OnCancelListener onCancelListener) {
        if (Utilities.ATLEAST_N) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.wallpaper_instructions)
                    .setItems(R.array.which_wallpaper_options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedItemIndex) {
                            int whichWallpaper;
                            if (selectedItemIndex == 0) {
                                whichWallpaper = FLAG_SYSTEM;
                            } else if (selectedItemIndex == 1) {
                                whichWallpaper = FLAG_LOCK;
                            } else {
                                whichWallpaper = FLAG_SYSTEM
                                        | FLAG_LOCK;
                            }
                            cropTask.execute(whichWallpaper);
                        }
                    })
                    .setOnCancelListener(onCancelListener)
                    .show();
        } else {
            cropTask.execute(FLAG_SYSTEM);
        }
    }

    public static void setStream(Context context, final InputStream data, Rect visibleCropHint,
            boolean allowBackup, int whichWallpaper) throws IOException {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        if (Utilities.ATLEAST_N) {
            wallpaperManager.setStream(data);
//            TODO FIXME
//            wallpaperManager.setStream(data, visibleCropHint, allowBackup, whichWallpaper);
        } else {
            // Fall back to previous implementation (set system)
            wallpaperManager.setStream(data);
        }
    }

    public static void clear(Context context, int whichWallpaper) throws IOException {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        if (Utilities.ATLEAST_N) {
            wallpaperManager.clear();
//            TODO FIXME
//            wallpaperManager.clear(whichWallpaper);
        } else {
            // Fall back to previous implementation (clear system)
            wallpaperManager.clear();
        }
    }
}