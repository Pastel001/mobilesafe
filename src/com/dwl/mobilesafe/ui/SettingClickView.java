package com.dwl.mobilesafe.ui;



import com.dwl.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ��һ���������Բ��֡� һ���� ����������ʾ����2��textview
 */
public class SettingClickView extends RelativeLayout {
	private TextView tv_title;
	private TextView tv_desc;


	private void initView(Context context) {
		// ת�������ļ�->view���� ���view����ֱ�ӹ������Լ����ϡ�
		View.inflate(context, R.layout.ui_setting_click_view, this);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
	}

	public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public SettingClickView(Context context) {
		super(context);
		initView(context);
	}

	public void setTitle(String text) {
		tv_title.setText(text);
	}

	public void setDesc(String text) {
		tv_desc.setText(text);
	}

}
