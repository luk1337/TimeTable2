package com.luk.timetable2.listeners.MainActivity;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;

import com.luk.timetable2.activities.MainActivity;

/**
 * Created by luk on 9/13/15.
 */
public class DayChangeListener implements AdapterView.OnItemSelectedListener, ViewPager.OnPageChangeListener {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        MainActivity.getInstance().day = position;
        MainActivity.getInstance().sViewPager.setCurrentItem(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        MainActivity.getInstance().day = position;
        MainActivity.getInstance().mDaySelector.setSelection(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
