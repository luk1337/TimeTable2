package com.luk.timetable2.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.luk.timetable2.Utils;

/**
 * Created by luk on 9/29/15.
 */
public class OnScreenWakeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.refreshWidgets(context);
    }
}
