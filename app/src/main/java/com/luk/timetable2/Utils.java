package com.luk.timetable2;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.luk.timetable2.activities.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LuK on 2015-05-01.
 */
public class Utils {
    private static String TAG = "Utils";

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
                    array.add(Collections.singletonList(c.getString(0)));
                } while (c.moveToNext());
            }

            c.close();
            db.close();
        } catch(Exception e) {
            Log.e(TAG, "", e);
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

            c.close();
            db.close();
        } catch(Exception e) {
            Log.e(TAG, "", e);
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

            c.close();
            db.close();
        } catch(Exception e) {
            Log.e(TAG, "", e);
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

    public static int getCurrentTheme(Activity activity) {
        HashMap<String, Integer> themes = new HashMap<>();
        themes.put("dark", R.style.AppTheme);
        themes.put("light", R.style.AppTheme_Light);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        return themes.get(sharedPref.getString("theme", "dark"));
    }
}
