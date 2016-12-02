package com.ifnoif.launcher.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by syh on 2016/12/2.
 */

public class DebugUtils {
    public static void copyDBToSDCard(Context context, String dbName) {
        try {
            File file = context.getDatabasePath(dbName);
            File outFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/launcher_" + dbName);
            outFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(outFile);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer =new byte[2048];
            int count = 0;
            while ((count = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer,0,count);
            }
            fileInputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
