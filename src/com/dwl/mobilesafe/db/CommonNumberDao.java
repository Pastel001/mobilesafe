package com.dwl.mobilesafe.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumberDao {

	public static int getGroupCount(SQLiteDatabase db) {
		Cursor cursor = db.rawQuery("select count(*) from classlist", null);
		cursor.moveToFirst();
		int goupCount = cursor.getInt(0);
		cursor.close();
		return goupCount;
	}

	public static int getChildrenCount(SQLiteDatabase db, int groupPosition) {
		int newPosition = groupPosition + 1;
		Cursor cursor = db.rawQuery("select count(*) from table" + newPosition,
				null);
		cursor.moveToFirst();
		int childCount = cursor.getInt(0);
		cursor.close();
		return childCount;
	}

	public static String getGroupName(SQLiteDatabase db, int groupPosition) {
		int newPositon = groupPosition + 1;
		Cursor cursor = db.rawQuery("select name from classlist where idx=?",
				new String[] { newPositon + "" });
		cursor.moveToFirst();
		String groupName = cursor.getString(0);
		cursor.close();
		return groupName;
	}

	public static String getChild(SQLiteDatabase db, int groupPosition,
			int childPosition) {
		int newGroupPosition = groupPosition + 1;
		int newChildPosition = childPosition + 1;
		Cursor cursor = db.rawQuery("select name,number from table"
				+ newGroupPosition + " where _id=?",
				new String[] { newChildPosition + "" });
		cursor.moveToFirst();
		String child = cursor.getString(0) + "\n" + cursor.getString(1);
		cursor.close();
		return child;
	}

}
