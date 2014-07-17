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


import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import com.ntsync.shared.SyncDataHelper;

public class SyncDataHelperTest {

	@Test
	public void testReadLong() {
		Random rand = new Random();

		for (int i = 0; i < 500; i++) {
			long value = rand.nextLong();

			byte[] buffer = new byte[8];
			int index = SyncDataHelper.writeLong(buffer, value, 0);
			assertEquals(8, index);
			long readVal = SyncDataHelper.readLong(buffer, 0);
			assertEquals(value, readVal);
		}
	}

	@Test
	public void testReadInt() {
		Random rand = new Random();

		for (int i = 0; i < 500; i++) {
			int value = rand.nextInt();

			byte[] buffer = new byte[4];
			int index = SyncDataHelper.writeInt(buffer, value, 0);
			assertEquals(4, index);
			int readVal = SyncDataHelper.readInt(buffer, 0);
			assertEquals(value, readVal);
		}
	}

	@Test
	public void testReadShort() {
		Random rand = new Random();

		for (int i = 0; i < 500; i++) {
			short value = (short) rand.nextInt();

			byte[] buffer = new byte[2];
			int index = SyncDataHelper.writeShort(buffer, value, 0);
			assertEquals(2, index);
			short readVal = SyncDataHelper.readShort(buffer, 0);
			assertEquals(value, readVal);
		}
	}

	@Test
	public void testIsEmpty() {
		assertTrue(SyncDataHelper.isEmpty(null));
		assertTrue(SyncDataHelper.isEmpty(""));
		assertFalse(SyncDataHelper.isEmpty("a"));
	}

}
