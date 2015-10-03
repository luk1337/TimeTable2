package com.luk.timetable2.listeners.MainActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.luk.timetable2.R;
import com.luk.timetable2.Utils;
import com.luk.timetable2.activities.MainActivity;
import com.luk.timetable2.tasks.DeleteLessonTask;

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

            for (int i = 0; i < lessons.size(); i++) {
                items[i] = lessons.get(i).get(0);
            }

            new AlertDialog.Builder(mainActivity)
                    .setTitle(mainActivity.getString(R.string.select_lesson))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int number) {
                            new DeleteLessonTask(mainActivity, false, hour, lessons, selected[0]).execute();
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
                            new DeleteLessonTask(mainActivity, true, hour, null, null).execute();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }

        return false;
    }
}
