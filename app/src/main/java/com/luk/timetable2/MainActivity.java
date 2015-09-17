package com.luk.timetable2;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.luk.timetable2.listeners.dayChangeListener;
import com.luk.timetable2.listeners.deleteDialogListener;
import com.luk.timetable2.tasks.ClassesTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private LayoutInflater inflater;
    private static MainActivity instance;
    public int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // save instance
        instance = this;

        // setup layout
        setTheme(Utils.getCurrentTheme(this) ? R.style.AppTheme_Light : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        // define some variables we may need
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();

        // load lessons for current day
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (day == -1 || day == 5) day = 0; // set monday

        // set current day
        Spinner daySelector = (Spinner) findViewById(R.id.day);
        daySelector.setOnItemSelectedListener(new dayChangeListener());
        daySelector.setSelection(day);

        // Hide title name
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // setup theme change intent
        Utils.setThemeListener(this);
    }

    public void loadLessons(final int day) {
        LinearLayout container = (LinearLayout) findViewById(R.id.mainLayout);
        container.removeAllViews();

        TypedValue theme = new TypedValue();
        getTheme().resolveAttribute(R.attr.themeName, theme, true);
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

            View view = inflater.inflate(theme.string.toString().equals("light") ? R.layout.template_lesson_light : R.layout.template_lesson, null);

            TextView lesson = (TextView) view.findViewById(R.id.lesson);
            lesson.setText(_lesson.substring(0, _lesson.length() - 1));

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
                        .setMessage(getString(R.string.error_no_network))
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
