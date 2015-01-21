package com.example.yulong.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yulong on 2015/1/21.
 */
public class WeatherOpenHelper extends SQLiteOpenHelper {
    public static String CREATE_PROVINCE = "create table Province(id integer primary key autoincrement,\n" +
            "province_name text," +
            "province_code text" +
            ")";
    public static String CREATE_CITY = "create table city(id integer primary key autoincrement,\n" +
            "city_name text," +
            "city_code text," +
            "province_id integer" +
            ")";
    public static String CREATE_COUNTY = "create table county(id integer primary key autoincrement,\n" +
            "county_name text," +
            "county_code text," +
            "city_id integer" +
            ")";
    public WeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
