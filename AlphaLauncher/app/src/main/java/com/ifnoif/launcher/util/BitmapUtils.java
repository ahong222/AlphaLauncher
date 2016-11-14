package com.ifnoif.launcher.util;

import android.graphics.Bitmap;

/**
 * Created by syh on 2016/11/14.
 */
public class BitmapUtils {
    /**
     * 获取不包含透明区域的bitmap
     * @param bitmap
     * @return
     */
    public static Bitmap getNoTransparentBitmap(Bitmap bitmap) {
        int kipPix = 3;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int startX = 0;
        int startY = 0;
        int endX = 0;
        int endY = 0;
        for (int y = 0; y < height; y++) {
            boolean translate = true;
            for (int x = 0; x < width; x++) {
                if (x % kipPix != 0) {
                    continue;
                } else {
                    if (bitmap.getPixel(x, y) != 0) {
                        translate = false;
                    }
                }
            }
            if (translate) {
                startY = y;
            } else {
                break;
            }
        }
        for (int y = height - 1; y >= 0; y--) {
            boolean translate = true;
            for (int x = 0; x < width; x++) {
                if (x % kipPix != 0) {
                    continue;
                } else {
                    if (bitmap.getPixel(x, y) != 0) {
                        translate = false;
                    }
                }
            }
            if (translate) {
                endY = y;
            } else {
                break;
            }
        }

        for (int x = 0; x < width; x++) {
            boolean translate = true;
            for (int y = 0; y < height; y++) {
                if (y % kipPix != 0) {
                    continue;
                } else {
                    if (bitmap.getPixel(x, y) != 0) {
                        translate = false;
                    }
                }
            }
            if (translate) {
                startX = x;
            } else {
                break;
            }

        }
        for (int x = width - 1; x >= 0; x--) {
            boolean translate = true;
            for (int y = 0; y < height; y++) {
                if (y % kipPix != 0) {
                    continue;
                } else {
                    if (bitmap.getPixel(x, y) != 0) {
                        translate = false;
                    }
                }
            }
            if (translate) {
                endX = x;
            } else {
                break;
            }
        }
        if (endX == 0) endX = width;
        if (endY == 0) endY = height;

        try {
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, startX, startY, (endX - startX), (endY - startY));
            return newBitmap;
        } catch (Throwable e) {
            return bitmap;
        }
    }
}
