package com.dwl.mobilesafe;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class DragViewActivity extends Activity {
	protected static final String TAG = "DragViewActivity";
	private ImageView iv_drag;
	private SharedPreferences sp;
	private WindowManager wm;
	private int windowWith;
	private int windowHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_view);
		iv_drag = (ImageView) findViewById(R.id.iv_drag);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		windowWith = wm.getDefaultDisplay().getWidth();
		windowHeight = wm.getDefaultDisplay().getHeight();
		int lastx = sp.getInt("lastx", 0);
		int lasty = sp.getInt("lasty", 0);
		// layoutҪ�ڿؼ��Ѿ���Ⱦ��ϣ���ʾ�ڽ�����֮�����Ч
		// iv_drag.layout(lastx, lasty, iv_drag.getWidth()+lastx,
		// iv_drag.getHeight()+lasty);
		// ʹ��LayoutParams������δ��Ⱦ��ɣ���ʾ�ռ�֮ǰ���޸Ŀؼ�λ��
		LayoutParams params = (LayoutParams) iv_drag.getLayoutParams();
		params.leftMargin = lastx;
		params.topMargin = lasty;
		iv_drag.setLayoutParams(params);
		// iv_drag�����¼�
		iv_drag.setOnTouchListener(new OnTouchListener() {
			int startX;
			int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// ���ʱ��ʼ����λ��
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					// �����ƶ�ʱ��������ָ��λ��
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					int dx = newX - startX;
					int dy = newY - startY;
					// ������ָλ�Ƽ��㣬iv_drag��λ��
					// �ж�λ���Ƿ�Ϸ����Ƿ����ʾ����������
					int newl = iv_drag.getLeft() + dx;
					int newt = iv_drag.getTop() + dy;
					int newr = iv_drag.getRight() + dx;
					int newb = iv_drag.getBottom() + dy;
					if (newl < 0 || newt < 0 || newr > windowWith
							|| newb > windowHeight) {
						break;
					}
					iv_drag.layout(newl, newt, newr, newb);
					// ���¼����ֵ�����
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					// ��ָ�ƿ�ʱ��¼����iv_drag��λ�ã�������ʾ�ٴν����ҳ����ʾiv��λ��
					int lastx = iv_drag.getLeft();
					int lasty = iv_drag.getTop();
					Editor editor = sp.edit();
					editor.putInt("lastx", lastx);
					editor.putInt("lasty", lasty);
					editor.commit();
					break;
				}
				return false;
			}
		});
		// iv_drag��˫���¼�--����
		iv_drag.setOnClickListener(new OnClickListener() {
			long[] times = new long[2];

			@Override
			public void onClick(View v) {
				Log.i(TAG, "onCLick");
				System.arraycopy(times, 1, times, 0, times.length - 1);
				times[times.length - 1] = SystemClock.uptimeMillis();
				/*
				 * if (times[0]>0) { if (times[times.length-1] - times[0] <=
				 * 500) { times = new long[2]; Log.i(TAG, "˫��"); } }
				 */
				if (times[0] >= (times[times.length - 1] - 500)) {
					times = new long[2];
					Log.i(TAG, "˫��");
					iv_drag.layout(windowWith / 2 - iv_drag.getWidth() / 2,
							iv_drag.getTop(),
							windowWith / 2 + iv_drag.getWidth() / 2,
							iv_drag.getBottom());
					int lastx = iv_drag.getLeft();
					int lasty = iv_drag.getTop();
					Editor editor = sp.edit();
					editor.putInt("lastx", lastx);
					editor.putInt("lasty", lasty);
					editor.commit();
				}
			}
		});
	}
}
