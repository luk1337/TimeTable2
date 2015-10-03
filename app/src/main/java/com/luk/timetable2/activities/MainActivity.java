package com.luk.timetable2.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Spinner;

import com.luk.timetable2.R;
import com.luk.timetable2.Utils;
import com.luk.timetable2.listeners.MainActivity.DayChangeListener;
import com.luk.timetable2.services.RegisterReceivers;
import com.luk.timetable2.tasks.ClassesTask;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static MainActivity sInstance;
    public static int sCurrentTheme;
    public int day;
    public ViewPager sViewPager;
    public Spinner mDaySelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Save sInstance
        sInstance = this;

        // Setup layout
        sCurrentTheme = Utils.getCurrentTheme(this);
        setTheme(sCurrentTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();

        // Load lessons for current day
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (day == -1 || day == 5) day = 0; // set monday

        // Set current day
        mDaySelector = (Spinner) findViewById(R.id.day);
        mDaySelector.setOnItemSelectedListener(new DayChangeListener());
        mDaySelector.setSelection(day);

        // Hide title name
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        sViewPager = (ViewPager) findViewById(R.id.pager);
        sViewPager.setAdapter(new MainActivityAdapter(getSupportFragmentManager()));
        sViewPager.addOnPageChangeListener(new DayChangeListener());

        // Start services
        sendBroadcast(new Intent(this, RegisterReceivers.class));
    }

    @Override
    protected void onResume() {
        if (sCurrentTheme != Utils.getCurrentTheme(this)) {
            recreate();
        }

        super.onResume();
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
