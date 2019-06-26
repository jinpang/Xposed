package com.jp.allxposed;

import com.jp.allxposed.hook.IqiyiHook;
import com.jp.allxposed.hook.QQLiveHook;
import com.jp.allxposed.hook.SgameHook;
import com.jp.allxposed.hook.YoukuHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log(" packageName:" + loadPackageParam.packageName);
        //爱奇艺 IQiYi
        IqiyiHook.handleLoadPackage(loadPackageParam);
        //腾讯视频 QQLive
        QQLiveHook.handleLoadPackage(loadPackageParam);
        //优酷视频 YouKu
        YoukuHook.handleLoadPackage(loadPackageParam);
        //王者荣耀 开启高帧率插件
        SgameHook.handleLoadPackage(loadPackageParam);
    }
}