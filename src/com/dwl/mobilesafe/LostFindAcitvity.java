package com.dwl.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindAcitvity extends Activity {
	private SharedPreferences sp;
	private TextView tv_lostfind_number;
	private ImageView iv_lostfind_status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sp = getSharedPreferences("config",MODE_PRIVATE);
		super.onCreate(savedInstanceState);
		boolean result = sp.getBoolean("configed", false);
		if (result) {
			setContentView(R.layout.activity_lost_find);
			tv_lostfind_number = (TextView) findViewById(R.id.tv_lostfind_number);
			iv_lostfind_status= (ImageView) findViewById(R.id.iv_lostfind_status);
			boolean protecting = sp.getBoolean("protecting", false);
			if(protecting){
				iv_lostfind_status.setImageResource(R.drawable.lock);
			}else{
				iv_lostfind_status.setImageResource(R.drawable.unlock);
			}
			tv_lostfind_number.setText(sp.getString("safeNumber", ""));
		} else {			
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			this.finish();
		}
		
	}
	
	public void reEntrySetup(View view){
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		this.finish();
	}
}
