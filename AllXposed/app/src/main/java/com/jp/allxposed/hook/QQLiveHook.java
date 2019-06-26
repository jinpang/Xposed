package com.jp.allxposed.hook;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class QQLiveHook {
    public static final String PACKAGE_NAME = "com.tencent.qqlive";

    public static void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals(PACKAGE_NAME)) {

        }
    }
}
