package com.ifnoif.launcher.plugin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.ifnoif.launcher.LauncherApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by syh on 2016/12/1.
 */

public class WorkspaceParser {
    public static final String TAG = "WorkspaceParser";

    public static final String KEY_TYPE = "type";
    public static final String KEY_PACKAGE = "pkg";
    public static final String KEY_CLASSNAME = "class";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CELLX = "x";
    public static final String KEY_CELLY = "y";
    public static final String KEY_SCREEN = "screen";
    public static final String KEY_DATA = "data";

    public static final String TYPE_APP = "app";
    //    public static final String TYPE_SHORTCUT = "shortcut";
    public static final String TYPE_FOLDER = "folder";
    public HashMap<String, Parser> sParserMap = new HashMap<String, Parser>();
    int i = 0;
    private PackageManager mPackageManager;
    private Context mContext;

    public WorkspaceParser() {
        mContext = LauncherApplication.getApplication();
        mPackageManager = mContext.getPackageManager();
    }

    public ArrayList<CustomItemInfo> parseAll(JSONObject jsonObject) {
        Log.d(TAG, "parseAll start ==============");
        ArrayList<CustomItemInfo> result = null;
        try {
            JSONArray workspaceArray = jsonObject.getJSONArray(KEY_DATA);
            int count = workspaceArray.length();
            if (count > 0) {
                result = new ArrayList<CustomItemInfo>();
                JSONObject jObject;
                for (int i = 0; i < count; i++) {
                    jObject = workspaceArray.getJSONObject(i);
                    CustomItemInfo itemInfo = parseJSONObject(jObject);
                    if (itemInfo != null) {
                        Log.d(TAG, "parseAll itemInfo:" + itemInfo);
                        result.add(itemInfo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "parseAll end ==============");
        return result;
    }

    public CustomItemInfo parseJSONObject(JSONObject jObject) {
        if (jObject == null) {
            return null;
        }
        String type = jObject.optString(KEY_TYPE);
        Parser parser = getParser(type);
        if (parser != null) {
            CustomItemInfo itemInfo = (CustomItemInfo) parser.parse(jObject);
            return itemInfo;
        }
        return null;
    }

    public Parser getParser(String type) {
        if (TextUtils.isEmpty(type)) {
            return null;
        }
        Parser parser = sParserMap.get(type);
        if (parser != null) {
            return parser;
        }
        if (TYPE_APP.equals(type)) {
            parser = new AppParser();
//        } else if (TYPE_SHORTCUT.equals(type)) {
//            parser = new AppParser();
        } else if (TYPE_FOLDER.equals(type)) {
            parser = new FolderParser();
        }
        sParserMap.put(type, parser);
        return parser;
    }

    public void destroy() {
        sParserMap.clear();
    }

    public CustomApp getCustomApp(String title, ComponentName componentName, int cellX, int cellY) {
        CustomApp customApp = new CustomApp();
        customApp.title = title;
        customApp.componentName = componentName;
        customApp.cellX = cellX;
        customApp.cellY = cellY;

//        ResolveInfo info = mPackageManager.resolveActivity(intent, 0);
//        LauncherActivityInfoCompat launcherActivityInfoCompat = LauncherActivityInfoCompatV16.fromResolveInfo(info, mContext);
//        AppInfo appInfo = new AppInfo(mContext, launcherActivityInfoCompat, null, LauncherAppState.getInstance().getIconCache());
//        if (TextUtils.isEmpty(title)) {
//            appInfo.title = title;
//        }
//
//        ShortcutInfo shortcutInfo = new ShortcutInfo(appInfo);
        return customApp;
    }

    public CustomShortcut getShortcutInfo(String title, Intent intent, int cellX, int cellY) {
        CustomShortcut customShortcut = new CustomShortcut();
        customShortcut.title = title;
        customShortcut.intent = intent;
        customShortcut.cellX = cellX;
        customShortcut.cellY = cellY;

//        ResolveInfo info = mPackageManager.resolveActivity(intent, 0);
//        LauncherActivityInfoCompat launcherActivityInfoCompat = LauncherActivityInfoCompatV16.fromResolveInfo(info, mContext);
//        AppInfo appInfo = new AppInfo(mContext, launcherActivityInfoCompat, null, LauncherAppState.getInstance().getIconCache());
//        if (TextUtils.isEmpty(title)) {
//            appInfo.title = title;
//        }
//
//        ShortcutInfo shortcutInfo = new ShortcutInfo(appInfo);
        return customShortcut;
    }

    public CustomFolder getFolderInfo(String title, int cellX, int cellY, int screenIndex, List<CustomItemInfo> childList) {
        CustomFolder folderInfo = new CustomFolder();
        folderInfo.title = title;
        folderInfo.cellX = cellX;
        folderInfo.cellY = cellY;
        folderInfo.screenIndex = screenIndex;
        folderInfo.addChild(childList);
        return folderInfo;
    }

    public abstract static class Parser<T, V> {
        public abstract V parse(T arg);
    }

    public static abstract class CustomItemInfo {
        public int cellX = -1;//默认不限制位置
        public int cellY = -1;//默认不限制位置
        public int screenIndex = 0;//首屏
        public int spanX = 1;//占据的格子X
        public int spanY = 1;//占据的格子Y
        public String title;
    }

    public static class CustomFolder extends CustomItemInfo {
        public List<CustomItemInfo> childList = new ArrayList<CustomItemInfo>();

        public void addChild(List<CustomItemInfo> list) {
            if (list != null) {
                childList.addAll(list);
            }
        }

        public void addChild(CustomItemInfo childInfo) {
            if (childInfo != null) {
                childList.add(childInfo);
            }
        }
    }

    public static class CustomApp extends CustomItemInfo {
        public ComponentName componentName;
    }

    public static class CustomShortcut extends CustomItemInfo {
        public Intent intent;
    }

    public class FolderParser extends Parser<JSONObject, CustomItemInfo> {

        @Override
        public CustomItemInfo parse(JSONObject jsonObject) {
            CustomFolder folderInfo = null;
            try {
                folderInfo = getFolderInfo(jsonObject.optString(KEY_TITLE), jsonObject.optInt(KEY_CELLX), jsonObject.optInt(KEY_CELLY), jsonObject.optInt(KEY_SCREEN), null);

                ArrayList<CustomItemInfo> folderItems = parseAll(jsonObject);
                //delete invalid item
                for (int i = folderItems.size() - 1; i >= 0; i--) {
                    if (!(folderItems.get(i) instanceof CustomApp) || !(folderItems.get(i) instanceof CustomShortcut)) {
                        folderItems.remove(i);
                    }
                }
                folderInfo.addChild(folderItems);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return folderInfo;
        }
    }

    public class AppParser extends Parser<JSONObject, CustomItemInfo> {

        @Override
        public CustomItemInfo parse(JSONObject jsonObject) {
            try {
                final String packageName = jsonObject.optString(KEY_PACKAGE);
                final String className = jsonObject.optString(KEY_CLASSNAME);
                final int cellX = jsonObject.optInt(KEY_CELLX);
                final int cellY = jsonObject.optInt(KEY_CELLY);
                if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(className)) {
                    ActivityInfo info;
                    try {
                        ComponentName cn;
                        try {
                            cn = new ComponentName(packageName, className);
                            info = mPackageManager.getActivityInfo(cn, 0);
                        } catch (PackageManager.NameNotFoundException nnfe) {
                            String[] packages = mPackageManager.currentToCanonicalPackageNames(
                                    new String[]{packageName});
                            cn = new ComponentName(packages[0], className);
                            info = mPackageManager.getActivityInfo(cn, 0);
                        }
                        return getCustomApp(info.loadLabel(mPackageManager).toString(),
                                cn, cellX, cellY);
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e(TAG, "Unable to add favorite: " + packageName + "/" + className, e);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
