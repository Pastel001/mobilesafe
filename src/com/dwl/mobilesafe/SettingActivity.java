package com.dwl.mobilesafe;

import com.dwl.mobilesafe.service.AddressService;
import com.dwl.mobilesafe.service.CallSmsSafeService;
import com.dwl.mobilesafe.ui.SettingClickView;
import com.dwl.mobilesafe.ui.SettingItemView;
import com.dwl.mobilesafe.utils.ServiceStatusUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class SettingActivity extends Activity {

	private SettingItemView siv_update;
	private SettingItemView siv_address;
	private SettingClickView scv_change_bg;
	private SettingClickView scv_change_postion;
	private SettingItemView siv_black_number;
	/**
	 * 起始位置
	 */
	private int startx;
	private int starty;
	private SharedPreferences sp;
	private Editor editor;
	private Intent showAddresService;
	private Intent callSmsSafeService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		editor = sp.edit();
		boolean update = sp.getBoolean("update", false);
		setContentView(R.layout.activity_setting);
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		siv_address = (SettingItemView) findViewById(R.id.siv_address);
		scv_change_bg = (SettingClickView) findViewById(R.id.scv_change_bg);
		scv_change_postion = (SettingClickView) findViewById(R.id.scv_change_positon);
		siv_black_number = (SettingItemView) findViewById(R.id.siv_black_number);
		// 自动更新设置
		if (update) {
			siv_update.setChecked(true);
		} else {
			siv_update.setChecked(false);
		}
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (siv_update.isChecked()) {
					siv_update.setChecked(false);
					editor.putBoolean("update", false);
				} else {
					siv_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		// 号码归属地显示设置
		if (ServiceStatusUtils.isServiceRunning(this,
				"com.dwl.mobilesafe.service.AddressService")) {
			siv_address.setChecked(true);
		} else {
			siv_address.setChecked(false);
		}
		showAddresService = new Intent(this, AddressService.class);
		siv_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (siv_address.isChecked()) {
					siv_address.setChecked(false);
					stopService(showAddresService);
					editor.putBoolean("showAddress", false);
				} else {
					siv_address.setChecked(true);
					ComponentName result = startService(showAddresService);
					editor.putBoolean("showAddress", true);
				}
				editor.commit();
			}
		});
		// 更改归属地toast背景
		final String[] items = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
		int first_which = sp.getInt("which", 0);
		scv_change_bg.setTitle("归属地背景设置");
		scv_change_bg.setDesc(items[first_which]);
		scv_change_bg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SettingActivity.this);
				builder.setTitle("归宿地提示框风格");
				int which = sp.getInt("which", 0);
				builder.setSingleChoiceItems(items, which,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								editor.putInt("which", which);
								editor.commit();
								scv_change_bg.setDesc(items[which]);
								dialog.dismiss();
							}
						});
				builder.setNegativeButton("取消", null);
				builder.show();
			}
		});
		// 修改归属地在屏幕上显示的位置
		scv_change_postion.setTitle("归属地提示框位置");
		scv_change_postion.setDesc("设置归属地提示框位置");
		scv_change_postion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,DragViewActivity.class);
				startActivity(intent);
			}
		});
		//黑名单拦截设置
		if (ServiceStatusUtils.isServiceRunning(this,
				"com.dwl.mobilesafe.service.CallSmsSafeService")) {
			siv_black_number.setChecked(true);
		} else {
			siv_black_number.setChecked(false);
		}
		callSmsSafeService = new Intent(this, CallSmsSafeService.class);
		siv_black_number.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (siv_black_number.isChecked()) {
					siv_black_number.setChecked(false);
					stopService(callSmsSafeService);
				} else {
					siv_black_number.setChecked(true);
					startService(callSmsSafeService);
				}
			}
		});
	}
}
