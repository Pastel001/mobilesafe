package com.dwl.mobilesafe;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CleanCacheActivity extends Activity {
	private TextView tv_scan_status;
	private ProgressBar progressBar1;
	private LinearLayout ll_container;

	private PackageManager pm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);

		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);

		pm = getPackageManager();

		new Thread() {
			public void run() {
				List<PackageInfo> packageInfos = pm
						.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
				for (PackageInfo packageInfo : packageInfos) {
					Method[] methods = PackageManager.class.getMethods();
					for (Method method : methods) {
						if ("getPackageSizeInfo".equals(method.getName())) {
							try {
								method.invoke(pm, packageInfo.packageName,
										new MyObserver());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			};
		}.start();
	}

	private class MyObserver extends IPackageStatsObserver.Stub {

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			System.out.println(pStats.cacheSize);
			// pStats.packageName;
		}
	}
}
