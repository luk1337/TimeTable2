package com.luk.timetable2.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.luk.timetable2.R;
import com.luk.timetable2.Utils;
import com.luk.timetable2.models.Lesson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by luk on 5/12/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext = null;
    private Integer[] mWidgetColors;
    private ArrayList<String[]> mLessons = new ArrayList<>();

    public WidgetViewsFactory(Context mContext, String variant) {
        this.mContext = mContext;
        this.mWidgetColors = Utils.getWidgetColorsForVariant(variant);
    }

    @Override
    public void onCreate() {
        loadLessons();
    }

    private void loadLessons() {
        mLessons.clear();

        // load mLessons for current day
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0 || day == 6) day = 1; // set monday

        List<String> hours = Utils.getHours(day);
        for (String hour : hours) {
            List<Lesson> lessons = Utils.getLessonsForHour(day, hour);
            String name = "";
            String room = "";

            for (Lesson lesson : lessons) {
                name += lesson.getName();
                room += lesson.getClassRoom();

                if (lesson.getGroupNumber() != null) {
                    name += String.format(" (%s)", lesson.getGroupNumber());
                }

                if (lesson.getTeacher() != null) {
                    name += String.format(" [%s]", lesson.getTeacher());
                }

                if (lessons.size() > 1 && lessons.indexOf(lesson) + 1 < lessons.size()) {
                    name += "\n";
                    room += " / ";
                }
            }

            this.mLessons.add(new String[]{name, String.format("%s\n%s", hour, room)});
        }
    }

    @Override
    public void onDataSetChanged() {
        loadLessons();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mLessons.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews lesson = new RemoteViews(mContext.getPackageName(), R.layout.widget_lesson);

        // set lesson name
        lesson.setTextViewText(R.id.lesson, mLessons.get(position)[0]);

        // set lesson additional info { hours, classroom }
        lesson.setTextViewText(R.id.info, mLessons.get(position)[1]);

        // set colors
        lesson.setInt(R.id.background, "setBackgroundResource", mWidgetColors[0]);
        lesson.setTextColor(R.id.lesson, ContextCompat.getColor(mContext, mWidgetColors[2]));
        lesson.setTextColor(R.id.info, ContextCompat.getColor(mContext, mWidgetColors[2]));

        return lesson;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
