package com.luk.timetable2.listeners.MainActivity;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Spinner;

import com.luk.timetable2.R;
import com.luk.timetable2.activities.MainActivity;

/**
 * Created by luk on 9/28/15.
 */
public class OnSwipeListener implements View.OnTouchListener {
    private static int MIN_DISTANCE = 20;
    private float start, end;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                start = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                end = event.getX();

                Spinner daySelector = (Spinner) MainActivity.getInstance().findViewById(R.id.day);
                int currentPosition = daySelector.getSelectedItemPosition();

                if (Math.abs(end - start) > MIN_DISTANCE) {
                    if (end > start && currentPosition > 0) {
                        daySelector.setSelection(currentPosition - 1);
                    } else if (end < start && currentPosition < 4) {
                        daySelector.setSelection(currentPosition + 1);
                    }
                }

                break;
        }

        return true;
    }
}
