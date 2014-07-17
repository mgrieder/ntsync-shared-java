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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * Generate Request-Data for a sync-Request
 */
public final class RequestGenerator {

	private static final String INVALID_BUFFER_MSG = "Invalid Buffer length. Pos: {} BufLen: {} ContainerEndPos: {}";

	public static final String SERVER_FIELD_NAME = "server";

	public static final String CLIENT_FIELD_NAME = "client";

	public static final String VERSION_FIELD_NAME = "version";

	public static final String SYNCSTATE_FIELD_NAME = "syncstate";

	public static final String PARAM_SYNCONLYGROUP = "synconlygroup";

	public static final String PARAM_IS_PHOTO_SYNC_ENABLED = "photoSync";

	public static final String PARAM_FORCE_PHOTO_SAVE = "forcePhotoSave";

	public static final String FIELD_PWDSALT = "pwdsalt";

	public static final String PARAM_MAX_CONTACTS = "maxC";

	public static final String PARAM_VALID_UNTIL = "validuntil";

	public static final String PARAM_MAX_GROUPS = "maxG";

	public static final String TAG_SERVER_CONFIG = "config";

	public static final String TAG_GROUPIDS = "groupids";

	public static final String TAG_CONTACTIDS = "contactids";

	public static final String PARAM_CLIENTID = "clientId";

	public static final String FIELD_SOFTWARE = "software";

	private static final int VERSION_LENGTH = 2;
	private static final int HEADER_LENGTH = 4;

	public static final short PROT_VERSION = 1;

	private static final int HEADER_POS = 6;

	private static final int ROWID_LEN = 4;

	private static final int VALUE_LEN = 4;

	private static final Logger LOG = LoggerFactory
			.getLogger(RequestGenerator.class);

	public static final String PARAM_SYNC_ANCHOR = "syncanchor";

	private static final JsonFactory JSONFACTORY = new JsonFactory();

	private RequestGenerator() {

	}

	/**
	 * 
	 * @return JsonFactory which is cached
	 */
	public static JsonFactory getJsonFactory() {
		return JSONFACTORY;
	}

	/**
	 * Generate Request-Data for sync-Request
	 * 
	 * @param serverSyncState
	 * @param dirtyContacts
	 * @param key
	 * @param pkgVersion
	 * @param clientId
	 * @param pwdSaltHexStr
	 * @param newIdMap
	 *            could be null.
	 * @param explizitPhotoSave
	 * @return
	 * @throws HeaderCreateException
	 */
	public static byte[] prepareServerRequest(SyncAnchor syncAnchor,
			List<RawContact> dirtyContacts, List<ContactGroup> dirtyGroups,
			SecretKey key, String pkgVersion, String clientId,
			String pwdSaltHexStr, Map<Long, String> newIdMap,
			SyncPrepErrorStatistic prepError, Restrictions restr,
			boolean explizitPhotoSave) throws HeaderCreateException {
		int totContactBufLen = 0;
		int totGroupBufLen = 0;

		List<byte[]> contacts = new ArrayList<byte[]>();
		if (dirtyContacts != null) {
			final Date lastSync = syncAnchor
					.getAnchorDate(ContactConstants.TYPE_CONTACT);
			for (RawContact rawContact : dirtyContacts) {
				if (lastSync != null && rawContact.getLastModified() == null) {
					rawContact.setLastModified(lastSync);
				}

				byte[] buffer = rawContact.toDTO(key, pwdSaltHexStr);
				if (buffer != null) {
					contacts.add(buffer);
					totContactBufLen += buffer.length;
				} else if (prepError != null) {
					prepError.incIgnoredRows();
				}
			}
		}
		List<byte[]> contactGroups = new ArrayList<byte[]>();
		if (dirtyGroups != null) {
			final Date lastSync = syncAnchor
					.getAnchorDate(ContactConstants.TYPE_CONTACTGROUP);
			for (ContactGroup group : dirtyGroups) {
				if (lastSync != null && group.getLastModified() == null) {
					group.setLastModified(lastSync);
				}

				byte[] buffer = group.toDTO(key, pwdSaltHexStr);
				if (buffer != null) {
					contactGroups.add(buffer);
					totGroupBufLen += buffer.length;
				} else if (prepError != null) {
					prepError.incIgnoredRows();
				}
			}
		}

		boolean syncOnlyGroup = dirtyContacts == null;

		// Prepare our POST data
		byte[] syncHeaderBuff = createHeader(syncAnchor, pkgVersion, clientId,
				pwdSaltHexStr, newIdMap, syncOnlyGroup, restr,
				explizitPhotoSave);

		final int containerHeaderLen = 5;
		final boolean contactDataAvail = totContactBufLen > 0;
		final boolean groupDataAvail = totGroupBufLen > 0;

		int headerBuffLen = syncHeaderBuff.length;
		byte[] totBuffer = new byte[VERSION_LENGTH + HEADER_LENGTH
				+ headerBuffLen + (contactDataAvail ? containerHeaderLen : 0)
				+ totContactBufLen + (groupDataAvail ? containerHeaderLen : 0)
				+ totGroupBufLen];
		// Fill buffer
		int index = 0;
		index = SyncDataHelper.writeShort(totBuffer, PROT_VERSION, index);
		index = SyncDataHelper.writeInt(totBuffer, headerBuffLen, index);
		System.arraycopy(syncHeaderBuff, 0, totBuffer, index, headerBuffLen);
		index += headerBuffLen;

		if (contactDataAvail) {
			// Write ContainerHeader
			totBuffer[index] = ContactConstants.TYPE_CONTACT;
			index += 1;
			index = SyncDataHelper.writeInt(totBuffer, totContactBufLen, index);
			for (byte[] contactBuff : contacts) {
				System.arraycopy(contactBuff, 0, totBuffer, index,
						contactBuff.length);
				index += contactBuff.length;
			}
		}
		if (groupDataAvail) {
			// Write ContainerHeader
			totBuffer[index] = ContactConstants.TYPE_CONTACTGROUP;
			index += 1;
			index = SyncDataHelper.writeInt(totBuffer, totGroupBufLen, index);
			for (byte[] buff : contactGroups) {
				System.arraycopy(buff, 0, totBuffer, index, buff.length);
				index += buff.length;
			}
		}
		return totBuffer;
	}

	private static byte[] createHeader(SyncAnchor syncAnchor,
			String pkgVersion, String clientId, String pwdSaltHexStr,
			Map<Long, String> newIdMap, boolean syncOnlyGroup,
			Restrictions restr, boolean explizitPhotoSave)
			throws HeaderCreateException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			JsonGenerator g = getJsonFactory().createGenerator(out);

			g.writeStartObject();

			g.writeObjectFieldStart(CLIENT_FIELD_NAME);

			g.writeObjectFieldStart(PARAM_SYNC_ANCHOR);
			for (Byte contType : syncAnchor.containers()) {
				long anchor = syncAnchor.getAnchor(contType);
				if (anchor > 0) {
					String type = String.valueOf((char) contType.byteValue());
					g.writeNumberField(type, anchor);
				}
			}
			g.writeEndObject();

			if (syncOnlyGroup) {
				LOG.info("Sync only ContactGroups");
				g.writeBooleanField(PARAM_SYNCONLYGROUP, true);
			}

			g.writeStringField(FIELD_SOFTWARE, "Android|" + pkgVersion);

			// Set ClientId
			if (clientId != null) {
				g.writeStringField(PARAM_CLIENTID, clientId);
			}

			if (restr != null) {
				g.writeBooleanField(PARAM_IS_PHOTO_SYNC_ENABLED,
						restr.isPhotoSyncSupported());
			}

			if (explizitPhotoSave) {
				g.writeBooleanField(PARAM_FORCE_PHOTO_SAVE, true);
			}

			// Set PwdSalt
			if (pwdSaltHexStr != null) {
				g.writeStringField(FIELD_PWDSALT, pwdSaltHexStr);
			}

			if (newIdMap != null && !newIdMap.isEmpty()) {
				g.writeObjectFieldStart(TAG_CONTACTIDS);
				for (Map.Entry<Long, String> idRow : newIdMap.entrySet()) {
					String serverId = idRow.getValue();
					if (serverId != null && serverId.length() > 0) {
						g.writeStringField(String.valueOf(idRow.getKey()),
								serverId);
					}
				}
				g.writeEndObject();
			}
			g.writeEndObject();
			g.writeEndObject();
			g.close();
		} catch (IOException ex) {
			throw new HeaderCreateException(ex);
		}

		return out.toByteArray();
	}

	/**
	 * 
	 * @param key
	 * @param clientId
	 * @param response
	 * @return
	 * @throws HeaderParseException
	 */
	public static SyncResponse processServerResponse(SecretKey key,
			String clientId, final byte[] response) throws HeaderParseException {
		short version = SyncDataHelper.readShort(response, 0);
		SyncState syncState = null;

		Map<Long, String> newGroupIdMap = null;
		Map<Long, String> newContactIdMap = null;
		SyncAnchor newSyncAnchor = new SyncAnchor();
		int skippedRows = 0;
		List<RawContact> serverContactList = new ArrayList<RawContact>();
		List<ContactGroup> serverGroupList = new ArrayList<ContactGroup>();

		String newClientId = clientId;
		Restrictions restr = null;
		if (version == RequestGenerator.PROT_VERSION) {
			int headerLength = SyncDataHelper.readInt(response, 2);

			JsonParser jp = null;
			try {
				jp = getJsonFactory().createParser(response, HEADER_POS,
						headerLength);
				jp.nextToken();
				while (jp.nextToken() != JsonToken.END_OBJECT) {
					String fieldname = jp.getCurrentName();
					// move to value, or START_OBJECT/START_ARRAY
					if (jp.nextToken() == null) {
						break;
					}
					if (CLIENT_FIELD_NAME.equals(fieldname)) {
						while (jp.nextToken() != JsonToken.END_OBJECT) {
							String clientField = jp.getCurrentName();
							if (jp.nextToken() == null) {
								break;
							}
							if (PARAM_SYNC_ANCHOR.equals(clientField)) {
								while (jp.nextToken() != JsonToken.END_OBJECT) {
									String anchorType = jp.getCurrentName();
									if (jp.nextToken() == null) {
										break;
									}
									long syncAnchor = jp.getLongValue();
									if (anchorType != null
											&& anchorType.length() > 0) {
										newSyncAnchor.setAnchor(
												(byte) anchorType.charAt(0),
												syncAnchor);
									}
								}
							} else if (PARAM_CLIENTID.equals(clientField)) {
								newClientId = jp.getValueAsString();
							} else if (TAG_GROUPIDS.equals(clientField)) {
								newGroupIdMap = extractNewIdList(jp);
							} else if (TAG_CONTACTIDS.equals(clientField)) {
								newContactIdMap = extractNewIdList(jp);
							} else {
								LOG.warn("Unsupported Client-Header-Field: {}",
										clientField);
							}
						}
					} else if (SERVER_FIELD_NAME.equals(fieldname)) {
						while (jp.nextToken() != JsonToken.END_OBJECT) {
							String serverField = jp.getCurrentName();
							if (jp.nextToken() == null) {
								break;
							}
							if (RequestGenerator.SYNCSTATE_FIELD_NAME
									.equals(serverField)) {
								String syncStateStr = jp.getValueAsString();
								if (syncStateStr != null
										&& syncStateStr.length() > 0) {
									syncState = SyncState
											.fromErrorVal(syncStateStr);
								}
							} else if (RequestGenerator.TAG_SERVER_CONFIG
									.equals(serverField)) {
								restr = parseRestr(jp);
							}
						}

					}
				}

				final int respLen = response.length;

				if (respLen > headerLength + HEADER_POS) {
					skippedRows = getUpdatedRows(key, serverContactList,
							serverGroupList, response, headerLength, respLen);
				}
			} catch (UnsupportedEncodingException ex) {
				throw new RuntimeException(ex);
			} catch (JsonParseException ex) {
				throw new HeaderParseException(ex);
			} catch (IOException e) {
				throw new HeaderParseException(e);
			} finally {
				if (jp != null) {
					try {
						jp.close();
					} catch (IOException ex) {
						LOG.warn("Could not close JSONParser", ex);
					}
				}
			}
		}

		return new SyncResponse(syncState, serverContactList, serverGroupList,
				newSyncAnchor, newClientId, newGroupIdMap, newContactIdMap,
				skippedRows, restr);
	}

	private static Map<Long, String> extractNewIdList(JsonParser jp)
			throws IOException {
		Map<Long, String> newIdMap = new HashMap<Long, String>();
		while (jp.nextToken() != JsonToken.END_OBJECT) {
			String clientIdStr = jp.getCurrentName();
			if (jp.nextToken() == null) {
				break;
			}
			String serverRowId = jp.getValueAsString();
			try {
				Long clientRowId = Long.valueOf(clientIdStr);
				newIdMap.put(clientRowId, serverRowId);
			} catch (NumberFormatException ex) {
				LOG.warn("Invalid ID from server. Id:" + clientIdStr
						+ " ServerId:" + serverRowId, ex);
			}

		}
		return newIdMap;
	}

	/**
	 * Parse a JSON-Object with "Server/Config/*" which the Restrictions
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static Restrictions parseRestr(InputStream is) throws IOException {
		JsonParser jp = getJsonFactory().createParser(is);
		jp.nextToken();
		Restrictions restr = null;
		while (jp.nextToken() != JsonToken.END_OBJECT) {
			String fieldname = jp.getCurrentName();
			// move to value, or START_OBJECT/START_ARRAY
			if (jp.nextToken() == null) {
				break;
			}
			if (SERVER_FIELD_NAME.equals(fieldname)) {
				while (jp.nextToken() != JsonToken.END_OBJECT) {
					String serverField = jp.getCurrentName();
					if (jp.nextToken() == null) {
						break;
					}
					if (RequestGenerator.TAG_SERVER_CONFIG.equals(serverField)) {
						restr = parseRestr(jp);
					}
				}
			}
		}
		return restr;
	}

	private static Restrictions parseRestr(JsonParser jp) throws IOException {
		int maxContacts = Integer.MAX_VALUE;
		int maxGroups = Integer.MAX_VALUE;
		boolean photoSyncSupported = false;
		Date validUntil = null;

		while (jp.nextToken() != JsonToken.END_OBJECT) {
			String configName = jp.getCurrentName();
			if (jp.nextToken() == null) {
				break;
			}
			if (PARAM_IS_PHOTO_SYNC_ENABLED.equals(configName)) {
				photoSyncSupported = jp.getBooleanValue();
			} else if (PARAM_MAX_CONTACTS.equals(configName)) {
				maxContacts = jp.getIntValue();
			} else if (PARAM_MAX_GROUPS.equals(configName)) {
				maxGroups = jp.getIntValue();
			} else if (PARAM_VALID_UNTIL.equals(configName)) {
				validUntil = new Date(jp.getLongValue());
			}
		}
		return new Restrictions(maxContacts, maxGroups, photoSyncSupported,
				validUntil);
	}

	private static int getUpdatedRows(SecretKey key,
			List<RawContact> serverDirtyList,
			List<ContactGroup> serverGroupList, final byte[] response,
			int xmlLength, final int respLen)
			throws UnsupportedEncodingException {

		int skippedRows = 0;

		int pos = xmlLength + HEADER_POS;

		// Container abfragen
		while (pos < respLen - VALUE_LEN) {
			byte contType = response[pos];
			pos++;
			int contLen = SyncDataHelper.readInt(response, pos);
			pos += VALUE_LEN;

			int contEndPos = pos + contLen;
			if (contEndPos > respLen) {
				LOG.warn("Invalid Container length. Pos:" + pos
						+ " ContainerLen:" + contLen + " RespLen:" + respLen);
				return 1;
			}

			// Container lesen
			if (contType == ContactConstants.TYPE_CONTACT
					|| contType == ContactConstants.TYPE_CONTACTGROUP) {
				skippedRows += readRowContainer(key, serverDirtyList,
						serverGroupList, response, contEndPos, pos, contType);
			}
			pos += contLen;
		}

		return skippedRows;
	}

	private static int readRowContainer(SecretKey key,
			List<RawContact> serverDirtyList,
			List<ContactGroup> serverGroupList, final byte[] response,
			final int contEndPos, int startPos, byte contType)
			throws UnsupportedEncodingException {
		int skippedRows = 0;
		String rowId = null;
		Map<Byte, ByteBuffer> values = new HashMap<Byte, ByteBuffer>();
		int pos = startPos;

		while (pos < contEndPos && pos >= 0) {
			byte valueKey = response[pos];
			if (valueKey == ContactConstants.ROWID) {
				boolean ok = addRow(key, serverDirtyList, serverGroupList,
						contType, rowId, values);
				if (!ok) {
					skippedRows++;
				}
				values.clear();

				// Read RowId
				pos += 1;
				int rowIdLen = SyncDataHelper.readInt(response, pos);
				pos += ROWID_LEN;
				// UTF-8 is default on Android
				rowId = new String(response, pos, rowIdLen,
						SyncDataHelper.DEFAULT_CHARSET_NAME);
				pos += rowIdLen;
			} else if (valueKey == ContactConstants.SERVERROW_ID
					|| valueKey == ContactConstants.MODIFIED
					|| valueKey == ContactConstants.HASH
					|| valueKey == ContactConstants.DELETED) {
				pos += 1;
				int valueLen = SyncDataHelper.readInt(response, pos);
				pos += VALUE_LEN;
				if (pos + valueLen <= contEndPos && valueLen >= 0) {
					values.put(valueKey,
							ByteBuffer.wrap(response, pos, valueLen));
				} else {
					LOG.warn(INVALID_BUFFER_MSG, pos, valueLen, contEndPos);
				}
				pos += valueLen;
			} else {
				// 1byte key, //16byte iv// //4 len
				pos += 1;

				int valueLen = SyncDataHelper.readInt(response, pos
						+ CryptoHelper.IV_LEN);
				int bufLen = CryptoHelper.PREAMBLE_LEN + valueLen;
				if (pos + bufLen <= contEndPos && valueLen >= 0) {
					values.put(Byte.valueOf((byte) valueKey),
							ByteBuffer.wrap(response, pos, bufLen));
				} else {
					LOG.warn(INVALID_BUFFER_MSG, pos, valueLen, contEndPos);
				}
				pos += bufLen;
			}
		}

		boolean ok = addRow(key, serverDirtyList, serverGroupList, contType,
				rowId, values);
		if (!ok) {
			skippedRows++;
		}
		return skippedRows;
	}

	/**
	 * 
	 * @param key
	 * @param serverDirtyList
	 * @param serverGroupList
	 * @param contType
	 * @param rowId
	 * @param values
	 * @return false if row could not be added due to an conversion error.
	 */
	private static boolean addRow(SecretKey key,
			List<RawContact> serverDirtyList,
			List<ContactGroup> serverGroupList, byte contType, String rowId,
			Map<Byte, ByteBuffer> values) {
		boolean ok = true;
		if (rowId != null) {
			try {
				if (contType == ContactConstants.TYPE_CONTACT) {
					RawContact rawContact = RawContact.valueOf(rowId, values,
							key);
					if (rawContact != null) {
						serverDirtyList.add(rawContact);
					}
				} else if (contType == ContactConstants.TYPE_CONTACTGROUP) {
					ContactGroup group = ContactGroup.valueOf(rowId, values,
							key);
					if (group != null) {
						serverGroupList.add(group);
					}
				}
			} catch (InvalidKeyException e) {
				LOG.warn("Could not convert DataRow: " + rowId, e);
				ok = false;
			}

		}
		return ok;
	}

	public static class SyncResponse {
		public final SyncState syncstate;
		public final List<RawContact> serverContacts;
		public final List<ContactGroup> serverGroups;
		public final SyncAnchor newServerAnchor;
		public final String clientId;
		public final int skippedResponse;
		public final Map<Long, String> newGroupIdMap;
		public final Map<Long, String> newContactIdMap;
		public final Restrictions restrictions;

		public SyncResponse(SyncState syncstate,
				List<RawContact> serverContacts,
				List<ContactGroup> serverGroups, SyncAnchor newServerAnchor,
				String clientId, Map<Long, String> newGroupIdMap,
				Map<Long, String> newContactIdMap, int skippedResponse,
				Restrictions restrictions) {
			super();
			this.syncstate = syncstate;
			this.serverContacts = serverContacts;
			this.serverGroups = serverGroups;
			this.newServerAnchor = newServerAnchor;
			this.clientId = clientId;
			this.newGroupIdMap = newGroupIdMap;
			this.newContactIdMap = newContactIdMap;
			this.skippedResponse = skippedResponse;
			this.restrictions = restrictions;
		}
	}

	/**
	 * Statistic about skipped errors / warnings during a sync.
	 */
	public static class SyncPrepErrorStatistic {
		private int ignoredRows = 0;

		public void incIgnoredRows() {
			ignoredRows++;
		}

		public int getIgnoredRows() {
			return ignoredRows;
		}
	}

}
