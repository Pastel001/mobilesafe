package com.dwl.mobilesafe;

import android.app.Activity;
import android.os.Bundle;

public class TrafficManagerActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * TrafficStats.getMobileRxBytes(); TrafficStats.getMobileTxBytes();
		 * TrafficStats.getTotalRxBytes(); TrafficStats.getTotalTxBytes();
		 * TrafficStats.getUidRxBytes(uid);
		 * TrafficStats.getUidTxBytes(uid);//PackageInfo.applicationInfo.uid
		 */
		setContentView(R.layout.activity_traffic_manager);
	}
}
