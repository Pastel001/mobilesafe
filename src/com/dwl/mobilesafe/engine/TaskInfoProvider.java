package com.dwl.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.dwl.mobilesafe.R;
import com.dwl.mobilesafe.dto.TaskInfo;

public class TaskInfoProvider {
	/**
	 * 获取运行中进程信息
	 * @param context
	 * @return
	 */
	public static List<TaskInfo> getTaskInfos(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo processInfo : processInfos) {
			TaskInfo taskInfo = new TaskInfo();
			String packageName = processInfo.processName;
			taskInfo.setPackageName(packageName);
			MemoryInfo[] memInfo = am
					.getProcessMemoryInfo(new int[] { processInfo.pid });
			long memsize = memInfo[0].getTotalPrivateDirty() * 1024l;
			taskInfo.setMemsize(memsize);
			try {
				PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
				Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
				taskInfo.setIcon(icon);
				String name = (String) packageInfo.applicationInfo
						.loadLabel(pm);
				taskInfo.setName(name);
				if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					// 用户进程
					taskInfo.setUserTask(true);
				} else {
					// 系统进程
					taskInfo.setUserTask(false);
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				//有些系统应用不是Android应用，没有安装包信息
				taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
				taskInfo.setName(packageName);
			}
			taskInfos.add(taskInfo);
		}
		return taskInfos;
	}
}
