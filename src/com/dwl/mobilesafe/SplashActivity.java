package com.dwl.mobilesafe;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SplashActivity extends Activity {
	private TextView tv_splash_version;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("°æ±¾ºÅ£º"+getVersion());
	}
	/**
	 * get app versionName by PackageManager and PackageInfo
	 * @return
	 */
	public String getVersion() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pg = pm.getPackageInfo(getPackageName(), 0);
			return pg.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// cant reach
			return "";
		}
	}
}
