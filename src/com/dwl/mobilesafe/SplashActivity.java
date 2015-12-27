package com.dwl.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.dwl.mobilesafe.service.AddressService;
import com.dwl.mobilesafe.utils.ServiceStatusUtils;
import com.dwl.mobilesafe.utils.StreamTools;

public class SplashActivity extends Activity {
	protected static final String TAG = "SplashActivity";
	protected static final int SHOW_UPDATE_DIALOG = 1;
	protected static final int ENTER_HOME = 2;
	protected static final int URL_ERROR = 3;
	protected static final int NETWORK_ERROR = 4;
	protected static final int JSON_ERROR = 5;
	protected static final int UPDATE_INSTALL = 6;
	private TextView tv_splash_version;
	private TextView tv_splash_progress;
	private SharedPreferences sp;
	private Editor editor;
	/**
	 * 升级地址path
	 */
	private String path;
	/**
	 * 升级描述desc
	 */
	private String desc;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			case ENTER_HOME:
				enterHome();
				break;
			case URL_ERROR:
				Toast.makeText(SplashActivity.this, "服务器路径错误。",
						Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), "网络连接错误。",
						Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case JSON_ERROR:
				Toast.makeText(getApplicationContext(), "解析json数据错误。",
						Toast.LENGTH_SHORT).show();
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
		tv_splash_progress = (TextView) findViewById(R.id.tv_splash_progress);
		tv_splash_version.setText("版本号：" + getVersion());
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean update = sp.getBoolean("update", false);
		editor = sp.edit();
		// 初始化，拷贝数据库到fils目录
		copyDB("address.db");
		copyDB("commonnum.db");
		// 创建桌面快捷图标
		creteDesktopShotcut();
		if (update) {
			checkUpdate();
		} else {
			handler.postDelayed(new Runnable() {
				public void run() {
					enterHome();
				}
			}, 2000);
		}
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		findViewById(R.id.rl_splash_root).startAnimation(aa);
		// 默认开启归属地号码显示
		Intent service = new Intent(this, AddressService.class);
		if (sp.contains("showAddress")) {
			if (sp.getBoolean("showAddress", false)) {
				if (!ServiceStatusUtils.isServiceRunning(this,
						"com.dwl.mobilesafe.service.AddressService")) {
					startService(service);
				}
			} else {
				if (ServiceStatusUtils.isServiceRunning(this,
						"com.dwl.mobilesafe.service.AddressService")) {
					stopService(service);
				}

			}
		} else {// 首次进入，开启服务
			if (!ServiceStatusUtils.isServiceRunning(this,
					"com.dwl.mobilesafe.service.AddressService")) {
				startService(service);
				System.out.println("start");
			}
			editor.putBoolean("showAddress", true);
		}
		editor.commit();
	}

	private void creteDesktopShotcut() {
		boolean shotcut = sp.getBoolean("shotcut", false);
		if (shotcut) {
			return;
		}
		// 创建发给laucnher的广播意图
		Intent intent = new Intent();
		intent.setAction("com.android.laucher.action.INSTALL_SHOT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
				BitmapFactory.decodeResource(getResources(), R.drawable.safe));
		// 手机卫士主页的隐式意图
		Intent homeIntent = new Intent();
		homeIntent.setAction("com.dwl.mobilesafe.home");
		homeIntent.addCategory(intent.CATEGORY_DEFAULT);
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, homeIntent);
		// 发送广播给launcher，创建快捷图标，shortcut
		sendBroadcast(intent);
		editor.putBoolean("shotcut", true);
	}

	private void copyDB(String dbname) {
		File file = new File(getFilesDir(), dbname);
		if (file.exists() && file.length() > 0) {
			Log.i(TAG, "数据库已经存在，不需要重新拷贝");
		} else {
			AssetManager am = getAssets();
			try {
				InputStream is = am.open(dbname);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		// 设置dialog出现时按返回键进入首页
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
			}
		});
		// dialog配置
		builder.setTitle("有新版本啦！！！");
		builder.setMessage(desc);
		builder.setPositiveButton("立即更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载apk
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					FinalHttp fh = new FinalHttp();
					fh.download(path, Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/update.apk",
							new AjaxCallBack<File>() {

								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									super.onFailure(t, errorNo, strMsg);
									Toast.makeText(getApplicationContext(),
											"更新失败，请稍后重试！", Toast.LENGTH_SHORT)
											.show();
									enterHome();
								}

								@Override
								public void onLoading(long count, long current) {
									super.onLoading(count, current);
									tv_splash_progress.setText("下载进度" + current
											/ count * 100 + "%");
								}

								@Override
								public void onSuccess(File t) {
									super.onSuccess(t);
									installApk(t);
								}

								private void installApk(File t) {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setDataAndType(Uri.fromFile(t),
											"application/vnd.android.package-archive");
									// startActivity(intent);
									startActivityForResult(intent,
											UPDATE_INSTALL);
								}
							});
				} else {
					Toast.makeText(getApplicationContext(), "sdcard不可用",
							Toast.LENGTH_SHORT).show();
					enterHome();
					return;
				}

			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				enterHome();
			}
		});
		// dialog显示
		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, String.valueOf(resultCode));
		// 判断本地安装失败,安装失败进入首页
		// 系统安装页面关闭返回0，安装包解析错误1
		if (requestCode == UPDATE_INSTALL && resultCode != 2) {
			enterHome();
		}
		return;
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
					// setConnectTimeout：设置连接主机超时（单位：毫秒）
					// setReadTimeout：设置从主机读取数据超时（单位：毫秒）
					conn.setConnectTimeout(5000);
					// conn.setReadTimeout(5000);
					conn.setRequestMethod("GET");
					int resultCode = conn.getResponseCode();
					if (resultCode == 200) {
						InputStream is = conn.getInputStream();
						String result = StreamTools.readFromStream(is);
						Log.i(TAG, result);
						JSONObject obj = new JSONObject(result);
						String version = (String) obj.get("version");
						desc = (String) obj.get("description");
						path = (String) obj.get("path");
						if (Double.parseDouble(version) > Double
								.parseDouble(getVersion())) {
							// 升级
							msg.what = SHOW_UPDATE_DIALOG;
							Log.i(TAG, "update");
						} else {
							// 已经是新版本，不用升级
							msg.what = ENTER_HOME;
							Log.i(TAG, "dont update");
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					msg.what = URL_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					msg.what = NETWORK_ERROR;
					Log.i(TAG, "dont update2");
				} catch (JSONException e) {
					e.printStackTrace();
					msg.what = JSON_ERROR;
					Log.i(TAG, "dont update3");
				} finally {
					long endTime = System.currentTimeMillis();
					long dtime = endTime - startTime;
					if (dtime < 2000) {
						try {
							sleep(2000 - dtime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendMessage(msg);
					Log.i(TAG, "sendmsg");
				}
			}
		}.start();
		;
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
