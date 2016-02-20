package com.dwl.mobilesafe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.dwl.mobilesafe.db.dao.AntivirusDao;
import com.dwl.mobilesafe.utils.Md5utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AntivirusActivity extends Activity {

	protected static final int SCANING = 1;
	protected static final int SCAN_FINISH = 2;
	private ImageView iv_scan;
	private ProgressBar progressBar1;
	private TextView tv_scan_status;
	private LinearLayout ll_container;

	private List<ScanInfo> virusInfos;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANING:
				ScanInfo info = (ScanInfo) msg.obj;
				tv_scan_status.setText("正在扫描：" + info.appName);
				TextView tv = new TextView(getApplicationContext());
				if (info.isVirus) {
					tv.setText("发现病毒：" + info.appName);
					tv.setTextColor(Color.RED);
				} else {
					tv.setText("扫描安全：" + info.appName);
					tv.setTextColor(Color.BLACK);
				}
				ll_container.addView(tv, 0);
				break;
			case SCAN_FINISH:
				iv_scan.clearAnimation();
				tv_scan_status.setText("扫描完毕");
				if (virusInfos.size()>0) {
					AlertDialog.Builder builder = new Builder(AntivirusActivity.this);
					builder.setTitle("警告！！！");
					builder.setMessage("您的手机发现了"+virusInfos.size()+"个病毒，请立刻处理！！！");
					builder.setNegativeButton("下次再说", null);
					builder.setPositiveButton("立刻处理", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							for (ScanInfo virusInfo : virusInfos) {
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_DELETE);
								intent.setData(Uri.parse("package:"+virusInfo.packageName));
								startActivity(intent);
							}
						}
					});
					builder.show();
				} else {
					Toast.makeText(getApplicationContext(),"您的手机没有发现病毒",0).show();
				}
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);

		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);

		//显示扫描动画
		RotateAnimation ra = new RotateAnimation(0, 360f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setDuration(1000);
		ra.setRepeatCount(Animation.INFINITE);
		iv_scan.startAnimation(ra);
		
		tv_scan_status.setText("正在初始化100核杀毒引擎！！！");
		virusInfos = new ArrayList<ScanInfo>();
		new Thread() {
			public void run() {
				//sleep2s，假装初始化100核杀毒引擎
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				//获取md5，查询病毒数据库
				PackageManager pm = getPackageManager();
				List<PackageInfo> packageInfos = pm
						.getInstalledPackages(PackageManager.GET_SIGNATURES
								+ PackageManager.GET_UNINSTALLED_PACKAGES);
				progressBar1.setMax(packageInfos.size());
				int progress = 0;
				Random random = new Random();
				for (PackageInfo packageInfo : packageInfos) {
					ScanInfo scanInfo = new ScanInfo();
					scanInfo.appName = (String) packageInfo.applicationInfo
							.loadLabel(pm).toString();
					scanInfo.packageName = packageInfo.applicationInfo.packageName;
					String md5 = Md5utils.encode(packageInfo.signatures[0]
							.toCharsString());
					System.out.println(scanInfo.appName+":"+md5);
					Boolean result = AntivirusDao.isVirus(md5);
					if (result) {
						scanInfo.isVirus = true;
						virusInfos.add(scanInfo);
					} else {
						scanInfo.isVirus = false;
					}
					progress++;
					progressBar1.setProgress(progress);
					Message msg = Message.obtain();
					msg.what = SCANING;
					msg.obj = scanInfo;
					handler.sendMessage(msg);
					try {
						Thread.sleep(random.nextInt(500));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				handler.sendMessage(msg);
			};
		}.start();
	}

	private static class ScanInfo {
		private boolean isVirus;
		private String appName;
		private String packageName;
	}
}
