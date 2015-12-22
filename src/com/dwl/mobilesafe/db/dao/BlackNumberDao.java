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
	 * 构造方法中完成数据库打开帮助类的初始化
	 * 
	 * @param context
	 */
	public BlackNumberDao(Context context) {
		this.helper = new BlackNumberDBOpenHelper(context);
	}

	/**
	 * 添加一条黑名单数据
	 * 
	 * @param number
	 *            手机号码
	 * @param mode
	 *            拦截模式 1电话拦截 2短信拦截 3电话+短信拦截
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
	 * 删除一条黑名单
	 * 
	 * @param number
	 *            黑名单号码
	 */
	public void delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blacknumber", "number=?", new String[] { number });
		db.close();
	}

	/**
	 * 修改一条黑名单
	 * 
	 * @param number
	 *            按照黑名单号码查询
	 * @param mode
	 *            修改对应黑名单的拦截模式
	 */
	public void update(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		db.update("blacknumber", values, "number=?", new String[] { number });
		db.close();
	}

	/**
	 * 查询某条黑名单是否存在
	 * 
	 * @param number
	 *            按照手机号查询
	 * @return 存在返回true 不存在发挥flase
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
	 * 查询对应手机号的拦截模式
	 * 
	 * @param number
	 *            手机号码
	 * @return 拦截模式 1电话拦截 2短信拦截 3电话+短信拦截 null代表不存在
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
	 * 查询全部黑名单
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
