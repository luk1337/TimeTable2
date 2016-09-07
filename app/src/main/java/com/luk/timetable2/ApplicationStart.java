package com.luk.timetable2;

import android.app.Application;
import android.content.Intent;

import com.luk.timetable2.services.RegisterReceivers;
import com.orm.SugarContext;

/**
 * Created by luk on 10/15/15.
 */
public class ApplicationStart extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Sugar ORM
        SugarContext.init(this);

        // Start services
        sendBroadcast(new Intent(this, RegisterReceivers.class));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // Terminate Sugar ORM
        SugarContext.terminate();
    }
}
