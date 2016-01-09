package com.dwl.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Parser;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.PatternMatcher;
import android.os.StatFs;
import android.text.format.Formatter;

public class SystemInfoUtils {
	/**
	 * 获取指定path的剩余空间大小
	 * 
	 * @param context
	 * @param path
	 * @return
	 */
	public static String getTotalSpace(Context context, String path) {
		StatFs statFs = new StatFs(path);
		long blocks = statFs.getAvailableBlocks();
		long size = statFs.getBlockSize();
		return Formatter.formatFileSize(context, blocks * size);
	}
	/**
	 * 获取运行中app个数
	 * @param context
	 * @return
	 */
	public static int getRunningAppCount(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		return am.getRunningAppProcesses().size();
	}
	/**
	 * 获取可用内存大小，单位byte
	 * @param context
	 * @return
	 */
	public static long getAvailRam(Context context){
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	/**
	 * 获取手机总内存大小
	 * @param context
	 * @return
	 */
	public static long getTotalRam(Context context){
		/*ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.totalMem;*/
		//totalMemw为新api，需要sdk版本为16以上
		try {
			File file = new File("/proc/meminfo");
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = br.readLine();
			Pattern pattern = Pattern.compile("^memtotal:\\s*(\\w*)\\s*kb$", Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE);
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				String memtotal = matcher.group(1);
				return Long.parseLong(memtotal)*1024l;
			}else {
				return 0l;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		
	}
}
