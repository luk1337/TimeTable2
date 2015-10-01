package com.luk.timetable2.listeners;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by luk on 9/29/15.
 */
public class DateChangeReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        startWakefulService(context, new Intent(context, WidgetRefreshIntent.class));

        context.stopService(new Intent(context, WidgetRefreshService.class));
        context.startService(new Intent(context, WidgetRefreshService.class));

        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {
            context.stopService(new Intent(context, LessonNotifyService.class));
            context.startService(new Intent(context, LessonNotifyService.class));
        }
    }
}