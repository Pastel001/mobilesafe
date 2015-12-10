package com.dwl.mobilesafe.service;

import com.dwl.mobilesafe.db.AddressDao;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class AddressService extends Service {
	private TelephonyManager tm;
	private PhoneStateListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	private class MyListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			String address = AddressDao.getAddress(incomingNumber);
			Toast.makeText(getApplicationContext(), incomingNumber, 1).show();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}
}