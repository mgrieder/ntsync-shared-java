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
 * Constants for serializing contacts-data to or from the sync-server.
 */
public final class ContactConstants {

	private ContactConstants() {
	}

	// Container-Type-Constants

	/** 99 */
	public static final byte TYPE_CONTACT = 'c';

	/** 103 */
	public static final byte TYPE_CONTACTGROUP = 'g';

	// Main-Constants

	/** 105 */
	public static final byte ROWID = 'i';

	/** 116 */
	public static final byte TEXTDATA = 't';

	/** 112 */
	public static final byte PHOTO = 'p';

	/** 72 */
	public static final byte HASH = 'H';

	/**
	 * long value. 109
	 */
	public static final byte MODIFIED = 'm';

	/** 115 */
	public static final byte SERVERROW_ID = 's';

	/**
	 * 1: deleted. not encrypted. 100
	 */
	public static final byte DELETED = 'd';

	// Text-Objects

	public static final String STRUCTUREDNAME = "s";

	public static final String PHONE = "t";

	public static final String EMAIL = "e";

	public static final String ORGANIZATION = "o";

	public static final String IM = "m";

	public static final String NICKNAME = "c";

	public static final String NOTE = "n";

	public static final String STRUCTUREDPOSTAL = "a";

	public static final String GROUPMEMBERSHIP = "g";

	public static final String WEBSITE = "w";

	public static final String EVENT = "v";

	public static final String RELATION = "r";

	public static final String SIPADDRESS = "d";

	public static final String PHOTO_SUPERPRIMARY = "p";

	public static final String STARRED = "x";

	public static final String DROID_CUSTOM_RINGTONE = "l";

	public static final String SEND_TO_VOICE_MAIL = "f";

	// StructuredPostal Conctants (includes List Constants without Data)
	public static final String STREET = "s";

	public static final String CITY = "c";

	public static final String POSTCODE = "z";

	public static final String POBOX = "b";

	public static final String REGION = "r";

	public static final String NEIGHBORHOOD = "n";

	public static final String COUNTRY = "o";

	// StructuredName Constants

	public static final String DISPLAY_NAME = "d";

	public static final String GIVEN_NAME = "g";

	public static final String FAMILY_NAME = "f";

	public static final String MIDDLE_NAME = "m";

	public static final String SUFFIX_NAME = "s";

	public static final String PREFIX_NAME = "p";

	public static final String PHONETIC_GIVEN = "G";

	public static final String PHONETIC_MIDDLE = "M";

	public static final String PHONETIC_FAMILY = "F";

	// Im List Constants

	public static final String PROTOCOL_TYPE = "i";

	public static final String PROTOCOL_CUSTOM_PROT = "c";

	// Organization Constants
	public static final String ORGANIZATION_TITLE = "e";

	public static final String ORGANIZATION_DEPARTMENT = "m";

	public static final String ORGANIZATION_JOB = "j";

	// List Constants (Phone / EMail / Website)
	public static final String DATA = "d";

	public static final String TYPE = "t";

	public static final String LABEL = "l";

	public static final String SUPERPRIMARY = "p";

	public static final String PRIMARY = "P";

	@SuppressWarnings("unchecked")
	public static <T extends ListType> T fromVal(Class<T> typeClass, int val) {
		T type = null;
		if (typeClass == PhoneType.class) {
			type = (T) PhoneType.fromVal(val);
		} else if (typeClass == EmailType.class) {
			type = (T) EmailType.fromVal(val);
		} else if (typeClass == WebsiteType.class) {
			type = (T) WebsiteType.fromVal(val);
		} else if (typeClass == NicknameType.class) {
			type = (T) NicknameType.fromVal(val);
		} else if (typeClass == EventType.class) {
			type = (T) EventType.fromVal(val);
		} else if (typeClass == RelationType.class) {
			type = (T) RelationType.fromVal(val);
		} else if (typeClass == SipAddressType.class) {
			type = (T) SipAddressType.fromVal(val);
		} else if (typeClass == AddressType.class) {
			type = (T) AddressType.fromVal(val);
		} else if (typeClass == ImType.class) {
			type = (T) ImType.fromVal(val);
		} else if (typeClass == OrganizationType.class) {
			type = (T) OrganizationType.fromVal(val);
		}

		return type;
	}

	public enum PhoneType implements ListType {
		TYPE_CUSTOM(1), TYPE_HOME(2), TYPE_MOBILE(3), TYPE_WORK(4), TYPE_FAX_WORK(
				5), TYPE_FAX_HOME(6), TYPE_PAGER(7), TYPE_OTHER(8), TYPE_CALLBACK(
				9), TYPE_CAR(10), TYPE_COMPANY_MAIN(11), TYPE_ISDN(12), TYPE_MAIN(
				13), TYPE_OTHER_FAX(14), TYPE_RADIO(15), TYPE_TELEX(16), TYPE_TTY_TDD(
				17), TYPE_WORK_MOBILE(18), TYPE_WORK_PAGER(19), TYPE_ASSISTANT(
				20), TYPE_MMS(21);

		private final int val;

		private PhoneType(int val) {
			this.val = val;
		}

		public int getVal() {
			return val;
		}

		/**
		 * 
		 * @param errorVal
		 * @return null if not found or the corresponding SyncStatus
		 */
		public static PhoneType fromVal(int val) {
			PhoneType s = null;
			for (PhoneType state : PhoneType.values()) {
				if (state.getVal() == val) {
					s = state;
					break;
				}
			}
			return s;
		}
	}

	public interface ListType {
		int getVal();
	}

	public enum WebsiteType implements ListType {
		TYPE_CUSTOM(1), TYPE_HOMEPAGE(2), TYPE_BLOG(3), TYPE_PROFILE(4), TYPE_HOME(
				5), TYPE_WORK(6), TYPE_FTP(7), TYPE_OTHER(8);

		private final int val;

		private WebsiteType(int val) {
			this.val = val;
		}

		public int getVal() {
			return val;
		}

		/**
		 * 
		 * @param errorVal
		 * @return null if not found or the corresponding WebsiteType
		 */
		public static WebsiteType fromVal(int val) {
			WebsiteType s = null;
			for (WebsiteType state : WebsiteType.values()) {
				if (state.getVal() == val) {
					s = state;
					break;
				}
			}
			return s;
		}
	}

	public enum EmailType implements ListType {
		TYPE_CUSTOM(1), TYPE_HOME(2), TYPE_WORK(3), TYPE_OTHER(4), TYPE_MOBILE(
				5);

		private final int val;

		private EmailType(int val) {
			this.val = val;
		}

		public int getVal() {
			return val;
		}

		/**
		 * 
		 * @param errorVal
		 * @return null if not found or the corresponding SyncStatus
		 */
		public static EmailType fromVal(int val) {
			EmailType s = null;
			for (EmailType state : EmailType.values()) {
				if (state.getVal() == val) {
					s = state;
					break;
				}
			}
			return s;
		}
	}

	public enum AddressType implements ListType {
		TYPE_CUSTOM(1), TYPE_HOME(2), TYPE_WORK(3), TYPE_OTHER(4);

		private final int val;

		private AddressType(int val) {
			this.val = val;
		}

		public int getVal() {
			return val;
		}

		/**
		 * 
		 * @param errorVal
		 * @return null if not found or the corresponding SyncStatus
		 */
		public static AddressType fromVal(int val) {
			AddressType s = null;
			for (AddressType state : AddressType.values()) {
				if (state.getVal() == val) {
					s = state;
					break;
				}
			}
			return s;
		}
	}

	public enum ImType implements ListType {
		TYPE_CUSTOM(1), TYPE_HOME(2), TYPE_WORK(3), TYPE_OTHER(4);

		private final int val;

		private ImType(int val) {
			this.val = val;
		}

		public int getVal() {
			return val;
		}

		/**
		 * 
		 * @param errorVal
		 * @return null if not found or the corresponding SyncStatus
		 */
		public static ImType fromVal(int val) {
			ImType s = null;
			for (ImType state : ImType.values()) {
				if (state.getVal() == val) {
					s = state;
					break;
				}
			}
			return s;
		}
	}

	public enum OrganizationType implements ListType {
		TYPE_CUSTOM(1), TYPE_WORK(2), TYPE_OTHER(3);

		private final int val;

		private OrganizationType(int val) {
			this.val = val;
		}

		public int getVal() {
			return val;
		}

		/**
		 * 
		 * @param errorVal
		 * @return null if not found or the corresponding SyncStatus
		 */
		public static OrganizationType fromVal(int val) {
			OrganizationType s = null;
			for (OrganizationType state : OrganizationType.values()) {
				if (state.getVal() == val) {
					s = state;
					break;
				}
			}
			return s;
		}
	}

	public enum ImProtocolType implements ListType {
		PROTOCOL_CUSTOM(1), PROTOCOL_AIM(2), PROTOCOL_MSN(3), PROTOCOL_YAHOO(4), PROTOCOL_SKYPE(
				5), PROTOCOL_QQ(6), PROTOCOL_GOOGLE_TALK(7), PROTOCOL_ICQ(8), PROTOCOL_JABBER(
				9), PROTOCOL_NETMEETING(10);

		private final int val;

		private ImProtocolType(int val) {
			this.val = val;
		}

		public int getVal() {
			return val;
		}

		/**
		 * 
		 * @param errorVal
		 * @return null if not found or the corresponding SyncStatus
		 */
		public static ImProtocolType fromVal(int val) {
			ImProtocolType s = null;
			for (ImProtocolType state : ImProtocolType.values()) {
				if (state.getVal() == val) {
					s = state;
					break;
				}
			}
			return s;
		}
	}

	public enum NicknameType implements ListType {
		TYPE_CUSTOM(1), TYPE_DEFAULT(2), TYPE_OTHER_NAME(3), TYPE_MAIDEN_NAME(4), TYPE_SHORT_NAME(
				5), TYPE_INITIALS(6);

		private final int val;

		private NicknameType(int val) {
			this.val = val;
		}

		public int getVal() {
			return val;
		}

		/**
		 * 
		 * @param errorVal
		 * @return null if not found or the corresponding SyncStatus
		 */
		public static NicknameType fromVal(int val) {
			NicknameType s = null;
			for (NicknameType state : NicknameType.values()) {
				if (state.getVal() == val) {
					s = state;
					break;
				}
			}
			return s;
		}
	}

	public enum EventType implements ListType {
		TYPE_CUSTOM(1), TYPE_ANNIVERSARY(2), TYPE_OTHER(3), TYPE_BIRTHDAY(4);

		private final int val;

		private EventType(int val) {
			this.val = val;
		}

		public int getVal() {
			return val;
		}

		/**
		 * 
		 * @param errorVal
		 * @return null if not found or the corresponding SyncStatus
		 */
		public static EventType fromVal(int val) {
			EventType s = null;
			for (EventType state : EventType.values()) {
				if (state.getVal() == val) {
					s = state;
					break;
				}
			}
			return s;
		}
	}

	public enum RelationType implements ListType {
		TYPE_CUSTOM(1), TYPE_ASSISTANT(2), TYPE_BROTHER(3), TYPE_CHILD(4), TYPE_DOMESTIC_PARTNER(
				5), TYPE_FATHER(6), TYPE_FRIEND(7), TYPE_MANAGER(8), TYPE_MOTHER(
				9), TYPE_PARENT(10), TYPE_PARTNER(11), TYPE_REFERRED_BY(12), TYPE_RELATIVE(
				13), TYPE_SISTER(14), TYPE_SPOUSE(15);

		private final int val;

		private RelationType(int val) {
			this.val = val;
		}

		public int getVal() {
			return val;
		}

		/**
		 * 
		 * @param errorVal
		 * @return null if not found or the corresponding SyncStatus
		 */
		public static RelationType fromVal(int val) {
			RelationType s = null;
			for (RelationType state : RelationType.values()) {
				if (state.getVal() == val) {
					s = state;
					break;
				}
			}
			return s;
		}
	}

	public enum SipAddressType implements ListType {
		TYPE_CUSTOM(1), TYPE_HOME(2), TYPE_WORK(3), TYPE_OTHER(4);

		private final int val;

		private SipAddressType(int val) {
			this.val = val;
		}

		public int getVal() {
			return val;
		}

		/**
		 * 
		 * @param errorVal
		 * @return null if not found or the corresponding SyncStatus
		 */
		public static SipAddressType fromVal(int val) {
			SipAddressType s = null;
			for (SipAddressType state : SipAddressType.values()) {
				if (state.getVal() == val) {
					s = state;
					break;
				}
			}
			return s;
		}
	}
}
