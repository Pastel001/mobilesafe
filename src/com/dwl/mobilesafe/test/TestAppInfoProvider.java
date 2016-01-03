package com.dwl.mobilesafe.test;

import java.util.List;

import android.test.AndroidTestCase;

import com.dwl.mobilesafe.dto.AppInfo;
import com.dwl.mobilesafe.engine.AppInfoProvider;

public class TestAppInfoProvider extends AndroidTestCase {
	public void testGetAllApp() {
		List<AppInfo> appInfos = AppInfoProvider.getAppInfos(getContext());
		for (AppInfo appInfo : appInfos) {
			System.out.println(appInfo.toString());
		}
	}
}
