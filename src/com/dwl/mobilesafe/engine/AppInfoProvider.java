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
	 * ��ȡ�ֻ��ϰ�װ��Ӧ����Ϣ
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
			//Ӧ�õ�flags
			int flags = info.applicationInfo.flags;
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// �û�Ӧ��
				appInfo.setUserApp(true);
			} else {
				// ϵͳӦ��
				appInfo.setUserApp(false);
			}
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				//��װ���ֻ��ڴ�
				appInfo.setInRom(true);
			}else {
				//�ⲿ�洢sdcard
				appInfo.setInRom(false);
			}
			appInfos.add(appInfo);
		}
		return appInfos;
	}
}
