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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.ntsync.shared.ContactConstants.AddressType;
import com.ntsync.shared.ContactConstants.EmailType;
import com.ntsync.shared.ContactConstants.EventType;
import com.ntsync.shared.ContactConstants.ImProtocolType;
import com.ntsync.shared.ContactConstants.ImType;
import com.ntsync.shared.ContactConstants.NicknameType;
import com.ntsync.shared.ContactConstants.OrganizationType;
import com.ntsync.shared.ContactConstants.PhoneType;
import com.ntsync.shared.ContactConstants.RelationType;
import com.ntsync.shared.ContactConstants.SipAddressType;
import com.ntsync.shared.ContactConstants.WebsiteType;
import com.ntsync.shared.ListRawData.RawAddressData;
import com.ntsync.shared.ListRawData.RawImData;
import com.ntsync.shared.ListRawData.RawOrganizationData;
import com.ntsync.shared.RequestGenerator.SyncResponse;
import com.ntsync.shared.RequestGeneratorTest.KeyPair;

public class RawContactTest {

	@Test
	public void testContactSerializing() throws IOException,
			HeaderParseException, HeaderCreateException {
		byte[] photo = new byte[4000];
		Random rand = new Random();
		rand.nextBytes(photo);

		List<ListRawData<PhoneType>> phones = new ArrayList<ListRawData<PhoneType>>();
		phones.add(new ListRawData<PhoneType>("0834343",
				PhoneType.TYPE_FAX_WORK, "label3", false, false));
		phones.add(new ListRawData<PhoneType>("0355834343",
				PhoneType.TYPE_HOME, null, true, true));
		List<ListRawData<EmailType>> emails = new ArrayList<ListRawData<EmailType>>();
		emails.add(new ListRawData<EmailType>("test@example.com",
				EmailType.TYPE_MOBILE, null, true, true));
		emails.add(new ListRawData<EmailType>("tes2t@example.com",
				EmailType.TYPE_CUSTOM, "sdfsdfs", true, true));
		List<ListRawData<WebsiteType>> websites = new ArrayList<ListRawData<WebsiteType>>();
		websites.add(new ListRawData<WebsiteType>("www.gmx.ch",
				WebsiteType.TYPE_CUSTOM, "E-Mail", true, true));
		websites.add(new ListRawData<WebsiteType>("www.example.com",
				WebsiteType.TYPE_HOME, null, true, true));

		List<RawAddressData> addresses = new ArrayList<RawAddressData>();
		addresses.add(new RawAddressData(AddressType.TYPE_HOME, null, false,
				false, "homestreet", "pobox", "neighborhood", "city", "region",
				"postcode", "country"));
		addresses.add(new RawAddressData(AddressType.TYPE_CUSTOM, "Vacation",
				true, true, "vacstreet", "vacpobox", "vacneighborhood",
				"vaccity", "vacregion", "vacpostcode", "vaccountry"));

		List<ListRawData<EventType>> events = new ArrayList<ListRawData<EventType>>();
		events.add(new ListRawData<EventType>("1.12.2021",
				EventType.TYPE_OTHER, "Hochzeitstag", false, false));
		events.add(new ListRawData<EventType>("12.12.1954",
				EventType.TYPE_BIRTHDAY, null, true, true));

		List<ListRawData<RelationType>> relations = new ArrayList<ListRawData<RelationType>>();
		relations.add(new ListRawData<RelationType>("brothername",
				RelationType.TYPE_BROTHER, null, false, false));
		relations.add(new ListRawData<RelationType>("mothername",
				RelationType.TYPE_MOTHER, null, true, true));

		List<ListRawData<SipAddressType>> sipAddresses = new ArrayList<ListRawData<SipAddressType>>();
		sipAddresses.add(new ListRawData<SipAddressType>("hall@example.com",
				SipAddressType.TYPE_CUSTOM, "CustomName", true, true));
		sipAddresses.add(new ListRawData<SipAddressType>("home@example.com",
				SipAddressType.TYPE_HOME, null, true, true));

		List<ListRawData<NicknameType>> nicknames = new ArrayList<ListRawData<NicknameType>>();
		nicknames.add(new ListRawData<NicknameType>("tango",
				NicknameType.TYPE_CUSTOM, "Military nickname", true, true));
		nicknames.add(new ListRawData<NicknameType>("Shortname",
				NicknameType.TYPE_SHORT_NAME, null, true, true));

		List<RawImData> imAddresses = new ArrayList<RawImData>();
		imAddresses.add(new RawImData("imname", ImType.TYPE_HOME, null, false,
				false, ImProtocolType.PROTOCOL_MSN, null));
		imAddresses.add(new RawImData("workname", ImType.TYPE_CUSTOM, "Sky",
				true, true, ImProtocolType.PROTOCOL_CUSTOM, "customProt"));

		List<String> groupSourceIds = new ArrayList<String>();
		groupSourceIds.add("13213sdsdfsd");
		groupSourceIds.add("2df");
		String customRingTone = "uriRingtone";

		RawOrganizationData org = new RawOrganizationData("title",
				OrganizationType.TYPE_WORK, "CustomLabel", true, true, "title",
				"department", "jobTitle");

		String note = "Notiz dfdfs$ää}][]";

		boolean photoIsSuperPrimary = true;

		String c1ServerId = UUID.randomUUID().toString();
		List<Long> groupIds = new ArrayList<Long>();
		groupIds.add(1L);

		RawContact c = new RawContact("Hans Musteröäü", "Hans", "Muster",
				"Middle", "Dr.", "junior", "Hansli", "Middlei", "Musterli",
				phones, emails, websites, addresses, events, relations,
				sipAddresses, nicknames, imAddresses, note, org, photo,
				photoIsSuperPrimary, groupSourceIds, groupIds, true,
				customRingTone, true, null, false, c1ServerId, 3, false, 2);
		RawContact c2 = new RawContact("Hans Muster2", "Hans", "Muster2",
				"Mid", "Mr.", "junior", "Hansli", "Midd", "Muster", phones,
				emails, websites, addresses, events, relations, sipAddresses,
				nicknames, imAddresses, note, org, photo, !photoIsSuperPrimary,
				groupSourceIds, null, true, customRingTone, true, null, true,
				null, 4, true, 3);
		c2.setLastModified(new Date());

		KeyPair key = RequestGeneratorTest.generateKey();

		List<RawContact> contacts = new ArrayList<RawContact>();
		contacts.add(c);
		contacts.add(c2);

		byte[] requestClient1 = RequestGenerator.prepareServerRequest(
				new SyncAnchor(), contacts, null, key.key, "test", null,
				key.salt, null, null, null, false);

		// Zum Testen wird der Server-Request als client-Response verarbeitet,
		// da diese praktisch gleich sind.
		SyncResponse response = RequestGenerator.processServerResponse(key.key,
				null, requestClient1);

		assertEquals(2, response.serverContacts.size());
		RawContact newC = response.serverContacts.get(0);
		RawContact newC2 = response.serverContacts.get(1);

		compareRawContact(c, newC);
		compareRawContact(c2, newC2);

		// Werden nicht serialisiert: ServerId, Version, GroupId
		Assert.assertNull(newC.getServerContactId());
		Assert.assertNull(newC.getGroupIds());
		Assert.assertEquals(-1, newC.getVersion());
	}

	private void compareRawContact(RawContact c, RawContact newC) {
		assertEquals(c.getFirstName(), newC.getFirstName());
		assertEquals(c.getLastName(), newC.getLastName());
		assertEquals(c.getMiddleName(), newC.getMiddleName());
		assertEquals(c.getPrefixName(), newC.getPrefixName());
		assertEquals(c.getSuffixName(), newC.getSuffixName());
		assertEquals(c.getFullName(), newC.getFullName());
		assertEquals(c.getPhoneticFamilyName(), newC.getPhoneticFamilyName());
		assertEquals(c.getPhoneticGivenName(), newC.getPhoneticGivenName());
		assertEquals(c.getPhoneticMiddleName(), newC.getPhoneticMiddleName());
		assertEquals(c.getPhone(), newC.getPhone());
		assertEquals(c.getEmail(), newC.getEmail());
		assertEquals(c.getEmail(), newC.getEmail());
		assertEquals(c.getWebsite(), newC.getWebsite());
		assertEquals(c.getEvents(), newC.getEvents());
		assertEquals(c.getSipAddresses(), newC.getSipAddresses());
		assertEquals(c.getRelations(), newC.getRelations());
		assertEquals(c.getNicknames(), newC.getNicknames());
		assertEquals(c.getAddress(), newC.getAddress());
		assertEquals(c.getNote(), newC.getNote());
		assertEquals(c.getOrganization(), newC.getOrganization());
		assertEquals(c.getImAddresses(), newC.getImAddresses());
		assertEquals(c.getGroupSourceIds(), newC.getGroupSourceIds());
		Assert.assertArrayEquals(c.getPhoto(), newC.getPhoto());
		assertEquals(c.getRawContactId(), newC.getRawContactId());
		assertEquals(c.getLastModified(), newC.getLastModified());
		assertEquals(c.isPhotoSuperPrimary(), newC.isPhotoSuperPrimary());
		assertEquals(c.isDeleted(), newC.isDeleted());
	}
}
