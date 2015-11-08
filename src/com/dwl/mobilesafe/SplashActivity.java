package com.dwl.mobilesafe;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.http.HttpConnection;
import org.json.JSONException;
import org.json.JSONObject;

import com.dwl.mobilesafe.utils.StreamTools;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {
	protected static final String TAG = "SplashActivity";
	protected static final int SHOW_UPDATE_DIALOG = 1;
	protected static final int ENTER_HOME = 2;
	protected static final int URL_ERROR = 3;
	protected static final int NETWORK_ERROR = 4;
	protected static final int JSON_ERROR = 5;
	private TextView tv_splash_version;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG:

				break;
			case ENTER_HOME:
				enterHome();
				break;
			case URL_ERROR:
				Toast.makeText(SplashActivity.this, "服务器路径错误。", 0).show();
				enterHome();
				break;
			case NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), "网络连接错误。", 0).show();
				enterHome();
				break;
			case JSON_ERROR:
				Toast.makeText(getApplicationContext(), "解析json数据错误。", 0)
						.show();
				enterHome();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("版本号：" + getVersion());
		checkUpdate();
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		findViewById(R.id.rl_splash_root).startAnimation(aa);
	}

	protected void enterHome() {
		Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
		startActivity(intent);
		this.finish();
	}

	private void checkUpdate() {
		new Thread() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis(); 
				Message msg = Message.obtain();
				try {
					URL url = new URL(getString(R.string.updateURL));
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setReadTimeout(5000);
					conn.setRequestMethod("GET");
					int resultCode = conn.getResponseCode();
					System.out.println(resultCode);
					if (resultCode == 200) {
						InputStream is = conn.getInputStream();
						String result = StreamTools.readFromStream(is);
						Log.i(TAG, result);
						JSONObject obj = new JSONObject(result);
						String version = (String) obj.get("version");
						String desc = (String) obj.get("description");
						String path = (String) obj.get("path");
						if (Integer.parseInt(version) > Integer
								.parseInt(getVersion())) {
							// 升级
							msg.what = SHOW_UPDATE_DIALOG;
							Log.i(TAG, "uodate");
						} else {
							// 已经是新版本，不用升级
							msg.what = ENTER_HOME;
							Log.i(TAG, "dont uodate");
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					msg.what = URL_ERROR;
					Log.i(TAG, "dont uodate1");
				} catch (IOException e) {
					e.printStackTrace();
					msg.what = NETWORK_ERROR;
					Log.i(TAG, "dont uodate2");
				} catch (JSONException e) {
					e.printStackTrace();
					msg.what = JSON_ERROR;
					Log.i(TAG, "dont uodate3");
				} finally {
					long endTime = System.currentTimeMillis();
					long dtime = endTime - startTime;
					if (dtime < 2000) {
						try {
							sleep(2000-dtime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendMessage(msg);
					Log.i(TAG, "dont uodate,sendmsg");
				}
			}
		}.start();;
	}

	/**
	 * get app versionName by PackageManager and PackageInfo
	 * 
	 * @return
	 */
	public String getVersion() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pg = pm.getPackageInfo(getPackageName(), 0);
			return pg.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// cant reach
			return "";
		}
	}
}
