package com.example.tom.demotide;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TOM on 2017/3/23.
 */

public class MyDBhelper4 extends SQLiteOpenHelper {
    public MyDBhelper4(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String create =
                ("CREATE TABLE tblTable4 (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "LackNo TEXT, "
                        + "LackName TEXT);");
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
