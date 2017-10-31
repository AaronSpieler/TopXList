package com.whynoteasy.topxlist;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Whatever on 31.10.2017.
 */

public class AppDatabaseDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "appdatabase.db";

    public AppDatabaseDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_XLISTTABLE_ENTRIES);
        db.execSQL(SQL_CREATE_XELEMTABLE_ENTRIES);
        db.execSQL(SQL_CREATE_XTAGTABLE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
        DONT DO ANYTHING FOR NOW
        TUTORIAL FOR LATER: https://thebhwgroup.com/blog/how-android-sqlite-onupgrade
        */
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    //PREPARED STRINGS
    private static final String SQL_CREATE_XLISTTABLE_ENTRIES =
            "CREATE TABLE " + AppDatabaseContract.XListTable.TABLE_NAME + " (" +
                    AppDatabaseContract.XListTable._ID + " INTEGER PRIMARY KEY," +
                    AppDatabaseContract.XListTable.COLUMN_NAME_XLISTTITLE + " TEXT," +
                    AppDatabaseContract.XListTable.COLUMN_NAME_XLISTSHORTDESCRIPTION + " TEXT)" +
                    AppDatabaseContract.XListTable.COLUMN_NAME_XLISTLONGDESCRIPTION + " TEXT)" +
                    AppDatabaseContract.XListTable.COLUMN_NAME_XLISTNUM + " INTEGER)";

    private static final String SQL_DELETE_XLISTTABLE_ENTRIES =
            "DROP TABLE IF EXISTS " + AppDatabaseContract.XListTable.TABLE_NAME;

    private static final String SQL_CREATE_XELEMTABLE_ENTRIES =
            "CREATE TABLE " + AppDatabaseContract.XElemTable.TABLE_NAME + " (" +
                    AppDatabaseContract.XElemTable._ID + " INTEGER PRIMARY KEY," +
                    AppDatabaseContract.XElemTable.COLUMN_XELEMTITLE + " TEXT," +
                    AppDatabaseContract.XElemTable.COLUMN_XELEMDESCRIPTION + " TEXT," +
                    AppDatabaseContract.XElemTable.COLUMN_XELEMNUM + " INTEGER)" +
                    AppDatabaseContract.XElemTable.COLUMN_XLISTID + " INTEGER)";

    private static final String SQL_DELETE_XELEMTABLE_ENTRIES =
            "DROP TABLE IF EXISTS " + AppDatabaseContract.XElemTable.TABLE_NAME;

    private static final String SQL_CREATE_XTAGTABLE_ENTRIES =
            "CREATE TABLE " + AppDatabaseContract.XTagTable.TABLE_NAME + " (" +
                    AppDatabaseContract.XTagTable._ID + " INTEGER PRIMARY KEY," +
                    AppDatabaseContract.XTagTable.COLUMN_NAME_XTAGNAME + " TEXT," +
                    AppDatabaseContract.XTagTable.COLUMN_NAME_XLISTID + " INTEGER,";

    private static final String SQL_DELETE_XTAGTABLE_ENTRIES =
            "DROP TABLE IF EXISTS " + AppDatabaseContract.XTagTable.TABLE_NAME;
}
