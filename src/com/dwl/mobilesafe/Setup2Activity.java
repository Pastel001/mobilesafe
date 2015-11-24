package com.dwl.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.dwl.mobilesafe.ui.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView siv_setup2_bindsim;
	private TelephonyManager tm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		siv_setup2_bindsim = (SettingItemView) findViewById(R.id.siv_setup2_bindsim);
		String status = sp.getString("sim", null);
		if (TextUtils.isEmpty(status)) {
			siv_setup2_bindsim.setChecked(false);
		}else{
			siv_setup2_bindsim.setChecked(true);
		}
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		siv_setup2_bindsim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String result = sp.getString("sim", null);
				Editor editor = sp.edit();
				if (TextUtils.isEmpty(result)) {
					String number = tm.getSimSerialNumber();
					System.out.println(number);
					if (number != null) {
						editor.putString("sim", number);
						System.out.println("fuck");
						editor.commit();
					}
					siv_setup2_bindsim.setChecked(true);
				} else {
					editor.putString("sim", null);
					editor.commit();
					siv_setup2_bindsim.setChecked(false);
				}

			}
		});
	}

	@Override
	public void showNext() {
		if (siv_setup2_bindsim.isChecked()) {
			Intent intent = new Intent(this, Setup3Activity.class);
			startActivity(intent);
			this.finish();
			overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
		} else {
			Toast.makeText(this, "Çë°ó¶¨sim¿¨", 0).show();
			return;
		}
		
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		this.finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
}
