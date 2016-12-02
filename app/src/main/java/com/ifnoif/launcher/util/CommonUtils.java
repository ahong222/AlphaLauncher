package com.ifnoif.launcher.util;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by syh on 2016/12/2.
 */

public class CommonUtils {
    public static String getResContent(Context context, int id) {
        try {
            InputStream inputStream = context.getResources().openRawResource(id);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int count = 0;
            byte[] buffer = new byte[2048];
            while ((count = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, count);
            }
            inputStream.close();
            String result = byteArrayOutputStream.toString("utf-8");
            byteArrayOutputStream.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
