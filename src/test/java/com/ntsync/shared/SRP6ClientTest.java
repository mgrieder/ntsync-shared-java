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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.agreement.srp.SRP6Client;
import org.spongycastle.crypto.agreement.srp.SRP6Server;
import org.spongycastle.util.BigIntegers;

public class SRP6ClientTest {

	@Test
	public void testSRP() throws UnsupportedEncodingException, CryptoException {
		String username = "test@ntsync";
		String password = "testtest";

		Pair<byte[], BigInteger> verif = SRP6Helper.createUserVerification(
				username, password.getBytes(SyncDataHelper.DEFAULT_CHARSET));
		assertNotNull(verif);
		assertNotNull(verif.left);
		assertNotNull(verif.right);

		boolean ok = authenticate(username, password, verif.right,
				new BigInteger(1, verif.left));
		assertTrue(ok);
	}

	@Test
	public void testLongUsername() throws UnsupportedEncodingException,
			CryptoException {

		Random random = new Random();

		byte[] usernameBytes = new byte[SRP6Helper.PADLENGTH + 10];
		random.nextBytes(usernameBytes);
		String username = new String(usernameBytes,
				SyncDataHelper.DEFAULT_CHARSET_NAME);
		byte[] passwordBytes = new byte[30];
		String password = new String(passwordBytes,
				SyncDataHelper.DEFAULT_CHARSET_NAME);
		int passwordLen = random.nextInt(password.length()) + 1;
		password = password.substring(0, passwordLen);

		Pair<byte[], BigInteger> verif = SRP6Helper.createUserVerification(
				username, password.getBytes(SyncDataHelper.DEFAULT_CHARSET));
		boolean ok = authenticate(username, password, verif.right,
				new BigInteger(1, verif.left));
		assertTrue(ok);
	}

	@Test
	public void testSpecialChars() throws UnsupportedEncodingException,
			CryptoException {
		String username = "öäüöäöçç%àç&%/(=";
		String password = "öäü¨ü¨üè!:à£;:?`+ç¢*%ç";
		Pair<byte[], BigInteger> verif = SRP6Helper.createUserVerification(
				username, password.getBytes(SyncDataHelper.DEFAULT_CHARSET));
		boolean ok = authenticate(username, password, verif.right,
				new BigInteger(1, verif.left));
		assertTrue(ok);
	}

	@Test
	public void testWrongValues() throws UnsupportedEncodingException,
			CryptoException {
		// Use a real example
		BigInteger verifier = new BigInteger(
				"55257755262044051030844223562146083101403528561063739374620616087411961259409408433401659856756271274106010628403732374598504150677291845583731542312473787519128304749708533916353254685421335580347616149820200271641396740033815237605918919720803210216808951095083477023312762795205701441832050757193530578967");
		BigInteger userSalt = new BigInteger("4447357637071522079");
		String username = "test@ntsync";
		String password = "testtest";

		// Test some random values
		Random random = new Random();

		for (int i = 0; i < 10; i++) {
			// Test wrong verifier
			boolean ok = authenticate(username, password,
					verifier.add(BigInteger.valueOf(random.nextLong())),
					userSalt);
			assertTrue(!ok);

			// Test wrong username
			ok = authenticate(
					username.substring(0,
							Math.max(1, random.nextInt(username.length()))),
					password, verifier, userSalt);
			assertTrue(!ok);

			// Test wrong password
			ok = authenticate(
					username,
					password.substring(0,
							Math.max(1, random.nextInt(password.length()))),
					verifier, userSalt);
			assertTrue(!ok);
		}
	}

	@Test
	public void testCreateVerifier() throws UnsupportedEncodingException,
			CryptoException {
		// Test some random values
		Random random = new Random();
		byte[] usernameBytes = new byte[30];
		byte[] passwordBytes = new byte[30];
		CharsetDecoder decoder = Charset.forName(
				SyncDataHelper.DEFAULT_CHARSET_NAME).newDecoder();
		decoder.onMalformedInput(CodingErrorAction.IGNORE)
				.onUnmappableCharacter(CodingErrorAction.IGNORE);

		for (int i = 0; i < 20; i++) {
			String username = createRandomString(usernameBytes, decoder, random);
			String password = createRandomString(passwordBytes, decoder, random);

			Pair<byte[], BigInteger> verif = SRP6Helper.createUserVerification(
					username, password.getBytes(SyncDataHelper.DEFAULT_CHARSET));
			assertNotNull(verif.left);
			assertNotNull(verif.right);

			boolean ok = authenticate(username, password, verif.right,
					new BigInteger(1, verif.left));
			if (!ok) {
				assertTrue("Username:" + username + " Password: " + password,
						ok);
			}
		}
	}

	@Test
	public void createVerification() throws UnsupportedEncodingException {
		// Test some random values
		Random random = new Random();
		byte[] usernameBytes = new byte[30];
		byte[] passwordBytes = new byte[30];
		CharsetDecoder decoder = Charset.forName(
				SyncDataHelper.DEFAULT_CHARSET_NAME).newDecoder();
		decoder.onMalformedInput(CodingErrorAction.IGNORE)
				.onUnmappableCharacter(CodingErrorAction.IGNORE);

		for (int i = 0; i < 500; i++) {
			String username = createRandomString(usernameBytes, decoder, random);
			String password = createRandomString(passwordBytes, decoder, random);

			Pair<byte[], BigInteger> verif = SRP6Helper.createUserVerification(
					username, password.getBytes(SyncDataHelper.DEFAULT_CHARSET));
			assertNotNull(verif.left);
			assertNotNull(verif.right);
			assertTrue(verif.left.length > 0);
		}
	}

	private String createRandomString(byte[] stringBuffer,
			CharsetDecoder decoder, Random random) {
		random.nextBytes(stringBuffer);
		CharBuffer buffer = null;
		do {
			try {
				buffer = decoder.decode(ByteBuffer.wrap(stringBuffer));
			} catch (CharacterCodingException e) {
				// ignore
			}
		} while (buffer == null);
		String str = buffer.toString();
		int len = random.nextInt(str.length()) + 1;
		StringBuilder buff = new StringBuilder();
		for (int i = 0; buff.length() < len && i < len; i++) {
			int codePoint = str.codePointAt(i);
			if (!Character.isISOControl(codePoint)) {
				buff.appendCodePoint(codePoint);
			}
		}
		return buff.toString();
	}

	/**
	 * 
	 * @return false if authentification failed.
	 */
	private boolean authenticate(String username, String password,
			BigInteger verifier, BigInteger userSalt)
			throws UnsupportedEncodingException, CryptoException {
		SRP6Client srpClient = new SRP6Client();
		srpClient.init(SRP6Helper.N_2024, SRP6Helper.G_2024,
				SRP6Helper.createDigest(), new SecureRandom());

		BigInteger srpA = srpClient.generateClientCredentials(
				BigIntegers.asUnsignedByteArray(userSalt),
				username.getBytes("UTF-8"), password.getBytes("UTF-8"));

		BigInteger serverB = null;
		BigInteger serverS;
		{
			// ServerCode
			SRP6Server srp6Server = new SRP6Server();
			srp6Server.init(SRP6Helper.N_2024, SRP6Helper.G_2024, verifier,
					SRP6Helper.createDigest(), new SecureRandom());
			serverB = srp6Server.generateServerCredentials();
			try {
				serverS = srp6Server.calculateSecret(srpA);
			} catch (CryptoException ex) {
				serverS = null;
			}
		}

		BigInteger clientS = srpClient.calculateSecret(serverB);

		BigInteger srpK = SRP6Helper.createHash(SRP6Helper.createDigest(),
				clientS);
		BigInteger clientM = SRP6Helper.createClientM(
				SRP6Helper.createDigest(), username, userSalt, srpA, serverB,
				srpK);

		BigInteger serverM;
		{
			// ServerCode
			BigInteger serverK = SRP6Helper.createK(SRP6Helper.createDigest(),
					serverS);
			BigInteger verifClientM = SRP6Helper.createClientM(
					SRP6Helper.createDigest(), username, userSalt, srpA,
					serverB, serverK);
			if (!verifClientM.equals(clientM)) {
				return false;
			}
			serverM = SRP6Helper.createServerM(SRP6Helper.createDigest(), srpA,
					clientM, serverK);
		}

		BigInteger verifServerM = SRP6Helper.createServerM(
				SRP6Helper.createDigest(), srpA, clientM, srpK);
		if (!verifServerM.equals(serverM)) {
			assertEquals(serverM, verifServerM);
		}
		return true;
	}

	@Test
	public void testCreateSRPassword() {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[SRP6Helper.PWD_SALT_LENGTH];
		random.nextBytes(salt);
		byte[] pwd = SRP6Helper.createSRPPassword("dfadfsdfködfks", salt);
		assertEquals(32, pwd.length);

		random.nextBytes(salt);
		byte[] pwd2 = SRP6Helper.createSRPPassword("dfadfsdfködfks", salt);

		Assert.assertFalse(
				"Should be always another Password with a different salt",
				Arrays.equals(pwd, pwd2));
	}
}
