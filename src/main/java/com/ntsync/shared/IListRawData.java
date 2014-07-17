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

import com.ntsync.shared.ContactConstants.ListType;

/**
 * Provides the common API for List-Properties in  Contacts/Groups. 
 *
 * @param <T> Concrete List-Type 
 */
public interface IListRawData<T extends ListType> {

	String getData();

	T getType();

	String getLabel();
	
	boolean isPrimary();

	boolean isSuperPrimary();

}