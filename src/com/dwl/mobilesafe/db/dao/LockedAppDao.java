package com.dwl.mobilesafe.db.dao;

import com.dwl.mobilesafe.db.LockedAppDBOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LockedAppDao {
	private LockedAppDBOpenHelper helper;

	/**
	 * ���췽����������ݿ�򿪰�����ĳ�ʼ��
	 * 
	 * @param context
	 */
	public LockedAppDao(Context context) {
		this.helper = new LockedAppDBOpenHelper(context);
	}

	/**
	 * ���һ����������
	 * 
	 * @param packagename
	 *            ����
	 */
	public void add(String packageName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packagename", packageName);
		db.insert("lockedapp", null, values);
		db.close();
	}

	/**
	 * ɾ��һ��������¼
	 * 
	 * @param packagename
	 *            ����
	 */
	public void delete(String packageName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("lockedapp", "packagename=?", new String[] { packageName });
		db.close();
	}

	/**
	 * ��ѯĳ�������Ƿ����
	 * 
	 * @param packagename
	 *            ����
	 * @return ���ڷ���true �����ڷ���flase
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
