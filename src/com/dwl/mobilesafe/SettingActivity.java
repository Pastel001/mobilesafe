package com.dwl.mobilesafe;

import com.dwl.mobilesafe.service.AddressService;
import com.dwl.mobilesafe.ui.SettingItemView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	
	private SettingItemView siv_update;
	private SettingItemView siv_address;
	private SharedPreferences sp;
	private Intent showAddresService = new Intent(SettingActivity.this,AddressService.class);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean update = sp.getBoolean("update", false);
		setContentView(R.layout.activity_setting);
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		siv_address = (SettingItemView) findViewById(R.id.siv_address);
		//自动更新设置
		if (update) {
			siv_update.setChecked(true);
		} else {
			siv_update.setChecked(false);
		}
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(siv_update.isChecked()){
					siv_update.setChecked(false);
					editor.putBoolean("update", false);
				}else{
					siv_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		//号码归属地显示设置
		siv_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(siv_address.isChecked()){
					siv_address.setChecked(false);
					stopService(showAddresService);
				}else{
					siv_address.setChecked(true);
					startService(showAddresService);
				}
			}
		});
	}
}
