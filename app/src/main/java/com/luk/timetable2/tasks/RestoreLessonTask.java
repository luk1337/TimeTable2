package com.luk.timetable2.tasks;

import android.os.AsyncTask;

import com.luk.timetable2.models.Lesson;

import java.util.List;

/**
 * Created by LuK on 2015-05-01.
 */
public class RestoreLessonTask extends AsyncTask<Integer, Integer, Integer> {
    private String mId;

    public RestoreLessonTask(String id) {
        mId = id;
    }

    @Override
    protected Integer doInBackground(Integer... strings) {
        List<Lesson> lessons = Lesson.find(Lesson.class, "id = ?", mId);
        Lesson lesson = lessons.get(0);
        lesson.setHidden(false);
        lesson.save();

        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}