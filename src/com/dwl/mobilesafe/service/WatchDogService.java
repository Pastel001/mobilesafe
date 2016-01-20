package com.dwl.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Intent;
import android.os.IBinder;

public class WatchDogService extends Service {
	private ActivityManager am;
	private boolean flag;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		new Thread() {
			public void run() {
				flag = true;
				while (flag) {
					RunningTaskInfo taskInfo = am.getRunningTasks(1).get(0);
					String packageName = taskInfo.topActivity.getPackageName();
					System.out.println(packageName);
					try {
						Thread.sleep(100);
						// sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		}.start();
		/*
		//直接写while循环会阻塞主线程
		flag = true;
		while (flag) {
			RunningTaskInfo taskInfo = am.getRunningTasks(1).get(0);
			String packageName = taskInfo.topActivity.getPackageName();
			System.out.println(packageName);
			try {
				Thread.sleep(100);
				// sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		flag = false;
	}
}
