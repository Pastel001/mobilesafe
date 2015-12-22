package com.dwl.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dwl.mobilesafe.db.dao.BlackNumberDao;
import com.dwl.mobilesafe.dto.BlackNumber;

public class CallSmsSafeActivity extends Activity {
	private ListView lv_call_sms_safe;
	private List<BlackNumber> list;
	private BlackNumberDao dao;
	private LinearLayout ll_loading;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ll_loading.setVisibility(View.INVISIBLE);
			lv_call_sms_safe.setAdapter(new CallSmsSafeAdapter());
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		lv_call_sms_safe = (ListView) findViewById(R.id.lv_call_sms_safe);
		ll_loading = (LinearLayout) findViewById(R.id.ll_laoding);
		dao = new BlackNumberDao(this);
		// 耗时的操作写在子线程
		new Thread() {
			public void run() {
				ll_loading.setVisibility(View.VISIBLE);
				list = dao.findAll();
				// 子线程不能更新UI
				// lv_call_sms_safe.setAdapter(new CallSmsSafeAdapter());
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private class CallSmsSafeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_call_sms_safe_item, null);
				holder = new ViewHolder();
				holder.tv_number = (TextView) view.findViewById(R.id.number);
				holder.tv_mode = (TextView) view.findViewById(R.id.mode);
				view.setTag(holder);
			}
			BlackNumber blackNumber = list.get(position);
			holder.tv_number.setText(blackNumber.getNumber());
			String mode = blackNumber.getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("电话拦截");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("短信拦截");
			} else if ("3".equals(mode)) {
				holder.tv_mode.setText("全部拦截");
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	class ViewHolder {
		TextView tv_number;
		TextView tv_mode;
	}
}
