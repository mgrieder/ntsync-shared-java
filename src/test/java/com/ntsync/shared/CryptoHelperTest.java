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

import java.io.IOException;
import java.security.SecureRandom;

import org.junit.Test;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.modes.AEADBlockCipher;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.KeyParameter;

import com.ntsync.shared.RequestGeneratorTest.KeyPair;

public class CryptoHelperTest {

	@Test
	public void testCipherMAC() throws IOException, InvalidCipherTextException {
		AEADBlockCipher cipher = CryptoHelper.getCipher();

		// Change 1bit in Message and try to decode

		KeyPair secret = RequestGeneratorTest.generateKey();
		byte[] iv = new byte[CryptoHelper.IV_LEN];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);

		AEADParameters params = new AEADParameters(new KeyParameter(
				secret.key.getEncoded()), CryptoHelper.MAC_SIZE, iv);
		cipher.init(true, params);

		// Create Dummy Data
		byte[] data = new byte[100];
		random.nextBytes(data);

		byte[] encoded = CryptoHelper.cipherData(cipher, data);

		cipher.init(false, params);
		// Test decode without a change
		byte[] decodedData = CryptoHelper.cipherData(cipher, encoded);
		assertArrayEquals(data, decodedData);

		for (int i = 0; i < encoded.length; i++) {
			// Change 1bit
			final byte old = encoded[i];
			for (int j = 0; j < 8; j++) {
				int bitLocation = j % 8;
				encoded[i] = (byte) (encoded[i] ^ (byte) (1 << bitLocation));
				InvalidCipherTextException ex = null;
				try {
					decodedData = CryptoHelper.cipherData(cipher, encoded);
				} catch (InvalidCipherTextException e) {
					ex = e;
				}
				encoded[i] = old;
				assertNotNull(
						"Expected Exception for bit-modification in byte:" + i,
						ex);
			}
			// Change byte
			encoded[i] = (byte) (encoded[i] ^ 0xFF);
			InvalidCipherTextException ex = null;
			try {
				decodedData = CryptoHelper.cipherData(cipher, encoded);
			} catch (InvalidCipherTextException e) {
				ex = e;
			}
			encoded[i] = old;
			assertNotNull("Expected Exception for byte-modification in byte:"
					+ i, ex);
		}
	}

	@Test
	public void testMaxMessageSize() throws IOException,
			InvalidCipherTextException {

		AEADBlockCipher cipher = CryptoHelper.getCipher();

		KeyPair secret = RequestGeneratorTest.generateKey();
		byte[] iv = new byte[CryptoHelper.IV_LEN];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);

		AEADParameters params = new AEADParameters(new KeyParameter(
				secret.key.getEncoded()), CryptoHelper.MAC_SIZE, iv);
		cipher.init(true, params);

		int msgSize = (int) Math.pow(2, 24) - 1;
		// Create Dummy Data
		byte[] data = new byte[msgSize];

		CryptoHelper.cipherData(cipher, data);

		// to big (over ~16M)
		msgSize = (int) Math.pow(2, 24);
		data = new byte[msgSize];
		IllegalStateException ex = null;
		try {
			CryptoHelper.cipherData(cipher, data);
		} catch (IllegalStateException e) {
			ex = e;
		}
		assertNotNull("there should be an exception if message is too big", ex);
	}
}
