package com.ntsync.shared;

/*
 * Copyright (C) 2014 Markus Grieder
 * 
 * This file is based on RawContact.java from the SampleSyncAdapter-Example in Android SDK
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

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.modes.AEADBlockCipher;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.ntsync.shared.ContactConstants.AddressType;
import com.ntsync.shared.ContactConstants.EmailType;
import com.ntsync.shared.ContactConstants.EventType;
import com.ntsync.shared.ContactConstants.ImProtocolType;
import com.ntsync.shared.ContactConstants.ImType;
import com.ntsync.shared.ContactConstants.ListType;
import com.ntsync.shared.ContactConstants.NicknameType;
import com.ntsync.shared.ContactConstants.OrganizationType;
import com.ntsync.shared.ContactConstants.PhoneType;
import com.ntsync.shared.ContactConstants.RelationType;
import com.ntsync.shared.ContactConstants.SipAddressType;
import com.ntsync.shared.ContactConstants.WebsiteType;
import com.ntsync.shared.ListRawData.RawAddressData;
import com.ntsync.shared.ListRawData.RawImData;
import com.ntsync.shared.ListRawData.RawOrganizationData;

/**
 * Represents a low-level contacts RawContact - or at least the fields of the
 * RawContact that we care about.
 */
public final class RawContact {

	private static final String ERROR_CONVERT_TOJSON = "Error converting RawContact to ByteStream: ";

	private static final String JSON_FIELDNOTRECOGNIZED = "Unrecognized List field for row with Id:";

	private static final int DEFAULT_BYTEARRAY_SIZE = 1000;

	private static final Logger LOG = LoggerFactory.getLogger(RawContact.class);

	private final String displayName;

	private final String firstName;

	private final String lastName;

	private final String middleName;

	private final String suffixName;

	private final String prefixName;

	private final String phoneticGivenName;

	private final String phoneticMiddleName;

	private final String phoneticFamilyName;

	private final List<ListRawData<PhoneType>> phones;

	private final List<ListRawData<EmailType>> emails;

	private final List<ListRawData<WebsiteType>> websites;

	private final List<RawAddressData> addresses;

	private final List<ListRawData<EventType>> events;

	private final List<ListRawData<RelationType>> relations;

	private final List<ListRawData<SipAddressType>> sipAddresses;

	private final List<ListRawData<NicknameType>> nicknames;

	private final List<RawImData> imAddresses;

	private final RawOrganizationData organization;

	private final String note;

	private final byte[] photo;

	private final boolean photoSuperPrimary;

	private final boolean mDeleted;

	private final boolean mDirty;

	private final String mServerContactId;

	private final long mRawContactId;

	private final List<String> groupSourceIds;

	private final List<Long> groupIds;

	private Date lastModified;

	private final boolean starred;

	private final String droidCustomRingtone;

	private final boolean sendToVoiceMail;

	private final long version;

	/**
	 * 
	 * @param fullName
	 * @param firstName
	 * @param lastName
	 * @param middleName
	 * @param prefixName
	 * @param suffixName
	 * @param phoneticGivenName
	 * @param phoneticMiddleName
	 * @param phoneticFamilyName
	 * @param phones
	 * @param emails
	 * @param websites
	 * @param addresses
	 * @param events
	 * @param relations
	 * @param sipAddresses
	 * @param nicknames
	 * @param imAddresses
	 * @param note
	 * @param organization
	 * @param photo
	 * @param photoIsSuperPrimary
	 * @param groupSourceIds
	 * @param groupIds
	 *            When groupSourceIds are not available (for import)
	 * @param starred
	 * @param droidCustomRingtone
	 * @param sendToVoiceMail
	 * @param lastModified
	 * @param deleted
	 * @param serverContactId
	 * @param rawContactId
	 * @param dirty
	 * @param version
	 */
	public RawContact(String fullName, String firstName, String lastName,
			String middleName, String prefixName, String suffixName,
			String phoneticGivenName, String phoneticMiddleName,
			String phoneticFamilyName, List<ListRawData<PhoneType>> phones,
			List<ListRawData<EmailType>> emails,
			List<ListRawData<WebsiteType>> websites,
			List<RawAddressData> addresses,
			List<ListRawData<EventType>> events,
			List<ListRawData<RelationType>> relations,
			List<ListRawData<SipAddressType>> sipAddresses,
			List<ListRawData<NicknameType>> nicknames,
			List<RawImData> imAddresses, String note,
			RawOrganizationData organization, byte[] photo,
			boolean photoIsSuperPrimary, List<String> groupSourceIds,
			List<Long> groupIds, boolean starred, String droidCustomRingtone,
			boolean sendToVoiceMail, Date lastModified, boolean deleted,
			String serverContactId, long rawContactId, boolean dirty,
			long version) {
		displayName = fullName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
		this.prefixName = prefixName;
		this.suffixName = suffixName;
		this.phoneticGivenName = phoneticGivenName;
		this.phoneticMiddleName = phoneticMiddleName;
		this.phoneticFamilyName = phoneticFamilyName;
		this.phones = phones;
		this.emails = emails;
		this.websites = websites;
		this.addresses = addresses;
		this.events = events;
		this.relations = relations;
		this.sipAddresses = sipAddresses;
		this.nicknames = nicknames;
		this.imAddresses = imAddresses;
		this.note = note;
		this.organization = organization;
		this.photoSuperPrimary = photoIsSuperPrimary;
		this.photo = photo;
		this.groupSourceIds = groupSourceIds;
		this.groupIds = groupIds;
		this.starred = starred;
		this.droidCustomRingtone = droidCustomRingtone;
		this.sendToVoiceMail = sendToVoiceMail;
		mDeleted = deleted;
		mServerContactId = serverContactId;
		mRawContactId = rawContactId;
		this.lastModified = lastModified;
		mDirty = dirty;
		this.version = version;
	}

	public String getServerContactId() {
		return mServerContactId;
	}

	public long getRawContactId() {
		return mRawContactId;
	}

	public String getFirstName() {
		return firstName;
	}

	public long getVersion() {
		return version;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFullName() {
		return displayName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getSuffixName() {
		return suffixName;
	}

	public String getPrefixName() {
		return prefixName;
	}

	public String getPhoneticGivenName() {
		return phoneticGivenName;
	}

	public String getPhoneticMiddleName() {
		return phoneticMiddleName;
	}

	public String getPhoneticFamilyName() {
		return phoneticFamilyName;
	}

	public List<ListRawData<PhoneType>> getPhone() {
		return phones;
	}

	public List<ListRawData<EmailType>> getEmail() {
		return emails;
	}

	public List<ListRawData<WebsiteType>> getWebsite() {
		return websites;
	}

	public List<ListRawData<EventType>> getEvents() {
		return events;
	}

	public List<ListRawData<RelationType>> getRelations() {
		return relations;
	}

	public List<ListRawData<SipAddressType>> getSipAddresses() {
		return sipAddresses;
	}

	public List<ListRawData<NicknameType>> getNicknames() {
		return nicknames;
	}

	public List<RawImData> getImAddresses() {
		return imAddresses;
	}

	public List<RawAddressData> getAddress() {
		return addresses;
	}

	public String getNote() {
		return note;
	}

	public RawOrganizationData getOrganization() {
		return organization;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public boolean isPhotoSuperPrimary() {
		return photoSuperPrimary;
	}

	public List<String> getGroupSourceIds() {
		return groupSourceIds;
	}

	public List<Long> getGroupIds() {
		return groupIds;
	}

	public boolean isDeleted() {
		return mDeleted;
	}

	public boolean isDirty() {
		return mDirty;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public boolean isStarred() {
		return starred;
	}

	public String getDroidCustomRingtone() {
		return droidCustomRingtone;
	}

	public boolean isSendToVoiceMail() {
		return sendToVoiceMail;
	}

	public String getBestName() {
		if (!SyncDataHelper.isEmpty(displayName)) {
			return displayName;
		} else if (SyncDataHelper.isEmpty(firstName)) {
			return lastName;
		} else {
			return firstName;
		}
	}

	/**
	 * Convert the RawContact object into a DTO. From the JSONString interface.
	 * 
	 * @return a JSON string representation of the object
	 */
	public byte[] toDTO(Key secret, String pwdSaltBase64) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream(
					DEFAULT_BYTEARRAY_SIZE);
			AEADBlockCipher ecipher = CryptoHelper.getCipher();

			byte[] iv = new byte[CryptoHelper.IV_LEN];
			SecureRandom random = new SecureRandom();

			StringBuilder hashValue = new StringBuilder();
			hashValue.append(pwdSaltBase64);
			hashValue.append(displayName);
			hashValue.append(lastName);
			hashValue.append(firstName);
			hashValue.append(middleName);

			out.write(ContactConstants.ROWID);
			byte[] rowId = String.valueOf(mRawContactId).getBytes(
					SyncDataHelper.DEFAULT_CHARSET_NAME);

			SyncDataHelper.writeInt(out, rowId.length);
			out.write(rowId);

			JsonFactory json = new JsonFactory();
			StringWriter writer = new StringWriter();
			JsonGenerator g = json.createGenerator(writer);
			g.writeStartObject();

			writeStructuredName(g);

			writeList(hashValue, g, ContactConstants.PHONE, phones, true);
			writeList(hashValue, g, ContactConstants.EMAIL, emails, true);
			writeList(hashValue, g, ContactConstants.EVENT, events, false);
			writeList(hashValue, g, ContactConstants.RELATION, relations, false);
			writeList(hashValue, g, ContactConstants.SIPADDRESS, sipAddresses,
					false);
			writeList(hashValue, g, ContactConstants.NICKNAME, nicknames, false);
			writeList(hashValue, g, ContactConstants.WEBSITE, websites, false);
			writeAddress(hashValue, g, addresses);
			writeImList(g, imAddresses);
			writeOrganization(g, organization);

			writeField(g, ContactConstants.NOTE, note);
			if (starred) {
				g.writeBooleanField(ContactConstants.STARRED, true);
			}
			if (sendToVoiceMail) {
				g.writeBooleanField(ContactConstants.SEND_TO_VOICE_MAIL, true);
			}
			writeField(g, ContactConstants.DROID_CUSTOM_RINGTONE,
					droidCustomRingtone);

			if (photoSuperPrimary) {
				g.writeBooleanField(ContactConstants.PHOTO_SUPERPRIMARY, true);
			}

			writeStringList(g, ContactConstants.GROUPMEMBERSHIP, groupSourceIds);

			g.writeEndObject();
			g.close();

			String textData = writer.toString();

			CryptoHelper.writeValue(secret, out, ecipher, iv, random,
					ContactConstants.TEXTDATA, textData);
			CryptoHelper.writeValue(secret, out, ecipher, iv, random,
					ContactConstants.PHOTO, photo);

			if (lastModified != null) {
				writeRawValue(
						out,
						ContactConstants.MODIFIED,
						String.valueOf(lastModified.getTime()).getBytes(
								SyncDataHelper.DEFAULT_CHARSET_NAME));
			}

			if (mDeleted) {
				writeRawValue(out, ContactConstants.DELETED,
						"1".getBytes(SyncDataHelper.DEFAULT_CHARSET_NAME));
			}
			writeRawValue(out, ContactConstants.HASH, createHash(hashValue));

			return out.toByteArray();
		} catch (final IOException ex) {
			LOG.error(ERROR_CONVERT_TOJSON + ex.toString(), ex);
		} catch (GeneralSecurityException ex) {
			LOG.error(ERROR_CONVERT_TOJSON + ex.toString(), ex);
		} catch (InvalidCipherTextException ex) {
			LOG.error(ERROR_CONVERT_TOJSON + ex.toString(), ex);
		}
		return null;
	}

	private void writeStructuredName(JsonGenerator g) throws IOException {
		g.writeObjectFieldStart(ContactConstants.STRUCTUREDNAME);

		writeField(g, ContactConstants.DISPLAY_NAME, displayName);
		writeField(g, ContactConstants.FAMILY_NAME, lastName);
		writeField(g, ContactConstants.GIVEN_NAME, firstName);
		writeField(g, ContactConstants.MIDDLE_NAME, middleName);
		writeField(g, ContactConstants.PREFIX_NAME, prefixName);
		writeField(g, ContactConstants.SUFFIX_NAME, suffixName);
		writeField(g, ContactConstants.PHONETIC_FAMILY, phoneticFamilyName);
		writeField(g, ContactConstants.PHONETIC_GIVEN, phoneticGivenName);
		writeField(g, ContactConstants.PHONETIC_MIDDLE, phoneticMiddleName);
		g.writeEndObject();
	}

	private byte[] createHash(StringBuilder hashValue)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");

		md.update(hashValue.toString().getBytes(
				SyncDataHelper.DEFAULT_CHARSET_NAME));
		byte[] hash = md.digest();
		return hash;
	}

	private static void writeField(JsonGenerator g, String fielName,
			String field) throws IOException {
		if (field != null && field.length() > 0) {
			g.writeStringField(fielName, field);
		}
	}

	private static void writeOrganization(JsonGenerator g,
			RawOrganizationData org) throws IOException {
		if (org != null) {
			g.writeObjectFieldStart(ContactConstants.ORGANIZATION);
			writeField(g, ContactConstants.DATA, org.getData());
			g.writeStringField(ContactConstants.TYPE,
					String.valueOf(org.getType().getVal()));
			writeField(g, ContactConstants.LABEL, org.getLabel());
			writeField(g, ContactConstants.ORGANIZATION_TITLE, org.getTitle());

			writeField(g, ContactConstants.ORGANIZATION_DEPARTMENT,
					org.getDepartment());
			writeField(g, ContactConstants.ORGANIZATION_JOB,
					org.getJobDescription());
			if (org.isSuperPrimary()) {
				g.writeBooleanField(ContactConstants.SUPERPRIMARY, true);
			}
			if (org.isPrimary()) {
				g.writeBooleanField(ContactConstants.PRIMARY, true);
			}
			g.writeEndObject();
		}
	}

	private static void writeImList(JsonGenerator g, List<RawImData> list)
			throws IOException {
		if (list != null) {
			g.writeArrayFieldStart(ContactConstants.IM);
			for (RawImData listItem : list) {
				g.writeStartObject();
				writeField(g, ContactConstants.DATA, listItem.getData());
				g.writeStringField(ContactConstants.TYPE,
						String.valueOf(listItem.getType().getVal()));
				writeField(g, ContactConstants.LABEL, listItem.getLabel());
				g.writeStringField(ContactConstants.PROTOCOL_TYPE,
						String.valueOf(listItem.getProtType().getVal()));
				writeField(g, ContactConstants.PROTOCOL_CUSTOM_PROT,
						listItem.getCustomProtocolName());
				if (listItem.isSuperPrimary()) {
					g.writeBooleanField(ContactConstants.SUPERPRIMARY, true);
				}
				if (listItem.isPrimary()) {
					g.writeBooleanField(ContactConstants.PRIMARY, true);
				}
				g.writeEndObject();
			}
			g.writeEndArray();
		}
	}

	private static void writeList(StringBuilder hashValue, JsonGenerator g,
			String key, List<? extends IListRawData<?>> list, boolean hashRel)
			throws IOException {
		if (list != null) {
			g.writeArrayFieldStart(key);
			for (IListRawData<?> listItem : list) {
				g.writeStartObject();
				writeField(g, ContactConstants.DATA, listItem.getData());
				g.writeStringField(ContactConstants.TYPE,
						String.valueOf(listItem.getType().getVal()));
				writeField(g, ContactConstants.LABEL, listItem.getLabel());
				if (listItem.isSuperPrimary()) {
					g.writeBooleanField(ContactConstants.SUPERPRIMARY, true);
				}
				if (listItem.isPrimary()) {
					g.writeBooleanField(ContactConstants.PRIMARY, true);
				}
				g.writeEndObject();

				if (hashRel) {
					hashValue.append(listItem.getData());
					hashValue.append(':');
					hashValue.append(listItem.getType().getVal());
				}
			}
			g.writeEndArray();
		}
	}

	private static void writeStringList(JsonGenerator g, String key,
			List<String> list) throws IOException {
		if (list != null) {
			g.writeArrayFieldStart(key);
			for (String listItem : list) {
				if (listItem != null && listItem.length() > 0) {
					g.writeString(listItem);
				}
			}
			g.writeEndArray();
		}
	}

	private static void writeAddress(StringBuilder hashValue, JsonGenerator g,
			List<RawAddressData> list) throws IOException {
		if (list != null) {
			g.writeArrayFieldStart(ContactConstants.STRUCTUREDPOSTAL);
			for (RawAddressData listItem : list) {
				g.writeStartObject();
				g.writeStringField(ContactConstants.TYPE,
						String.valueOf(listItem.getType().getVal()));
				writeField(g, ContactConstants.LABEL, listItem.getLabel());

				writeField(g, ContactConstants.STREET, listItem.getStreet());
				writeField(g, ContactConstants.POBOX, listItem.getPobox());
				writeField(g, ContactConstants.POSTCODE, listItem.getPostcode());
				writeField(g, ContactConstants.COUNTRY, listItem.getCountry());
				writeField(g, ContactConstants.CITY, listItem.getCity());
				writeField(g, ContactConstants.REGION, listItem.getRegion());
				writeField(g, ContactConstants.NEIGHBORHOOD,
						listItem.getNeighborhood());

				if (listItem.isSuperPrimary()) {
					g.writeBooleanField(ContactConstants.SUPERPRIMARY, true);
				}
				if (listItem.isPrimary()) {
					g.writeBooleanField(ContactConstants.PRIMARY, true);
				}
				g.writeEndObject();

				hashValue.append(listItem.getStreet());
				hashValue.append(':');
				hashValue.append(listItem.getType().getVal());
			}
			g.writeEndArray();
		}
	}

	private void writeRawValue(ByteArrayOutputStream out, byte key, byte[] value)
			throws InvalidKeyException, IOException {
		if (value != null && value.length > 0) {
			// 1byte key, //4 len //value
			out.write(key);

			SyncDataHelper.writeInt(out, value.length);
			out.write(value);
		}
	}

	/**
	 * Creates and returns an instance of the RawContact from encrypted data
	 * 
	 * */
	public static RawContact valueOf(String rowId,
			Map<Byte, ByteBuffer> values, Key privateKey)
			throws InvalidKeyException {
		try {
			String serverContactId = null;
			long rawContactId = -1;
			if (values.containsKey(ContactConstants.SERVERROW_ID)) {
				serverContactId = readRawString(values
						.get(ContactConstants.SERVERROW_ID));
			}
			String lastModStr = readRawString(values
					.get(ContactConstants.MODIFIED));
			Date lastModified = null;
			if (lastModStr != null) {
				lastModified = new Date(Long.parseLong(lastModStr));
			}

			if (serverContactId == null || !serverContactId.equals(rowId)) {
				// If ServerContactId is different, then rowId is the clientId
				rawContactId = Long.parseLong(rowId);
			}

			if (serverContactId == null && rawContactId < 0) {
				throw new IllegalArgumentException("Missing RowId in data");
			}

			AEADBlockCipher cipher = CryptoHelper.getCipher();
			final boolean deleted = values
					.containsKey(ContactConstants.DELETED);

			final String textData = CryptoHelper.decodeStringValue(
					ContactConstants.TEXTDATA, values, cipher, privateKey);

			if (textData == null && !deleted) {
				LOG.error("No textdata found for row with Id:" + rowId);
				return null;
			}

			String fullName = null;
			String firstName = null;
			String lastName = null;
			String middleName = null;
			String prefixName = null;
			String suffixName = null;
			String phonecticFirst = null;
			String phonecticMiddle = null;
			String phonecticLast = null;
			List<String> groupSourceIds = null;
			String note = null;
			List<ListRawData<PhoneType>> phones = null;
			List<ListRawData<EmailType>> emails = null;
			List<ListRawData<WebsiteType>> websites = null;
			List<ListRawData<EventType>> events = null;
			List<ListRawData<RelationType>> relations = null;
			List<ListRawData<SipAddressType>> sipaddresses = null;
			List<ListRawData<NicknameType>> nicknames = null;
			List<RawAddressData> addresses = null;
			List<RawImData> imAddresses = null;
			RawOrganizationData organization = null;
			boolean photoSuperPrimary = false;
			boolean starred = false;
			String customRingtone = null;
			boolean sendToVoiceMail = false;

			if (!SyncDataHelper.isEmpty(textData)) {
				JsonFactory fac = new JsonFactory();
				JsonParser jp = fac.createParser(textData);
				jp.nextToken();
				while (jp.nextToken() != JsonToken.END_OBJECT) {
					String fieldname = jp.getCurrentName();
					// move to value, or START_OBJECT/START_ARRAY
					jp.nextToken();
					if (ContactConstants.STRUCTUREDNAME.equals(fieldname)) {
						while (jp.nextToken() != JsonToken.END_OBJECT) {
							String namefield = jp.getCurrentName();
							// move to value
							if (jp.nextToken() == null) {
								throw new JsonParseException(
										"Invalid JSON-Structure. End of Object missing.",
										jp.getCurrentLocation());
							}
							if (ContactConstants.DISPLAY_NAME.equals(namefield)) {
								fullName = jp.getValueAsString();
							} else if (ContactConstants.FAMILY_NAME
									.equals(namefield)) {
								lastName = jp.getValueAsString();
							} else if (ContactConstants.GIVEN_NAME
									.equals(namefield)) {
								firstName = jp.getValueAsString();
							} else if (ContactConstants.MIDDLE_NAME
									.equals(namefield)) {
								middleName = jp.getValueAsString();
							} else if (ContactConstants.SUFFIX_NAME
									.equals(namefield)) {
								suffixName = jp.getValueAsString();
							} else if (ContactConstants.PREFIX_NAME
									.equals(namefield)) {
								prefixName = jp.getValueAsString();
							} else if (ContactConstants.PHONETIC_FAMILY
									.equals(namefield)) {
								phonecticLast = jp.getValueAsString();
							} else if (ContactConstants.PHONETIC_GIVEN
									.equals(namefield)) {
								phonecticFirst = jp.getValueAsString();
							} else if (ContactConstants.PHONETIC_MIDDLE
									.equals(namefield)) {
								phonecticMiddle = jp.getValueAsString();
							} else {
								LOG.error("Unrecognized structurednamefield for row with Id:"
										+ rowId + " Fieldname:" + fieldname);
								break;
							}
						}
					} else if (ContactConstants.STRUCTUREDPOSTAL
							.equals(fieldname)) {
						addresses = readAddressList(rowId, addresses, jp);
					} else if (ContactConstants.PHONE.equals(fieldname)) {
						phones = readJsonList(rowId, phones, jp, fieldname,
								PhoneType.TYPE_OTHER, PhoneType.class);
					} else if (ContactConstants.EMAIL.equals(fieldname)) {
						emails = readJsonList(rowId, emails, jp, fieldname,
								EmailType.TYPE_OTHER, EmailType.class);
					} else if (ContactConstants.WEBSITE.equals(fieldname)) {
						websites = readJsonList(rowId, websites, jp, fieldname,
								WebsiteType.TYPE_OTHER, WebsiteType.class);
					} else if (ContactConstants.EVENT.equals(fieldname)) {
						events = readJsonList(rowId, events, jp, fieldname,
								EventType.TYPE_OTHER, EventType.class);
					} else if (ContactConstants.RELATION.equals(fieldname)) {
						relations = readJsonList(rowId, relations, jp,
								fieldname, RelationType.TYPE_CUSTOM,
								RelationType.class);
					} else if (ContactConstants.SIPADDRESS.equals(fieldname)) {
						sipaddresses = readJsonList(rowId, sipaddresses, jp,
								fieldname, SipAddressType.TYPE_OTHER,
								SipAddressType.class);
					} else if (ContactConstants.NICKNAME.equals(fieldname)) {
						nicknames = readJsonList(rowId, nicknames, jp,
								fieldname, NicknameType.TYPE_DEFAULT,
								NicknameType.class);
					} else if (ContactConstants.IM.equals(fieldname)) {
						imAddresses = readImList(rowId, imAddresses, jp);
					} else if (ContactConstants.NOTE.equals(fieldname)) {
						note = jp.getValueAsString();
					} else if (ContactConstants.GROUPMEMBERSHIP
							.equals(fieldname)) {
						while (jp.nextToken() != JsonToken.END_ARRAY) {
							String groupSourceId = jp.getValueAsString();
							if (groupSourceIds == null) {
								groupSourceIds = new ArrayList<String>();
							}
							groupSourceIds.add(groupSourceId);
						}
					} else if (ContactConstants.ORGANIZATION.equals(fieldname)) {
						organization = readOrg(rowId, jp);
					} else if (ContactConstants.PHOTO_SUPERPRIMARY
							.equals(fieldname)) {
						photoSuperPrimary = jp.getValueAsBoolean();
					} else if (ContactConstants.STARRED.equals(fieldname)) {
						starred = jp.getValueAsBoolean();
					} else if (ContactConstants.SEND_TO_VOICE_MAIL
							.equals(fieldname)) {
						sendToVoiceMail = jp.getValueAsBoolean();
					} else if (ContactConstants.DROID_CUSTOM_RINGTONE
							.equals(fieldname)) {
						customRingtone = jp.getValueAsString();
					} else {
						LOG.error("Unrecognized field for row with Id:" + rowId
								+ " Fieldname:" + fieldname);
					}
				}
				jp.close();
			}

			final byte[] photo = CryptoHelper.decodeValue(
					ContactConstants.PHOTO, values, cipher, privateKey);

			return new RawContact(fullName, firstName, lastName, middleName,
					prefixName, suffixName, phonecticFirst, phonecticMiddle,
					phonecticLast, phones, emails, websites, addresses, events,
					relations, sipaddresses, nicknames, imAddresses, note,
					organization, photo, photoSuperPrimary, groupSourceIds,
					null, starred, customRingtone, sendToVoiceMail,
					lastModified, deleted, serverContactId, rawContactId,
					false, -1);
		} catch (InvalidCipherTextException ex) {
			throw new InvalidKeyException("Invalid key detected.", ex);
		} catch (final IOException ex) {
			LOG.info("Error parsing contact data. Reason:" + ex.toString(), ex);
		} catch (IllegalArgumentException ex) {
			LOG.warn("Error parsing contact data. Reason:" + ex.toString(), ex);
		}

		return null;
	}

	private static String readRawString(ByteBuffer src)
			throws UnsupportedEncodingException {
		if (src != null) {
			int len = src.remaining();
			if (len >= 0) {
				byte[] output = new byte[len];
				src.get(output, 0, len);
				// Android has UTF-8 as default
				return new String(output, SyncDataHelper.DEFAULT_CHARSET_NAME);
			}
		}
		return null;
	}

	private static RawOrganizationData readOrg(String rowId, JsonParser jp)
			throws IOException {
		String orgname = null;
		OrganizationType orgtype = null;
		String orgLabel = null;
		String department = null;
		String jobTitle = null;
		String title = null;
		boolean isSuperPrimary = false;
		boolean isPrimary = false;

		while (jp.nextToken() != JsonToken.END_OBJECT) {
			String namefield = jp.getCurrentName();
			// move to value
			if (jp.nextToken() == null) {
				throw new JsonParseException(
						"Invalid JSON-Structure. End of Object missing.",
						jp.getCurrentLocation());
			}
			if (ContactConstants.DATA.equals(namefield)) {
				orgname = jp.getValueAsString();
			} else if (ContactConstants.TYPE.equals(namefield)) {
				orgtype = OrganizationType.fromVal(jp.getValueAsInt());
			} else if (ContactConstants.PRIMARY.equals(namefield)) {
				isPrimary = jp.getValueAsBoolean();
			} else if (ContactConstants.SUPERPRIMARY.equals(namefield)) {
				isSuperPrimary = jp.getValueAsBoolean();
			} else if (ContactConstants.LABEL.equals(namefield)) {
				orgLabel = jp.getValueAsString();
			} else if (ContactConstants.ORGANIZATION_DEPARTMENT
					.equals(namefield)) {
				department = jp.getValueAsString();
			} else if (ContactConstants.ORGANIZATION_TITLE.equals(namefield)) {
				title = jp.getValueAsString();
			} else if (ContactConstants.ORGANIZATION_JOB.equals(namefield)) {
				jobTitle = jp.getValueAsString();
			} else {
				LOG.error("Unrecognized Organization-field for row with Id:"
						+ rowId + " Fieldname:" + namefield);
			}
		}

		if (orgtype == null) {
			orgtype = OrganizationType.TYPE_OTHER;
		}

		return new RawOrganizationData(orgname, orgtype, orgLabel, isPrimary,
				isSuperPrimary, title, department, jobTitle);
	}

	private static List<RawImData> readImList(String rowId,
			List<RawImData> imAddresses, JsonParser jp) throws IOException {
		List<RawImData> newImAddresses = imAddresses;
		while (jp.nextToken() != JsonToken.END_ARRAY) {
			ImType type = null;
			ImProtocolType proType = null;
			String customProctocolName = null;
			String imAddress = null;
			String imTypeLabel = null;
			boolean isSuperPrimary = false;
			boolean isPrimary = false;
			while (jp.nextToken() != JsonToken.END_OBJECT) {
				String namefield = jp.getCurrentName();
				// move to value
				if (jp.nextToken() == null) {
					throw new JsonParseException(
							"Invalid JSON-Structure. End of Object missing.",
							jp.getCurrentLocation());
				}
				if (ContactConstants.DATA.equals(namefield)) {
					imAddress = jp.getValueAsString();
				} else if (ContactConstants.TYPE.equals(namefield)) {
					type = ImType.fromVal(jp.getValueAsInt());
				} else if (ContactConstants.SUPERPRIMARY.equals(namefield)) {
					isSuperPrimary = jp.getValueAsBoolean();
				} else if (ContactConstants.PRIMARY.equals(namefield)) {
					isPrimary = jp.getValueAsBoolean();
				} else if (ContactConstants.LABEL.equals(namefield)) {
					imTypeLabel = jp.getValueAsString();
				} else if (ContactConstants.PROTOCOL_TYPE.equals(namefield)) {
					proType = ImProtocolType.fromVal(jp.getValueAsInt());
				} else if (ContactConstants.PROTOCOL_CUSTOM_PROT
						.equals(namefield)) {
					customProctocolName = jp.getValueAsString();
				} else {
					LOG.error(JSON_FIELDNOTRECOGNIZED + rowId + " Fieldname:"
							+ namefield);
				}
			}
			if (newImAddresses == null) {
				newImAddresses = new ArrayList<RawImData>();
			}
			if (type == null) {
				type = ImType.TYPE_OTHER;
			}

			newImAddresses.add(new RawImData(imAddress, type, imTypeLabel,
					isPrimary, isSuperPrimary, proType, customProctocolName));
		}
		return newImAddresses;
	}

	private static List<RawAddressData> readAddressList(String rowId,
			List<RawAddressData> addresses, JsonParser jp) throws IOException {
		List<RawAddressData> newAddresses = addresses;
		while (jp.nextToken() != JsonToken.END_ARRAY) {
			AddressType type = null;
			String label = null;
			String street = null;
			String city = null;
			String postcode = null;
			String country = null;
			String region = null;
			String pobox = null;
			String neighborhood = null;
			boolean isSuperPrimary = false;
			boolean isPrimary = false;
			while (jp.nextToken() != JsonToken.END_OBJECT) {
				String namefield = jp.getCurrentName();
				// move to value
				if (jp.nextToken() == null) {
					throw new JsonParseException(
							"Invalid JSON-Structure. End of Array missing.",
							jp.getCurrentLocation());
				}
				if (ContactConstants.NEIGHBORHOOD.equals(namefield)) {
					neighborhood = jp.getValueAsString();
				} else if (ContactConstants.TYPE.equals(namefield)) {
					type = AddressType.fromVal(jp.getValueAsInt());
				} else if (ContactConstants.SUPERPRIMARY.equals(namefield)) {
					isSuperPrimary = jp.getValueAsBoolean();
				} else if (ContactConstants.PRIMARY.equals(namefield)) {
					isPrimary = jp.getValueAsBoolean();
				} else if (ContactConstants.LABEL.equals(namefield)) {
					label = jp.getValueAsString();
				} else if (ContactConstants.STREET.equals(namefield)) {
					street = jp.getValueAsString();
				} else if (ContactConstants.REGION.equals(namefield)) {
					region = jp.getValueAsString();
				} else if (ContactConstants.CITY.equals(namefield)) {
					city = jp.getValueAsString();
				} else if (ContactConstants.POSTCODE.equals(namefield)) {
					postcode = jp.getValueAsString();
				} else if (ContactConstants.COUNTRY.equals(namefield)) {
					country = jp.getValueAsString();
				} else if (ContactConstants.REGION.equals(namefield)) {
					region = jp.getValueAsString();
				} else if (ContactConstants.POBOX.equals(namefield)) {
					pobox = jp.getValueAsString();
				} else {
					LOG.error(JSON_FIELDNOTRECOGNIZED + rowId + " Fieldname:"
							+ namefield);
				}
			}
			if (newAddresses == null) {
				newAddresses = new ArrayList<RawAddressData>();
			}
			if (type == null) {
				type = AddressType.TYPE_OTHER;
			}

			newAddresses.add(new RawAddressData(type, label, isPrimary,
					isSuperPrimary, street, pobox, neighborhood, city, region,
					postcode, country));
		}
		return newAddresses;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T extends ListType> List<ListRawData<T>> readJsonList(
			String rowId, List<ListRawData<T>> listData, JsonParser jp,
			String fieldname, ListType defaultType, Class<T> typeClass)
			throws IOException {
		List<ListRawData<T>> newListData = listData;
		while (jp.nextToken() != JsonToken.END_ARRAY) {
			String number = null;
			ListType type = defaultType;
			String label = null;
			boolean isSuperPrimary = false;
			boolean isPrimary = false;
			while (jp.nextToken() != JsonToken.END_OBJECT) {
				String namefield = jp.getCurrentName();
				// move to value
				if (jp.nextToken() == null) {
					throw new JsonParseException(
							"Invalid JSON-Structure. End of Array missing.",
							jp.getCurrentLocation());
				}
				if (ContactConstants.DATA.equals(namefield)) {
					number = jp.getValueAsString();
				} else if (ContactConstants.TYPE.equals(namefield)) {
					type = ContactConstants.fromVal(typeClass,
							jp.getValueAsInt());
					if (type == null) {
						type = defaultType;
					}
				} else if (ContactConstants.SUPERPRIMARY.equals(namefield)) {
					isSuperPrimary = jp.getValueAsBoolean();
				} else if (ContactConstants.PRIMARY.equals(namefield)) {
					isPrimary = jp.getValueAsBoolean();
				} else if (ContactConstants.LABEL.equals(namefield)) {
					label = jp.getValueAsString();
				} else {
					LOG.error(JSON_FIELDNOTRECOGNIZED + rowId + " Fieldname:"
							+ fieldname + " Unrecognized: " + namefield);
					break;
				}
			}
			if (number != null) {
				if (newListData == null) {
					newListData = new ArrayList();
				}
				newListData.add(new ListRawData(number, type, label, isPrimary,
						isSuperPrimary));
			}
		}
		return newListData;
	}

	/**
	 * Creates and returns RawContact instance from all the supplied parameters.
	 */
	public static RawContact create(String fullName, String firstName,
			String lastName, String middleName, String prefix, String suffix,
			String phonecticFirst, String phonecticMiddle,
			String phonecticLast, List<ListRawData<PhoneType>> phones,
			List<ListRawData<EmailType>> emails,
			List<ListRawData<WebsiteType>> websites,
			List<RawAddressData> addresses,
			List<ListRawData<EventType>> events,
			List<ListRawData<RelationType>> relations,
			List<ListRawData<SipAddressType>> sipaddresses,
			List<ListRawData<NicknameType>> nickname,
			List<RawImData> imAddresses, String note,
			RawOrganizationData organization, byte[] photo,
			boolean photoSuperPrimary, List<String> groupSourceIds,
			List<Long> groupIds, boolean starred, String droidCustomRingtone,
			boolean sendToVoicemail, Date lastModified, boolean deleted,
			long rawContactId, String serverContactId, long version) {
		return new RawContact(fullName, firstName, lastName, middleName,
				prefix, suffix, phonecticFirst, phonecticMiddle, phonecticLast,
				phones, emails, websites, addresses, events, relations,
				sipaddresses, nickname, imAddresses, note, organization, photo,
				photoSuperPrimary, groupSourceIds, groupIds, starred,
				droidCustomRingtone, sendToVoicemail, lastModified, deleted,
				serverContactId, rawContactId, true, version);
	}

	/**
	 * Creates and returns a User instance that represents a deleted user. Since
	 * the user is deleted, all we need are the client/server IDs.
	 * 
	 * @param clientUserId
	 *            The client-side ID for the contact
	 * @param serverUserId
	 *            The server-side ID for the contact
	 * @return a minimal User object representing the deleted contact.
	 */
	public static RawContact createDeletedContact(long rawContactId,
			String serverContactId) {
		return new RawContact(null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, false, null, null, false, null, false, null,
				true, serverContactId, rawContactId, true, -1);
	}
}
