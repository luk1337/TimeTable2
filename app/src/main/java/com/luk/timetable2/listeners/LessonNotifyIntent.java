package com.luk.timetable2.listeners;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import com.luk.timetable2.Utils;

/**
 * Created by luk on 9/29/15.
 */
public class LessonNotifyIntent extends IntentService {
    public LessonNotifyIntent() {
        super("WakefulService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (sharedPref.getBoolean("notifications_vibrate", false)) {
            Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        }

        LessonNotifyWakeReceiver.completeWakefulIntent(intent);
    }
}