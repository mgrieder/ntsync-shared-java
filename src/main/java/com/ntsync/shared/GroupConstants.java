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

/**
 * Provide Constants for serializing group-data to or from server 
 */
public final class GroupConstants {

	public static final byte ROWID = ContactConstants.ROWID;

	public static final byte TEXTDATA = 't';

	public static final byte HASH = 'H';

	public static final byte MODIFIED = 'm';

	public static final byte SERVERROW_ID = ContactConstants.SERVERROW_ID;

	/**
	 * 1: deleted. not encrypted
	 */
	public static final byte DELETED = ContactConstants.DELETED;

	// Text-Objects

	public static final String TITLE = "t";

	public static final String NOTES = "n";

	private GroupConstants() {
	}
}
