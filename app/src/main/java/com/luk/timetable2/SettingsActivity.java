package com.luk.timetable2;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // setup layout
        setTheme(Utils.getCurrentTheme(this) ? R.style.AppTheme_Light : R.style.AppTheme);
        super.onCreate(savedInstanceState);

        // setup theme change intent
        Utils.setThemeListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        root.addView(bar, 0);
        
        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.

        bindPreferenceSummaryToValue(findPreference("school"));
        findPreference("school").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        findPreference("light_theme").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                sendBroadcast(new Intent("THEME_CHANGE"));

                return true;
            }
        });

        findPreference("restore_lessons").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                final View layout = View.inflate(SettingsActivity.this, R.layout.settings_restore_lessons, null);
                final LinearLayout view = (LinearLayout) layout.findViewById(R.id.lessons);
                List<String> days = Arrays.asList(getResources().getStringArray(R.array.days));

                for (int day = 0; day < 5; day++) {
                    ArrayList<List<String>> lessons = Utils.getHiddenLessons(getApplicationContext(), day);

                    if (lessons != null && lessons.size() > 0) {
                        TextView textView = new TextView(getApplicationContext());
                        textView.setText(String.format("%s:", days.get(day)));
                        textView.setPadding(0, 15, 0, 15);

                        view.addView(textView);

                        for (List<String> l : lessons) {
                            CheckBox checkBox = new CheckBox(getApplicationContext());
                            checkBox.setTag(l.get(3));
                            checkBox.setText(String.format("%s: %s", l.get(2), l.get(0)));

                            view.addView(checkBox);
                        }
                    }
                }

                if (view.getChildCount() > 0) {
                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle(getString(R.string.restore_title))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    File dbFile = getDatabasePath("db");
                                    SQLiteDatabase db = openOrCreateDatabase(dbFile.getAbsolutePath(), Context.MODE_PRIVATE, null);

                                    for (int i = 0; i < view.getChildCount(); i++) {
                                        if (view.getChildAt(i).getTag() != null) {
                                            CheckBox checkBox = (CheckBox) view.getChildAt(i);

                                            if (checkBox.isChecked()) {
                                                SQLiteStatement stmt = db.compileStatement("UPDATE `lessons` SET hidden = '0' WHERE _id = ?");
                                                stmt.bindString(1, checkBox.getTag().toString());
                                                stmt.execute();
                                            }
                                        }
                                    }

                                    MainActivity.getInstance().recreate();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setView(layout)
                            .show();
                } else {
                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle(getString(R.string.error_title))
                            .setMessage(getString(R.string.restore_empty))
                            .setPositiveButton(android.R.string.yes, null)
                            .show();
                }

                return false;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.

                if (value.toString().equals(""))
                       return false;

                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
}
