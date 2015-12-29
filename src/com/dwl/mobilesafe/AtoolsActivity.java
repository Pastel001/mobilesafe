package com.dwl.mobilesafe;

import java.io.File;

import com.dwl.mobilesafe.utils.SmsTools;
import com.dwl.mobilesafe.utils.SmsTools.SmsDoingCallBack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

public class AtoolsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}

	public void numberAddressQuery(View view) {
		Intent intent = new Intent(this, NumberAddressQueryActivity.class);
		startActivity(intent);
	}
	
	public void commonNumberQuery(View view){
		Intent intent = new Intent(this, CommonNumberQueryActivity.class);
		startActivity(intent);
	}
	
	public void smsBackup(View view){
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			final File file = new File(Environment.getExternalStorageDirectory(), "smsBackup.xml");
			final ProgressDialog pd = new ProgressDialog(AtoolsActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setMessage("备份中...");
			pd.show();
			new Thread(){
				public void run() {
					try {
						SmsTools.smsBackup(AtoolsActivity.this, file.getAbsolutePath(),new SmsDoingCallBack() {
							@Override
							public void onBackup(int progress) {
								pd.setProgress(progress);
							}
							@Override
							public void beforeBackup(int total) {
								pd.setMax(total);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
					pd.dismiss();
				};
			}.start();
		}
		else {
			Toast.makeText(this, "sd卡不可用", 0).show();
		}
	}
	public void smsRecovery(View view){
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			final File file = new File(Environment.getExternalStorageDirectory(), "smsBackup.xml");
			final ProgressDialog pd = new ProgressDialog(AtoolsActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setMessage("恢复中...");
			pd.show();
			new Thread(){
				public void run() {
					try {
						SmsTools.smsRecovery(AtoolsActivity.this, file.getAbsolutePath(),new SmsDoingCallBack() {
							@Override
							public void onBackup(int progress) {
								pd.setProgress(progress);
							}
							@Override
							public void beforeBackup(int total) {
								pd.setMax(total);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
					pd.dismiss();
				};
			}.start();
		}
		else {
			Toast.makeText(this, "sd卡不可用", 0).show();
		}
	}
}
