package com.luk.timetable2.tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.luk.timetable2.DatabaseHandler;
import com.luk.timetable2.Parser;
import com.luk.timetable2.R;
import com.luk.timetable2.Utils;
import com.luk.timetable2.activities.MainActivity;
import com.luk.timetable2.activities.MainActivityAdapter;
import com.luk.timetable2.services.LessonNotify.LessonNotifyService;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by LuK on 2015-05-01.
 */
public class SyncTask extends AsyncTask<Integer, Integer, Integer> {
    private static String TAG = "SyncTask";
    private ProgressDialog dialog;
    private int _class;
    private String _api;

    public SyncTask(MainActivity activity, int _class) {
        // load prefs
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        this._class = _class;
        this._api = sharedPref.getString("school", "");
    }

    @Override
    protected Integer doInBackground(Integer... strings) {
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            public void run() {
                dialog = ProgressDialog.show(MainActivity.getInstance(), null, MainActivity.getInstance().getString(R.string.sync_in_progress), true);
            }
        });

        HashMap<Integer, ArrayList<HashMap<String, String>>> data = null;

        try {
            data = new Parser(String.format("%s/plany/o%d.html", _api, _class)).parseLessons();
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return -1;
        }

        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        SQLiteDatabase db = databaseHandler.getDB(MainActivity.getInstance());
        databaseHandler.createTables(MainActivity.getInstance());

        for (int day = 1; day <= 5; day++) {
            if (data.get(day) == null) continue;

            for (HashMap<String, String> lesson : data.get(day)) {
                String _hour = lesson.get("hour");
                String _lesson = lesson.get("lesson");
                String _group = lesson.get("group");
                String _room = lesson.get("room");

                if (_group.length() > 0) {
                    _lesson += String.format(" (%s)", _group);
                }

                SQLiteStatement stmt = db.compileStatement("INSERT INTO `lessons` VALUES (NULL, ?, ?, ?, ?, '0');");
                stmt.bindString(1, String.valueOf(day - 1));
                stmt.bindString(2, _lesson);
                stmt.bindString(3, _room);
                stmt.bindString(4, _hour);
                stmt.execute();
            }
        }

        db.close();
        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        if (result == -1) {
            MainActivity.getInstance().runOnUiThread(new Runnable() {
                public void run() {
                    new AlertDialog.Builder(MainActivity.getInstance())
                            .setTitle(MainActivity.getInstance().getString(R.string.error_title))
                            .setMessage(MainActivity.getInstance().getString(R.string.error_offline))
                            .setPositiveButton(android.R.string.yes, null)
                            .show();
                }
            });
        }

        Utils.refreshWidgets(MainActivity.getInstance());
        MainActivity.getInstance().stopService(new Intent(MainActivity.getInstance(), LessonNotifyService.class));
        MainActivity.getInstance().startService(new Intent(MainActivity.getInstance(), LessonNotifyService.class));

        MainActivityAdapter mainActivityAdapter = new MainActivityAdapter(MainActivity.getInstance().getSupportFragmentManager());
        MainActivity.getInstance().sViewPager.setAdapter(mainActivityAdapter);

        if (dialog != null) {
            dialog.dismiss();
        }
    }
}