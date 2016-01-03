package com.dwl.mobilesafe;

import com.dwl.mobilesafe.utils.Md5utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	private GridView gv_home;
	private static final String[] functionName = { "�ֻ�����", "ͨѶ��ʿ", "�������",
			"���̹���", "����ͳ��", "�ֻ�ɱ��", "��������", "�߼�����", "��������" };
	private static final int[] icon = { R.drawable.safe,
			R.drawable.callmsgsafe, R.drawable.app, R.drawable.taskmanager,
			R.drawable.netmanager, R.drawable.trojan, R.drawable.sysoptimize,
			R.drawable.atools, R.drawable.settings };
	protected static final String TAG = "HomeActivity";
	private SharedPreferences sp;
	private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		gv_home = (GridView) findViewById(R.id.gv_home);
		gv_home.setAdapter(new HomeAdapter());
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
				switch (position) {
				case 0:// �ֻ�����
					showLostFindDialog();
					break;
				case 1:// ͨѶ��ʿ
					intent = new Intent(HomeActivity.this,
							CallSmsSafeActivity.class);
					startActivity(intent);
					break;
				case 2:// �������
					intent = new Intent(HomeActivity.this, AppManagementActivity.class);
					startActivity(intent);
					break;
				case 7:// �߼�����
					intent = new Intent(HomeActivity.this, AtoolsActivity.class);
					startActivity(intent);
					break;
				case 8:// ��������
					intent = new Intent(HomeActivity.this,
							SettingActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
			}

		});
	}

	private void showLostFindDialog() {
		if (isSetupPassword()) {
			showEnterDialog();
		} else {
			showSetPasswordDialog();
		}
	}

	private EditText et_password;
	private EditText et_password_confirm;
	private Button bt_ok;
	private Button bt_cancel;

	/**
	 * �]�����ù����룬������������
	 */
	private void showSetPasswordDialog() {
		AlertDialog.Builder builder = new Builder(this);
		View v = View.inflate(this, R.layout.dialog_setup_password, null);
		et_password = (EditText) v.findViewById(R.id.et_password);
		et_password_confirm = (EditText) v
				.findViewById(R.id.et_password_confirm);
		bt_ok = (Button) v.findViewById(R.id.bt_ok);
		bt_cancel = (Button) v.findViewById(R.id.bt_cancel);
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String pwd = et_password.getText().toString().trim();
				String pwd_confirm = et_password_confirm.getText().toString()
						.trim();
				if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd_confirm)) {
					Toast.makeText(HomeActivity.this, "���벻��Ϊ��", 0).show();
					return;
				}
				if (pwd.equals(pwd_confirm)) {
					Editor editor = sp.edit();
					editor.putString("password", Md5utils.encode(pwd));
					editor.commit();
					dialog.dismiss();
					Log.i(TAG, "���óɹ��������ֻ�����ҳ��");
					Intent intent = new Intent(getApplicationContext(),
							LostFindAcitvity.class);
					startActivity(intent);
				} else {
					Toast.makeText(HomeActivity.this, "���벻һ��", 0).show();
					return;
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		builder.setView(v);
		dialog = builder.show();
	}

	/**
	 * ���ù����룬У�����룬������ȷ������ֻ�����ҳ��
	 */
	private void showEnterDialog() {
		AlertDialog.Builder builder = new Builder(this);
		View v = View.inflate(this, R.layout.dialog_enter_password, null);
		et_password = (EditText) v.findViewById(R.id.et_password);

		bt_ok = (Button) v.findViewById(R.id.bt_ok);
		bt_cancel = (Button) v.findViewById(R.id.bt_cancel);
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String pwd = et_password.getText().toString().trim();
				String pwd_confirm = sp.getString("password", null);
				if (TextUtils.isEmpty(pwd)) {
					Toast.makeText(getApplicationContext(), "���벻��Ϊ��", 0).show();
					return;
				}
				if (Md5utils.encode(pwd).equals(pwd_confirm)) {
					dialog.dismiss();
					Log.i(TAG, "���óɹ��������ֻ�����ҳ��");
					Intent intent = new Intent(HomeActivity.this,
							LostFindAcitvity.class);
					startActivity(intent);
				} else {
					Toast.makeText(HomeActivity.this, "���벻һ��", 0).show();
					return;
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		builder.setView(v);
		dialog = builder.show();
	}

	/**
	 * �Ƿ����ù�����
	 * 
	 * @return true ���ù�
	 */
	private boolean isSetupPassword() {
		String password = sp.getString("password", null);
		return !TextUtils.isEmpty(password);
	}

	private class HomeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return functionName.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = View.inflate(HomeActivity.this,
					R.layout.list_homepage_item, null);
			ImageView iv_icon = (ImageView) v.findViewById(R.id.iv_home_item);
			TextView tv_name = (TextView) v.findViewById(R.id.tv_home_item);
			iv_icon.setImageResource(icon[position]);
			tv_name.setText(functionName[position]);
			return v;
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
}
