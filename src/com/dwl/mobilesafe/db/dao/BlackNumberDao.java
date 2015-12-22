package com.dwl.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dwl.mobilesafe.db.BlackNumberDBOpenHelper;
import com.dwl.mobilesafe.dto.BlackNumber;

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

	/**
	 * ��ѯȫ��������
	 * 
	 * @return
	 */
	public List<BlackNumber> findAll() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<BlackNumber> list = new ArrayList<BlackNumber>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db
				.query("blacknumber", new String[] { "number", "mode" }, null,
						null, null, null, null);
		while (cursor.moveToNext()) {
			BlackNumber blackNumber = new BlackNumber();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			blackNumber.setNumber(number);
			blackNumber.setMode(mode);
			list.add(blackNumber);
		}
		cursor.close();
		db.close();
		return list;
	}

}
