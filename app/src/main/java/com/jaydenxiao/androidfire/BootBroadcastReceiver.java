package com.jaydenxiao.androidfire;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jaydenxiao.androidfire.ui.ClockResult.ClockResultActivity;


/**
 * Created by xtt on 2017/10/16.
 */

public class BootBroadcastReceiver extends BroadcastReceiver{
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Intent mainActivityIntent = new Intent(context, ClockResultActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }
    }
}
