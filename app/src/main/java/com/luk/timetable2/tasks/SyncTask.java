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
    private MainActivity mMainActivity;
    private ProgressDialog mDialog;
    private int mClass;
    private String mUrl;

    public SyncTask(int mClass) {
        mMainActivity = MainActivity.getInstance();

        // load prefs
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mMainActivity);

        this.mClass = mClass;
        this.mUrl = sharedPref.getString("school", "");
    }

    @Override
    protected Integer doInBackground(Integer... strings) {
        mMainActivity.runOnUiThread(new Runnable() {
            public void run() {
                mDialog = ProgressDialog.show(mMainActivity, null, MainActivity.getInstance().getString(R.string.sync_in_progress), true);
            }
        });

        HashMap<Integer, ArrayList<HashMap<String, String>>> data;

        try {
            data = new Parser(String.format("%s/plany/o%d.html", mUrl, mClass)).parseLessons();
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return -1;
        }

        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        SQLiteDatabase db = databaseHandler.getDB(mMainActivity);
        databaseHandler.createTables(mMainActivity);

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
            mMainActivity.runOnUiThread(new Runnable() {
                public void run() {
                    new AlertDialog.Builder(mMainActivity)
                            .setTitle(MainActivity.getInstance().getString(R.string.error_title))
                            .setMessage(MainActivity.getInstance().getString(R.string.error_offline))
                            .setPositiveButton(android.R.string.yes, null)
                            .show();
                }
            });
        }

        Utils.refreshWidgets(mMainActivity);
        mMainActivity.stopService(new Intent(mMainActivity, LessonNotifyService.class));
        mMainActivity.startService(new Intent(mMainActivity, LessonNotifyService.class));

        MainActivityAdapter mMainActivityAdapter = new MainActivityAdapter(mMainActivity.getSupportFragmentManager());
        mMainActivity.getPager().setAdapter(mMainActivityAdapter);

        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}