package com.dwl.mobilesafe.receiver;

import com.dwl.mobilesafe.service.UpdateWidgetService;
import com.dwl.mobilesafe.utils.ServiceStatusUtils;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class MyWidget extends AppWidgetProvider {
	private Intent intent;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		if (!ServiceStatusUtils.isServiceRunning(context,
				"com.dwl.mobilesafe.service.GPSService")) {
			intent = new Intent(context, UpdateWidgetService.class);
			context.startService(intent);
		}
		System.out.println("Widget udpate");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
		super.onDisabled(context);
	}

}
