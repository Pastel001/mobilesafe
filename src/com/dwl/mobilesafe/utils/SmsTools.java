package com.dwl.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class SmsTools {

	public interface SmsDoingCallBack {
		/**
		 * �����е��õķ��������ñ��ݽ���
		 * @param progress
		 */
		public abstract void onBackup(int progress);
		/**
		 * ����ǰ���õķ��������ñ��ݶ���������
		 * @param total
		 */
		public abstract void beforeBackup(int total);
	}

	/**
	 * 
	 * @param context ������
	 * @param path �����ļ�·��
	 * @param smsBackupCallBack ��¶һ����ڸ�Activity��UI����ʽ�޸Ĳ���Ҫ�����˹��߷���
	 * @throws Exception
	 */
	public static void smsBackup(Context context, String path,
			SmsDoingCallBack smsDoingCallBack) throws Exception {
		// xml���л�
		XmlSerializer serializer = Xml.newSerializer();
		File file = new File(path);
		OutputStream fos = new FileOutputStream(file);
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		String namespace = "com.dwl.mobilesafe";
		serializer.startTag(namespace, "smss");
		// ���ݽ����߻�ȡϵͳ����Ӧ�����ݿ��еĶ���
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms");
		Cursor cursor = resolver.query(uri, new String[] { "address", "date",
				"type", "body" }, null, null, null);
		int progess = 0;
		smsDoingCallBack.beforeBackup(cursor.getCount());
		while (cursor.moveToNext()) {
			serializer.startTag(namespace, "sms");

			serializer.startTag(namespace, "address");
			serializer.text(cursor.getString(0));
			serializer.endTag(namespace, "address");

			serializer.startTag(namespace, "date");
			serializer.text(cursor.getString(1));
			serializer.endTag(namespace, "date");

			serializer.startTag(namespace, "type");
			serializer.text(cursor.getString(2));
			serializer.endTag(namespace, "type");

			serializer.startTag(namespace, "body");
			serializer.text(cursor.getString(3));
			serializer.endTag(namespace, "body");

			serializer.endTag(namespace, "sms");
			progess++;
			smsDoingCallBack.onBackup(progess);
			Thread.sleep(2000);
		}
		serializer.endTag(namespace, "smss");
		serializer.endDocument();
		fos.close();
	}
	
	public static void smsRecovery(Context context, String path,
			SmsDoingCallBack smsDoingCallBack) throws Exception {
		
		XmlPullParser  parser  = Xml.newPullParser();
		File file = new File(path);
		InputStream fis = new FileInputStream(file);
		parser.setInput(fis, "utf-8");
		
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms");
		int progess = 0;

		smsDoingCallBack.beforeBackup(parser.getNamespaceCount(1));
		
	}
	
	private class Sms{
		private String address;
		private String date;
		private String type;
		private String body;
		
	}
}
