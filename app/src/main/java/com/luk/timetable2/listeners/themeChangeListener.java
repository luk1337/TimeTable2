package com.luk.timetable2.listeners;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.luk.timetable2.MainActivity;
import com.luk.timetable2.R;

/**
 * Created by luk on 9/12/15.
 */
public class themeChangeListener {
    private static themeChangeListener instance;
    private static SharedPreferences sharedPref;
    private MainActivity activity;

    public void setupListener() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getInstance());
        sharedPref.registerOnSharedPreferenceChangeListener(themeListener);

        activity = MainActivity.getInstance();
        activity.light_theme = sharedPref.getBoolean("light_theme", false);
        activity.setTheme(activity.light_theme ? R.style.AppTheme_Light : R.style.AppTheme);
    }

    SharedPreferences.OnSharedPreferenceChangeListener themeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key.equals("light_theme")) {
                activity.light_theme = sharedPref.getBoolean("light_theme", false);
                activity.setTheme(activity.light_theme ? R.style.AppTheme_Light : R.style.AppTheme);
                activity.recreate();
            }
        }
    };

    public static themeChangeListener getInstance() {
        if (instance == null) {
            instance = new themeChangeListener();
        }

        return instance;
    }
}
