package com.jp.allxposed.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DingPassHook {
    public static final String PACKAGE_NAME = "com.alibaba.android.rimet";

    public static void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals(PACKAGE_NAME)) {
            XposedHelpers.findAndHookMethod("defpackage.dck", loadPackageParam.classLoader, "a", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("defpackage a:" + param.args[0] + "," + param.args[1]);
                }

            });
            XposedHelpers.findAndHookMethod("defpackage.dck", loadPackageParam.classLoader, "a", String.class, String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("defpackage a:" + param.args[0] + "," + param.args[1] + "," + param.args[2]);
                }

            });
            XposedHelpers.findAndHookMethod("defpackage.dck", loadPackageParam.classLoader, "c", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("defpackage c:" + param.args[0] + "," + param.args[1]);
                }

            });
            XposedHelpers.findAndHookMethod("defpackage.dck", loadPackageParam.classLoader, "b", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("defpackage b:" + param.args[0] + "," + param.args[1]);
                }

            });
        }
    }
}
