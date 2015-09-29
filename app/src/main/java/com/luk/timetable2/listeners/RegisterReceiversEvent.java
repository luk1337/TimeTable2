package com.luk.timetable2.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by luk on 9/29/15.
 */
public class RegisterReceiversEvent extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.getApplicationContext().registerReceiver(new OnScreenWakeReceiver(), new IntentFilter(Intent.ACTION_SCREEN_ON));
    }
}
