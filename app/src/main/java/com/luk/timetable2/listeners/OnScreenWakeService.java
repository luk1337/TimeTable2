package com.luk.timetable2.listeners;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * Created by luk on 9/29/15.
 */
public class OnScreenWakeService extends Service {
    private static BroadcastReceiver mOnScreenWakeReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mOnScreenWakeReceiver = new OnScreenWakeReceiver();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(mOnScreenWakeReceiver, filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mOnScreenWakeReceiver);
        mOnScreenWakeReceiver = null;
    }
}
