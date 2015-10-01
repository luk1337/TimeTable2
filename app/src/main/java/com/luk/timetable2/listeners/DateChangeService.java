package com.luk.timetable2.listeners;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by luk on 9/29/15.
 */
public class DateChangeService extends Service {
    private static DateChangeReceiver mDateChangeReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mDateChangeReceiver = new DateChangeReceiver();
        getApplicationContext().registerReceiver(mDateChangeReceiver, new IntentFilter(Intent.ACTION_TIME_CHANGED));
    }

    @Override
    public void onDestroy() {
        getApplicationContext().unregisterReceiver(mDateChangeReceiver);
        mDateChangeReceiver = null;
    }
}