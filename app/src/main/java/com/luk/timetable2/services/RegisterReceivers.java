package com.luk.timetable2.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.luk.timetable2.services.DateChange.DateChangeService;
import com.luk.timetable2.services.LessonNotify.LessonNotifyService;
import com.luk.timetable2.services.WidgetRefresh.WidgetRefreshService;

/**
 * Created by luk on 9/29/15.
 */
public class RegisterReceivers extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.getApplicationContext().startService(new Intent(context, WidgetRefreshService.class));
        context.getApplicationContext().startService(new Intent(context, LessonNotifyService.class));
        context.getApplicationContext().startService(new Intent(context, DateChangeService.class));
    }
}
