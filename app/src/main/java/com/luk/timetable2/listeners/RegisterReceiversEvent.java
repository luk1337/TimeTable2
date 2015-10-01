package com.luk.timetable2.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by luk on 9/29/15.
 */
public class RegisterReceiversEvent extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.getApplicationContext().startService(new Intent(context, WidgetRefreshService.class));
        context.getApplicationContext().startService(new Intent(context, DateChangeService.class));
    }
}
