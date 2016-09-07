package com.luk.timetable2.tasks;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.luk.timetable2.activities.MainActivity;
import com.luk.timetable2.models.Lesson;

import java.util.List;

/**
 * Created by LuK on 2015-05-01.
 */
public class DeleteLessonTask extends AsyncTask<Integer, Integer, Integer> {
    private MainActivity mMainActivity;
    private boolean mIsSingleLesson;
    private String mHour;
    private int mDay;
    private Integer mLessonSelected;

    public DeleteLessonTask(MainActivity mainActivity, boolean isSingleLesson, String hour, int day,
                            @Nullable Integer lessonSelected) {
        mMainActivity = mainActivity;
        mIsSingleLesson = isSingleLesson;
        mHour = hour;
        mDay = day;
        mLessonSelected = lessonSelected;
    }

    @Override
    protected Integer doInBackground(Integer... strings) {
        List<Lesson> lessons = Lesson.find(Lesson.class, "day = ? AND time = ? AND hidden = 0", String.valueOf(mDay), mHour);
        Lesson lesson = lessons.get(mIsSingleLesson ? 0 : mLessonSelected);
        lesson.setHidden(true);
        lesson.save();

        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        mMainActivity.refreshContent();
    }
}