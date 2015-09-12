package com.luk.timetable2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luk.timetable2.listeners.deleteDialogListener;
import com.luk.timetable2.tasks.ClassesTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity implements ActionBar.OnNavigationListener {
    private LayoutInflater inflater;
    static MainActivity instance;
    public int day;
    public boolean light_theme = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.instance = this;

        // load light theme
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(themeListener);

        if (sharedPref.getBoolean("light_theme", false)) {
            light_theme = true;
            super.setTheme(R.style.AppTheme_Light);
        }

        setContentView(R.layout.layout_main);

        // define some variables we may need
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ActionBar actionBar = getActionBar();

        // load list of days Mon -> Fri
        List<String> days = Arrays.asList(getResources().getStringArray(R.array.days));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, days);

        // load lessons for current day
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (day == -1 || day == 5) day = 0; // set monday

        // Set navigation on the action bar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            actionBar.setListNavigationCallbacks(arrayAdapter, this);
            actionBar.setSelectedNavigationItem(day);
        }
    }

    SharedPreferences.OnSharedPreferenceChangeListener themeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key.equals("light_theme")) {
                recreate();
            }
        }
    };

    public void loadLessons(final int day) {
        LinearLayout container = (LinearLayout) findViewById(R.id.mainLayout);
        container.removeAllViews(); // remove current set of lessons

        ArrayList<List<String>> hours = Utils.getHours(this, day);

        if (hours == null) return;

        for (List<String> hour : hours) {
            ArrayList<List<String>> lessons = Utils.getLessonsForHour(this, day, hour.get(0));

            if (lessons == null) return;

            String _lesson = "";
            String _room = "";
            String _hour = hour.get(0);

            for (List<String> l : lessons) {
                // set lesson names
                _lesson += l.get(0) + "\n";

                // set rooms
                _room += l.get(1) + " / ";
            }

            View view = inflater.inflate(light_theme ? R.layout.template_lesson_light : R.layout.template_lesson, null);

            TextView lesson = (TextView) view.findViewById(R.id.lesson);
            lesson.setText(_lesson.substring(0, _lesson.length() - 1));
            lesson.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.calculateFontSize(_lesson.substring(0, _lesson.length() - 1)));

            // set lesson additional info { hours, classroom }
            TextView info = (TextView) view.findViewById(R.id.info);
            info.setText(_hour + "\n" + _room.substring(0, _room.length() - 3));

            // set long click listener (currently disabled)
            view.findViewById(R.id.card_lesson).setOnLongClickListener(new deleteDialogListener());

            // add to view
            container.addView(view);
        }
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        day = i;
        loadLessons(day);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.update) {
            if (!new Utils().isOnline()) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.error_title))
                        .setMessage(getString(R.string.error_offline))
                        .setPositiveButton(android.R.string.yes, null).show();
                return false;
            }

            new ClassesTask(this).execute();
        } else if (item.getItemId() == R.id.settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        }

        return false;
    }

    public static MainActivity getInstance() {
        return instance;
    }
}
