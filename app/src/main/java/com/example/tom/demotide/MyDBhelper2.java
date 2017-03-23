package com.example.tom.demotide;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TOM on 2017/3/21.
 * 更新日期和次數
 */

public class MyDBhelper2 extends SQLiteOpenHelper {
    public MyDBhelper2(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String create =
                ("CREATE TABLE tblTable2 (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "cUpdateDT TEXT);");
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
