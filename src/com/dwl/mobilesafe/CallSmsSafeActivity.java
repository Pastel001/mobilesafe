package com.dwl.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dwl.mobilesafe.db.dao.BlackNumberDao;
import com.dwl.mobilesafe.dto.BlackNumber;

public class CallSmsSafeActivity extends Activity {
	private ListView lv_call_sms_safe;
	private List<BlackNumber> list;
	private BlackNumberDao dao;
	private LinearLayout ll_loading;
	private CallSmsSafeAdapter adapter;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			isloading = false;
			ll_loading.setVisibility(View.INVISIBLE);
			if (adapter == null) {
				adapter = new CallSmsSafeAdapter();
				lv_call_sms_safe.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
		};
	};
	// 分页查询先关
	// 总数量
	private int counts;
	// 每次查询的数量，默认为10
	private int count = 10;
	// 每次查询的偏移量
	private int offset = 0;
	// 是否正在加载，从代码层防止重复加载
	private boolean isloading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		lv_call_sms_safe = (ListView) findViewById(R.id.lv_call_sms_safe);
		ll_loading = (LinearLayout) findViewById(R.id.ll_laoding);
		dao = new BlackNumberDao(this);
		// 获取一共有多少数据
		counts = dao.findCount();
		fillData();
		lv_call_sms_safe.setOnScrollListener(new OnScrollListener() {
			// 滚动状态变化调用的方法
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case SCROLL_STATE_IDLE:
					if (list.size() - view.getLastVisiblePosition() <= 3) {// getLastVisiblePosition,listView的开始位置为0
						if (isloading) {
							return;
						}
						fillData();
						if (offset >= counts
								&& (list.size() == view
										.getLastVisiblePosition() + 1)) {//只有当不需要再加载数据，且拉动到最后一个位置时显示没有更多数据
							Toast.makeText(getApplicationContext(), "没有更多数据了",
									0).show();
						}
					}
					break;

				case SCROLL_STATE_FLING:

					break;
				case SCROLL_STATE_TOUCH_SCROLL:

					break;
				}

			}

			// 滚动完成后调用的方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void fillData() {
		// 耗时的操作写在子线程
		if (offset >= counts) {
			return;
		}
		ll_loading.setVisibility(View.VISIBLE);
		isloading = true;
		new Thread() {
			public void run() {
				if (list == null) {
					list = dao.findPart(count, offset);
				} else {
					list.addAll(dao.findPart(count, offset));
				}
				offset += count;
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
