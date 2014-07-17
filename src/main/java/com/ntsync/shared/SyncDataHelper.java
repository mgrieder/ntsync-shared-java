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
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Provides helper methods for serializing of out sync-data to or from server.
 */
public final class SyncDataHelper {

	private static final int BYTE7 = 56;
	private static final int BYTE6 = 48;
	private static final int BYTE5 = 40;
	private static final int BYTE4 = 32;
	private static final int BYTE1 = 8;
	private static final int BYTE2 = 16;
	private static final int BYTE3 = 24;

	private static final int BYTE_MASK = 0xFF;

	public static final String DEFAULT_CHARSET_NAME = "UTF-8";
	public static final Charset DEFAULT_CHARSET = Charset
			.forName(DEFAULT_CHARSET_NAME);

	private SyncDataHelper() {

	}

	public static int writeShort(byte[] buffer, short value, int index) {
		int i = index;
		buffer[i++] = (byte) value;
		buffer[i++] = (byte) (value >>> BYTE1);
		return i;
	}

	public static int writeInt(byte[] buffer, int value, int index) {
		int i = index;
		buffer[i++] = (byte) value;
		buffer[i++] = (byte) (value >>> BYTE1);
		buffer[i++] = (byte) (value >>> BYTE2);
		buffer[i++] = (byte) (value >>> BYTE3);
		return i;
	}

	public static void writeInt(OutputStream buffer, int value)
			throws IOException {
		buffer.write(value & BYTE_MASK);
		buffer.write((value >>> BYTE1) & BYTE_MASK);
		buffer.write((value >>> BYTE2) & BYTE_MASK);
		buffer.write((value >>> BYTE3) & BYTE_MASK);
	}

	public static int writeLong(byte[] buffer, long value, int index) {
		int i = index;
		buffer[i++] = (byte) value;
		buffer[i++] = (byte) (value >>> BYTE1);
		buffer[i++] = (byte) (value >>> BYTE2);
		buffer[i++] = (byte) (value >>> BYTE3);
		buffer[i++] = (byte) (value >>> BYTE4);
		buffer[i++] = (byte) (value >>> BYTE5);
		buffer[i++] = (byte) (value >>> BYTE6);
		buffer[i++] = (byte) (value >>> BYTE7);
		return i;
	}

	public static long readLong(byte[] buffer, int index) {
		int i = index;
		return (buffer[i++] & BYTE_MASK << 0)
				+ ((buffer[i++] & BYTE_MASK) << BYTE1)
				+ ((buffer[i++] & BYTE_MASK) << BYTE2)
				+ ((long) (buffer[i++] & BYTE_MASK) << BYTE3)
				+ ((long) (buffer[i++] & BYTE_MASK) << BYTE4)
				+ ((long) (buffer[i++] & BYTE_MASK) << BYTE5)
				+ ((long) (buffer[i++] & BYTE_MASK) << BYTE6)
				+ ((long) (buffer[i++]) << BYTE7);
	}

	public static int readInt(byte[] buffer, int index) {
		int i = index;
		int value = buffer[i++] & BYTE_MASK;
		value = value | ((buffer[i++] & BYTE_MASK) << BYTE1);
		value = value | ((buffer[i++] & BYTE_MASK) << BYTE2);
		value = value | ((buffer[i++] & BYTE_MASK) << BYTE3);
		return value;
	}

	public static short readShort(byte[] buffer, int index) {
		int i = index;
		int value = buffer[i++] & BYTE_MASK;
		value = value | ((buffer[i++] & BYTE_MASK) << BYTE1);
		return (short) value;
	}

	/**
	 * 
	 * @param text
	 * @return true if text is has length 0
	 */
	public static boolean isEmpty(String text) {
		return text == null || text.length() == 0;
	}
}
