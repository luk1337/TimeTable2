package com.luk.timetable2.tasks;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.luk.timetable2.DatabaseHandler;
import com.luk.timetable2.activities.MainActivity;
import com.luk.timetable2.activities.MainActivityAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LuK on 2015-05-01.
 */
public class DeleteLessonTask extends AsyncTask<Integer, Integer, Integer> {
    private MainActivity mMainActivity;
    private boolean mIsSingleLesson;
    private String mHour;
    private ArrayList<List<String>> mLessons;
    private Integer mLessonSelected;

    public DeleteLessonTask(MainActivity mainActivity, boolean isSingleLesson, String hour, @Nullable ArrayList<List<String>> lessons, @Nullable Integer lessonSelected) {
        mMainActivity = mainActivity;
        mIsSingleLesson = isSingleLesson;
        mHour = hour;
        mLessons = lessons;
        mLessonSelected = lessonSelected;
    }

    @Override
    protected Integer doInBackground(Integer... strings) {
        SQLiteDatabase sqLiteDatabase = DatabaseHandler.getInstance().getDB(mMainActivity);

        if (mIsSingleLesson) {
            SQLiteStatement stmt = sqLiteDatabase.compileStatement("UPDATE `lessons` SET hidden = '1' WHERE day = ? AND time = ?");
            stmt.bindString(1, String.valueOf(mMainActivity.getDay()));
            stmt.bindString(2, mHour);

            stmt.execute();
        } else {
            SQLiteStatement stmt = sqLiteDatabase.compileStatement("UPDATE `lessons` SET hidden = '1' WHERE day = ? AND time = ? AND lesson = ?");
            stmt.bindString(1, String.valueOf(mMainActivity.getDay()));
            stmt.bindString(2, mHour);

            for (int i = 0; i < mLessons.size(); i++) {
                if (i == mLessonSelected) {
                    stmt.bindString(3, mLessons.get(i).get(0));
                }
            }

            stmt.execute();
        }

        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        mMainActivity.runOnUiThread(new Runnable() {
            public void run() {
                mMainActivity.getPager().setAdapter(new MainActivityAdapter(mMainActivity.getSupportFragmentManager()));
            }
        });
    }
}