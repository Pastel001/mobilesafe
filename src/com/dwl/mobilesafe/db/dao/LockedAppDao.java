package com.dwl.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.dwl.mobilesafe.db.LockedAppDBOpenHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class LockedAppDao {
	private LockedAppDBOpenHelper helper;
	private Context context;
	private ContentResolver resolver;

	/**
	 * 构造方法中完成数据库打开帮助类的初始化
	 * 
	 * @param context
	 */
	public LockedAppDao(Context context) {
		this.helper = new LockedAppDBOpenHelper(context);
		this.context = context;
		this.resolver = this.context.getContentResolver();
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
		Uri uri = Uri.parse("content://com.dwl.mobilesafe.lockedapp");
		resolver.notifyChange(uri, null);
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
		Uri uri = Uri.parse("content://com.dwl.mobilesafe.lockedapp");
		resolver.notifyChange(uri, null);
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

	/**
	 * 查询所有被锁定的app包名
	 * 
	 * @return
	 */
	public List<String> findAll() {
		List<String> result = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("lockedapp", null, null, null, null, null,
				null);
		while (cursor.moveToNext()) {
			String packageNmae = cursor.getString(1);
			result.add(packageNmae);
		}
		cursor.close();
		db.close();
		return result;
	}

}
