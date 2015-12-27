package com.dwl.mobilesafe.service;

import java.lang.reflect.Method;
import java.util.Iterator;

import com.android.internal.telephony.ITelephony;
import com.dwl.mobilesafe.db.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;

public class CallSmsSafeService extends Service {
	private CallSmsSafeReceiver receiver;

	private TelephonyManager tm;
	private MyPhoneStateListener listener;

	private BlackNumberDao dao;

	private class CallSmsSafeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objs = (Object[]) intent.getExtras().get("puds");
			for (Object obj : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String number = smsMessage.getOriginatingAddress();
				String mode = dao.findMode(number);
				if ("2".equals(mode) || "3".equals(mode)) {// ��"2".equals(mode)������mode.equals("2"),��ֹmodeΪnull������õ�һ��д����Ҫ�ж�mode��Ϊnull
					abortBroadcast();
					// �ֻ���ʿһ��ᱣ����ţ���ֹ��ɾ�����û��ָ��Ļ���
				}
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// sms
		receiver = new CallSmsSafeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, filter);
		// phone
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

	}

	private class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String mode = dao.findMode(incomingNumber);
				if ("1".equals(mode) || "3".equals(mode)) {
					
					Uri uri = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(uri, true,
							new CallLogObserver(new Handler(), incomingNumber));
					endCall();// �Ҷϵ绰������ɾ��ͨ����¼̫���ˣ����ܵ�ʱͨ����¼��û�����ɣ�����ע��һ�����ݹ۲��ߣ�������ͨ����¼���ٵ���ɾ��
				}
				break;
			}
		}
	}

	private class CallLogObserver extends ContentObserver {

		private String incomingNumber;

		public CallLogObserver(Handler handler, String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			getContentResolver().unregisterContentObserver(this);
			deleteCallLog(incomingNumber);
		}

	}

	/**
	 * ɾ������ͨ����¼
	 * 
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://call_log/calls");
		resolver.delete(uri, "number=?", new String[] { incomingNumber });
	}

	/**
	 * �Ҷϵ绰
	 */
	public void endCall() {
		// ServiceManager.getService(TELEPHONY_SERVICE);
		try {
			Class serviceManager = CallSmsSafeService.class.getClassLoader()
					.loadClass("android.os.ServiceManager");
			Method getService = serviceManager.getDeclaredMethod("getService",
					String.class);
			IBinder b = (IBinder) getService.invoke(null, TELEPHONY_SERVICE);
			ITelephony iTelephony = ITelephony.Stub.asInterface(b);
			iTelephony.endCall();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		if (listener != null) {
			tm.listen(listener, PhoneStateListener.LISTEN_NONE);
			listener = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
