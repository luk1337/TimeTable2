package com.luk.timetable2.listeners.SettingsActivity;

import android.content.Intent;
import android.preference.Preference;

import com.luk.timetable2.activities.SettingsActivity;
import com.luk.timetable2.services.RegisterReceivers;

/**
 * Created by luk on 9/22/15.
 */
public class NotificationsChangeListener implements Preference.OnPreferenceChangeListener {
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SettingsActivity settingsActivity = SettingsActivity.getInstance();
        settingsActivity.sendBroadcast(new Intent(settingsActivity, RegisterReceivers.class));

        return true;
    }
}
