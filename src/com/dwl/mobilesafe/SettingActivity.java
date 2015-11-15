package com.dwl.mobilesafe;

import com.dwl.mobilesafe.ui.SettingItemView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	
	private SettingItemView siv_update;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(siv_update.isChecked()){
					siv_update.setChecked(false);
					siv_update.setDesc("自动更新已经关闭");
				}else{
					siv_update.setChecked(true);
					siv_update.setDesc("自动更新已经开启");
				}
			}
		});
	}
}
