package com.luk.timetable2.listeners;

import android.app.IntentService;
import android.content.Intent;

import com.luk.timetable2.Utils;

/**
 * Created by luk on 9/29/15.
 */
public class OnScreenWakeIntent extends IntentService {
    public OnScreenWakeIntent() {
        super("WakefulService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Utils.refreshWidgets(getApplicationContext());
        OnScreenWakeReceiver.completeWakefulIntent(intent);
    }
}