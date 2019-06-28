package com.jp.xposedtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jp.xposedtest.bean.IndexItemBean;
import com.jp.xposedtest.touch.GestureTouchUtils;
import com.jp.xposedtest.utils.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jp.xposedtest.utils.ReflectionUtil.printParentViewGroup;

public class HookTest implements IXposedHookLoadPackage {
    XSharedPreferences xSharedPreferences;
    ListView listView;

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log(" packageName:" + loadPackageParam.packageName);
        if (loadPackageParam.packageName.equals("com.UCMobile")) {
            XposedBridge.log(" has Hooked!");
            if (xSharedPreferences == null) {
                xSharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, SharedPreferenceUtils.FILE_NAME);
            }
            boolean isOpen = xSharedPreferences.getBoolean("is_open", true);
            if (isOpen) {
                XposedHelpers.findAndHookConstructor("com.uc.framework.ui.widget.ListViewEx", loadPackageParam.classLoader, Context.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("InfoFlowListViewEx 请注意，你将被劫持:" + param.thisObject);
                        ReflectionUtil.printConstructors(param.thisObject.getClass(), true);
                        ReflectionUtil.printMethods(param.thisObject.getClass(), true);
                        ReflectionUtil.printFields(param.thisObject.getClass(), true);
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
                                        if (position < count){
                                            ReflectionUtil.printParentViewGroup(listView, 4);
                                            if (listView.getContext() instanceof Activity){
                                                XposedBridge.log("InfoFlowListViewEx 滑动");
                                                Activity activity = (Activity)listView.getContext();
                                                GestureTouchUtils.simulateScroll(activity.getWindow().getDecorView(), 30, 0, 30, 500);
                                            }
                                            listView.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listView.performItemClick(listView.getChildAt(position), position, listView.getItemIdAtPosition(position));
                                                }
                                            }, 2000 + 50);
                                            Object o = listAdapter.getItem(position);
                                            Gson gson = new Gson();
                                            String json = gson.toJson(o);
                                            XposedBridge.log("InfoFlowListViewEx list count:" + count + ", item:" + o + ", json:" + json + ", position:" + position);
                                            sendData(listView.getContext(), XposedReceiver.ACTION_ITEM, "item", json);
                                            for (int i=0; i<count; i++){
                                                Object o2 = listAdapter.getItem(i);
                                                String str = "";
                                                if (o2 != null) {
                                                    String os = gson.toJson(o2);
                                                    //XposedBridge.log(os);
                                                    IndexItemBean ucIndexItemBean = gson.fromJson(os, IndexItemBean.class);
                                                    if (ucIndexItemBean != null) {
                                                        str = ucIndexItemBean.toString();
                                                    }else {
                                                        str = "" + os;
                                                    }
                                                }
                                                XposedBridge.log("InfoFlowListViewEx: position:" + i + ", " + str);
                                            }
                                        }else {
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
                        ReflectionUtil.printConstructors(param.thisObject.getClass(), true);
                        ReflectionUtil.printMethods(param.thisObject.getClass(), true);
                        ReflectionUtil.printFields(param.thisObject.getClass(), true);
                        ReflectionUtil.printConstructors(param.args[0].getClass(), true);
                        ReflectionUtil.printMethods(param.args[0].getClass(), true);
                        ReflectionUtil.printFields(param.args[0].getClass(), true);
                        String arg = "";
                        CharSequence title = "";
                        final int position = (int) param.args[1];
                        ViewGroup itemView = (ViewGroup) param.args[0];
                        printParentViewGroup(itemView, 4);
                        final ListView listView = ((ListView) param.thisObject);
                        printParentViewGroup(listView, 4);
                        ListAdapter listAdapter = listView.getAdapter();
                        if (listAdapter != null) {
                            int count = listAdapter.getCount();
                            Object o = listAdapter.getItem(position);
                            if (o != null){
                                Method method = o.getClass().getMethod("getTitle", new Class<?>[]{});
                                title = (String)method.invoke(o, new Object[]{});
                                XposedBridge.log("beforeHookedMethod!:title2:" + title);
                            }
                            Gson gson = new Gson();
                            String json = gson.toJson(o);
                            sendData(listView.getContext(), XposedReceiver.ACTION_ITEM, "item", json);
                            XposedBridge.log("beforeHookedMethod!:list count:" + count + ", item:" + o + ", json:" + json);
                            IndexItemBean ucIndexItemBean = gson.fromJson(json, IndexItemBean.class);
                            if (ucIndexItemBean != null) {
                                XposedBridge.log(ucIndexItemBean.toString());
                                XposedBridge.log("beforeHookedMethod!:+++++++++++++++++++");
                            }
                            for (int i=0; i<count; i++){
                                Object o2 = listAdapter.getItem(i);
                                String str = "";
                                if (o2 != null) {
                                    String os = gson.toJson(o2);
                                    ucIndexItemBean = gson.fromJson(os, IndexItemBean.class);
                                    if (ucIndexItemBean != null) {
                                        str = ucIndexItemBean.toString();
                                    }else {
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
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    }
                });
                XposedHelpers.findAndHookMethod("com.uc.webview.export.WebView", loadPackageParam.classLoader, "loadUrl", String.class, Map.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("WebViewMap 请注意，你将被劫持:" + param.args[0] + ", Map:" + param.args[1]);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    }
                });
                XposedHelpers.findAndHookMethod("com.uc.webview.export.WebView", loadPackageParam.classLoader, "getTitle", new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String title = (String)param.getResult();
                        sendData(listView.getContext(), XposedReceiver.ACTION_TITLE, "title", title);
                        XposedBridge.log("getTitle 请注意，你将被劫持:" + title);
                    }
                });
            }
            LinearLayout linearLayout;
        }
    }

    private void sendData(Context context, String action, String key, String dataStr){
        Intent intent = new Intent(action);
        Bundle data = new Bundle();
        data.putString(key, dataStr);
        intent.putExtras(data);
        context.sendBroadcast(intent);
    }

    //-----------------获取 activity中的所有view
    public void getAllViews(Activity act) {
        List<View> list = getAllChildViews(act.getWindow().getDecorView());
    }

    public List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                allchildren.add(viewchild);
                //再次 调用本身（递归）
                allchildren.addAll(getAllChildViews(viewchild));
            }
        }
        return allchildren;
    }

    public List<TextView> getAllChildTextViews(View view) {
        List<TextView> allchildren = new ArrayList<TextView>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                if (viewchild instanceof TextView) {
                    allchildren.add((TextView) viewchild);
                }
                //再次 调用本身（递归）
                allchildren.addAll(getAllChildTextViews(viewchild));
            }
        }
        return allchildren;
    }

    public TextView getFontSizeMaxTextView(List<TextView> list) {
        TextView maxText = null;
        if (list != null) {
            int count = list.size();
            if (count > 1) {
                maxText = list.get(0);
                for (int i = 1; i < count; i++) {
                    TextView textView = list.get(i);
                    if (maxText.getTextSize() < textView.getTextSize()) {
                        maxText = textView;
                    }
                }
            } else {
                if (count == 1) {
                    maxText = list.get(0);
                }
            }
        }
        return maxText;
    }
}