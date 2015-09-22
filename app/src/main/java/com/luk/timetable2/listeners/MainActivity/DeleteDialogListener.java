package com.luk.timetable2.listeners.MainActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.view.View;
import android.widget.TextView;

import com.luk.timetable2.activities.MainActivity;
import com.luk.timetable2.R;
import com.luk.timetable2.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luk on 5/7/15.
 */
public class DeleteDialogListener implements View.OnLongClickListener {
    @Override
    public boolean onLongClick(View v) {
        TextView info = (TextView) v.findViewById(R.id.info);
        final String hour = info.getText().toString().split("\n")[0]; // hour

        final ArrayList<List<String>> lessons = Utils.getLessonsForHour(MainActivity.getInstance(), MainActivity.getInstance().day, hour);

        if (lessons != null && lessons.size() > 1) {
            final CharSequence[] items = new String[lessons.size()];;
            final int[] selected = {1};
            int i = 0;

            for (List<String> l : lessons) {
                items[i] = l.get(0);
                i++;
            }

            new AlertDialog.Builder(MainActivity.getInstance())
                    .setTitle(MainActivity.getInstance().getString(R.string.select_class))
                    .setPositiveButton(android.R.string.yes,  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int number) {
                            File dbFile = MainActivity.getInstance().getDatabasePath("db");
                            SQLiteDatabase db = MainActivity.getInstance().openOrCreateDatabase(dbFile.getAbsolutePath(), Context.MODE_PRIVATE, null);
                            int i = 1;

                            SQLiteStatement stmt = db.compileStatement("UPDATE `lessons` SET hidden = '1' WHERE day = ? AND time = ? AND lesson = ?");
                            stmt.bindString(1, String.valueOf(MainActivity.getInstance().day));
                            stmt.bindString(2, hour);

                            for (List<String> l : lessons) {
                                if (i == selected[0]) {
                                    stmt.bindString(3, l.get(0));
                                }

                                i++;
                            }

                            stmt.execute();

                            MainActivity.getInstance().loadLessons(MainActivity.getInstance().day);
                        }
                    })
                    .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int number) {
                            selected[0] = number + 1;
                        }
                    })
                    .show();
        } else {
            new AlertDialog.Builder(MainActivity.getInstance())
                    .setTitle(MainActivity.getInstance().getString(R.string.hide_title))
                    .setMessage(MainActivity.getInstance().getString(R.string.hide_text))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            File dbFile = MainActivity.getInstance().getDatabasePath("db");
                            SQLiteDatabase db = MainActivity.getInstance().openOrCreateDatabase(dbFile.getAbsolutePath(), Context.MODE_PRIVATE, null);

                            SQLiteStatement stmt = db.compileStatement("UPDATE `lessons` SET hidden = '1' WHERE day = ? AND time = ?");
                            stmt.bindString(1, String.valueOf(MainActivity.getInstance().day));
                            stmt.bindString(2, hour);
                            stmt.execute();

                            MainActivity.getInstance().loadLessons(MainActivity.getInstance().day);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }

        return false;
    }
}
