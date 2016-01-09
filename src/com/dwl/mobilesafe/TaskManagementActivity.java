package com.dwl.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dwl.mobilesafe.engine.TaskInfoProvider;
import com.dwl.mobilesafe.utils.SystemInfoUtils;

public class TaskManagementActivity extends Activity {

	private int runningProcessCount;
	private long availRam;
	private long totalRam;

	private TextView tv_count;
	private TextView tv_mem;

	private ListView lv_tasks;
	private LinearLayout ll_loading;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			ll_loading.setVisibility(View.INVISIBLE);
			lv_tasks.setAdapter(new TaskInfosAdapter());
		};
	};
	private List taskInfos;
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

		fillData();
	}

	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				taskInfos = TaskInfoProvider.getTaskInfos(getApplicationContext());
				handler.sendEmptyMessage(0);
			};
		}.start();
	}
	
	private class TaskInfosAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return taskInfos.size();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = new TextView(getApplicationContext());
			tv.setText(taskInfos.get(position).toString());
			return tv;
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
}
