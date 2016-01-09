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
	 * ��ȡָ��path��ʣ��ռ��С
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
	 * ��ȡ������app����
	 * @param context
	 * @return
	 */
	public static int getRunningAppCount(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		return am.getRunningAppProcesses().size();
	}
	/**
	 * ��ȡ�����ڴ��С����λbyte
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
	 * ��ȡ�ֻ����ڴ��С
	 * @param context
	 * @return
	 */
	public static long getTotalRam(Context context){
		/*ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.totalMem;*/
		//totalMemwΪ��api����Ҫsdk�汾Ϊ16����
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
