package com.dwl.mobilesafe.ui;

import com.dwl.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 做一个特殊的相对布局。 一出生 布局里面显示的是2个textview 一个checkbox 还有一个view
 */
public class SettingItemView extends RelativeLayout {
	private TextView tv_title;
	private TextView tv_desc;
	private CheckBox cb_status;

	private void initView(Context context) {
		//转化布局文件->view对象 这个view对象直接挂载在自己身上。
		View.inflate(context, R.layout.ui_setting_item_view, this);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		cb_status = (CheckBox) this.findViewById(R.id.cb_status);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}

	public boolean isChecked() {
		return cb_status.isChecked();
	}
	public void setChecked(boolean checked){
		cb_status.setChecked(checked);
	}
	
	public void setTitle(String text){
		tv_title.setText(text);
	}
	public void setDesc(String text){
		tv_desc.setText(text);
	}

}
