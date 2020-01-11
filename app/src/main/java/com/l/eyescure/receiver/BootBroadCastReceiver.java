package com.l.eyescure.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.l.eyescure.activity.LoadingActivity;

/**
 * Created by Look on 2017/3/15.
 * 开机启动
 */

public class BootBroadCastReceiver extends BroadcastReceiver{
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Intent mainActivityIntent = new Intent(context, LoadingActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }
    }
}
