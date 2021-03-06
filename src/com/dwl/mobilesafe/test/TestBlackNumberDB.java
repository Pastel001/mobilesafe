package com.dwl.mobilesafe.test;

import java.util.Random;

import com.dwl.mobilesafe.db.BlackNumberDBOpenHelper;
import com.dwl.mobilesafe.db.dao.BlackNumberDao;

import android.test.AndroidTestCase;

public class TestBlackNumberDB extends AndroidTestCase {
	public void testCreateDB() throws Exception {
		BlackNumberDBOpenHelper help = new BlackNumberDBOpenHelper(getContext());
		help.getWritableDatabase();
	}

	public void TestAdd() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		long number = 18311049001l;
		Random random = new Random();
		for (long i = 0; i < 50; i++) {
			dao.add(String.valueOf(number + i),
					String.valueOf(random.nextInt(3) + 1));
		}
	}

	public void TestDelete() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.delete("119");
	}

	public void TestUpdate() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.update("119", "3");
	}

	public void TestFind() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		boolean result = dao.find("119");
		assertEquals(true, result);
	}

	public void TestFindMode() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		String mode = dao.findMode("119");
		assertEquals("3", mode);
	}
}
