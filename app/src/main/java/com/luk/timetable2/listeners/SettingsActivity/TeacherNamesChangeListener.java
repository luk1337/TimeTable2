package com.luk.timetable2.listeners.SettingsActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.preference.Preference;

import com.luk.timetable2.Utils;
import com.luk.timetable2.activities.MainActivity;

/**
 * Created by luk on 9/22/15.
 */
public class TeacherNamesChangeListener implements Preference.OnPreferenceChangeListener {
    private final Activity mActivity;

    public TeacherNamesChangeListener(Activity activity) {
        mActivity = activity;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Utils.refreshWidgets(mActivity);

        return true;
    }
}
