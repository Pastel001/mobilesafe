package com.dwl.mobilesafe;

import com.dwl.mobilesafe.db.dao.CommonNumberDao;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class CommonNumberQueryActivity extends Activity {
	private ExpandableListView elv;
	private static final String path = "/data/data/com.dwl.mobilesafe/files/commonnum.db";
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number_query);
		db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		elv = (ExpandableListView) findViewById(R.id.elv);
		elv.setAdapter(new CommonNumberAdapter());
		elv.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				TextView tv = (TextView) v;
				String phone = tv.getText().toString().split("\n")[1];
				Intent intent = new Intent();
				//intent.setAction("android.intent.action.DIAL");
				intent.setAction(Intent.ACTION_DIAL);
				//intent.addCategory("android.intent.category.DEFAULT");
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.setData(Uri.parse("tel:" + phone));
				startActivity(intent);
				return false;
			}
		});
	}

	private class CommonNumberAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return CommonNumberDao.getGroupCount(db);
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return CommonNumberDao.getChildrenCount(db, groupPosition);
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView tv;
			if (convertView != null) {
				tv = (TextView) convertView;
			} else {
				tv = new TextView(CommonNumberQueryActivity.this);
			}
			tv.setText("          "
					+ CommonNumberDao.getGroupName(db, groupPosition));
			return tv;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView tv;
			if (convertView != null) {
				tv = (TextView) convertView;
			} else {
				tv = new TextView(CommonNumberQueryActivity.this);
			}
			tv.setText(CommonNumberDao.getChild(db, groupPosition,
					childPosition));
			return tv;
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		db.close();
	}
}
