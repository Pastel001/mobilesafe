package com.dwl.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telecom.TelecomManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootCompleteReceiver extends BroadcastReceiver{
	private SharedPreferences sp;
	private TelephonyManager tm;
    
	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		
		boolean protecting = sp.getBoolean("protecting", false);
		if (protecting) {
			String sim = sp.getString("sim", "");
			tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
			String realSim = tm.getSimSerialNumber();
			if (realSim!=null) {
				if (sim.equals(realSim)) {
					
				}else {
					System.out.println("sim�����ŷ����˱仯�����ͱ�������Ϣ");
					String safeNumber = sp.getString("safeNumber", "");
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(safeNumber, null, "sim card changed!", null, null);
				}
			}
		}
	}

}