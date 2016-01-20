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
		// _id������������packagename ������Ӧ�ð���
		db.execSQL("create table lockedapp (_id integer primary key autoincrement,packagename varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
