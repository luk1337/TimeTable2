package com.luk.timetable2.listeners.MainActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.view.View;
import android.widget.TextView;

import com.luk.timetable2.DatabaseHandler;
import com.luk.timetable2.R;
import com.luk.timetable2.Utils;
import com.luk.timetable2.activities.MainActivity;
import com.luk.timetable2.activities.MainActivityAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luk on 5/7/15.
 */
public class DeleteDialogListener implements View.OnLongClickListener {
    @Override
    public boolean onLongClick(View v) {
        final MainActivity mainActivity = MainActivity.getInstance();
        final TextView info = (TextView) v.findViewById(R.id.info);
        final String hour = info.getText().toString().split("\n")[0]; // hour

        final ArrayList<List<String>> lessons = Utils.getLessonsForHour(mainActivity, mainActivity.getDay(), hour);

        if (lessons != null && lessons.size() > 1) {
            final CharSequence[] items = new String[lessons.size()];
            final int[] selected = {1};
            int i = 0;

            for (List<String> l : lessons) {
                items[i] = l.get(0);
                i++;
            }

            new AlertDialog.Builder(mainActivity)
                    .setTitle(mainActivity.getString(R.string.select_lesson))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int number) {
                            SQLiteDatabase sqLiteDatabase = DatabaseHandler.getInstance().getDB(mainActivity);

                            SQLiteStatement stmt = sqLiteDatabase.compileStatement("UPDATE `lessons` SET hidden = '1' WHERE day = ? AND time = ? AND lesson = ?");
                            stmt.bindString(1, String.valueOf(mainActivity.getDay()));
                            stmt.bindString(2, hour);

                            for (int i = 0; i < lessons.size(); i++) {
                                if (i == selected[0]) {
                                    stmt.bindString(3, lessons.get(i).get(0));
                                }
                            }

                            stmt.execute();

                            MainActivityAdapter mainActivityAdapter = new MainActivityAdapter(mainActivity.getSupportFragmentManager());
                            mainActivity.getPager().setAdapter(mainActivityAdapter);
                        }
                    })
                    .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int number) {
                            selected[0] = number;
                        }
                    })
                    .show();
        } else {
            new AlertDialog.Builder(mainActivity)
                    .setTitle(mainActivity.getString(R.string.hide_title))
                    .setMessage(mainActivity.getString(R.string.hide_text))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SQLiteDatabase sqLiteDatabase = DatabaseHandler.getInstance().getDB(mainActivity);

                            SQLiteStatement stmt = sqLiteDatabase.compileStatement("UPDATE `lessons` SET hidden = '1' WHERE day = ? AND time = ?");
                            stmt.bindString(1, String.valueOf(mainActivity.getDay()));
                            stmt.bindString(2, hour);
                            stmt.execute();

                            MainActivityAdapter mainActivityAdapter = new MainActivityAdapter(mainActivity.getSupportFragmentManager());
                            mainActivity.getPager().setAdapter(mainActivityAdapter);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }

        return false;
    }
}
