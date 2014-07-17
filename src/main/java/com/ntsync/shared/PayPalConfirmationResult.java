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
 * The Result of our Verification for a PayPal-Payment.
 * 
 */
public enum PayPalConfirmationResult {
	SUCCESS("Success"),
	/** Payment-Id could not be found */
	UNKNOWN_PAYMENT("UnknownPayment"),
	/** Syntax or context of ConfirmationData is invalid. */
	INVALID_SYNTAX("InvalidSyntax"),
	/** Payment is not approved. User can check in PayPal what is wrong. */
	NOT_APPROVED("NotApproved"),
	/** Payment was fully canceled. No Money was transferred. */
	CANCELED("Canceled"),

	/** Payment is not completed. Maybe a refund happen. */
	SALE_NOT_COMPLETED("SaleNotCompleted"),

	/**
	 * Used Price is not valid (for hacks which try to use another price).
	 * Transaction will be refunded or can be cancelled from the user.
	 */
	INVALID_PRICE("InvalidPrice"),

	/**
	 * This Payment was already processed and could not be used again.
	 */
	ALREADY_PROCESSED("AlreadyProcessed"),
	
	/** Connection I/O Error. Verification should be done later again. */
	NETWORK_ERROR("NetworkError"),

	/** Authentification failed, try later */
	AUTHENTICATION_FAILED("AUTHENTICATION_FAILED"),
	
	/** Internal Error. Retry later */
	VERIFIER_ERROR("VerifierError");

	private final String errorVal;

	private PayPalConfirmationResult(String errorVal) {
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
	public static PayPalConfirmationResult fromErrorVal(String errorVal) {
		PayPalConfirmationResult s = null;
		for (PayPalConfirmationResult state : PayPalConfirmationResult.values()) {
			if (state.getErrorVal().equals(errorVal)) {
				s = state;
				break;
			}
		}
		return s;
	}
}
