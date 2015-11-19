package com.dwl.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5utils {
	public static String encode(String pwd) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			byte[] result = digest.digest(pwd.getBytes());
			StringBuffer sb = new StringBuffer();
			for (byte b : result) {
				int number = b & 0xff;
				String str = Integer.toHexString(number);
				if (str.length() == 1) {
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
			// can't reach
		}
	}
}
