package com.jp.xposedtest.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Util {
    private final static String TAG = Util.class.getSimpleName();

    public static void printBundle(Bundle bundle) {
        for (String key : bundle.keySet()) {
            Log.d(TAG, "bundle.key: " + key + ", value: " + bundle.get(key));
        }
    }

    public static void printTreeView(Activity activity) {
        View rootView = activity.getWindow().getDecorView();
        printTreeView(rootView);
    }

    public static void printTreeView(View rootView) {
        if (rootView instanceof ViewGroup) {
            ViewGroup parentView = (ViewGroup) rootView;
            for (int i = 0; i < parentView.getChildCount(); i++) {
                printTreeView(parentView.getChildAt(i));
            }
        } else {
            Log.d(TAG, "view: " + rootView.getId() + ", class: " + rootView.getClass());
            // any view if you want something different
            if (rootView instanceof EditText) {
                Log.d(TAG, "edit:" + rootView.getTag()
                        + "， hint: " + ((EditText) rootView).getHint());
            } else if (rootView instanceof TextView) {
                Log.d(TAG, "text:" + ((TextView) rootView).getText().toString());
            }
        }
    }

    public static void printMethods(Class clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            Log.d(TAG, "" + method);
        }
    }

    public static void printFields(Class clazz) {
        for (Field field : clazz.getFields()) {
            Log.d(TAG, "" + field);
        }
    }

    //-----------------获取 activity中的所有view
    public static List<View> getAllViews(Activity act) {
        return getAllChildViews(act.getWindow().getDecorView());
    }

    public static List<View> getAllChildViews(View view) {
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

    public static List<TextView> getAllChildTextViews(View view) {
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

    public static TextView getFontSizeMaxTextView(List<TextView> list) {
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

    public static void sendBroadcast(Context context, String action, String key, String dataStr) {
        Intent intent = new Intent(action);
        Bundle data = new Bundle();
        data.putString(key, dataStr);
        intent.putExtras(data);
        context.sendBroadcast(intent);
    }
}
