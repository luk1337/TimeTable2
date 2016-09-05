package com.luk.timetable2.listeners.SettingsActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.preference.Preference;

/**
 * Created by luk on 9/22/15.
 */
public class ThemeChangeListener implements Preference.OnPreferenceChangeListener {
    private final Activity mActivity;

    public ThemeChangeListener(Activity activity) {
        mActivity = activity;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        mActivity.recreate();

        return true;
    }
}
