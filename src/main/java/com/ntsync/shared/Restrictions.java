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

/**
 * Restrictions which are different for free/paid versions.
 */
public class Restrictions {

	private final int maxContactCount;
	private final int maxGroupCount;

	private final boolean photoSyncSupported;

	private final Date validUntil;

	/**
	 * 
	 * @param maxContactCount
	 * @param maxGroupCount
	 * @param photoSyncSupported
	 * @param validUntil
	 *            null if no subscription is available, otherwise the last date
	 *            of the current subscription interval.
	 */
	public Restrictions(int maxContactCount, int maxGroupCount,
			boolean photoSyncSupported, Date validUntil) {
		super();
		this.maxContactCount = maxContactCount;
		this.maxGroupCount = maxGroupCount;
		this.photoSyncSupported = photoSyncSupported;
		this.validUntil = validUntil;
	}

	public int getMaxContactCount() {
		return maxContactCount;
	}

	public int getMaxGroupCount() {
		return maxGroupCount;
	}

	public boolean isPhotoSyncSupported() {
		return photoSyncSupported;
	}

	/**
	 * @return null if there is no end date
	 */
	public Date getValidUntil() {
		return validUntil;
	}

	@Override
	public int hashCode() {
		return maxContactCount;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Restrictions other = (Restrictions) obj;
		if (maxContactCount != other.maxContactCount) {
			return false;
		}
		if (maxGroupCount != other.maxGroupCount) {
			return false;
		}
		if (photoSyncSupported != other.photoSyncSupported) {
			return false;
		}
		if (validUntil == null && other.validUntil != null) {
			return false;
		}
		if (other.validUntil == null && validUntil != null) {
			return false;
		}
		if (validUntil != null && !validUntil.equals(other.validUntil)) {
			return false;
		}

		return true;
	}
}
