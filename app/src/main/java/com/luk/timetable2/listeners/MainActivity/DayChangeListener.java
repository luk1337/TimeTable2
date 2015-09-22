package com.luk.timetable2.listeners.MainActivity;

import android.view.View;
import android.widget.AdapterView;

import com.luk.timetable2.activities.MainActivity;

/**
 * Created by luk on 9/13/15.
 */
public class DayChangeListener implements AdapterView.OnItemSelectedListener {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        MainActivity.getInstance().day = position;
        MainActivity.getInstance().loadLessons(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
