package com.jp.xposedtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jp.xposedtest.bean.IndexItemBean;
import com.jp.xposedtest.touch.GestureTouchUtils;
import com.jp.xposedtest.utils.JSoupUtil;
import com.jp.xposedtest.utils.ReflectionUtil;
import com.jp.xposedtest.utils.Util;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class HookTest implements IXposedHookLoadPackage {
    XSharedPreferences xSharedPreferences;
    ListView listView;

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log(" packageName:" + loadPackageParam.packageName);
        //首页com.uc.browser.InnerUCMobile
        if (loadPackageParam.packageName.equals("com.UCMobile")) {
            XposedBridge.log(" has Hooked!");
            if (xSharedPreferences == null) {
                xSharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, SharedPreferenceUtils.FILE_NAME);
            }
            TextView view;
            boolean isOpen = xSharedPreferences.getBoolean("is_open", true);
            if (isOpen) {
                //com.uc.browser.InnerUCMobile
                Class clazz = XposedHelpers.findClassIfExists("com.uc.browser.InnerUCMobile", loadPackageParam.classLoader);
                if (clazz != null) {
                    XposedBridge.log("InnerUCMobile:"  +clazz.getName());
                    XposedHelpers.findAndHookMethod(clazz, "onAttachedToWindow", new XC_MethodHook() {
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                        }

                        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                            XposedBridge.log("InnerUCMobile onClick:" + param.thisObject);
                            Util.printTreeView(((Activity) param.thisObject).findViewById(R.id.content));
                            super.afterHookedMethod(param);
                        }
                    });
                }else {
                    XposedBridge.log("com.uc.application.infoflow.widget.o.c not found");
                }
                XposedHelpers.findAndHookConstructor("com.uc.framework.ui.widget.ListViewEx", loadPackageParam.classLoader, Context.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("InfoFlowListViewEx 请注意，你将被劫持:" + param.thisObject);
                        /*ReflectionUtil.printConstructors(param.thisObject.getClass(), true);
                        ReflectionUtil.printMethods(param.thisObject.getClass(), true);
                        ReflectionUtil.printFields(param.thisObject.getClass(), true);*/
                    }

                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        if (listView == null) {
                            listView = ((ListView) param.thisObject);
                            final int position = xSharedPreferences.getInt("index", 1);
                            listView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ListAdapter listAdapter = listView.getAdapter();
                                    if (listAdapter != null) {
//                            listView.performItemClick(listView.getChildAt(position), position, listView.getItemIdAtPosition(position));
                                        int count = listAdapter.getCount();
                                        if (position < count) {
                                            /*if (listView.getContext() instanceof Activity) {
                                                XposedBridge.log("InfoFlowListViewEx 滑动");
                                                Activity activity = (Activity) listView.getContext();
                                                GestureTouchUtils.simulateScroll(activity.findViewById(R.id.content), 30, 0, 30, 1000);
                                            }*/
                                            /*listView.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listView.performItemClick(listView.getChildAt(position), position, listView.getItemIdAtPosition(position));
                                                }
                                            }, 5000);*/
                                            Object o = listAdapter.getItem(position);
                                            Gson gson = new Gson();
                                            String json = gson.toJson(o);
                                            XposedBridge.log("InfoFlowListViewEx list count:" + count + ", item:" + o + ", json:" + json + ", position:" + position);
                                            sendBroadcast(listView.getContext(), XposedReceiver.ACTION_ITEM, "item", json);
                                            for (int i = 0; i < count; i++) {
                                                Object o2 = listAdapter.getItem(i);
                                                String str = "";
                                                if (o2 != null) {
                                                    String os = gson.toJson(o2);
                                                    IndexItemBean ucIndexItemBean = gson.fromJson(os, IndexItemBean.class);
                                                    if (ucIndexItemBean != null) {
                                                        str = ucIndexItemBean.toString();
                                                    } else {
                                                        str = "" + os;
                                                    }
                                                }
                                                XposedBridge.log("InfoFlowListViewEx: position:" + i + ", " + str);
                                            }
                                        } else {
                                            listView.smoothScrollToPosition(count);
                                            XposedBridge.log("InfoFlowListViewEx list count2:" + count + ", position:" + position);
                                        }
                                    } else {
                                        XposedBridge.log("InfoFlowListViewEx listAdapter is null");
                                    }
                                }
                            }, 15000);
                        }
                    }
                });

                XposedHelpers.findAndHookMethod("com.uc.framework.ui.widget.ListViewEx", loadPackageParam.classLoader, "performItemClick", View.class, int.class, long.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("beforeHookedMethod performItemClick 请注意，你将被劫持:" + param.args);
                        /*ReflectionUtil.printConstructors(param.thisObject.getClass(), true);
                        ReflectionUtil.printMethods(param.thisObject.getClass(), true);
                        ReflectionUtil.printFields(param.thisObject.getClass(), true);
                        ReflectionUtil.printConstructors(param.args[0].getClass(), true);
                        ReflectionUtil.printMethods(param.args[0].getClass(), true);
                        ReflectionUtil.printFields(param.args[0].getClass(), true);*/
                        String arg = "";
                        CharSequence title = "";
                        final int position = (int) param.args[1];
                        final ListView listView = ((ListView) param.thisObject);
                        ListAdapter listAdapter = listView.getAdapter();
                        if (listAdapter != null) {
                            int count = listAdapter.getCount();
                            Object o = listAdapter.getItem(position);
                            if (o != null) {
                                Method method = o.getClass().getMethod("getTitle", new Class<?>[]{});
                                title = (String) method.invoke(o, new Object[]{});
                                XposedBridge.log("beforeHookedMethod!:title2:" + title);
                            }
                            Gson gson = new Gson();
                            String json = gson.toJson(o);
                            sendBroadcast(listView.getContext(), XposedReceiver.ACTION_ITEM, "item", json);
                            XposedBridge.log("beforeHookedMethod!:list count:" + count + ", item:" + o + ", json:" + json);
                            IndexItemBean ucIndexItemBean = gson.fromJson(json, IndexItemBean.class);
                            if (ucIndexItemBean != null) {
                                XposedBridge.log(ucIndexItemBean.toString());
                                XposedBridge.log("beforeHookedMethod!:+++++++++++++++++++");
                            }
                            for (int i = 0; i < count; i++) {
                                Object o2 = listAdapter.getItem(i);
                                String str = "";
                                if (o2 != null) {
                                    String os = gson.toJson(o2);
                                    ucIndexItemBean = gson.fromJson(os, IndexItemBean.class);
                                    if (ucIndexItemBean != null) {
                                        str = ucIndexItemBean.toString();
                                    } else {
                                        str = "" + os;
                                    }
                                }
                                XposedBridge.log("InfoFlowListViewEx: position:" + i + ", " + str);
                            }
                        } else {
                            XposedBridge.log("beforeHookedMethod!:listAdapter is null");
                        }
                        for (Object o : param.args) {
                            arg = arg + o + "," + o.getClass().getName() + ", ";
                        }
                        XposedBridge.log("beforeHookedMethod!:" + arg);
                        Toast.makeText(((ListView) param.thisObject).getContext(), "请注意，第" + position + "条已劫持《" + title + "》", Toast.LENGTH_LONG).show();
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("afterHookedMethod performItemClick 请注意，你已被劫持");
                    }
                });

                /*XposedHelpers.findAndHookMethod("com.uc.framework.ui.widget.ListViewEx", loadPackageParam.classLoader, "onTouchEvent", MotionEvent.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("beforeHookedMethod onTouchEvent 请注意，你将被劫持:" + param.args);
                        ReflectionUtil.printConstructors(param.thisObject.getClass(), true);
                        ReflectionUtil.printMethods(param.thisObject.getClass(), true);
                        ReflectionUtil.printFields(param.thisObject.getClass(), true);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("afterHookedMethod onTouchEvent 请注意，你已被劫持");
                    }
                });*/

                /*XposedHelpers.findAndHookMethod("com.uc.application.infoflow.widget.listwidget.e", loadPackageParam.classLoader, "setAdapter", ListAdapter.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("beforeHookedMethod setAdapter 请注意，你将被劫持:" + param.args[0]);
                        ReflectionUtil.printConstructors(param.thisObject.getClass(), true);
                        ReflectionUtil.printMethods(param.thisObject.getClass(), true);
                        ReflectionUtil.printFields(param.thisObject.getClass(), true);
                        ReflectionUtil.printConstructors(param.args[0].getClass(), true);
                        ReflectionUtil.printMethods(param.args[0].getClass(), true);
                        ReflectionUtil.printFields(param.args[0].getClass(), true);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("afterHookedMethod setAdapter 请注意，你已被劫持");
                    }
                });*/
                /*XposedHelpers.findAndHookMethod("com.uc.sdk.ulog.LogInternal", loadPackageParam.classLoader, "i", String.class, String.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("LogInternal i:" + param.args[0] + ", " + param.args[1]);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    }
                });
                XposedHelpers.findAndHookMethod("com.uc.sdk.ulog.LogInternal", loadPackageParam.classLoader, "e", String.class, String.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("LogInternal e:" + param.args[0] + ", " + param.args[1]);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    }
                });
                XposedHelpers.findAndHookMethod("com.uc.sdk.ulog.LogInternal", loadPackageParam.classLoader, "w", String.class, String.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("LogInternal w:" + param.args[0] + ", " + param.args[1]);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    }
                });
                XposedHelpers.findAndHookMethod("com.uc.sdk.ulog.LogInternal", loadPackageParam.classLoader, "d", String.class, String.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("LogInternal d:" + param.args[0] + ", " + param.args[1]);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    }
                });
                XposedHelpers.findAndHookMethod("com.uc.sdk.ulog.LogInternal", loadPackageParam.classLoader, "f", String.class, String.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("LogInternal f:" + param.args[0] + ", " + param.args[1]);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    }
                });*/
                XposedHelpers.findAndHookMethod("com.uc.webview.export.WebView", loadPackageParam.classLoader, "loadUrl", String.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("WebView 请注意，你将被劫持:" + param.args[0]);
                        JSoupUtil.parseUrl(((FrameLayout)param.thisObject).getContext(), (String)param.args[0]);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    }
                });
                XposedHelpers.findAndHookMethod("com.uc.webview.export.WebView", loadPackageParam.classLoader, "loadUrl", String.class, Map.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("WebViewMap 请注意，你将被劫持:" + param.args[0] + ", Map:" + param.args[1]);
                        JSoupUtil.parseUrl(((FrameLayout)param.thisObject).getContext(), (String)param.args[0]);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    }
                });
                XposedHelpers.findAndHookMethod("com.uc.webview.export.WebView", loadPackageParam.classLoader, "getTitle", new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String title = (String) param.getResult();
                        if (!TextUtils.isEmpty(title) && !title.startsWith("http")){
                            Toast.makeText(((ListView) param.thisObject).getContext(), "已获取到《" + title + "》详情页的标题", Toast.LENGTH_LONG).show();
                        }
                        sendBroadcast(listView.getContext(), XposedReceiver.ACTION_TITLE, "title", title);
                        XposedBridge.log("getTitle 请注意，你将被劫持:" + title);
                    }
                });

                XposedHelpers.findAndHookMethod("com.uc.framework.a.i", loadPackageParam.classLoader, "handleMessage", Message.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Gson gson = new Gson();
                        String json = gson.toJson(param.args[0]);
                        XposedBridge.log("handleMessage:" + json);
                        /*Message msg = (Message)param.args[0];
                        if (msg.obj != null){

                        }*/
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    }
                });
                XposedHelpers.findAndHookMethod("com.uc.application.infoflow.controller.InfoFlowController", loadPackageParam.classLoader, "handleMessageSync", Message.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        /*try {
                            throw new NullPointerException("查看执行路径");
                        }catch (Exception e){
                            XposedBridge.log("bb run 查看执行路径 请注意，你已被劫持" + Log.getStackTraceString(e));
                        }*/
                        XposedBridge.log("InfoFlowController handleMessageSync:" + param.args[0] + ", " + param.getResult());
                    }
                });
                XposedHelpers.findAndHookMethod("com.uc.application.infoflow.controller.InfoFlowController", loadPackageParam.classLoader, "handleMessage", Message.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        /*try {
                            throw new NullPointerException("查看执行路径");
                        }catch (Exception e){
                            XposedBridge.log("bb run 查看执行路径 请注意，你已被劫持" + Log.getStackTraceString(e));
                        }*/
                        XposedBridge.log("InfoFlowController handleMessage:" + param.args[0]);
                    }
                });
            }
        }
    }

    public static void sendBroadcast(Context context, String action, String key, String dataStr) {
        Util.sendBroadcast(context, action, key, dataStr);
    }

}