package com.jp.xposedtest;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jp.xposedtest.utils.JSoupUtil;
import com.jp.xposedtest.utils.PropertyUtil;
import com.jp.xposedtest.utils.Util;

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
    Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log(" packageName:" + loadPackageParam.packageName);
        //首页com.uc.browser.InnerUCMobile
        //UC浏览器在模拟器中的版本是x86架构的，包名是com.UCMobile.x86，在普遍真机上的架构是arm，包名是com.UCMobile
        if (loadPackageParam.packageName.equals("com.UCMobile.x86") || loadPackageParam.packageName.equals("com.UCMobile")) {
            XposedBridge.log(" has Hooked!");
            if (xSharedPreferences == null) {
                xSharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, SharedPreferenceUtils.FILE_NAME);
            }
            boolean isOpen = Boolean.valueOf(PropertyUtil.readValue(MainActivity.CONFIG_PATH, "is_open", "true"));
            if (isOpen) {
                //com.uc.browser.InnerUCMobile
                Class clazz = XposedHelpers.findClassIfExists("com.uc.browser.InnerUCMobile", loadPackageParam.classLoader);
                if (clazz != null) {
                    XposedBridge.log("InnerUCMobile:" + clazz.getName());
                    XposedHelpers.findAndHookMethod(clazz, "onAttachedToWindow", new XC_MethodHook() {
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                        }

                        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                            XposedBridge.log("InnerUCMobile onClick:" + param.thisObject);
                            Toast.makeText(((Activity) param.thisObject), "进入UC浏览器中", Toast.LENGTH_LONG).show();
                            Util.printTreeView(((Activity) param.thisObject).findViewById(R.id.content));
                            super.afterHookedMethod(param);
                        }
                    });
                } else {
                    XposedBridge.log("com.uc.application.infoflow.widget.o.c not found");
                }
                /*XposedHelpers.findAndHookConstructor("com.uc.framework.ui.widget.ListViewEx", loadPackageParam.classLoader, Context.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("InfoFlowListViewEx 将劫持:" + param.thisObject);
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
                                        int count = listAdapter.getCount();
                                        if (position < count) {
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
                        XposedBridge.log("beforeHookedMethod performItemClick 将劫持:" + param.args);
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
                });*/
                XposedHelpers.findAndHookMethod("com.uc.webview.export.WebView", loadPackageParam.classLoader, "loadUrl", String.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        //String parentPath = PropertyUtil.readValue(MainActivity.CONFIG_PATH, "path", "获取不到路径");
                        XposedBridge.log("WebView 将劫持:" +  param.args[0] + ":" + gson.toJson(param.args));
                        JSoupUtil.parseUrl(((FrameLayout) param.thisObject).getContext(), (String) param.args[0]);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    }
                });
                XposedHelpers.findAndHookMethod("com.uc.webview.export.WebView", loadPackageParam.classLoader, "loadUrl", String.class, Map.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        //String parentPath = (String) xSharedPreferences.getString("path", "获取不到路径");
                        XposedBridge.log("WebViewMap 将劫持:保存路径" + param.args[0] + ", Map:" + param.args[1] + ":" + gson.toJson(param.args));
                        JSoupUtil.parseUrl(((FrameLayout) param.thisObject).getContext(), (String) param.args[0]);
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
                        /*if (!TextUtils.isEmpty(title) && !title.startsWith("http")){
                            Toast.makeText(((ListView) param.thisObject).getContext(), "已获取到《" + title + "》详情页的标题", Toast.LENGTH_LONG).show();
                        }*/
                        sendBroadcast(((FrameLayout) param.thisObject).getContext(), XposedReceiver.ACTION_TITLE, "title", title);
                        XposedBridge.log("getTitle 将劫持:" + title);
                    }
                });
                XposedHelpers.findAndHookMethod("com.uc.webview.export.WebView$b", loadPackageParam.classLoader, "onPageStarted", new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("onPageStarted:" + param.args[0] + ":" + param.args[1] + ":" + param.args[2]);
                    }
                });
                XposedHelpers.findAndHookMethod("com.uc.webview.export.WebView$b", loadPackageParam.classLoader, "onPageFinished", new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("onPageFinished:" + param.args[0] + ":" + param.args[1]);
                    }
                });
                /*XposedHelpers.findAndHookMethod("com.uc.framework.a.i", loadPackageParam.classLoader, "handleMessage", Message.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Gson gson = new Gson();
                        String json = gson.toJson(param.args[0]);
                        XposedBridge.log("handleMessage:" + json);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    }
                });*/
                /*XposedHelpers.findAndHookMethod("com.uc.application.infoflow.controller.InfoFlowController", loadPackageParam.classLoader, "handleMessageSync", Message.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("InfoFlowController handleMessageSync:" + param.args[0] + ", " + param.getResult());
                    }
                });
                XposedHelpers.findAndHookMethod("com.uc.application.infoflow.controller.InfoFlowController", loadPackageParam.classLoader, "handleMessage", Message.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("InfoFlowController handleMessage:" + param.args[0]);
                    }
                });*/
            }
        }
    }

    public static void sendBroadcast(Context context, String action, String key, String dataStr) {
        Util.sendBroadcast(context, action, key, dataStr);
    }
}