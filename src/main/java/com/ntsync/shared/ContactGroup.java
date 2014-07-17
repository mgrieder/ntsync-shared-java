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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.modes.AEADBlockCipher;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * Group for a RawContact
 */
public final class ContactGroup {

	private static final int DEFAULT_BYTEARRAY_SIZE = 100;

	private static final Logger LOG = LoggerFactory
			.getLogger(ContactGroup.class);

	private final Long rawId;
	private final String sourceId;
	private final String title;
	private final String notes;
	private final boolean deleted;
	private final long version;

	private Date lastModified;

	public ContactGroup(Long rawId, String sourceId, String title,
			String notes, boolean deleted, Date lastModified, long version) {
		super();
		this.rawId = rawId;
		this.sourceId = sourceId;
		this.title = title;
		this.notes = notes;
		this.deleted = deleted;
		this.lastModified = lastModified;
		this.version = version;
	}

	public Long getRawId() {
		return rawId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public String getTitle() {
		return title;
	}

	public String getNotes() {
		return notes;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public long getVersion() {
		return version;
	}

	/**
	 * Serialize this ContactGroup for transporting to a server
	 * 
	 * @param secret
	 * @param pwdSaltBase64
	 * @return null if serializing failed.
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
			hashValue.append(title);

			out.write(GroupConstants.ROWID);

			byte[] rowId = String.valueOf(rawId).getBytes(
					SyncDataHelper.DEFAULT_CHARSET_NAME);

			SyncDataHelper.writeInt(out, rowId.length);
			out.write(rowId);

			JsonFactory json = new JsonFactory();
			StringWriter writer = new StringWriter();
			JsonGenerator g = json.createGenerator(writer);
			g.writeStartObject();

			writeField(g, GroupConstants.TITLE, title);
			writeField(g, GroupConstants.NOTES, notes);

			g.writeEndObject();
			g.close();

			String textData = writer.toString();

			CryptoHelper.writeValue(secret, out, ecipher, iv, random,
					GroupConstants.TEXTDATA, textData);

			if (lastModified != null) {
				writeRawValue(
						out,
						GroupConstants.MODIFIED,
						String.valueOf(lastModified.getTime()).getBytes(
								SyncDataHelper.DEFAULT_CHARSET_NAME));
			}

			if (deleted) {
				writeRawValue(out, GroupConstants.DELETED,
						"1".getBytes(SyncDataHelper.DEFAULT_CHARSET_NAME));
			}
			if (sourceId != null) {
				writeRawValue(out, GroupConstants.SERVERROW_ID,
						sourceId.getBytes(SyncDataHelper.DEFAULT_CHARSET_NAME));
			}

			MessageDigest md = MessageDigest.getInstance("SHA-256");

			md.update(hashValue.toString().getBytes(
					SyncDataHelper.DEFAULT_CHARSET_NAME));
			byte[] hash = md.digest();
			writeRawValue(out, GroupConstants.HASH, hash);

			return out.toByteArray();
		} catch (final Exception ex) {
			LOG.error(
					"Error converting ContactGroup to ByteStream: "
							+ ex.toString(), ex);
		}
		return null;
	}

	/**
	 * Creates and returns an instance of the RawContact from encrypted data
	 * 
	 * */
	public static ContactGroup valueOf(String rowId,
			Map<Byte, ByteBuffer> values, Key privateKey)
			throws InvalidKeyException {
		try {
			String sourceId = null;
			Long rawId = null;

			if (values.containsKey(GroupConstants.SERVERROW_ID)) {
				sourceId = readRawString(values
						.get(GroupConstants.SERVERROW_ID));
			}

			if (sourceId == null || !sourceId.equals(rowId)) {
				// If ServerContactId is different, then rowId is the clientId
				rawId = Long.parseLong(rowId);
			}

			if (sourceId == null && rawId < 0) {
				throw new IllegalArgumentException("Missing RowId in data");
			}

			AEADBlockCipher cipher = CryptoHelper.getCipher();

			final boolean deleted = values.containsKey(GroupConstants.DELETED);

			final String textData = CryptoHelper.decodeStringValue(
					GroupConstants.TEXTDATA, values, cipher, privateKey);

			if (textData == null && !deleted) {
				LOG.error("No textdata found for row with Id:" + rowId);
				return null;
			}

			String title = null;
			String notes = null;

			if (!isEmpty(textData)) {
				JsonFactory fac = new JsonFactory();
				JsonParser jp = fac.createParser(textData);
				jp.nextToken();
				while (jp.nextToken() != JsonToken.END_OBJECT) {
					String fieldname = jp.getCurrentName();
					// move to value, or START_OBJECT/START_ARRAY
					jp.nextToken();
					if (GroupConstants.TITLE.equals(fieldname)) {
						title = jp.getValueAsString();
					} else if (GroupConstants.NOTES.equals(fieldname)) {
						notes = jp.getValueAsString();
					} else {
						LOG.error("Unrecognized field for row with Id:" + rowId
								+ " Fieldname:" + fieldname);
					}
				}
				jp.close();
			}

			String modStr = readRawString(values.get(GroupConstants.MODIFIED));
			Date lastModified = null;
			if (!isEmpty(modStr)) {
				lastModified = new Date(Long.parseLong(modStr));
			}

			return new ContactGroup(rawId, sourceId, title, notes, deleted,
					lastModified, -1);
		} catch (InvalidCipherTextException ex) {
			throw new InvalidKeyException("Invalid key detected.", ex);
		} catch (final Exception ex) {
			LOG.info(
					"Error parsing contactgroup data. Reason:" + ex.toString(),
					ex);
		}
		return null;
	}

	private static String readRawString(ByteBuffer src)
			throws UnsupportedEncodingException {
		int len = src != null ? src.remaining() : -1;
		if (len >= 0) {
			byte[] output = new byte[len];
			src.get(output, 0, len);
			// Android has UTF-8 as default
			return new String(output, SyncDataHelper.DEFAULT_CHARSET_NAME);
		}
		return null;
	}

	private static boolean isEmpty(String text) {
		return text == null || text.length() == 0;
	}

	private void writeRawValue(ByteArrayOutputStream out, byte key, byte[] value)
			throws IOException {
		if (value != null && value.length > 0) {
			// 1byte key, //4 len //value
			out.write(key);

			SyncDataHelper.writeInt(out, value.length);
			out.write(value);
		}
	}

	private static void writeField(JsonGenerator g, String fielName,
			String field) throws IOException {
		if (field != null && field.length() > 0) {
			g.writeStringField(fielName, field);
		}
	}

	public static ContactGroup createDeletedGroup(long rawId, String sourceId) {
		return new ContactGroup(rawId, sourceId, null, null, true, null, -1);
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
}
