package com.dwl.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dwl.mobilesafe.db.BlackNumberDBOpenHelper;

public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper;

	/**
	 * ���췽����������ݿ�򿪰�����ĳ�ʼ��
	 * 
	 * @param context
	 */
	public BlackNumberDao(Context context) {
		this.helper = new BlackNumberDBOpenHelper(context);
	}

	/**
	 * ���һ������������
	 * 
	 * @param number
	 *            �ֻ�����
	 * @param mode
	 *            ����ģʽ 1�绰���� 2�������� 3�绰+��������
	 */
	public void add(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		db.close();
	}

	/**
	 * ɾ��һ��������
	 * 
	 * @param number
	 *            ����������
	 */
	public void delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blacknumber", "number=?", new String[] { number });
		db.close();
	}

	/**
	 * �޸�һ��������
	 * 
	 * @param number
	 *            ���պ����������ѯ
	 * @param mode
	 *            �޸Ķ�Ӧ������������ģʽ
	 */
	public void update(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		db.update("blacknumber", values, "number=?", new String[] { number });
		db.close();
	}

	/**
	 * ��ѯĳ���������Ƿ����
	 * 
	 * @param number
	 *            �����ֻ��Ų�ѯ
	 * @return ���ڷ���true �����ڷ���flase
	 */
	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blacknumber", null, "number=?",
				new String[] { number }, null, null, null);
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * ��ѯ��Ӧ�ֻ��ŵ�����ģʽ
	 * 
	 * @param number
	 *            �ֻ�����
	 * @return ����ģʽ 1�绰���� 2�������� 3�绰+�������� null��������
	 */
	public String findMode(String number) {
		String mode = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blacknumber", new String[] { "mode" },
				"number=?", new String[] { number }, null, null, null);
		if (cursor.moveToNext()) {
			mode = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return mode;
	}

}
