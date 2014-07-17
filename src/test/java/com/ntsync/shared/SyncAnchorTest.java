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

public class SyncAnchorTest {

	@Test
	public void test() {
		SyncAnchor anchor = new SyncAnchor();

		long firstAnchor = System.currentTimeMillis();
		anchor.setAnchor(ContactConstants.TYPE_CONTACT, firstAnchor);

		Assert.assertEquals(firstAnchor,
				anchor.getAnchor(ContactConstants.TYPE_CONTACT).longValue());
		Assert.assertEquals(1, anchor.containers().size());
		Assert.assertEquals(new Date(firstAnchor),
				anchor.getAnchorDate(ContactConstants.TYPE_CONTACT));
		Assert.assertTrue(anchor.containers().contains(
				ContactConstants.TYPE_CONTACT));

		Assert.assertNull(anchor
				.getAnchorDate(ContactConstants.TYPE_CONTACTGROUP));

		anchor.setAnchor(ContactConstants.TYPE_CONTACTGROUP, 0);
		Assert.assertNull(anchor
				.getAnchorDate(ContactConstants.TYPE_CONTACTGROUP));
		Assert.assertEquals(2, anchor.containers().size());
	}
}
