package com.dwl.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
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
					if (offset >= counts) {// 不需要再加载数据，直接return
						if (list.size() == view.getLastVisiblePosition() + 1) {// 只有当不需要再加载数据，且拉动到最后一个位置时显示没有更多数据
							Toast.makeText(getApplicationContext(), "没有更多数据了",
									0).show();
							return;
						}
						return;
					}
					if (list.size() - view.getLastVisiblePosition() <= 3) {// getLastVisiblePosition,listView的开始位置为0，小于3条数据未展示时开始加载
						if (isloading) {// 因为停止时可能是倒数第3个，加载过程中再次停止到第2个，此判断避免fillData同时被执行
							return;
						}
						fillData();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_call_sms_safe_item, null);
				holder = new ViewHolder();
				holder.tv_number = (TextView) view.findViewById(R.id.tv_number);
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);
				view.setTag(holder);
			}
			BlackNumber blackNumber = list.get(position);
			holder.tv_number.setText(blackNumber.getNumber());
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final BlackNumber blackNumber = list.get(position);
					AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
					builder.setMessage("确定从黑名单中移除"+blackNumber.getNumber()+"吗？");
					builder.setTitle("提示");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dao.delete(blackNumber.getNumber());
							list.remove(position);
							counts--;
							offset--;
							adapter.notifyDataSetChanged();
						}
					});
					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
					builder.create().show();
				}
			});
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
		ImageView iv_delete;
	}

	public void addBlackNumber(View view) {
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View dialogView = View.inflate(this, R.layout.dialog_add_black_number,
				null);
		final EditText et_black_number = (EditText) dialogView
				.findViewById(R.id.et_black_number);
		final RadioGroup rg_mode = (RadioGroup) dialogView
				.findViewById(R.id.rg_mode);
		Button bt_ok = (Button) dialogView.findViewById(R.id.bt_ok);
		Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String number = et_black_number.getText().toString().trim();
				if (TextUtils.isEmpty(number)) {
					Toast.makeText(getApplicationContext(), "电话号码不能为空", 0)
							.show();
					return;
				}
				int id = rg_mode.getCheckedRadioButtonId();
				String mode = "3";
				switch (id) {
				case R.id.rb_all:
					mode = "3";
					break;
				case R.id.rb_phone:
					mode = "1";
					break;
				case R.id.rb_sms:
					mode = "2";
					break;
				}
				// 数据库
				dao.add(number, mode);
				// 界面
				BlackNumber blackNumber = new BlackNumber(number, mode);
				list.add(0, blackNumber);
				counts++;
				offset++;
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setView(dialogView, 0, 0, 0, 0);
		dialog.show();
	}
}
