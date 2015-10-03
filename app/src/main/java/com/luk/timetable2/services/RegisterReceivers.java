package com.luk.timetable2.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by luk on 9/29/15.
 */
public class RegisterReceivers extends BroadcastReceiver {
    private static String TAG = "RegisterReceivers";
    private static String[] SERVICES = new String[] {
            "com.luk.timetable2.services.DateChange.DateChangeService",
            "com.luk.timetable2.services.LessonNotify.LessonNotifyService",
            "com.luk.timetable2.services.WidgetRefresh.WidgetRefreshService"
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        for (String service : SERVICES) {
            try {
                Class<?> serviceClass = Class.forName(service);

                context.getApplicationContext().stopService(new Intent(context, serviceClass));
                context.getApplicationContext().startService(new Intent(context, serviceClass));
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "", e);
            }
        }
    }
}
