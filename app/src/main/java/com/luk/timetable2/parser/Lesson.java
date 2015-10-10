package com.luk.timetable2.parser;

/**
 * Created by luk on 10/10/15.
 */
public class Lesson {
    private String mName;
    private String mRoom;
    private String mHour;
    private String mGroup;

    public Lesson(String name, String room, String hour, String group) {
        mName = name;
        mRoom = room;
        mHour = hour;
        mGroup = group;
    }

    public String getName() {
        return mName;
    }

    public String getRoom() {
        return mRoom;
    }

    public String getHour() {
        return mHour;
    }

    public String getGroup() {
        return mGroup;
    }
}
