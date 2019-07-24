package com.jp.xposedtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class XposedReceiver extends BroadcastReceiver {
    public final static String TAG = "Xposed";
    public final static String ACTION_TITLE = "com.jp.xposedtest.ACTION_TITLE";
    public final static String ACTION_ITEM = "com.jp.xposedtest.ACTION_ITEM";
    public final static String ACTION_SEARCH_OK = "com.jp.xposedtest.ACTION_SEARCH_OK";
    public final static String ACTION_SEARCH_FAILED = "com.jp.xposedtest.ACTION_SEARCH_FAILED";
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
        }else if (ACTION_SEARCH_OK.equals(action)){
            String item = bundle.getString("search", "");
            Toast.makeText(context, "保存成功，路径：" + item, Toast.LENGTH_LONG).show();
        }else if (ACTION_SEARCH_FAILED.equals(action)){
            String item = bundle.getString("search", "");
            Toast.makeText(context, "保存失败，原因：" + item, Toast.LENGTH_LONG).show();
        }
    }
}
