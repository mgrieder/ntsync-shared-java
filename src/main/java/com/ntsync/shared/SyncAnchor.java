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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Storing the Anchor of the last synchronization time for one
 * client.
 */
public class SyncAnchor {

	private Map<Byte, Long> anchors = new HashMap<Byte, Long>();

	public Long getAnchor(byte containerType) {
		return anchors.get(containerType);
	}

	/**
	 * @param containerType
	 * @return Anchor as Date or null.
	 */
	public Date getAnchorDate(byte containerType) {
		Long val = anchors.get(containerType);
		return val != null && val.longValue() > 0 ? new Date(val.longValue())
				: null;
	}

	public void setAnchor(byte containerType, long anchor) {
		anchors.put(containerType, anchor);
	}

	/**
	 * Get the Container-Types from all available SyncAnchors
	 * 
	 * @return never null
	 */
	public Set<Byte> containers() {
		return anchors.keySet();
	}

}
