package com.dwl.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {

	private CheckBox cb_setup4_status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		cb_setup4_status = (CheckBox) findViewById(R.id.cb_setup4_status);
		boolean protecting = sp.getBoolean("protecting", false);
		if(protecting){
			cb_setup4_status.setChecked(true);
			cb_setup4_status.setText("防盗保护已经开启");
		}else{
			cb_setup4_status.setChecked(false);
			cb_setup4_status.setText("防盗保护没有开启");
		}
		cb_setup4_status.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					cb_setup4_status.setText("防盗保护已经开启");
				}else{
					cb_setup4_status.setText("防盗保护没有开启");
				}
				Editor editor = sp.edit();
				editor.putBoolean("protecting", isChecked);
				editor.commit();
			}
		});
	}

	@Override
	public void showNext() {
		Editor editor = sp.edit();
		editor.putBoolean("configed", true);
		editor.commit();

		Intent intent = new Intent(this, LostFindAcitvity.class);
		startActivity(intent);
		this.finish();
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		this.finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
}
