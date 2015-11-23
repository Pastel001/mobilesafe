package com.dwl.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {
	private EditText et_setup3_phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_setup3_phone = (EditText) findViewById(R.id.et_setup3_phone);
		et_setup3_phone.setText(sp.getString("safeNumber", ""));
	}

	public void selectContact(View view) {
		Intent intent = new Intent(this, SelectContactActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			String phone = data.getStringExtra("phone").replace("-", "");
			et_setup3_phone.setText(phone);
		}
	}

	@Override
	public void showNext() {
		String safeNumber = et_setup3_phone.getText().toString().trim();
		if (TextUtils.isEmpty(safeNumber)) {
			Toast.makeText(this, "请先设置安全号码", 0).show();
			return;
		} else {
			sp.edit().putString("safeNumber", safeNumber).commit();
			Intent intent = new Intent(this, Setup4Activity.class);
			startActivity(intent);
			this.finish();
			overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
		}

	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		this.finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}

}
