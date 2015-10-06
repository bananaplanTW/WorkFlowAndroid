package com.bananaplan.workflowandroid.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


/**
 * Receiver to listen ACTION_TIME_TICK(every minute)
 *
 * @author Danny Lin
 * @since 2015/10/6.
 */
public class MinuteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Get minute", Toast.LENGTH_SHORT).show();
    }
}
