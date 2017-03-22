package com.example.tom.demotide;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TOM on 2017/3/22.
 */

public class MyDBhelper3 extends SQLiteOpenHelper {
    public MyDBhelper3 (Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String create =
                ("CREATE TABLE tblTable3 (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "timeUp TEXT);");
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}