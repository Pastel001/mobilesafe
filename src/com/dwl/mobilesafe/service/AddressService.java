package com.dwl.mobilesafe.service;

import com.dwl.mobilesafe.R;
import com.dwl.mobilesafe.db.AddressDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class AddressService extends Service {
	public static final String TAG = "AddressService";
	private TelephonyManager tm;
	private PhoneStateListener listener;
	private OutCallReceiver receiver;
	private SharedPreferences sp;

	private WindowManager wm;
	private LayoutParams params;
	/**
	 * 自定义toast显示归属地的View对象
	 */
	private View view;

	private class OutCallReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String number = getResultData();
			String address = AddressDao.getAddress(number);
			// Toast.makeText(context, number, 1).show();
			Log.i(TAG, "收到外拨电话广播");
			showMyToast(address);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "address service");
		sp= getSharedPreferences("config", MODE_PRIVATE);
		wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter);
	}

	private class MyListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String address = AddressDao.getAddress(incomingNumber);
				// Toast.makeText(getApplicationContext(), incomingNumber,
				// 1).show();
				showMyToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (view != null) {
					wm.removeView(view);
					view = null;
				}
				break;
			}

		}
	}

	/**
	 * 显示自定义归属地toast
	 */
	public void showMyToast(String address) {
		int which = sp.getInt("which", 0);
		// "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"
		int[] bgs = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		view = View.inflate(this, R.layout.toast_address, null);
		view.setBackgroundResource(bgs[which]);
		TextView tv = (TextView) view.findViewById(R.id.tv_location);
		tv.setText(address);

		view.setOnTouchListener(new OnTouchListener() {
			int startX = 0;
			int startY = 0;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					int dx = newX - startX;
					int dy = newY - startY;
					params.x += dx;
					params.y += dy;
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.x > wm.getDefaultDisplay().getWidth()
							- view.getWidth()) {
						params.x = wm.getDefaultDisplay().getWidth()
								- view.getWidth();
					}
					if (params.y < 0) {
						params.y = 0;
					}
					if (params.y > wm.getDefaultDisplay().getHeight()
							- view.getHeight()) {
						params.y = wm.getDefaultDisplay().getHeight()
								- view.getHeight();
					}
					wm.updateViewLayout(view, params);
					// 移动之后重新初始化起始位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					Editor editor = sp.edit();
					editor.putInt("lastx", startX);
					editor.putInt("lasty", startY);
					editor.commit();
					break;
				}
				return true;
			}
		});
	    params = new LayoutParams();
		params.gravity = Gravity.TOP + Gravity.LEFT;
		params.x = sp.getInt("lastx", 0);
		params.y = sp.getInt("lasty", 0);
		params.height = LayoutParams.WRAP_CONTENT;
		params.width = LayoutParams.WRAP_CONTENT;
		params.flags = LayoutParams.FLAG_NOT_FOCUSABLE
				| LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = LayoutParams.TYPE_PRIORITY_PHONE;

		wm.addView(view, params);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}

}