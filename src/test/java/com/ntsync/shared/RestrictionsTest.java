package com.ntsync.shared;

/*
 * Copyright (C) 2014 Markus Grieder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>. 
 */


import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class RestrictionsTest {

	@Test
	public void testEquals() {

		Restrictions rest1 = new Restrictions(20, 30, false, null);
		Restrictions rest2 = new Restrictions(20, 30, false, null);
		Date time = new Date();
		Restrictions rest3 = new Restrictions(20, 30, false, time);
		Restrictions rest4 = new Restrictions(30, 30, false, null);
		Restrictions rest5 = new Restrictions(20, 30, true, null);
		Restrictions rest6 = new Restrictions(20, 40, false, null);
		Restrictions rest7 = new Restrictions(20, 30, false, time);

		Assert.assertNotEquals(rest1, null);
		Assert.assertNotEquals(rest1, "");
		Assert.assertNotEquals(rest1, rest3);
		Assert.assertNotEquals(rest1, rest3);
		Assert.assertNotEquals(rest1, rest4);
		Assert.assertNotEquals(rest1, rest5);
		Assert.assertNotEquals(rest1, rest6);
		Assert.assertNotEquals(rest3, rest1);
		Assert.assertEquals(rest1, rest2);
		Assert.assertEquals(rest1, rest1);
		Assert.assertEquals(rest3, rest7);

		Assert.assertEquals(20, rest1.getMaxContactCount());
		Assert.assertEquals(30, rest1.getMaxGroupCount());
		Assert.assertEquals(true, rest5.isPhotoSyncSupported());
		Assert.assertEquals(time, rest3.getValidUntil());
	}
}
