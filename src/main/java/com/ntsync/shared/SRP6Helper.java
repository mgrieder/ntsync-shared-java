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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.agreement.srp.SRP6VerifierGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.BigIntegers;
import org.spongycastle.util.encoders.Hex;

/**
 * Provides Helper Method for SRP6 Clients and Servers
 */
public final class SRP6Helper {

	private static final int PBE_DEFAULT_ITERATIONS = 20000;

	private SRP6Helper() {
	}

	private static BigInteger fromHex(String hex) {
		return new BigInteger(1, Hex.decode(hex));
	}

	/** 2024 Group Parameters prime from RFC5054 and corresponding generator */
	public static final BigInteger N_2024 = fromHex("AC6BDB41 324A9A9B F166DE5E 1389582F AF72B665 1987EE07 FC319294"
			+ "3DB56050 A37329CB B4A099ED 8193E075 7767A13D D52312AB 4B03310D"
			+ "CD7F48A9 DA04FD50 E8083969 EDB767B0 CF609517 9A163AB3 661A05FB"
			+ "D5FAAAE8 2918A996 2F0B93B8 55F97993 EC975EEA A80D740A DBF4FF74"
			+ "7359D041 D5C33EA7 1D281E44 6B14773B CA97B43A 23FB8016 76BD207A"
			+ "436C6481 F1D2B907 8717461A 5B9D32E6 88F87748 544523B5 24B0D57D"
			+ "5EA77A27 75D2ECFA 032CFBDB F52FB378 61602790 04E57AE6 AF874E73"
			+ "03CE5329 9CCC041C 7BC308D8 2A5698F3 A8D0C382 71AE35F8 E9DBFBB6"
			+ "94B5C803 D89F7AE4 35DE236D 525F5475 9B65E372 FCD68EF2 0FA7111F"
			+ "9E4AFF73");

	public static final BigInteger G_2024 = BigInteger.valueOf(2);

	public static final int PADLENGTH = (N_2024.bitLength() + 7) / 8;

	/**
	 * @return Hash-Function used for SRP. Actually SHA-256
	 */
	public static Digest createDigest() {
		return new SHA256Digest();
	}

	/**
	 * Hash-Length in Bits
	 */
	public static final int HASH_LENGTH = 256;

	/**
	 * UserSalt-Length in Bytes
	 */
	public static final int SRP_SALT_LENGTH = 8;

	/**
	 * Salt for Password based derivation function in bytes
	 */
	public static final int PWD_SALT_LENGTH = 8;

	/**
	 * Length for PBKDF2 derived function in bytes to generate the SRP-Password
	 * from the real password
	 */
	private static final int PWD_KEY_LENGTH = 32;

	/**
	 * Create Hash of SRP-Secret
	 * 
	 * @param digest
	 * @param N
	 * @param S
	 * @return
	 */
	public static BigInteger createK(Digest digest, BigInteger srpS) {
		byte[] sBytes = getPadded(srpS, PADLENGTH);
		digest.update(sBytes, 0, sBytes.length);
		byte[] output = new byte[digest.getDigestSize()];
		digest.doFinal(output, 0);

		return new BigInteger(1, output);
	}

	public static BigInteger createHash(Digest digest, BigInteger... n) {

		for (int i = 0; i < n.length; i++) {
			byte[] nBytes = getPadded(n[i], PADLENGTH);
			digest.update(nBytes, 0, nBytes.length);
		}
		byte[] output = new byte[digest.getDigestSize()];
		digest.doFinal(output, 0);
		digest.reset();
		return new BigInteger(1, output);
	}

	public static BigInteger createServerM(Digest digest, BigInteger srpA,
			BigInteger clientM, BigInteger k) {
		return createHash(digest, srpA, clientM, k);
	}

	public static BigInteger createClientM(Digest digest, String username,
			BigInteger userSalt, BigInteger srpA, BigInteger srpB,
			BigInteger srpK) throws UnsupportedEncodingException {
		byte[] nBytes = getPadded(N_2024, PADLENGTH);
		digest.update(nBytes, 0, nBytes.length);
		byte[] outputN = new byte[digest.getDigestSize()];
		digest.doFinal(outputN, 0);
		digest.reset();

		byte[] gBytes = getPadded(G_2024, PADLENGTH);
		digest.update(gBytes, 0, gBytes.length);
		byte[] outputG = new byte[digest.getDigestSize()];
		digest.doFinal(outputG, 0);
		digest.reset();

		byte[] e = new byte[outputG.length];
		for (int i = 0; i < e.length; i++) {
			e[i] = (byte) (outputN[i] ^ outputG[i]);
		}
		BigInteger tmpNG = new BigInteger(1, e);

		byte[] iBytes = username.getBytes("UTF-8");
		if (iBytes.length < PADLENGTH) {
			byte[] tmp = new byte[PADLENGTH];
			System.arraycopy(iBytes, 0, tmp, PADLENGTH - iBytes.length,
					iBytes.length);
			iBytes = tmp;
		}
		digest.update(iBytes, 0, iBytes.length);
		byte[] outputI = new byte[digest.getDigestSize()];
		digest.doFinal(outputI, 0);
		digest.reset();

		BigInteger tmpI = new BigInteger(1, outputI);

		return createHash(digest, tmpNG, tmpI, userSalt, srpA, srpB, srpK);
	}

	private static byte[] getPadded(BigInteger n, int length) {
		byte[] bs = BigIntegers.asUnsignedByteArray(n);
		if (bs.length < length) {
			byte[] tmp = new byte[length];
			System.arraycopy(bs, 0, tmp, length - bs.length, bs.length);
			bs = tmp;
		}
		return bs;
	}

	/**
	 * Create a SRP6 Verification based on username and password.
	 * 
	 * @param username
	 *            null is not allowd.
	 * @param password
	 *            null is not allowd.
	 * @return usersalt and verification
	 * @throws UnsupportedEncodingException
	 *             when UTF-8 is not supported
	 */
	public static Pair<byte[], BigInteger> createUserVerification(
			String username, byte[] password)
			throws UnsupportedEncodingException {
		SRP6VerifierGenerator verifGen = new SRP6VerifierGenerator();
		verifGen.init(N_2024, G_2024, createDigest());

		byte[] userSalt = new byte[SRP_SALT_LENGTH];
		SecureRandom random = new SecureRandom();
		random.nextBytes(userSalt);

		// Remove leading 0, because Verification works with unsigned
		// BigIntegers.
		if (userSalt[0] == 0) {
			byte[] tmp = new byte[userSalt.length - 1];
			System.arraycopy(userSalt, 1, tmp, 0, tmp.length);
			userSalt = tmp;
		}

		BigInteger verif = verifGen.generateVerifier(userSalt,
				username.getBytes(SyncDataHelper.DEFAULT_CHARSET_NAME),
				password);
		return new Pair<byte[], BigInteger>(userSalt, verif);
	}

	/**
	 * Derive a key from the password to protect the real password against
	 * brute-force hacking the SRP-verifier-database.
	 * 
	 * @param password
	 * @return SRP-Password
	 */
	public static byte[] createSRPPassword(String password, byte[] salt) {
		if (password == null) {
			throw new IllegalArgumentException("Password is null");
		}

		PBEParametersGenerator generator = new PKCS5S2ParametersGenerator();
		generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(password
				.toCharArray()), salt, PBE_DEFAULT_ITERATIONS);
		KeyParameter params = (KeyParameter) generator
				.generateDerivedParameters(PWD_KEY_LENGTH * 8);

		return params.getKey();
	}
}
