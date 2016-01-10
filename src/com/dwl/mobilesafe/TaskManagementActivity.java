package com.dwl.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dwl.mobilesafe.dto.TaskInfo;
import com.dwl.mobilesafe.engine.TaskInfoProvider;
import com.dwl.mobilesafe.service.AutoKillProcessService;
import com.dwl.mobilesafe.utils.ServiceStatusUtils;
import com.dwl.mobilesafe.utils.SystemInfoUtils;

public class TaskManagementActivity extends Activity {

	private int runningProcessCount;
	private long availRam;
	private long totalRam;

	private TextView tv_count;
	private TextView tv_mem;

	private ListView lv_tasks;
	private LinearLayout ll_loading;
	private TextView tv_status;
	private TaskInfosAdapter adapter;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ll_loading.setVisibility(View.INVISIBLE);
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			} else {
				adapter = new TaskInfosAdapter();
				lv_tasks.setAdapter(adapter);
			}
		};
	};
	private List<TaskInfo> taskInfos;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> systemTaskInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_management);
		tv_count = (TextView) findViewById(R.id.tv_count);
		tv_mem = (TextView) findViewById(R.id.tv_mem);
		runningProcessCount = SystemInfoUtils.getRunningAppCount(this);
		availRam = SystemInfoUtils.getAvailRam(this);
		totalRam = SystemInfoUtils.getTotalRam(this);
		tv_count.setText("运行中进程个数(" + runningProcessCount + ")");
		tv_mem.setText("剩余/总内存：" + Formatter.formatFileSize(this, availRam)
				+ "/" + Formatter.formatFileSize(this, totalRam));

		lv_tasks = (ListView) findViewById(R.id.lv_tasks);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		tv_status = (TextView) findViewById(R.id.tv_status);
		lv_tasks.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (userTaskInfos != null && systemTaskInfos != null) {
					if (firstVisibleItem <= userTaskInfos.size()) {
						tv_status.setText("用户进程（" + userTaskInfos.size() + "）");
					} else {
						tv_status.setText("系统进程（" + systemTaskInfos.size()
								+ "）");
					}
				}
			}
		});
		lv_tasks.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object object = lv_tasks.getItemAtPosition(position);
				CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
				if (object != null) {
					TaskInfo taskInfo = (TaskInfo) object;
					if (taskInfo.getPackageName().equals(getPackageName())) {
						return;
					}
					if (taskInfo.isChecked()) {
						taskInfo.setChecked(false);
						cb.setChecked(false);
					} else {
						taskInfo.setChecked(true);
						cb.setChecked(true);
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
				taskInfos = TaskInfoProvider
						.getTaskInfos(getApplicationContext());
				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();
				for (TaskInfo taskInfo : taskInfos) {
					if (taskInfo.isUserTask()) {
						userTaskInfos.add(taskInfo);
					} else {
						systemTaskInfos.add(taskInfo);
					}
				}
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private class TaskInfosAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return userTaskInfos.size() + 1 + systemTaskInfos.size() + 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("用户进程（" + userTaskInfos.size() + "）");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position == userTaskInfos.size() + 1) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("系统进程（" + systemTaskInfos.size() + "）");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			}
			View view;
			ViewHolder viewHolder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_task_info_item, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_icon);
				viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				viewHolder.tv_mem = (TextView) view.findViewById(R.id.tv_mem);
				viewHolder.cb = (CheckBox) view.findViewById(R.id.cb);
				view.setTag(viewHolder);
			}
			TaskInfo taskInfo;
			if (position <= userTaskInfos.size()) {
				taskInfo = (TaskInfo) userTaskInfos.get(position - 1);
			} else {
				taskInfo = (TaskInfo) systemTaskInfos.get(position - 1
						- userTaskInfos.size() - 1);
			}
			viewHolder.iv_icon.setImageDrawable(taskInfo.getIcon());
			viewHolder.tv_name.setText(taskInfo.getName());
			viewHolder.tv_mem.setText(Formatter.formatFileSize(
					getApplicationContext(), taskInfo.getMemsize()));
			viewHolder.cb.setChecked(taskInfo.isChecked());
			if (taskInfo.getPackageName().equals(getPackageName())) {
				viewHolder.cb.setVisibility(view.INVISIBLE);
			} else {
				viewHolder.cb.setVisibility(view.VISIBLE);
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			TaskInfo taskInfo;
			if (position == 0) {
				return null;
			} else if (position == userTaskInfos.size() + 1) {
				return null;
			} else if (position <= userTaskInfos.size()) {
				taskInfo = (TaskInfo) userTaskInfos.get(position - 1);
			} else {
				taskInfo = (TaskInfo) systemTaskInfos.get(position - 1
						- userTaskInfos.size() - 1);
			}
			return taskInfo;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	private static class ViewHolder {
		private ImageView iv_icon;
		private TextView tv_name;
		private TextView tv_mem;
		private CheckBox cb;

	}

	public void selectAll(View view) {
		/*
		 * for (TaskInfo userTaskInfo : userTaskInfos) {
		 * userTaskInfo.setChecked(true); } for (TaskInfo systemTaskInfo :
		 * systemTaskInfos) { systemTaskInfo.setChecked(true); }
		 */
		for (TaskInfo taskInfo : taskInfos) {
			if (taskInfo.getPackageName().equals(getPackageName())) {
				continue;
			}
			taskInfo.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}

	public void unSelect(View view) {
		/*
		 * for (TaskInfo userTaskInfo : userTaskInfos) {
		 * userTaskInfo.setChecked(!userTaskInfo.isChecked()); } for (TaskInfo
		 * systemTaskInfo : systemTaskInfos) {
		 * systemTaskInfo.setChecked(!systemTaskInfo.isChecked()); }
		 */
		for (TaskInfo taskInfo : taskInfos) {
			if (taskInfo.getPackageName().equals(getPackageName())) {
				continue;
			}
			taskInfo.setChecked(!taskInfo.isChecked());
		}
		adapter.notifyDataSetChanged();
	}

	public void clearAll(View view) {
		int total = 0;
		int memSum = 0;
		List<TaskInfo> killTaskInfos = new ArrayList<TaskInfo>();
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (TaskInfo userTaskInfo : userTaskInfos) {
			if (userTaskInfo.isChecked()) {
				am.killBackgroundProcesses(userTaskInfo.getPackageName());
				total++;
				memSum += userTaskInfo.getMemsize();
				// userTaskInfos.remove(userTaskInfo);
				killTaskInfos.add(userTaskInfo);
			}
		}
		for (TaskInfo systemTaskInfo : systemTaskInfos) {
			if (systemTaskInfo.isChecked()) {
				am.killBackgroundProcesses(systemTaskInfo.getPackageName());
				total++;
				memSum += systemTaskInfo.getMemsize();
				// systemTaskInfos.remove(systemTaskInfo);
				killTaskInfos.add(systemTaskInfo);
			}
		}
		if (total == 0) {
			Toast.makeText(this, "请选择要清理的进程", 0).show();
			return;
		} else {
			for (TaskInfo taskInfo : killTaskInfos) {
				if (taskInfo.isUserTask()) {
					userTaskInfos.remove(taskInfo);
				} else {
					systemTaskInfos.remove(taskInfo);
				}
			}
			Toast.makeText(
					this,
					"结束了" + total + "个进程,释放了"
							+ Formatter.formatFileSize(this, memSum) + "内存", 0)
					.show();
			adapter.notifyDataSetChanged();
			tv_count.setText("运行中进程个数(" + (runningProcessCount - total) + ")");
			tv_mem.setText("剩余/总内存："
					+ Formatter.formatFileSize(this, availRam + memSum) + "/"
					+ Formatter.formatFileSize(this, totalRam));
		}
	}

	public void enterSetting(View view) {
		Intent intent = new Intent();
		intent.setClass(this,AutoKillProcessService.class);
		boolean result = ServiceStatusUtils.isServiceRunning(this, "com.dwl.mobilesafe.service.AutoKillProcessService");
		if (result) {
			stopService(intent);
			Toast.makeText(this, "关闭锁屏清理进程服务", 0).show();
		}else {
			startService(intent);
			Toast.makeText(this, "开启锁屏清理进程服务", 0).show();
		}
		
	}
}
