package com.dwl.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LockedAppDBOpenHelper extends SQLiteOpenHelper {

	public LockedAppDBOpenHelper(Context context) {
		super(context, "lockedapp.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// _id主键自增长，packagename 锁定的应用包名
		db.execSQL("create table lockedapp (_id integer primary key autoincrement,packagename varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
