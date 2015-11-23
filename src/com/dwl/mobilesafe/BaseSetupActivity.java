package com.dwl.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {
	protected GestureDetector gesDetector;
	protected SharedPreferences sp;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		gesDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					/**
					 * 在屏幕上滑动执行的方法
					 */
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						System.out.println();
						// 从右向左滑动，显示下一个页面
						if (e1.getX() - e2.getX() > 100) {
							System.out.println("从右向左滑动，显示下一个页面");
							showNext();
							return true;
						}
						// 从左向右滑动，显示上一个页面
						if (e2.getX() - e1.getX() > 100) {
							System.out.println("从左向右滑动，显示上一个页面");
							showPre();
							return true;
						}
						return super.onFling(e1, e2, velocityX, velocityY);
					}

				});
		System.out.println("onCreate in BaseSetupActivity");
	}

	public abstract void showNext();

	public abstract void showPre();

	public void next(View view) {
		showNext();
	}

	public void pre(View view) {
		showPre();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gesDetector.onTouchEvent(event);
		System.out.println("BaseActivity--onTouchEvent");
		return super.onTouchEvent(event);
	}
}
