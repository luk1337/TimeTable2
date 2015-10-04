package com.luk.timetable2.listeners.SettingsActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.Preference;

import org.apache.commons.io.FilenameUtils;

/**
 * Created by luk on 9/22/15.
 */
public class URLChangeListener implements Preference.OnPreferenceChangeListener {
    private final Activity mActivity;

    public URLChangeListener(Activity activity) {
        mActivity = activity;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences.Editor editor = preference.getSharedPreferences().edit();
        editor.putString("school", fixURL((String) newValue));
        editor.apply();

        mActivity.recreate();

        return false;
    }

    private String fixURL(String url) {
        if (!(url.startsWith("http://") || url.startsWith("https://")) && url.length() > 0) {
            url = "http://" + url;
        }

        if (FilenameUtils.getExtension(url).length() == 0) {
            return FilenameUtils.getFullPath(url) + FilenameUtils.getBaseName(url);
        }

        if (FilenameUtils.getFullPath(url).equals("http:/") ||
                FilenameUtils.getFullPath(url).equals("http://")) {
            return FilenameUtils.getFullPath(url + "/");
        }

        return FilenameUtils.getFullPath(url);
    }
}
