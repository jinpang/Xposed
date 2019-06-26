package com.jp.allxposed.hook;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class YoukuHook {
    public static final String PACKAGE_NAME = "com.youku.phone";

    public static void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals(PACKAGE_NAME)) {
        }
    }
}
