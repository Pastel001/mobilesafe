package com.dwl.mobilesafe.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
	public static final String path = "/data/data/com.dwl.mobilesafe/files/address.db";

	public static String getAddress(String number) {
		String address = number;
		if (number.matches("^1[3458]\\d{5,9}$")) {
			SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READONLY);
			Cursor cursor = db.rawQuery(
					"select location from data2 where id=(select outkey from data1 where id=?)",
					new String[] { number.substring(0, 7) });
			while (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			cursor.close();
			db.close();
		}
		return address;
	}
}
