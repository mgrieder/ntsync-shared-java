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

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;

/**
 * Generates a Key with PBKDF2
 */
public class KeyGenerator {

	private static final int KEY_LENGTH = 256;
	private static final int DEFAULT_ITERATIONS = 20000;
	private int iterationCount;
	private int keyLength;

	public KeyGenerator() {
		this(DEFAULT_ITERATIONS, KEY_LENGTH);
	}

	/**
	 * 
	 * @param iterationCount
	 *            count for PBKDF2
	 * @param keyLength
	 *            in bites
	 */
	public KeyGenerator(int iterationCount, int keyLength) {
		this.iterationCount = iterationCount;
		this.keyLength = keyLength;

	}

	/**
	 * Creates a Key for Data Encryption based on a Password
	 * 
	 * @param pwd
	 * @param salt
	 * @return never null
	 */
	public SecretKey generateKey(String pwd, byte[] salt) {
		PBEParametersGenerator generator = new PKCS5S2ParametersGenerator();
		generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(pwd
				.toCharArray()), salt, iterationCount);
		KeyParameter params = (KeyParameter) generator
				.generateDerivedParameters(keyLength);

		byte[] endcoded = params.getKey();
		return new SecretKeySpec(endcoded, "AES");
	}
}
