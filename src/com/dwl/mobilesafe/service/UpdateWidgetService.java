package com.dwl.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.dwl.mobilesafe.R;
import com.dwl.mobilesafe.receiver.MyWidget;
import com.dwl.mobilesafe.utils.SystemInfoUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {

	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm;

	private InnerScreenOffReceiver offReceiver;
	private InnerScreenOnReceiver onReceiver;

	private class InnerScreenOffReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("Screen OFF");
			if (timer != null && task != null) {
				timer.cancel();
				task.cancel();
				timer = null;
				task = null;
			}
		}
	}

	private class InnerScreenOnReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("Screen ON");
			if (timer == null && task == null) {
				startWidgetUpdate();
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
		onReceiver = new InnerScreenOnReceiver();
		offReceiver = new InnerScreenOffReceiver();

		IntentFilter onfilter = new IntentFilter();
		onfilter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(onReceiver, onfilter);

		IntentFilter offfilter = new IntentFilter();
		offfilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(offReceiver, offfilter);

		awm = AppWidgetManager.getInstance(this);
		startWidgetUpdate();
		super.onCreate();
	}

	private void startWidgetUpdate() {
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				System.out.println("timerTask run");
				RemoteViews views = new RemoteViews(getPackageName(),
						R.layout.widget_task);
				ComponentName provider = new ComponentName(
						getApplicationContext(), MyWidget.class);
				views.setTextViewText(
						R.id.process_count,
						"正在运行中的进程个数："
								+ SystemInfoUtils
										.getRunningAppCount(getApplicationContext()));
				views.setTextViewText(
						R.id.process_memory,
						"可用内存："
								+ Formatter
										.formatFileSize(
												getApplicationContext(),
												SystemInfoUtils
														.getAvailRam(getApplicationContext())));
				Intent intent = new Intent();
				intent.setAction("com.dwl.mobilsafe.killprocess");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						getApplicationContext(), 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				awm.updateAppWidget(provider, views);
			}
		};
		timer.schedule(task, 0, 5000);
	}

	@Override
	public void onDestroy() {
		if (timer != null && task != null) {
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
		if (onReceiver != null && offReceiver != null) {
			unregisterReceiver(onReceiver);
			unregisterReceiver(offReceiver);
			onReceiver = null;
			offReceiver = null;
		}
		super.onDestroy();
	}

}
