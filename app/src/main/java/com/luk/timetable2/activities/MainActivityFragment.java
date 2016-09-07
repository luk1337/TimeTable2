package com.luk.timetable2.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luk.timetable2.R;
import com.luk.timetable2.Utils;
import com.luk.timetable2.listeners.MainActivity.DeleteDialogListener;
import com.luk.timetable2.models.Lesson;

import java.util.List;

/**
 * Created by LuK on 2015-10-03.
 */
public class MainActivityFragment extends Fragment {
    public static final String ARG_DAY = "day";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int day = getArguments().getInt(ARG_DAY);
        View rootView = inflater.inflate(R.layout.layout_lessons, container, false);

        LayoutInflater mInflater =
                (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout mainLayout = (LinearLayout) rootView.findViewById(R.id.mainLayout);
        mainLayout.removeAllViews();

        List<String> hours = Utils.getHours(day);
        Integer[] colors = Utils.getColorsForVariant(Utils.getCurrentTheme(getActivity()));

        for (String hour : hours) {
            List<Lesson> lessons = Utils.getLessonsForHour(day, hour);
            String name = "";
            String room = "";

            for (Lesson lesson : lessons) {
                name += lesson.getName();
                room += lesson.getClassRoom();

                if (lesson.getGroupNumber() != null) {
                    name += String.format(" (%s)", lesson.getGroupNumber());
                }

                if (lessons.size() > 1 && lessons.indexOf(lesson) + 1 < lessons.size()) {
                    name += "\n";
                    room += " / ";
                }
            }

            View template = mInflater.inflate(R.layout.template_lesson, null);
            if (template != null) {
                CardView cardView = (CardView) template.findViewById(R.id.card_lesson);

                TextView lesson = (TextView) template.findViewById(R.id.lesson);
                lesson.setText(name);

                // set lesson additional info { hours, classroom }
                TextView info = (TextView) template.findViewById(R.id.info);
                info.setText(String.format("%s\n%s", hour, room));

                // set long click listener
                template.findViewById(R.id.card_lesson).setOnLongClickListener(
                        new DeleteDialogListener((MainActivity) getActivity(), day));

                // set colors
                cardView.setCardBackgroundColor(getActivity().getResources().getColor(colors[0]));
                lesson.setTextColor(ContextCompat.getColor(getActivity(), (colors[1])));
                info.setTextColor(ContextCompat.getColor(getActivity(), colors[1]));

                // add to view
                mainLayout.addView(template);
            }
        }

        return rootView;
    }
}
