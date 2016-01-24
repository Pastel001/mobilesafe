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
	 * ���췽����������ݿ�򿪰�����ĳ�ʼ��
	 * 
	 * @param context
	 */
	public LockedAppDao(Context context) {
		this.helper = new LockedAppDBOpenHelper(context);
		this.context = context;
		this.resolver = this.context.getContentResolver();
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
		Uri uri = Uri.parse("content://com.dwl.mobilesafe.lockedapp");
		resolver.notifyChange(uri, null);
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
		Uri uri = Uri.parse("content://com.dwl.mobilesafe.lockedapp");
		resolver.notifyChange(uri, null);
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

	/**
	 * ��ѯ���б�������app����
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
