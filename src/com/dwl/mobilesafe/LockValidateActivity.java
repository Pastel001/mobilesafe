package com.dwl.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LockValidateActivity extends Activity {
	private PackageManager pm;
	private ImageView iv_icon;
	private TextView tv_name;
	private EditText et_password;
	private String packageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_validate);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		tv_name = (TextView) findViewById(R.id.tv_name);
		et_password = (EditText) findViewById(R.id.et_password);
		pm = getPackageManager();
		Intent intent = getIntent();
		packageName = (String) intent.getExtras().get("packageName");
		try {
			ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
			iv_icon.setImageDrawable(appInfo.loadIcon(pm));
			tv_name.setText(appInfo.loadLabel(pm));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void lockValidate(View view) {
		String password = et_password.getText().toString().trim();
		if (TextUtils.isEmpty(password)) {
			Toast.makeText(this, "请输入密码...", 0).show();
		} else {//不写首次进入进入程序锁设定密码的逻辑了，输入不为空就算正确
			Intent intent = new Intent();
			intent.setAction("com.dwl.mobilesafe.validate");
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.putExtra("packageName", packageName);
			sendBroadcast(intent);
			finish();
		}
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		goHomeLauncher();
		finish();
	}
	private void goHomeLauncher() {
		Intent home = new Intent();
		home.setAction(Intent.ACTION_MAIN);
		home.addCategory(Intent.CATEGORY_HOME);
		startActivity(home);
	}

	public void cancel(View view) {
		goHomeLauncher();
		finish();
	}
}
