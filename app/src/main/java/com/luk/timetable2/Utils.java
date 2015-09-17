package com.luk.timetable2;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LuK on 2015-05-01.
 */
public class Utils {
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) MainActivity.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static ArrayList<List<String>> getHours(Context context, Integer day) {
        ArrayList<List<String>> array = new ArrayList<>();
        File dbFile = context.getDatabasePath("db");
        SQLiteDatabase db = context.openOrCreateDatabase(dbFile.getAbsolutePath(), Context.MODE_PRIVATE, null);

        try {
            Cursor c = db.rawQuery(String.format("SELECT time FROM lessons WHERE day = '%d' AND hidden = '0' GROUP by time ORDER by _id", day), null);

            if (c.moveToFirst()) {
                do {
                    array.add(Arrays.asList(c.getString(0)));
                } while (c.moveToNext());
            }

            db.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return array;
    }

    public static ArrayList<List<String>> getLessonsForHour(Context context, Integer day, String hour) {
        ArrayList<List<String>> array = new ArrayList<>();
        File dbFile = context.getDatabasePath("db");
        SQLiteDatabase db = context.openOrCreateDatabase(dbFile.getAbsolutePath(), Context.MODE_PRIVATE, null);

        try {
            Cursor c = db.rawQuery(String.format("SELECT * from lessons WHERE day = '%d' AND time = '%s' AND hidden = '0'", day, hour), null);

            if (c.moveToFirst()) {
                do {
                    array.add(Arrays.asList(c.getString(2), c.getString(3), c.getString(4), String.valueOf(c.getInt(0))));
                } while (c.moveToNext());
            }

            db.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return array;
    }

    public static ArrayList<List<String>> getHiddenLessons(Context context, Integer day) {
        ArrayList<List<String>> array = new ArrayList<>();
        File dbFile = context.getDatabasePath("db");
        SQLiteDatabase db = context.openOrCreateDatabase(dbFile.getAbsolutePath(), Context.MODE_PRIVATE, null);

        try {
            Cursor c = db.rawQuery(String.format("SELECT * from lessons WHERE day = '%d' AND hidden = '1'", day), null);

            if (c.moveToFirst()) {
                do {
                    array.add(Arrays.asList(c.getString(2), c.getString(3), c.getString(4), String.valueOf(c.getInt(0))));
                } while (c.moveToNext());
            }

            db.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return array;
    }

    public static void refreshWidgets(Context context) {
        int widgetIDs_dark[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, com.luk.timetable2.widget.dark.WidgetProvider.class));
        int widgetIDs_light[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, com.luk.timetable2.widget.light.WidgetProvider.class));

        for (int id : widgetIDs_dark) {
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(id, R.id.widget);
        }

        for (int id : widgetIDs_light) {
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(id, R.id.widget);
        }
    }

    public static boolean getCurrentTheme(Activity activity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        return sharedPref.getBoolean("light_theme", false);
    }

    public static void setThemeListener(final Activity activity) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("THEME_CHANGE");

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                activity.recreate();
            }
        };

        activity.registerReceiver(broadcastReceiver, intentFilter);
    }
}
