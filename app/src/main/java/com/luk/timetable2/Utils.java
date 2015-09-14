package com.luk.timetable2;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

    public static void refreshWidgets() {
        int widgetIDs_dark[] = AppWidgetManager.getInstance(MainActivity.getInstance().getApplication()).getAppWidgetIds(new ComponentName(MainActivity.getInstance().getApplication(), com.luk.timetable2.widget.dark.WidgetProvider.class));
        int widgetIDs_light[] = AppWidgetManager.getInstance(MainActivity.getInstance().getApplication()).getAppWidgetIds(new ComponentName(MainActivity.getInstance().getApplication(), com.luk.timetable2.widget.light.WidgetProvider.class));

        for (int id : widgetIDs_dark) {
            AppWidgetManager.getInstance(MainActivity.getInstance().getApplication()).notifyAppWidgetViewDataChanged(id, R.id.widget);
        }

        for (int id : widgetIDs_light) {
            AppWidgetManager.getInstance(MainActivity.getInstance().getApplication()).notifyAppWidgetViewDataChanged(id, R.id.widget);
        }
    }
}
