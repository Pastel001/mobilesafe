package com.dwl.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberAddressQuery extends Activity {
	
	private static final String TAG = "NumberAddressQuery";
	private EditText et_number;
	private TextView tv_result;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address_query);
		et_number = (EditText) findViewById(R.id.et_number);
		tv_result = (TextView) findViewById(R.id.tv_result);
	}
	
	public void query(View view){
		String number = et_number.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(this, "号码 不能为空", 0).show();
			return;
		} else {
			Log.i(TAG, "查询"+number+"的归属地");
		}
		
	}
}
