package com.dwl.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.dwl.mobilesafe.dto.AppInfo;

public class AppInfoProvider {
	/**
	 * 获取手机上安装的应用信息
	 * 
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {

		PackageManager pm = context.getPackageManager();
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		for (PackageInfo info : packageInfos) {
			String packageName = info.packageName;
			Drawable icon = info.applicationInfo.loadIcon(pm);
			String name = (String) info.applicationInfo.loadLabel(pm);
			
			AppInfo appInfo = new AppInfo();
			appInfo.setIcon(icon);
			appInfo.setName(name);
			appInfo.setPackageName(packageName);
			//应用的flags
			int flags = info.applicationInfo.flags;
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// 用户应用
				appInfo.setUserApp(true);
			} else {
				// 系统应用
				appInfo.setUserApp(false);
			}
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				//安装在手机内存
				appInfo.setInRom(true);
			}else {
				//外部存储sdcard
				appInfo.setInRom(false);
			}
			appInfos.add(appInfo);
		}
		return appInfos;
	}
}
