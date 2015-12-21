package com.dwl.mobilesafe;

import com.dwl.mobilesafe.db.dao.AddressDao;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberAddressQueryActivity extends Activity {
	
	private static final String TAG = "NumberAddressQuery";
	private EditText et_number;
	private TextView tv_result;
	private Vibrator vibrator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address_query);
		et_number = (EditText) findViewById(R.id.et_number);
		tv_result = (TextView) findViewById(R.id.tv_result);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	}
	
	public void query(View view){
		String number = et_number.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_number.startAnimation(shake);
			vibrator.vibrate(100);
			Toast.makeText(this, "号码 不能为空", 0).show();
			return;
		} else {
			Log.i(TAG, "查询"+number+"的归属地");
			String address = AddressDao.getAddress(number);
			tv_result.setText(address);
		}
		
	}
}
