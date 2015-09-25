package com.luk.timetable2.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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

import com.luk.timetable2.R;
import com.luk.timetable2.Utils;
import com.luk.timetable2.listeners.MainActivity.DayChangeListener;
import com.luk.timetable2.listeners.MainActivity.DeleteDialogListener;
import com.luk.timetable2.tasks.ClassesTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LayoutInflater inflater;
    private static MainActivity instance;
    private static int currentTheme;
    public int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // save instance
        instance = this;

        // setup layout
        currentTheme = Utils.getCurrentTheme(this);
        setTheme(currentTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        daySelector.setOnItemSelectedListener(new DayChangeListener());
        daySelector.setSelection(day);

        // Hide title name
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        if (currentTheme != Utils.getCurrentTheme(this)) {
            recreate();
        }

        super.onResume();
    }

    public void loadLessons(final int day) {
        LinearLayout container = (LinearLayout) findViewById(R.id.mainLayout);
        container.removeAllViews();

        TypedValue fontColor = new TypedValue();
        TypedValue backgroundColor = new TypedValue();
        getTheme().resolveAttribute(R.attr.template_fontColor, fontColor, true);
        getTheme().resolveAttribute(R.attr.template_backgroundColor, backgroundColor, true);

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

            View view = null;

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                view = inflater.inflate(R.layout.template_lesson, null);
            } else {
                switch (currentTheme) {
                    case R.style.AppTheme_Dark:
                        view = inflater.inflate(R.layout.template_lesson_dark, null);
                        break;
                    case R.style.AppTheme_Dark_Red:
                        view = inflater.inflate(R.layout.template_lesson_dark_red, null);
                        break;
                    case R.style.AppTheme_Light:
                        view = inflater.inflate(R.layout.template_lesson_light, null);
                        break;
                    case R.style.AppTheme_Light_Red:
                        view = inflater.inflate(R.layout.template_lesson_light_red, null);
                        break;
                }
            }

            CardView cardView = (CardView) view.findViewById(R.id.card_lesson);

            TextView lesson = (TextView) view.findViewById(R.id.lesson);
            lesson.setText(_lesson.substring(0, _lesson.length() - 1));

            // set lesson additional info { hours, classroom }
            TextView info = (TextView) view.findViewById(R.id.info);
            info.setText(_hour + "\n" + _room.substring(0, _room.length() - 3));

            // set long click listener
            view.findViewById(R.id.card_lesson).setOnLongClickListener(new DeleteDialogListener());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setBackgroundColor(backgroundColor.data);
                lesson.setTextColor(fontColor.data);
                info.setTextColor(fontColor.data);
            }

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
