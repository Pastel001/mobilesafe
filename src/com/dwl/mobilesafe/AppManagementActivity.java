package com.dwl.mobilesafe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dwl.mobilesafe.dto.AppInfo;
import com.dwl.mobilesafe.engine.AppInfoProvider;

public class AppManagementActivity extends Activity {
	private TextView tv_rom;
	private TextView tv_sdcard;
	private ListView lv_apps;
	private LinearLayout ll_loading;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ll_loading.setVisibility(View.INVISIBLE);
			lv_apps.setAdapter(new AppInfoAdapter());
		};
	};

	private List<AppInfo> appInfos;
	private List<AppInfo> userAppInfos;
	private List <AppInfo> systemAppInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_management);
		tv_rom = (TextView) findViewById(R.id.tv_rom);
		tv_sdcard = (TextView) findViewById(R.id.tv_sdcard);
		tv_rom.setText("手机内存可用："
				+ getTotalSpace(Environment.getDataDirectory()
						.getAbsolutePath()));
		tv_sdcard.setText("SD卡可用："
				+ getTotalSpace(Environment.getExternalStorageDirectory()
						.getAbsolutePath()));
		lv_apps = (ListView) findViewById(R.id.lv_apps);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
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
			return userAppInfos.size()+systemAppInfos.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder viewHolder;
			if (convertView != null) {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_app_info_item, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				viewHolder.tv_location = (TextView) view.findViewById(R.id.tv_location);
				view.setTag(viewHolder);
			}
			AppInfo appInfo;
			if (position<userAppInfos.size()) {
				appInfo = userAppInfos.get(position);
			} else {
				appInfo = systemAppInfos.get(position-userAppInfos.size());
			}
			viewHolder.iv_icon.setImageDrawable(appInfo.getIcon());
			viewHolder.tv_name.setText(appInfo.getName());
			if (appInfo.isInRom()) {
				viewHolder.tv_location.setText("手机内存");
			}else{
				viewHolder.tv_location.setText("外部存储卡");
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
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

	private String getTotalSpace(String path) {
		StatFs statFs = new StatFs(path);
		long blocks = statFs.getAvailableBlocks();
		long size = statFs.getBlockSize();
		return Formatter.formatFileSize(this, blocks * size);
	}
}
