package com.luk.timetable2.tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.luk.timetable2.MainActivity;
import com.luk.timetable2.Parser;
import com.luk.timetable2.R;
import com.luk.timetable2.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by LuK on 2015-05-01.
 */
public class SyncTask extends AsyncTask<Integer, Integer, Integer> {
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
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        // create database
        File dbFile = MainActivity.getInstance().getDatabasePath("db");
        SQLiteDatabase db = MainActivity.getInstance().openOrCreateDatabase(dbFile.getAbsolutePath(), Context.MODE_PRIVATE, null);

        try{
            db.execSQL("DROP TABLE lessons;");
        } catch(Exception ex) {
            // do nothing
        }

        db.execSQL("CREATE TABLE lessons ( _id INTEGER PRIMARY KEY AUTOINCREMENT, day INTEGER, lesson STRING, room STRING, time STRING, hidden STRING );");

        for (int day = 1; day <= 5; day++) {
            if (data.get(day) == null) continue;

            for (HashMap<String, String> lesson : data.get(day)) {
                String _hour = lesson.get("hour");
                String _lesson = lesson.get("lesson");
                String _group = lesson.get("group");
                String _room = lesson.get("room");

                if(_group.length() > 0) {
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

        MainActivity.getInstance().loadLessons(MainActivity.getInstance().getActionBar().getSelectedNavigationIndex());

        if(dialog != null) {
            dialog.dismiss();
        }
    }
}