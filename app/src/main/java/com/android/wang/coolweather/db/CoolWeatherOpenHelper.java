package com.android.wang.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/5/10.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
    public static final String Create_Provice="create table Province("+"id integer primary key antoincrement,"+"province_name text," +
            "province_code text)";
    public static final String Create_City="create table City("+"id integer primary key antoincrement,"+"city_name text," +
            "city_code text,"+"province_id integer)";
    public static final String Create_County="create table County("+"id integer primary key antoincrement,"+"county_name text," +
            "county_code text,"+"city_id integer)";
    public CoolWeatherOpenHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version)
    {
        super(context,name,factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(Create_Provice);
        db.execSQL(Create_City);
        db.execSQL(Create_County);
    }
    @Override
    public void  onUpgrade(SQLiteDatabase db,int oldversion,int newversion)
    {

    }
}
