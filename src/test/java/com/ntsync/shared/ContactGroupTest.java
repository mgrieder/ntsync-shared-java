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


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.ntsync.shared.RequestGenerator.SyncResponse;
import com.ntsync.shared.RequestGeneratorTest.KeyPair;

public class ContactGroupTest {

	@Test
	public void testContactGroupSerializing() throws HeaderParseException,
			IOException, HeaderCreateException {

		String sourceId = UUID.randomUUID().toString();
		Date lastMod = new Date();
		ContactGroup c = new ContactGroup(5L, sourceId, "title", "notes",
				false, lastMod, -1);

		KeyPair key = RequestGeneratorTest.generateKey();

		List<ContactGroup> groups = new ArrayList<ContactGroup>();
		groups.add(c);
		byte[] requestClient1 = RequestGenerator.prepareServerRequest(
				new SyncAnchor(), null, groups, key.key, "test", null,
				key.salt, null, null, null, false);

		// Zum Testen wird der Server-Request als client-Response verarbeitet,
		// da diese praktisch gleich sind.
		SyncResponse response = RequestGenerator.processServerResponse(key.key,
				null, requestClient1);

		assertEquals(1, response.serverGroups.size());
		ContactGroup newC = response.serverGroups.get(0);
		assertEquals(c.getTitle(), newC.getTitle());
		assertEquals(c.getNotes(), newC.getNotes());
		assertEquals(c.getRawId(), newC.getRawId());
		assertEquals(c.getSourceId(), newC.getSourceId());
		assertEquals(c.getLastModified(), newC.getLastModified());
	}
}
