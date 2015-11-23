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
					 * ����Ļ�ϻ���ִ�еķ���
					 */
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						System.out.println();
						// �������󻬶�����ʾ��һ��ҳ��
						if (e1.getX() - e2.getX() > 100) {
							System.out.println("�������󻬶�����ʾ��һ��ҳ��");
							showNext();
							return true;
						}
						// �������һ�������ʾ��һ��ҳ��
						if (e2.getX() - e1.getX() > 100) {
							System.out.println("�������һ�������ʾ��һ��ҳ��");
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
