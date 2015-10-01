package com.luk.timetable2.listeners.MainActivity;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Spinner;

import com.luk.timetable2.R;
import com.luk.timetable2.activities.MainActivity;

/**
 * Created by luk on 9/28/15.
 */
public class OnSwipeListener extends GestureDetector.SimpleOnGestureListener {
    private static String TAG = "OnSwipeListener";
    private static final int SWIPE_MIN_DISTANCE = 80;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Spinner daySelector = (Spinner) MainActivity.getInstance().findViewById(R.id.day);
        int currentPosition = daySelector.getSelectedItemPosition();

        try {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (currentPosition < 4) daySelector.setSelection(currentPosition + 1);
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (currentPosition > 0) daySelector.setSelection(currentPosition - 1);
            }
        } catch(Exception e) {
            Log.e(TAG, "", e);
        }

        return false;
    }
}
