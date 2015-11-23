package com.dwl.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class SelectContactActivity extends Activity {
	private ListView lv_contact;
	private List<Map<String, String>> data;
	private String phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		lv_contact = (ListView) findViewById(R.id.lv_contact);
		data = getContactData();
		if (data != null && data.size() > 0) {
			lv_contact.setAdapter(new SimpleAdapter(this, data,
					R.layout.list_contact_item,
					new String[] { "name", "phone" }, new int[]{R.id.name,R.id.phone}));
		} else {
			Toast.makeText(this, "联系人为空，请先创建联系人，或者手动输入电话号码", 0).show();
			this.finish();
		}

		lv_contact.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				phone = data.get(position).get("phone");
				Intent data = new Intent();
				data.putExtra("phone", phone);
				setResult(0, data);
				SelectContactActivity.this.finish();
			}
		});
	}

	private List<Map<String, String>> getContactData() {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri datauri = Uri.parse("content://com.android.contacts/data");
		// 查询raw_contact表 取联系人id
		Cursor cursor = resolver.query(uri, new String[] { "contact_id" },
				null, null, null);
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			System.out.println("联系人的id为:" + id);
			if (id != null) {
				Map<String, String> map = new HashMap<String, String>();
				// 查询data表 把当前联系人的数据给取出来.
				Cursor dataCursor = resolver.query(datauri, null,
						"raw_contact_id=?", new String[] { id }, null);
				while (dataCursor.moveToNext()) {
					String data1 = dataCursor.getString(dataCursor
							.getColumnIndex("data1"));
					String mimetype = dataCursor.getString(dataCursor
							.getColumnIndex("mimetype"));
					if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
						System.out.println("电话:" + data1);
						map.put("phone", data1);
					} else if ("vnd.android.cursor.item/email_v2"
							.equals(mimetype)) {
						System.out.println("邮件地址:" + data1);
						map.put("email", data1);
					} else if ("vnd.android.cursor.item/name".equals(mimetype)) {
						System.out.println("姓名:" + data1);
						map.put("name", data1);
					}
				}
				data.add(map);
				dataCursor.close();
			}
		}
		cursor.close();
		return data;
	}
}
