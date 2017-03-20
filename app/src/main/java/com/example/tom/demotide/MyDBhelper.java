package com.example.tom.demotide;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TOM on 2017/3/20.
 */

public class MyDBhelper extends SQLiteOpenHelper {
    public MyDBhelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String create =
                ("CREATE TABLE tblTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "cProductID TEXT, "
                        + "cProductName TEXT, "
                        + "cGoodsNo TEXT, "
                        + "cUpdateDT TEXT);");
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
