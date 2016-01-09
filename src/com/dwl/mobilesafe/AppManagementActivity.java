package com.dwl.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dwl.mobilesafe.dto.AppInfo;
import com.dwl.mobilesafe.engine.AppInfoProvider;
import com.dwl.mobilesafe.utils.SystemInfoUtils;

public class AppManagementActivity extends Activity implements OnClickListener {
	private static final String TAG = "AppManagementActivity";
	private TextView tv_rom;
	private TextView tv_sdcard;
	private ListView lv_apps;
	private LinearLayout ll_loading;
	private TextView tv_status;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ll_loading.setVisibility(View.INVISIBLE);
			lv_apps.setAdapter(new AppInfoAdapter());
		};
	};

	private List<AppInfo> appInfos;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;
	/**
	 * listView点击的item获取的对象
	 */
	private AppInfo appInfo;
	private PopupWindow popupWindow;

	private LinearLayout ll_uninstall;
	private LinearLayout ll_start;
	private LinearLayout ll_share;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_management);
		tv_rom = (TextView) findViewById(R.id.tv_rom);
		tv_sdcard = (TextView) findViewById(R.id.tv_sdcard);
		tv_rom.setText("手机内存可用："
				+ SystemInfoUtils.getTotalSpace(this, Environment
						.getDataDirectory().getAbsolutePath()));
		tv_sdcard.setText("SD卡可用："
				+ SystemInfoUtils.getTotalSpace(this, Environment
						.getExternalStorageDirectory().getAbsolutePath()));
		lv_apps = (ListView) findViewById(R.id.lv_apps);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		tv_status = (TextView) findViewById(R.id.tv_status);
		lv_apps.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dismissPopupWindow();
				Object object = lv_apps.getItemAtPosition(position);
				if (object != null) {
					appInfo = (AppInfo) object;
					View contentView = View.inflate(getApplicationContext(),
							R.layout.popup_window_item, null);
					ll_uninstall = (LinearLayout) contentView
							.findViewById(R.id.ll_uninstall);
					ll_start = (LinearLayout) contentView
							.findViewById(R.id.ll_start);
					ll_share = (LinearLayout) contentView
							.findViewById(R.id.ll_share);
					ll_uninstall.setOnClickListener(AppManagementActivity.this);
					ll_start.setOnClickListener(AppManagementActivity.this);
					ll_share.setOnClickListener(AppManagementActivity.this);
					popupWindow = new PopupWindow(contentView,
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);// The popup does not
														// provide any
														// background
					// 播放动画需要view有background，popupWindow默认没有背景，需要设置一个透明背景
					popupWindow.setBackgroundDrawable(new ColorDrawable(
							Color.TRANSPARENT));
					int[] location = new int[2];
					view.getLocationInWindow(location);
					popupWindow.showAtLocation(parent, Gravity.TOP
							+ Gravity.LEFT, 60, location[1]);
					AlphaAnimation animation = new AlphaAnimation(0.5f, 1.0f);
					animation.setDuration(500);
					ScaleAnimation animation2 = new ScaleAnimation(0.5f, 1.0f,
							0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 1.0f);
					animation2.setDuration(500);
					AnimationSet set = new AnimationSet(false);
					set.addAnimation(animation);
					set.addAnimation(animation2);
					contentView.startAnimation(set);
				}
			}
		});
		lv_apps.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				// System.out.println("我滚动了？");初始化listView的时候居然调用了此方法？
				// 此时数据在子线程执行初始化的list还没有初始化完成，所以要判空
				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size()) {
						tv_status.setText("系统应用（" + systemAppInfos.size() + "）");
					} else {
						tv_status.setText("用户应用（" + userAppInfos.size() + "）");
					}
				}
			}
		});
		fillData();
	}

	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				appInfos = AppInfoProvider
						.getAppInfos(AppManagementActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isUserApp()) {
						userAppInfos.add(appInfo);
					} else {
						systemAppInfos.add(appInfo);
					}
				}
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private class AppInfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("用户应用（" + userAppInfos.size() + ")");
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setTextSize(15);
				return tv;
			} else if (position == userAppInfos.size() + 1) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("系统应用（" + systemAppInfos.size() + ")");
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setTextSize(15);
				return tv;
			}
			View view;
			ViewHolder viewHolder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_app_info_item, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_icon);
				viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				viewHolder.tv_location = (TextView) view
						.findViewById(R.id.tv_location);
				view.setTag(viewHolder);
			}
			AppInfo appInfo;
			if (position <= userAppInfos.size()) {
				appInfo = userAppInfos.get(position - 1);
			} else {
				appInfo = systemAppInfos.get(position - userAppInfos.size() - 1
						- 1);
			}
			viewHolder.iv_icon.setImageDrawable(appInfo.getIcon());
			viewHolder.tv_name.setText(appInfo.getName());
			if (appInfo.isInRom()) {
				viewHolder.tv_location.setText("手机内存");
			} else {
				viewHolder.tv_location.setText("外部存储卡");
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			if (position == 0) {
				return null;
			} else if (position == userAppInfos.size() + 1) {
				return null;
			}
			AppInfo appInfo;
			if (position <= userAppInfos.size()) {
				appInfo = userAppInfos.get(position - 1);
			} else {
				appInfo = systemAppInfos.get(position - userAppInfos.size() - 1
						- 1);
			}
			return appInfo;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	private static class ViewHolder {
		private ImageView iv_icon;
		private TextView tv_name;
		private TextView tv_location;

	}

	private void dismissPopupWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_uninstall:
			Log.i(TAG, "卸载" + appInfo.getName());
			uninstall();
			break;
		case R.id.ll_start:
			Log.i(TAG, "启动" + appInfo.getName());
			startApp();
			break;
		case R.id.ll_share:
			Log.i(TAG, "分享" + appInfo.getName());
			shareApplication();
			break;
		}
	}

	private void startApp() {
		Intent intent = new Intent();
		PackageManager pm = getPackageManager();
		String packageName = appInfo.getPackageName();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(packageName,
					PackageManager.GET_ACTIVITIES);// 不传入PackageManager.GET_ACTIVITIES，不能获取到Activitys
			ActivityInfo[] activityInfo = packageInfo.activities;
			if (activityInfo[0] != null && activityInfo.length > 0) {
				ActivityInfo acInfo = activityInfo[0];
				intent.setClassName(packageName, acInfo.name);
				startActivity(intent);
			} else {
				Toast.makeText(this, "不能打开此应用", 0).show();
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction(intent.ACTION_SEND);
		intent.addCategory(intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "推荐使用一款软件，名称：" + appInfo.getName()
				+ "\n下载地址:http://www.hupu.com");
		startActivity(intent);
	}

	private void uninstall() {
		if (appInfo.isUserApp()) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_DELETE);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
			startActivityForResult(intent, 0);// 卸載后需要刷新界面，所以使用startActivityForResult
		} else {
			Toast.makeText(this, "需要root权限，不能卸载", 0).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillData();
	}
}
