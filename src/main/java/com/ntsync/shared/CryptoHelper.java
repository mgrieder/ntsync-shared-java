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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Map;

import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.modes.AEADBlockCipher;
import org.spongycastle.crypto.modes.CCMBlockCipher;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.KeyParameter;

/**
 * Provides Helper methods for encoding or decoding contacts/groups data
 */
public final class CryptoHelper {

	/** 3 Bytes are used to encode length in CCM (15 - 3 = 12), see rfc3610 */
	public static final int IV_LEN = 12;

	private static final int VALUE_LEN = 4;

	public static final int PREAMBLE_LEN = IV_LEN + VALUE_LEN;

	public static final int MAC_SIZE = 128;

	/**
	 * Maximal Message is 2^24 because we use a fixed IV and 3 bytes (L) for
	 * length encoding
	 */
	public static final int MAX_MSG_SIZE = 16777216;

	private CryptoHelper() {
		// private: is a utility-class
	}

	/**
	 * 
	 * @param key
	 * @param values
	 * @param cipher
	 * @param privateKey
	 * @return
	 * @throws InvalidCipherTextException
	 * @throws UnsupportedEncodingException
	 */
	static String decodeStringValue(byte key, Map<Byte, ByteBuffer> values,
			AEADBlockCipher cipher, Key privateKey)
			throws InvalidCipherTextException, UnsupportedEncodingException {
		byte[] val = decodeValue(key, values, cipher, privateKey);
		String orgValue = null;
		if (val != null) {
			// UTF-8 is default on Android
			orgValue = new String(val, SyncDataHelper.DEFAULT_CHARSET_NAME);
		}
		return orgValue;
	}

	/**
	 * 
	 * @param key
	 * @param values
	 * @param cipher
	 * @param privateKey
	 * @return
	 * @throws InvalidCipherTextException
	 * @throws UnsupportedEncodingException
	 */
	static byte[] decodeValue(byte key, Map<Byte, ByteBuffer> values,
			AEADBlockCipher cipher, Key privateKey)
			throws InvalidCipherTextException, UnsupportedEncodingException {

		byte[] orgValue = null;
		ByteBuffer buf = values.get(key);
		if (buf != null) {
			byte[] data = buf.array();

			int pos = buf.position();
			int len = buf.limit() - pos;
			if (data != null && len > PREAMBLE_LEN) {
				// data: 12byte iv, 4 len value
				byte[] iv = new byte[IV_LEN];
				System.arraycopy(data, pos, iv, 0, IV_LEN);
				cipher.init(false, new AEADParameters(new KeyParameter(
						privateKey.getEncoded()), MAC_SIZE, iv));
				orgValue = cipherData(cipher, data, pos + PREAMBLE_LEN, len
						- PREAMBLE_LEN);
			}
		}
		return orgValue;
	}

	/**
	 * Write a String for transport to Server
	 * 
	 * @param secret
	 * @param out
	 * @param ecipher
	 * @param iv
	 * @param random
	 * @param key
	 * @param value
	 * @throws IOException
	 * @throws InvalidCipherTextException
	 */
	static void writeValue(Key secret, ByteArrayOutputStream out,
			AEADBlockCipher ecipher, byte[] iv, SecureRandom random, byte key,
			String value) throws IOException, InvalidCipherTextException {
		if (!SyncDataHelper.isEmpty(value)) {
			writeValue(secret, out, ecipher, iv, random, key,
					value.getBytes(SyncDataHelper.DEFAULT_CHARSET_NAME));
		}
	}

	/**
	 * Writes a Byte[]-Value for Transport to Server
	 * 
	 * @param secret
	 * @param out
	 * @param ecipher
	 *            will be initialized
	 * @param iv
	 *            buffer for a IV. Length has to be {@link #IV_LEN}
	 * @param random
	 * @param key
	 *            Value-Key
	 * @param value
	 * @throws IOException
	 * @throws InvalidCipherTextException
	 */
	static void writeValue(Key secret, ByteArrayOutputStream out,
			AEADBlockCipher ecipher, byte[] iv, SecureRandom random, byte key,
			byte[] value) throws IOException, InvalidCipherTextException {
		if (value != null) {
			// 1byte key, //16byte iv// //4 len
			out.write(key);
			random.nextBytes(iv);

			ecipher.init(true,
					new AEADParameters(new KeyParameter(secret.getEncoded()),
							MAC_SIZE, iv));
			out.write(iv);
			// convert to utf-8 (default android)
			byte[] enc = cipherData(ecipher, value);

			SyncDataHelper.writeInt(out, enc.length);

			out.write(enc);
		}
	}

	/**
	 * @return New Cipher for Decoding or Encoding
	 */
	public static AEADBlockCipher getCipher() {
		return new CCMBlockCipher(new AESEngine());
	}

	/**
	 * Encode or Decode some data
	 * 
	 * @param cipher
	 * @param data
	 * @return
	 * @throws InvalidCipherTextException
	 *             if the MAC fails to match
	 */
	public static byte[] cipherData(AEADBlockCipher cipher, byte[] data)
			throws InvalidCipherTextException {
		return cipherData(cipher, data, 0, data.length);
	}

	/**
	 * Encode or Decode a part of a buffer.
	 * 
	 * @param cipher
	 *            Already initialized cipher.
	 * @param data
	 * @param offset
	 * @param length
	 * @return encoded/decoded data
	 * @throws InvalidCipherTextException
	 *             if the MAC fails to match
	 */
	public static byte[] cipherData(AEADBlockCipher cipher, byte[] data,
			int offset, int length) throws InvalidCipherTextException {
		int minSize = cipher.getOutputSize(length);
		byte[] outBuf = new byte[minSize];
		int outLen = cipher.processBytes(data, offset, length, outBuf, 0);
		outLen += cipher.doFinal(outBuf, outLen);
		byte[] result = new byte[outLen];
		System.arraycopy(outBuf, 0, result, 0, result.length);
		return result;
	}
}
