package com.luk.timetable2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.luk.timetable2.activities.MainActivity;

import java.io.File;

/**
 * Created by luk on 9/28/15.
 */
public class DatabaseHandler {
    private static DatabaseHandler _instance;
    private static String DB_FILE = "db";

    public SQLiteDatabase getDB(Context context) {
        File dbFile = context.getDatabasePath(DB_FILE);

        return context.openOrCreateDatabase(dbFile.getAbsolutePath(), Context.MODE_PRIVATE, null);
    }

    public void createTables(Context context) {
        SQLiteDatabase sqLiteDatabase = getDB(context);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS lessons;");
        sqLiteDatabase.execSQL("CREATE TABLE lessons ( _id INTEGER PRIMARY KEY AUTOINCREMENT, day INTEGER, lesson TEXT, room TEXT, time TEXT, hidden TEXT );");
    }

    public static DatabaseHandler getInstance() {
        if (_instance == null) {
            _instance = new DatabaseHandler();
        }

        return _instance;
    }
}