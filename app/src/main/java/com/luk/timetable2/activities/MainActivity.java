package com.luk.timetable2.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.luk.timetable2.R;
import com.luk.timetable2.Utils;
import com.luk.timetable2.services.LessonNotify.LessonNotifyService;
import com.luk.timetable2.listeners.MainActivity.DayChangeListener;
import com.luk.timetable2.listeners.MainActivity.DeleteDialogListener;
import com.luk.timetable2.listeners.MainActivity.OnSwipeListener;
import com.luk.timetable2.services.WidgetRefresh.WidgetRefreshService;
import com.luk.timetable2.tasks.ClassesTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LayoutInflater mInflater;
    private static MainActivity sInstance;
    private static int sCurrentTheme;
    public int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // save sInstance
        sInstance = this;

        // setup layout
        sCurrentTheme = Utils.getCurrentTheme(this);
        setTheme(sCurrentTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // define some variables we may need
        mInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        // Set on swipe listener
        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new OnSwipeListener());
        findViewById(R.id.mainScrollView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });

        // Start services
        startService(new Intent(this, WidgetRefreshService.class));
        startService(new Intent(this, LessonNotifyService.class));
    }

    @Override
    protected void onResume() {
        if (sCurrentTheme != Utils.getCurrentTheme(this)) {
            recreate();
        }

        super.onResume();
    }

    public void loadLessons(final int day) {
        LinearLayout container = (LinearLayout) findViewById(R.id.mainLayout);
        container.removeAllViews();

        ArrayList<List<String>> hours = Utils.getHours(this, day);
        Integer[] colors = Utils.getColorsForVariant(sCurrentTheme);

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

            View view = mInflater.inflate(R.layout.template_lesson, null);

            if (view != null) {
                CardView cardView = (CardView) view.findViewById(R.id.card_lesson);

                TextView lesson = (TextView) view.findViewById(R.id.lesson);
                lesson.setText(_lesson.substring(0, _lesson.length() - 1));

                // set lesson additional info { hours, classroom }
                TextView info = (TextView) view.findViewById(R.id.info);
                info.setText(_hour + "\n" + _room.substring(0, _room.length() - 3));

                // set long click listener
                view.findViewById(R.id.card_lesson).setOnLongClickListener(new DeleteDialogListener());

                // set colors
                cardView.setCardBackgroundColor(getApplicationContext().getResources().getColor(colors[0]));
                lesson.setTextColor(getApplicationContext().getResources().getColor(colors[1]));
                info.setTextColor(getApplicationContext().getResources().getColor(colors[1]));

                // add to view
                container.addView(view);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.update) {
            if (!new Utils().isOnline(this)) {
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
        return sInstance;
    }
}
