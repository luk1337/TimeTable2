package com.luk.timetable2.tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.luk.timetable2.MainActivity;
import com.luk.timetable2.Parser;
import com.luk.timetable2.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by LuK on 2015-05-01.
 */
public class ClassesTask extends AsyncTask<Integer, Integer, Integer> {
    private ProgressDialog dialog;
    private String _api;
    private HashMap<Integer, String> data;

    public ClassesTask(MainActivity activity) {
        // load prefs
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        this._api = sharedPref.getString("school", "");
    }

    @Override
    protected Integer doInBackground(Integer... strings) {
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            public void run() {
                dialog = ProgressDialog.show(MainActivity.getInstance(), null, MainActivity.getInstance().getString(R.string.sync_in_progress), true);
            }
        });

        data = null;

        try {
            data = new Parser(String.format("%s/lista.html", _api)).parseClasses();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        if (result == -1) {
            MainActivity.getInstance().runOnUiThread(new Runnable() {
                public void run() {
                    new AlertDialog.Builder(MainActivity.getInstance())
                            .setTitle(MainActivity.getInstance().getString(R.string.error_title))
                            .setMessage(MainActivity.getInstance().getString(R.string.error_offline))
                            .setPositiveButton(android.R.string.yes, null)
                            .show();
                }
            });
        } else {
            final CharSequence[] items = new String[data.size()];;
            final int[] selected = {1};

            for (int i = 1; i <= data.size(); i++) {
                items[i -1] = data.get(i);
            }

            MainActivity.getInstance().runOnUiThread(new Runnable() {
                public void run() {
                    new AlertDialog.Builder(MainActivity.getInstance())
                            .setTitle(MainActivity.getInstance().getString(R.string.select_class))
                            .setPositiveButton(android.R.string.yes,  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int number) {
                                    new SyncTask(MainActivity.getInstance(), selected[0]).execute();
                                }
                            })
                            .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int number) {
                                    selected[0] = number + 1;
                                }
                            })
                            .show();
                }
            });
        }

        if(dialog != null) {
            dialog.dismiss();
        }
    }
}