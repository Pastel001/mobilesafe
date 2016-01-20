package com.dwl.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dwl.mobilesafe.db.dao.LockedAppDao;
import com.dwl.mobilesafe.dto.AppInfo;
import com.dwl.mobilesafe.engine.AppInfoProvider;

public class AppLockActivity extends Activity {
	private TextView tv_unlock;
	private TextView tv_locked;
	private LinearLayout ll_unlock;
	private LinearLayout ll_locked;
	private ListView lv_unlock;
	private ListView lv_locked;
	private TextView tv_unlock_number;
	private TextView tv_locked_number;

	private LockedAppDao dao;
	List<AppInfo> appInfos;
	List<AppInfo> unlockAppInfos;
	List<AppInfo> lockedAppInfos;

	private boolean isUnLockApp = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		tv_locked = (TextView) findViewById(R.id.tv_locked);
		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		ll_locked = (LinearLayout) findViewById(R.id.ll_locked);
		lv_unlock = (ListView) findViewById(R.id.lv_unlock);
		lv_locked = (ListView) findViewById(R.id.lv_locked);
		tv_unlock_number = (TextView) findViewById(R.id.tv_unlock_number);
		tv_locked_number = (TextView) findViewById(R.id.tv_locked_number);

		dao = new LockedAppDao(this);
		appInfos = AppInfoProvider.getAppInfos(this);
		lockedAppInfos = new ArrayList<AppInfo>();
		unlockAppInfos = new ArrayList<AppInfo>();

		for (AppInfo appInfo : appInfos) {
			if (dao.find(appInfo.getPackageName())) {
				lockedAppInfos.add(appInfo);
			} else {
				unlockAppInfos.add(appInfo);
			}
		}
		AppLockInfoAdapter adapter = new AppLockInfoAdapter();
		lv_unlock.setAdapter(adapter);
		lv_locked.setAdapter(adapter);
	}

	private class AppLockInfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (isUnLockApp) {
				tv_unlock_number.setText("未加锁应用程序个数：" + unlockAppInfos.size());
				return unlockAppInfos.size();
			} else {
				tv_locked_number.setText("已加锁应用程序个数：" + lockedAppInfos.size());
				return lockedAppInfos.size();
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final View view;
			ViewHolder holder;
			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_app_lock_info_item, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				holder.iv_status = (ImageView) view
						.findViewById(R.id.iv_status);
				view.setTag(holder);
			}
			final AppInfo appInfo;

			if (isUnLockApp) {
				appInfo = unlockAppInfos.get(position);
				holder.iv_status.setBackgroundResource(R.drawable.lock);
			} else {
				appInfo = lockedAppInfos.get(position);
				holder.iv_status.setBackgroundResource(R.drawable.unlock);
			}
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getName());
			holder.iv_status.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isUnLockApp) {
						unlockAppInfos.remove(appInfo);
						dao.add(appInfo.getPackageName());
						lockedAppInfos.add(appInfo);
						TranslateAnimation ta = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 1.0f,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0);
						ta.setDuration(500);
						view.startAnimation(ta);
					} else {
						lockedAppInfos.remove(appInfo);
						dao.delete(appInfo.getPackageName());
						unlockAppInfos.add(appInfo);
						TranslateAnimation ta = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, -1.0f,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0);
						ta.setDuration(500);
						view.startAnimation(ta);
					}
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							notifyDataSetChanged();
						}
					}, 500);
					/*try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					notifyDataSetChanged();*/
				}
			});
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
		private ImageView iv_status;

	}

	public void click(View view) {
		switch (view.getId()) {
		case R.id.tv_unlock:
			tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
			tv_locked.setBackgroundResource(R.drawable.tab_right_default);
			ll_unlock.setVisibility(View.VISIBLE);
			ll_locked.setVisibility(View.GONE);
			isUnLockApp = true;
			break;

		case R.id.tv_locked:
			tv_locked.setBackgroundResource(R.drawable.tab_right_pressed);
			tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
			ll_locked.setVisibility(View.VISIBLE);
			ll_unlock.setVisibility(View.GONE);
			isUnLockApp = false;
			break;
		}
	}
}
