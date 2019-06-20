package com.jp.xposedtest;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookTest implements IXposedHookLoadPackage {

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        if (loadPackageParam.packageName.equals("com.jp.xposedtest")) {

            XposedBridge.log(" has Hooked!");

            Class clazz = loadPackageParam.classLoader.loadClass(

                    "com.jp.xposedtest.MainActivity");

            XposedHelpers.findAndHookMethod(clazz, "toastMessage", new XC_MethodHook() {

                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    super.beforeHookedMethod(param);

                    //XposedBridge.log(" has Hooked!");

                }

                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    param.setResult("你已被劫持");

                }

            });

        }
        if (loadPackageParam.packageName.equals("com.yunzhou.funlive.develop")) {

            XposedBridge.log(" has Hooked!");

            Class clazz = loadPackageParam.classLoader.loadClass(

                    "com.yunzhou.funlive.module.main.mainfragmentlive.fragment.HomeFragment");

            XposedHelpers.findAndHookMethod(clazz, "getMarquee", new XC_MethodHook() {

                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    super.beforeHookedMethod(param);

                    //XposedBridge.log(" has Hooked!");

                }

                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    param.setResult("请注意，你已被劫持");

                }

            });

        }
    }

}