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
 * Result of a User Registration from the server
 */
public enum UserRegistrationState {
	/** Registration was successfull. */
	SUCCESS("success"),
	/** Username is already in use -> create a new user */
	USERNAME_IN_USE("InUse"),
	/** Invalid Username */
	USERNAME_INVALID("InvalidName"),
	/** Provided E-Mail had a invalid syntax */
	EMAIL_INVALID("InvalidEmail"),
	/** Provided PasswordData was invalid or empty */
	INVALID_PASSWORDDATA("InvalidPasswordData"),
	/** Used E-Mail was rejected, because it's known bad hoster */
	EMAIL_REJECTED("EmailRejected"),
	/** E-Mails could not be registrated more than one time for free Accounts */
	EMAIL_ALREADY_REGISTRATED("EmailAlreadyRegistrated"),
	/** Registration is blocked right now, try it later. */
	TRY_LATER("TryLater"),	
	/** Registration was successful, but verification E-Mail could not be sent. */
	EMAILSEND_FAILED("SendFailed");

	private final String stateVal;

	private UserRegistrationState(String stateVal) {
		this.stateVal = stateVal;
	}

	public String getStateVal() {
		return stateVal;
	}

	/**
	 * 
	 * @param stateVal
	 * @return null if not found or the corresponding SyncStatus
	 */
	public static UserRegistrationState fromErrorVal(String stateVal) {
		UserRegistrationState s = null;
		for (UserRegistrationState state : UserRegistrationState.values()) {
			if (state.getStateVal().equals(stateVal)) {
				s = state;
				break;
			}
		}
		return s;
	}
}
