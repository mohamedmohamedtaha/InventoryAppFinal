package com.imagine.mohamedtaha.store.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.imagine.mohamedtaha.store.data.TaskContract.TaskEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by MANASATT on 25/11/17.
 */

public class TaskDbHelper extends SQLiteOpenHelper {
    //the name of the database
    public static final String DATABASE_NAME = "stores.db";
    // If you change the database schema, you must increment the database version
    private static final int VERSION = 8;
    Context mContext;

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * Called when the stores database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create  tables (careful to follow sQL formatting rules)
        //Table Caregory
        final String CREATE_TABLE_CATEGORIES =
                "CREATE TABLE " + TaskEntry.TABLE_CATEGORIES + " (" + TaskEntry._ID +
                        " INTEGER PRIMARY KEY, " + TaskEntry.KEY_PRODUCT_NAME + " TEXT NOT NULL, " +
                        TaskEntry.KEY_SUPPLIER_NAME + " TEXT NOT NULL, " + TaskEntry.KEY_SUPPLIER_PHONE_NUMBER +
                        " TEXT NOT NULL," + TaskEntry.KEY_PRICE + " INTEGER," +
                        TaskEntry.KEY_QUANTITY + " INTEGER," + TaskEntry.KEY_PICTURE + " BLOB" + ")";

        db.execSQL(CREATE_TABLE_CATEGORIES);

    }

    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskEntry.TABLE_CATEGORIES);
        onCreate(db);

    }
}