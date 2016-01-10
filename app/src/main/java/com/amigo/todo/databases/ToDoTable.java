package com.amigo.todo.databases;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by sudhanshu.gupta on 07/01/16.
 */
public class ToDoTable {

    private static String TAG = ToDoTable.class.getSimpleName();

    //Database Table
    public static final String TABLE_TODO = "todo";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TASK = "task";
    public static final String COLUMN_SUMMARY = "summary";
    public static final String COLUMN_DUE_DATE = "due_date";
    public static final String COLUMN_IS_DONE = "is_done";

    //Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TODO
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TASK + " text not null, "
            + COLUMN_SUMMARY + " text not null, "
            + COLUMN_IS_DONE + " integer default 0, "
            + COLUMN_DUE_DATE + " text not null"
            + ");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(TAG, "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(database);
    }
}
