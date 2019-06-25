package com.jp.xposedtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class XposedReceiver extends BroadcastReceiver {
    public final static String TAG = "Xposed";
    public final static String ACTION_TITLE = "com.jp.xposedtest.ACTION_TITLE";
    public final static String ACTION_ITEM = "com.jp.xposedtest.ACTION_ITEM";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String action = intent.getAction();
        Log.e(TAG, "action:" + action);
        if (ACTION_TITLE.equals(action)){
            String title = bundle.getString("title", "");
            SharedPreferenceUtils.setParam(context, "title", title);
        }else if (ACTION_ITEM.equals(action)){
            String item = bundle.getString("item", "");
            SharedPreferenceUtils.setParam(context, "item", item);
        }
    }
}
