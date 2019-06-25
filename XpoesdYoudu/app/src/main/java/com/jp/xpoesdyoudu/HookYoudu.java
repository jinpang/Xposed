package com.jp.xpoesdyoudu;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookYoudu implements IXposedHookLoadPackage {
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log(" packageName:" + loadPackageParam.packageName);
        if (loadPackageParam.packageName.equals("im.xinda.youdu")) {
            XSharedPreferences xSharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, SharedPreferenceUtils.FILE_NAME);
            boolean isOpen = xSharedPreferences.getBoolean("is_open", false);
            XposedBridge.log(" has Hooked! is_open:" + isOpen);
            if (isOpen) {
                XposedHelpers.findAndHookMethod("android.webkit.WebView", loadPackageParam.classLoader, "loadUrl", String.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("WebView 请注意，你将被劫持:" + param.args[0]);
                        String jsString = (String) param.args[0];
                        if (jsString.startsWith("javascript:onGetWiFiBSSID")) {
                            param.args[0] = "javascript:onGetWiFiBSSID('{\"wifiInfo\":{\"bssid\":\"fc:2f:ef:6b:23:d8\",\"ssid\":\"冰禾科技5G\"}}')";
                        }
                        if (jsString.startsWith("javascript:onLocationChanged")) {
                            final long time = System.currentTimeMillis();
                            param.args[0] = "javascript:onLocationChanged({\"address\":\"广东省深圳市南山区科发路2009号靠近尚美科技大厦\",\"city\":\"深圳市\",\"cityCode\":\"0755\",\"latitude\":22.543577,\"typeName\":\"Wifi定位\",\"streetNum\":\"2009号\",\"accuracy\":29.0,\"type\":5,\"province\":\"广东省\",\"street\":\"科发路\",\"district\":\"南山区\",\"adCode\":\"440305\",\"longitude\":113.95884,\"timestamp\":" + time + "})";
                        }
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("WebView 请注意，你已被劫持:" + param.args[0]);
                    }
                });
            }
        }
    }
}