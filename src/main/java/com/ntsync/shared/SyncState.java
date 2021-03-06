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
 * Sync-State returned from server. 
 */
public enum SyncState {
	INVALID_KEY("InvalidKey"), FORCE_FULLSYNC("ForceFullsync");

	private final String errorVal;

	private SyncState(String errorVal) {
		this.errorVal = errorVal;
	}

	public String getErrorVal() {
		return errorVal;
	}

	/**
	 * 
	 * @param errorVal
	 * @return null if not found or the corresponding SyncStatus
	 */
	public static SyncState fromErrorVal(String errorVal) {
		SyncState s = null;
		for (SyncState state : SyncState.values()) {
			if (state.getErrorVal().equals(errorVal)) {
				s = state;
				break;
			}
		}
		return s;
	}
}
