package com.luk.timetable2.tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.luk.timetable2.R;
import com.luk.timetable2.Utils;
import com.luk.timetable2.activities.MainActivity;
import com.luk.timetable2.parser.Lesson;
import com.luk.timetable2.parser.Parser;
import com.luk.timetable2.services.RegisterReceivers;

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

    public SyncTask(MainActivity mainActivity, int mClass) {
        mMainActivity = mainActivity;

        // load prefs
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mMainActivity);

        this.mClass = mClass;
        this.mUrl = sharedPref.getString("school", "");
    }

    @Override
    protected Integer doInBackground(Integer... strings) {
        mMainActivity.runOnUiThread(new Runnable() {
            public void run() {
                mDialog = ProgressDialog.show(
                        mMainActivity,
                        null,
                        mMainActivity.getString(R.string.sync_in_progress),
                        true
                );
            }
        });

        HashMap<Integer, ArrayList<Lesson>> data;

        try {
            data = new Parser(mUrl).parseLessons(mClass);
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return -1;
        }

        com.luk.timetable2.models.Lesson.deleteAll(Lesson.class);

        for (int day = 1; day <= 5; day++) {
            if (data.get(day) == null) continue;

            for (Lesson l : data.get(day)) {
                String name = l.getName();
                String teacher = l.getTeacher();
                String room = l.getRoom();
                String hour = l.getHour();
                String group = l.getGroup();

                com.luk.timetable2.models.Lesson lesson = new com.luk.timetable2.models.Lesson();
                lesson.setName(name);
                lesson.setTeacher(teacher);
                lesson.setClassRoom(room);
                lesson.setGroupNumber(group);
                lesson.setTime(hour);
                lesson.setDay(day);
                lesson.setHidden(false);
                lesson.save();
            }
        }

        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        if (result == -1) {
            mMainActivity.runOnUiThread(new Runnable() {
                public void run() {
                    new AlertDialog.Builder(mMainActivity)
                            .setTitle(mMainActivity.getString(R.string.error_title))
                            .setMessage(mMainActivity.getString(R.string.error_offline))
                            .setPositiveButton(android.R.string.yes, null)
                            .show();
                }
            });
        }

        Utils.refreshWidgets(mMainActivity);
        mMainActivity.refreshContent();
        mMainActivity.sendBroadcast(new Intent(mMainActivity, RegisterReceivers.class));

        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}