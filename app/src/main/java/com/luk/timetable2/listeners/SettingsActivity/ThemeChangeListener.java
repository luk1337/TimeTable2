package com.luk.timetable2.listeners.SettingsActivity;

import android.preference.Preference;

import com.luk.timetable2.activities.SettingsActivity;

/**
 * Created by luk on 9/22/15.
 */
public class ThemeChangeListener implements Preference.OnPreferenceChangeListener {
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SettingsActivity.getInstance().recreate();

        return true;
    }
}
