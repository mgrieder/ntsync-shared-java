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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public class PasswortGeneratorTest {

	@Test
	public void testCreatePwd() throws IOException {
		String pwd = PasswortGenerator.createPwd(5);
		assertNotNull(pwd);
		assertFalse("Password should not contains 'null':" + pwd,
				pwd.contains("null"));
		assertEquals(5, pwd.split(" ").length);
	}

	@Test
	public void getPasswd() {
		String[] pwds = PasswortGenerator.getPasswords();
		Assert.assertTrue(pwds.length > 0);
	}

	public static void main(String[] args) throws IOException {
		// Converts Dice-list to property-list
		BufferedReader reader = new BufferedReader(new FileReader(
				"C:\\Users\\<username>\\Downloads\\diceware_german.txt"));
		String line = null;
		Properties props = new Properties();
		while ((line = reader.readLine()) != null) {
			line = line.replace(" ", "").replace("\t", "");
			if (line.length() > 0) {
				props.setProperty(line.substring(0, 5), line.substring(5));
			}
		}
		props.store(new FileOutputStream(
				"C:\\Users\\<username>\\Downloads\\dicelist.txt"), null);
		reader.close();
	}
}
