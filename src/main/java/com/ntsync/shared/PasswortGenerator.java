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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a password based on <a
 * href="http://en.wikipedia.org/wiki/Diceware">Diceware</a>
 */
public final class PasswortGenerator {

	private static final int DICE_MAXNUMBER = 5;
	private static final int DICE_COUNT = 5;

	private static final Logger LOG = LoggerFactory
			.getLogger(PasswortGenerator.class);

	private PasswortGenerator() {
	}

	/**
	 * List of of all possible Passwords.
	 * 
	 * @return never null.
	 * @throws IOException
	 */
	public static String[] getPasswords() {
		try {
			Properties props = getDiceList();
			return props.values().toArray(new String[props.size()]);
		} catch (IOException ex) {
			LOG.warn("Loading Passwords failed.", ex);
			return new String[] {};
		}
	}

	private static Properties getDiceList() throws IOException {
		Properties props = new Properties();
		String filename = "/dicelist.properties";
		InputStream is = PasswortGenerator.class.getResourceAsStream(filename);
		if (is == null) {
			throw new FileNotFoundException("Could not find " + filename);
		}
		try {
			props.load(is);
		} finally {
			is.close();
		}
		return props;
	}

	/**
	 * 
	 * @param wordCount
	 * @return
	 * @throws IOException
	 *             if generating a password failed.
	 */
	public static String createPwd(int wordCount) throws IOException {
		// Read dicelist
		Properties props = getDiceList();

		// Generate Words
		SecureRandom rand;
		try {
			rand = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException ex) {
			rand = new SecureRandom();
			LOG.warn(
					"SHA1PRNG not found. Using default algorithm: "
							+ rand.getAlgorithm(), ex);
		}

		StringBuilder pwd = new StringBuilder();

		StringBuilder wordKey = new StringBuilder();
		for (int i = 0; i < wordCount; i++) {
			String wordVal = null;
			int retry = -1;
			do {
				if (retry > 10) {
					throw new IOException(
							"Could not found Words. Dicelist-Size:"
									+ props.size());
				}
				for (int j = 0; j < DICE_COUNT; j++) {
					wordKey.append(rand.nextInt(DICE_MAXNUMBER) + 1);
				}
				wordVal = props.getProperty(wordKey.toString());
				wordKey.delete(0, wordKey.length());
				retry++;
			} while (wordVal == null || wordVal.length() == 0);

			pwd.append(wordVal);
			if (i != wordCount - 1) {
				pwd.append(' ');
			}

		}

		return pwd.toString();
	}
}
