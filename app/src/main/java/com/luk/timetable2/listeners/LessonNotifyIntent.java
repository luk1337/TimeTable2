package com.luk.timetable2.listeners;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
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
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(200);

        LessonNotifyWakeReceiver.completeWakefulIntent(intent);
    }
}