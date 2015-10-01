package com.luk.timetable2.listeners;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by luk on 9/29/15.
 */
public class OnScreenWakeReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        startWakefulService(context, new Intent(context, OnScreenWakeIntent.class));
    }
}