package com.dwl.mobilesafe.db.dao;

import com.dwl.mobilesafe.db.LockedAppDBOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LockedAppDao {
	private LockedAppDBOpenHelper helper;

	/**
	 * 构造方法中完成数据库打开帮助类的初始化
	 * 
	 * @param context
	 */
	public LockedAppDao(Context context) {
		this.helper = new LockedAppDBOpenHelper(context);
	}

	/**
	 * 添加一条锁定数据
	 * 
	 * @param packagename
	 *            包名
	 */
	public void add(String packageName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packagename", packageName);
		db.insert("lockedapp", null, values);
		db.close();
	}

	/**
	 * 删除一条锁定记录
	 * 
	 * @param packagename
	 *            包名
	 */
	public void delete(String packageName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("lockedapp", "packagename=?", new String[] { packageName });
		db.close();
	}

	/**
	 * 查询某条锁定是否存在
	 * 
	 * @param packagename
	 *            包名
	 * @return 存在返回true 不存在发挥flase
	 */
	public boolean find(String packageName) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("lockedapp", null, "packagename=?",
				new String[] { packageName }, null, null, null);
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}

}
