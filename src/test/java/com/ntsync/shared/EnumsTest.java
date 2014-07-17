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


import org.junit.Assert;
import org.junit.Test;

public class EnumsTest {

	@Test
	public void testPayPalConfirmationResult() {
		PayPalConfirmationResult[] values = PayPalConfirmationResult.values();
		for (PayPalConfirmationResult value : values) {
			Assert.assertEquals(value,
					PayPalConfirmationResult.fromErrorVal(value.getErrorVal()));
		}
	}

	@Test
	public void testUserRegistrationState() {
		UserRegistrationState[] values = UserRegistrationState.values();
		for (UserRegistrationState value : values) {
			Assert.assertEquals(value,
					UserRegistrationState.fromErrorVal(value.getStateVal()));
		}
	}

	@Test
	public void testSyncState() {
		SyncState[] values = SyncState.values();
		for (SyncState value : values) {
			Assert.assertEquals(value,
					SyncState.fromErrorVal(value.getErrorVal()));
		}
	}
}
