package com.dwl.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.dwl.mobilesafe.LockValidateActivity;
import com.dwl.mobilesafe.db.dao.LockedAppDao;

public class WatchDogService extends Service {
	private ActivityManager am;
	private boolean flag;
	private LockedAppDao dao;
	private List<String> lockedApps;
	private ContentResolver resolver;
	private MyObserver observer;
	/**
	 * 最近输入过密码的应用程序 锁屏后应该清空，注册锁屏广播就可以，就不写了 锁屏后也可以停止此服务，避免浪费电池资源，也不写了
	 */
	private String passedPackageName;
	private InnerBraodcastReceiver receiver;

	private class InnerBraodcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			passedPackageName = intent.getExtras().getString("packageName");
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		final Intent intent = new Intent(this, LockValidateActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		dao = new LockedAppDao(WatchDogService.this);
		lockedApps = dao.findAll();
		resolver = getContentResolver();
		Uri uri = Uri.parse("content://com.dwl.mobilesafe.lockedapp");
		observer = new MyObserver(new Handler());
		resolver.registerContentObserver(uri, true, observer);

		receiver = new InnerBraodcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.dwl.mobilesafe.validate");
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		registerReceiver(receiver, filter);

		new Thread() {
			public void run() {
				flag = true;
				while (flag) {
					RunningTaskInfo taskInfo = am.getRunningTasks(1).get(0);
					String packageName = taskInfo.topActivity.getPackageName();
					// System.out.println(packageName);
					if (lockedApps.contains(packageName)
							&& !(packageName.equals(passedPackageName))) {// 加锁的程序&&不是最近输入过密码的程序，弹出输入密码页面
						intent.putExtra("packageName", packageName);
						startActivity(intent);
					} else {

					}
					try {
						Thread.sleep(100);
						// sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		/*
		 * //直接写while循环会阻塞主线程 flag = true; while (flag) { RunningTaskInfo
		 * taskInfo = am.getRunningTasks(1).get(0); String packageName =
		 * taskInfo.topActivity.getPackageName();
		 * System.out.println(packageName); try { Thread.sleep(100); //
		 * sleep(100); } catch (InterruptedException e) { catch block
		 * e.printStackTrace(); } }
		 */

	}

	private class MyObserver extends ContentObserver {
		public MyObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			lockedApps = dao.findAll();
		}
	}

	@Override
	public void onDestroy() {
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		if (resolver != null) {
			resolver.unregisterContentObserver(observer);
			resolver = null;
		}
		super.onDestroy();
		flag = false;
	}
}
