package com.dwl.mobilesafe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CleanCacheActivity extends Activity {
	protected static final int SCANING = 1;
	private static final int SHOW_CACHE_INFO = 2;
	protected static final int SCANING_FINISH = 3;
	private TextView tv_scan_status;
	private ProgressBar progressBar1;
	private LinearLayout ll_container;

	private PackageManager pm;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANING:
				tv_scan_status.setText("正在扫描：" + msg.obj);
				break;
			case SHOW_CACHE_INFO:
				ll_container = (LinearLayout) findViewById(R.id.ll_container);
				View view = View.inflate(getApplicationContext(),
						R.layout.list_app_cache_info_item, null);
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
				ImageView iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);
				TextView tv_cache_size = (TextView) view
						.findViewById(R.id.tv_cache_size);
				final CacheInfo info = (CacheInfo) msg.obj;
				iv_icon.setImageDrawable(info.icon);
				tv_name.setText(info.appNmme);
				tv_cache_size.setText("缓存"
						+ Formatter.formatFileSize(getApplicationContext(),
								info.cacheSize));

				iv_delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Method[] methods = PackageManager.class.getMethods();
						for (Method method : methods) {
							if ("deleteApplicationCacheFiles".equals(method
									.getName())) {
								try {
									method.invoke(pm, info.packageName,
											new IPackageDataObserver.Stub() {
												@Override
												public void onRemoveCompleted(
														String packageName,
														boolean succeeded)
														throws RemoteException {
													// TODO Auto-generated
													// method stub

												}
											});
								} catch (Exception e) {
									e.printStackTrace();
									Intent intent = new Intent();
									intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
									intent.addCategory(Intent.CATEGORY_DEFAULT);
									intent.setData(Uri.parse("package:"
											+ info.packageName));
									startActivity(intent);
								}
							}
						}
					}
				});
				ll_container.addView(view);
				break;
			case SCANING_FINISH:
				tv_scan_status.setText("扫描完成");
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);

		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);

		pm = getPackageManager();

		new Thread() {
			public void run() {
				List<PackageInfo> packageInfos = pm
						.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
				int porgress = 0;
				progressBar1.setMax(packageInfos.size());
				for (PackageInfo packageInfo : packageInfos) {
					try {
						Method method = PackageManager.class.getMethod(
								"getPackageSizeInfo", String.class,
								IPackageStatsObserver.class);
						method.invoke(pm, packageInfo.packageName,
								new MyObserver());
						Message msg = Message.obtain();
						msg.what = SCANING;
						msg.obj = pm
								.getApplicationInfo(packageInfo.packageName, 0)
								.loadLabel(pm).toString();
						handler.sendMessage(msg);
						porgress++;
						progressBar1.setProgress(porgress);
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				Message msg = Message.obtain();
				msg.what = SCANING_FINISH;
				handler.sendMessage(msg);
			};
		}.start();
	}

	private class MyObserver extends IPackageStatsObserver.Stub {
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			if (pStats.cacheSize > 0) {
				try {
					CacheInfo info = new CacheInfo();
					info.packageName = pStats.packageName;
					info.icon = pm.getApplicationInfo(info.packageName, 0)
							.loadIcon(pm);
					info.appNmme = pm.getApplicationInfo(info.packageName, 0)
							.loadLabel(pm).toString();
					info.cacheSize = pStats.cacheSize;

					Message msg = Message.obtain();
					msg.what = SHOW_CACHE_INFO;
					msg.obj = info;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class CacheInfo {
		private String packageName;
		private Drawable icon;
		private String appNmme;
		private long cacheSize;

	}

	public void clearAll(View view) {
		// freeStorageAndNotify
		Method[] methods = PackageManager.class.getMethods();
		for (Method method : methods) {
			if ("freeStorageAndNotify".equals(method.getName())) {
				try {
					method.invoke(pm, Long.MAX_VALUE,
							new IPackageDataObserver.Stub() {

								@Override
								public void onRemoveCompleted(
										String packageName, boolean succeeded)
										throws RemoteException {
									System.out.println(succeeded);
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
}
