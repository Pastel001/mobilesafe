package com.dwl.mobilesafe.receiver;

import com.dwl.mobilesafe.R;
import com.dwl.mobilesafe.service.GPSService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object obj : objs) {
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			String body = smsMessage.getMessageBody();
			String sender = smsMessage.getOriginatingAddress();
			switch (body) {
			case "#*location*#":
				Log.i(TAG,"返回手机的位置信息");
				Intent i = new Intent(context,GPSService.class);
				context.startService(i);
				SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
				String lastlocation = sp.getString("lastlocation", null);
				if(TextUtils.isEmpty(lastlocation)){
					SmsManager.getDefault().sendTextMessage(sender, null, "getting location...", null, null);
				}else{
					SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
				}
				abortBroadcast();
				break;

			case "#*alarm*#":
				Log.i(TAG,"播放报警音乐");
				MediaPlayer mediaplayer = MediaPlayer.create(context,R.raw.ylzs);
				mediaplayer.setVolume(1.0f, 1.0f);
				//mediaplayer.setLooping(true);
				mediaplayer.start();
				abortBroadcast();
				break;
			case "#*wipedata*#":
				Log.i(TAG,"清除手机数据");
				//dpm.wipedata();
				abortBroadcast();
				break;
			case "#*lockscreen*#":
				Log.i(TAG,"远程锁屏");
				abortBroadcast();
				//dpm.locknow();
				break;
			default:
				break;
			}
		}
	}

}
