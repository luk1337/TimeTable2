package com.luk.timetable2.listeners;

import android.preference.Preference;

import com.luk.timetable2.SettingsActivity;

/**
 * Created by luk on 9/22/15.
 */
public class themeChangeListener implements Preference.OnPreferenceChangeListener {
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SettingsActivity.getInstance().recreate();

        return true;
    }
}
