package com.jp.allxposed.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class IqiyiHook {
    public static final String PACKAGE_NAME = "com.qiyi.video";

    public static void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        //iqiyi
        if (loadPackageParam.packageName.equals(PACKAGE_NAME)) {
            XposedHelpers.findAndHookMethod("org.qiyi.android.coreplayer.b.aux", loadPackageParam.classLoader, "isLogin", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(true);
                    XposedBridge.log("isLogin 请注意:" + param.getResult());
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    param.setResult(true);
                    XposedBridge.log("isLogin 请注意:" + param.getResult());
                }
            });
            XposedHelpers.findAndHookMethod("org.qiyi.android.coreplayer.b.aux", loadPackageParam.classLoader, "isSilverVip", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("isSilverVip 请注意:" + param.getResult());
                    param.setResult(true);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("isSilverVip 请注意:" + param.getResult());
                    param.setResult(true);
                    XposedBridge.log("isSilverVip 请注意:" + param.getResult());
                }
            });
            XposedHelpers.findAndHookMethod("org.qiyi.android.coreplayer.b.aux", loadPackageParam.classLoader, "isTaiwanVip", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("isTaiwanVip 请注意:" + param.getResult());
                    //param.setResult(true);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("isTaiwanVip 请注意:" + param.getResult());
                    //param.setResult(true);
                    XposedBridge.log("isTaiwanVip 请注意:" + param.getResult());
                }
            });
            XposedHelpers.findAndHookMethod("org.qiyi.android.coreplayer.b.aux", loadPackageParam.classLoader, "isVip", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("isVip 请注意:" + param.getResult());
                    param.setResult(true);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("isVip 请注意:" + param.getResult());
                    param.setResult(true);
                    XposedBridge.log("isVip 请注意:" + param.getResult());
                }
            });
        }
    }
}
