package com.jp.xposedtest;

import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;
import android.os.Environment;

import com.jp.xposedtest.utils.Common;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;

public class XInternalSD implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    boolean detectedSdPath = false;
    public XC_MethodHook externalSdCardAccessHook;
    public XC_MethodHook externalSdCardAccessHook2;
    public XC_MethodHook getExternalFilesDirHook;
    public XC_MethodHook getExternalFilesDirsHook;
    public XC_MethodHook getExternalStorageDirectoryHook;
    public XC_MethodHook getExternalStoragePublicDirectoryHook;
    public XC_MethodHook getObbDirHook;
    public XC_MethodHook getObbDirsHook;
    public String internalSd;
    public XSharedPreferences prefs;

    public void initZygote(StartupParam startupParam) throws Throwable {
        this.prefs = new XSharedPreferences(XInternalSD.class.getPackage().getName());
        this.prefs.makeWorldReadable();
        this.getExternalStorageDirectoryHook = new XC_MethodHook() {
            /* access modifiers changed from: protected */
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                XInternalSD.this.changeDirPath(param);
            }
        };
        this.getExternalFilesDirHook = new XC_MethodHook() {
            /* access modifiers changed from: protected */
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                XInternalSD.this.changeDirPath(param);
            }
        };
        this.getObbDirHook = new XC_MethodHook() {
            /* access modifiers changed from: protected */
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                XInternalSD.this.changeDirPath(param);
            }
        };
        this.getExternalStoragePublicDirectoryHook = new XC_MethodHook() {
            /* access modifiers changed from: protected */
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                XInternalSD.this.changeDirPath(param);
            }
        };
        this.getExternalFilesDirsHook = new XC_MethodHook() {
            /* access modifiers changed from: protected */
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                XInternalSD.this.changeDirsPath(param);
            }
        };
        this.getObbDirsHook = new XC_MethodHook() {
            /* access modifiers changed from: protected */
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                XInternalSD.this.changeDirsPath(param);
            }
        };
        this.externalSdCardAccessHook = new XC_MethodHook() {
            /* access modifiers changed from: protected */
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                XInternalSD.this.prefs.reload();
                String permission = (String) param.args[1];
                if (XInternalSD.this.prefs.getBoolean("external_sdcard_full_access", true)) {
                    if (Common.PERM_WRITE_EXTERNAL_STORAGE.equals(permission) || Common.PERM_ACCESS_ALL_EXTERNAL_STORAGE.equals(permission)) {
                        int gid = ((Integer) XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.os.Process", null), "getGidForName", new Object[]{"media_rw"})).intValue();
                        Object permissions = null;
                        if (VERSION.SDK_INT >= 21) {
                            permissions = XposedHelpers.getObjectField(param.thisObject, "mPermissions");
                        } else if (VERSION.SDK_INT == 19) {
                            permissions = XposedHelpers.getObjectField(XposedHelpers.getObjectField(param.thisObject, "mSettings"), "mPermissions");
                        }
                        Object bp = XposedHelpers.callMethod(permissions, "get", new Object[]{permission});
                        XposedHelpers.setObjectField(bp, "gids", XInternalSD.this.appendInt((int[]) XposedHelpers.getObjectField(bp, "gids"), gid));
                    }
                }
            }
        };
        this.externalSdCardAccessHook2 = new XC_MethodHook() {
            /* access modifiers changed from: protected */
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                XInternalSD.this.prefs.reload();
                if (XInternalSD.this.prefs.getBoolean("external_sdcard_full_access", true)) {
                    Object ps = XposedHelpers.callMethod(XposedHelpers.getObjectField(param.args[0], "mExtras"), "getPermissionsState", new Object[0]);
                    Object permissions = XposedHelpers.getObjectField(XposedHelpers.getObjectField(param.thisObject, "mSettings"), "mPermissions");
                    if (!((Boolean) XposedHelpers.callMethod(ps, "hasInstallPermission", new Object[]{Common.PERM_WRITE_MEDIA_STORAGE})).booleanValue()) {
                        XposedHelpers.callMethod(ps, "grantInstallPermission", new Object[]{XposedHelpers.callMethod(permissions, "get", new Object[]{Common.PERM_WRITE_MEDIA_STORAGE})});
                    }
                }
            }
        };
    }

    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if ("android".equals(lpparam.packageName) && "android".equals(lpparam.processName)) {
            if (VERSION.SDK_INT >= 23) {
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.android.server.pm.PackageManagerService", lpparam.classLoader), "grantPermissionsLPw", new Object[]{Common.CLASS_PACKAGE_PARSER_PACKAGE, Boolean.TYPE, String.class, this.externalSdCardAccessHook2});
            } else if (VERSION.SDK_INT == 21 || VERSION.SDK_INT == 22) {
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.android.server.SystemConfig", lpparam.classLoader), "readPermission", new Object[]{XmlPullParser.class, String.class, this.externalSdCardAccessHook});
            } else if (VERSION.SDK_INT == 19) {
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.android.server.pm.PackageManagerService", lpparam.classLoader), "readPermission", new Object[]{XmlPullParser.class, String.class, this.externalSdCardAccessHook});
            }
        }
        if (!this.detectedSdPath) {
            try {
                this.internalSd = Environment.getExternalStorageDirectory().getPath();
                this.detectedSdPath = true;
            } catch (Exception e) {
            }
        }
        if (isEnabledApp(lpparam)) {
            XposedHelpers.findAndHookMethod(Environment.class, "getExternalStorageDirectory", new Object[]{this.getExternalStorageDirectoryHook});
            XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader), "getExternalFilesDir", new Object[]{String.class, this.getExternalFilesDirHook});
            XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader), "getObbDir", new Object[]{this.getObbDirHook});
            XposedHelpers.findAndHookMethod(Environment.class, "getExternalStoragePublicDirectory", new Object[]{String.class, this.getExternalStoragePublicDirectoryHook});
            if (VERSION.SDK_INT >= 19) {
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader), "getExternalFilesDirs", new Object[]{String.class, this.getExternalFilesDirsHook});
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader), "getObbDirs", new Object[]{this.getObbDirsHook});
            }
        }
    }

    public boolean isEnabledApp(LoadPackageParam lpparam) {
        boolean isEnabledApp = true;
        this.prefs.reload();
        if (!this.prefs.getBoolean("custom_internal_sd", true) || !isAllowedApp(lpparam.appInfo)) {
            return false;
        }
        String packageName = lpparam.packageName;
        if (this.prefs.getBoolean("enable_for_all_apps", false)) {
            Set<String> disabledApps = this.prefs.getStringSet("disable_for_apps", new HashSet());
            if (!disabledApps.isEmpty()) {
                isEnabledApp = !disabledApps.contains(packageName);
            }
        } else {
            Set<String> enabledApps = this.prefs.getStringSet("enable_for_apps", new HashSet());
            isEnabledApp = !enabledApps.isEmpty() ? enabledApps.contains(packageName) : 1 == 0;
        }
        return isEnabledApp;
    }

    public void changeDirPath(MethodHookParam param) {
        File oldDirPath = (File) param.getResult();
        if (oldDirPath != null) {
            String customInternalSd = getCustomInternalSd();
            if (!customInternalSd.isEmpty()) {
                String internalSd2 = getInternalSd();
                if (!internalSd2.isEmpty()) {
                    File newDirPath = new File(Common.appendFileSeparator(oldDirPath.getPath()).replaceFirst(internalSd2, customInternalSd));
                    if (!newDirPath.exists()) {
                        newDirPath.mkdirs();
                    }
                    param.setResult(newDirPath);
                }
            }
        }
    }

    public void changeDirsPath(MethodHookParam param) {
        File[] arr$;
        File[] oldDirPaths = (File[]) param.getResult();
        ArrayList<File> newDirPaths = new ArrayList<>();
        for (File oldDirPath : oldDirPaths) {
            if (oldDirPath != null) {
                newDirPaths.add(oldDirPath);
            }
        }
        String customInternalSd = getCustomInternalSd();
        if (!customInternalSd.isEmpty()) {
            String internalSd2 = getInternalSd();
            if (!internalSd2.isEmpty()) {
                File newDirPath = new File(Common.appendFileSeparator(oldDirPaths[0].getPath()).replaceFirst(internalSd2, customInternalSd));
                if (!newDirPaths.contains(newDirPath)) {
                    newDirPaths.add(newDirPath);
                }
                if (!newDirPath.exists()) {
                    newDirPath.mkdirs();
                }
                param.setResult((File[]) newDirPaths.toArray(new File[newDirPaths.size()]));
            }
        }
    }

    public String getCustomInternalSd() {
        this.prefs.reload();
        return Common.appendFileSeparator(this.prefs.getString("internal_sdcard_path", getInternalSd()));
    }

    public String getInternalSd() {
        this.internalSd = Common.appendFileSeparator(this.internalSd);
        return this.internalSd;
    }

    public boolean isAllowedApp(ApplicationInfo appInfo) {
        this.prefs.reload();
        boolean includeSystemApps = this.prefs.getBoolean("include_system_apps", false);
        if (appInfo == null) {
            return includeSystemApps;
        }
        if ((appInfo.flags & 1) != 0 && !includeSystemApps) {
            return false;
        }
        if (Arrays.asList(Common.MTP_APPS).contains(appInfo.packageName)) {
            return false;
        }
        return true;
    }

    public int[] appendInt(int[] cur, int val) {
        /*if (cur == null) {
            return new int[]{val};
        }
        for (int i : cur) {
            if (i == val) {
                return cur;
            }
        }
        int[] ret = new int[(N + 1)];
        System.arraycopy(cur, 0, ret, 0, N);
        ret[N] = val;
        return ret;*/
        return null;
    }
}