package com.dwl.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class LostFindAcitvity extends Activity {
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sp = getSharedPreferences("config",MODE_PRIVATE);
		super.onCreate(savedInstanceState);
		boolean result = sp.getBoolean("configed", false);
		if (result) {
			setContentView(R.layout.activity_lost_find);
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
