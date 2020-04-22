package com.example.ownmap.activity.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBServer extends SQLiteOpenHelper {

    public final static String dbName = "User";

    public DBServer(Context context) {
        super(context,dbName,null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_person_sql = "CREATE TABLE if not exists [person_tab](_id integer primary key autoincrement,name text, number text)";
        String create_shoucang_sql = "CREATE TABLE if not exists [shoucang_tab](_id integer primary key autoincrement,url text, title text,address text,time text,rand text,lat text,lon text,number text)";
        db.execSQL(create_person_sql);
        db.execSQL(create_shoucang_sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
