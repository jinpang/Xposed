package com.jp.xposedtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jp.xposedtest.bean.IndexItemBean;
import com.jp.xposedtest.touch.GestureTouchUtils;

import java.util.ArrayList;
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
                        XposedBridge.log("InfoFlowListViewEx 请注意，你将被劫持");
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (listView == null) {
                            listView = ((ListView) param.thisObject);
                            final int position = xSharedPreferences.getInt("index", 0);
                            listView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ListAdapter listAdapter = listView.getAdapter();
                                    if (listAdapter != null) {
//                            listView.performItemClick(listView.getChildAt(position), position, listView.getItemIdAtPosition(position));
                                        int count = listAdapter.getCount();
                                        if (position < count){
                                            String t = "listView!";
                                            if (listView.getParent() != null){
                                                t = t + " Parent1:" + listView.getParent();
                                                if (listView.getParent().getParent() != null){
                                                    t = t + " Parent2:" + listView.getParent().getParent();
                                                    if (listView.getParent().getParent().getParent() != null){
                                                        t = t + " Parent3:" + listView.getParent().getParent().getParent();
                                                        if (listView.getParent().getParent().getParent() != null){
                                                            t = t + " Parent3:" + listView.getParent().getParent().getParent();
                                                        }
                                                    }
                                                }
                                            }
                                            XposedBridge.log(t);
                                            //listView.smoothScrollToPosition(position);
                                            //listView.scrollListBy(500);
                                            //listView.smoothScrollToPositionFromTop(position, 0, 300);
                                            GestureTouchUtils.simulateScroll(listView, 30, 0, 30, 500);
                                            listView.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //listView.setSelection(position);
                                                    listView.performItemClick(listView.getChildAt(position), position, listView.getItemIdAtPosition(position));
                                                }
                                            }, 2000 + 50);
                                            Object o = listAdapter.getItem(position);
                                            Gson gson = new Gson();
                                            String json = gson.toJson(o);
                                            sendData(listView.getContext(), XposedReceiver.ACTION_ITEM, "item", json);
                                            XposedBridge.log("list count:" + count + ", item:" + o + ", json:" + json);
                                            StringBuffer buffer = new StringBuffer("titles:");
                                            for (int i=0; i<count; i++){
                                                Object o2 = listAdapter.getItem(i);
                                                if (o2 != null) {
                                                    String os = gson.toJson(o2);
                                                    XposedBridge.log(os);
                                                    IndexItemBean ucIndexItemBean = gson.fromJson(os, IndexItemBean.class);
                                                    if (ucIndexItemBean != null) {
                                                        buffer.append(ucIndexItemBean.toString()).append("\n");
                                                    }
                                                }
                                            }
                                        }else {
                                            listView.smoothScrollToPosition(count);
                                            XposedBridge.log("list count:" + count + ", position:" + position);
                                        }
                                    } else {
                                        XposedBridge.log("listAdapter is null");
                                    }
                                }
                            }, 15000);
                        }
                    }
                });

                XposedHelpers.findAndHookMethod("com.uc.framework.ui.widget.ListViewEx", loadPackageParam.classLoader, "performItemClick", View.class, int.class, long.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("performItemClick 请注意，你将被劫持");
                        String arg = "";
                        CharSequence title = "";
                        int childCount = 0;
                        if (param.args != null) {
                            final int position = (int) param.args[1];
                            final long id = (long) param.args[2];
                            ViewGroup itemView = (ViewGroup) param.args[0];
                            String t = "beforeHookedMethod!";
                            if (itemView.getParent() != null){
                                t = t + " Parent1:" + itemView.getParent();
                                if (itemView.getParent().getParent() != null){
                                    t = t + " Parent2:" + itemView.getParent().getParent();
                                    if (itemView.getParent().getParent().getParent() != null){
                                        t = t + " Parent3:" + itemView.getParent().getParent().getParent();
                                        if (itemView.getParent().getParent().getParent().getParent() != null){
                                            t = t + " Parent4:" + itemView.getParent().getParent().getParent().getParent();
                                        }
                                    }
                                }
                            }
                            XposedBridge.log(t);
                            TextView titleView = getFontSizeMaxTextView(getAllChildTextViews((ViewGroup) param.args[0]));
                            if (titleView != null) {
                                title = titleView.getText();
                            }
                            for (Object o : param.args) {
                                arg = arg + o + "," + o.getClass().getName() + ", ";
                            }
                            arg = arg + " childCount:" + childCount + ",title:" + title + ";";
                            final ListView listView = ((ListView) param.thisObject);
                            String ts = "listView!";
                            if (listView.getParent() != null){
                                ts = ts + " Parent1:" + listView.getParent();
                                if (listView.getParent().getParent() != null){
                                    ts = ts + " Parent2:" + listView.getParent().getParent();
                                    if (listView.getParent().getParent().getParent() != null){
                                        ts = ts + " Parent3:" + listView.getParent().getParent().getParent();
                                        if (listView.getParent().getParent().getParent() != null){
                                            ts = ts + " Parent3:" + listView.getParent().getParent().getParent();
                                        }
                                    }
                                }
                            }
                            XposedBridge.log(ts);
                            ListAdapter listAdapter = listView.getAdapter();
                            if (listAdapter != null) {
                                int count = listAdapter.getCount();
                                Object o = listAdapter.getItem((int) param.args[1]);
                                Gson gson = new Gson();
                                String json = gson.toJson(o);
                                sendData(listView.getContext(), XposedReceiver.ACTION_ITEM, "item", json);
                                XposedBridge.log("beforeHookedMethod!:list count:" + count + ", item:" + o + ", json:" + json);
                                IndexItemBean ucIndexItemBean = gson.fromJson(json, IndexItemBean.class);
                                if (ucIndexItemBean != null) {
                                    XposedBridge.log(ucIndexItemBean.toString());
                                }
                            } else {
                                XposedBridge.log("beforeHookedMethod!:listAdapter is null");
                            }
                        }
                        XposedBridge.log("beforeHookedMethod!:" + arg);
                        Toast.makeText(((ListView) param.thisObject).getContext(), "请注意，已劫持《" + title + "》", Toast.LENGTH_LONG).show();
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("performItemClick 请注意，你已被劫持");
                    }
                });

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