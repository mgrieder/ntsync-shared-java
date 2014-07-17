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


import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Base64;

import com.ntsync.shared.RequestGenerator.SyncResponse;

public class RequestGeneratorTest {

	@Test
	public void testPrepareServerRequest() throws IOException,
			HeaderParseException, HeaderCreateException {
		RawContact rawContact1 = new RawContact("Hans Muster", "Hans",
				"Muster", null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, false,
				null, null, false, null, false, null, false, null, 1, false, -1);
		List<RawContact> contacts1 = new ArrayList<RawContact>();
		contacts1.add(rawContact1);

		KeyPair pwd = generateKey();
		String saltStr = pwd.salt;
		SecretKey skey = pwd.key;

		byte[] requestClient1 = RequestGenerator.prepareServerRequest(
				new SyncAnchor(), contacts1, null, skey, "test", null, saltStr,
				null, null, null, false);

		// Zum Testen wird der Server-Request als client-Response verarbeitet,
		// da diese praktisch gleich sind.
		SyncResponse response = RequestGenerator.processServerResponse(skey,
				null, requestClient1);
		Assert.assertEquals(1, response.serverContacts.size());
	}

	static KeyPair generateKey() throws IOException {
		String pwd = PasswortGenerator.createPwd(5);
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		KeyGenerator keyGen = new KeyGenerator();
		SecretKey skey = keyGen.generateKey(pwd, salt);
		String saltStr = new String(Base64.encode(salt), "UTF-8");
		KeyPair pair = new KeyPair();
		pair.key = skey;
		pair.salt = saltStr;
		return pair;
	}

	public static class KeyPair {
		SecretKey key = null;
		String salt = null;
	}
}
