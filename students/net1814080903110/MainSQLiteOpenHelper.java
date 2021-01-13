package edu.hzuapps.androidlabs.net;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String db_name = "MySchedule";
    private static final int version = 1;
    public MainSQLiteOpenHelper(Context context) {
        super(context, db_name, null, version);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String  sql ="create table schedules(" +
                "id Integer primary key autoincrement," +
                "scheduleDetail varchar(50)," +
                "time varchar(30)" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists courses");
        onCreate(db);
    }
}