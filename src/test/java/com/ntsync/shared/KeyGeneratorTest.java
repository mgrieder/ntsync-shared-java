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


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.junit.Assert;
import org.junit.Test;

import com.ntsync.shared.KeyGenerator;

public class KeyGeneratorTest {

	@Test
	public void testGenerateKey() throws NoSuchAlgorithmException,
			InvalidKeySpecException {

		String pwd = "test dfdf adfd dfsdfs";
		int saltLength = 8;
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[saltLength];
		random.nextBytes(salt);

		int keyLength = 256;
		int iterations = 1000;
		KeyGenerator gen = new KeyGenerator(iterations, keyLength);
		byte[] key = gen.generateKey(pwd, salt).getEncoded();

		// Compare with JDK6 implementation
		KeySpec keySpec = new PBEKeySpec(pwd.toCharArray(), salt, iterations,
				keyLength);
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("PBKDF2WithHmacSHA1");
		byte[] key2 = keyFactory.generateSecret(keySpec).getEncoded();

		Assert.assertArrayEquals(key2, key);
	}

}
