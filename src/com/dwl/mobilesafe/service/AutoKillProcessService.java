package com.dwl.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AutoKillProcessService extends Service {
	private InnerScreenOffReceiver receiver;
	
	private class InnerScreenOffReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			ActivityManager am  = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> taskInfos= am.getRunningAppProcesses();
			for (RunningAppProcessInfo taskInfo : taskInfos) {
				am.killBackgroundProcesses(taskInfo.processName);
			}
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		receiver = new InnerScreenOffReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
	}

	public void onDestroy() {
		if (receiver!=null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
	};
}
