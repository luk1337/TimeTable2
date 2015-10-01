package com.luk.timetable2.listeners;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import java.util.Calendar;

/**
 * Created by luk on 9/29/15.
 */
public class WidgetRefreshService extends Service {
    private static AlarmManager mAlarmManager;
    private static PendingIntent mPendingIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Intent intent = new Intent(getApplicationContext(), WidgetRefreshReceiver.class);

        mAlarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + AlarmManager.INTERVAL_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mPendingIntent);
            return;
        }

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mPendingIntent);
    }

    @Override
    public void onDestroy() {
        mAlarmManager.cancel(mPendingIntent);
    }
}
