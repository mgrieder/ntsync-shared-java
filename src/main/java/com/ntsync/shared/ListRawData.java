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

import com.ntsync.shared.ContactConstants.AddressType;
import com.ntsync.shared.ContactConstants.ImProtocolType;
import com.ntsync.shared.ContactConstants.ImType;
import com.ntsync.shared.ContactConstants.ListType;
import com.ntsync.shared.ContactConstants.OrganizationType;

/**
 * Provides the common structure for an entry in a list data type. This is based
 * on the android db-structure.
 * 
 * @param <T>
 *            Assigend List-Type for this data-list.
 */
public class ListRawData<T extends ListType> implements IListRawData<T> {
	protected String data;
	protected T type;
	private String label;
	private boolean isPrimary;
	private boolean isSuperPrimary;

	public ListRawData(String data, T type, String label, boolean isPrimary,
			boolean isSuperPrimary) {
		this.data = data;
		this.type = type;
		this.label = label;
		this.isPrimary = isPrimary;
		this.isSuperPrimary = isSuperPrimary;
	}

	@Override
	public String getData() {
		return data;
	}

	@Override
	public T getType() {
		return type;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public boolean isSuperPrimary() {
		return isSuperPrimary;
	}

	@Override
	public boolean isPrimary() {
		return isPrimary;
	}

	@Override
	public int hashCode() {
		return data == null ? 0 : data.hashCode();
	}

	@SuppressWarnings("unchecked")
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
		ListRawData<T> other = (ListRawData<T>) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		if (isSuperPrimary != other.isSuperPrimary) {
			return false;
		}
		if (isPrimary != other.isPrimary) {
			return false;
		}

		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	public static class RawAddressData extends ListRawData<AddressType> {

		private String neighborhood;
		private String city;
		private String street;
		private String pobox;
		private String region;
		private String postcode;
		private String country;

		public RawAddressData(AddressType type, String label,
				boolean isPrimary, boolean isSuperPrimary, String street,
				String pobox, String neighborhood, String city, String region,
				String postcode, String country) {
			super(null, type, label, isPrimary, isSuperPrimary);
			this.street = street;
			this.pobox = pobox;
			this.neighborhood = neighborhood;
			this.city = city;
			this.region = region;
			this.postcode = postcode;
			this.country = country;
		}

		@Override
		public int hashCode() {
			int hash;
			if (street != null) {
				hash = street.hashCode();
			} else if (city != null) {
				hash = city.hashCode();
			} else {
				hash = 0;
			}
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (!super.equals(obj)) {
				return false;
			}
			RawAddressData other = (RawAddressData) obj;
			if (city == null) {
				if (other.city != null) {
					return false;
				}
			} else if (!city.equals(other.city)) {
				return false;
			}
			if (country == null) {
				if (other.country != null) {
					return false;
				}
			} else if (!country.equals(other.country)) {
				return false;
			}
			if (neighborhood == null) {
				if (other.neighborhood != null) {
					return false;
				}
			} else if (!neighborhood.equals(other.neighborhood)) {
				return false;
			}
			if (pobox == null) {
				if (other.pobox != null) {
					return false;
				}
			} else if (!pobox.equals(other.pobox)) {
				return false;
			}
			if (postcode == null) {
				if (other.postcode != null) {
					return false;
				}
			} else if (!postcode.equals(other.postcode)) {
				return false;
			}
			if (region == null) {
				if (other.region != null) {
					return false;
				}
			} else if (!region.equals(other.region)) {
				return false;
			}
			if (street == null) {
				if (other.street != null) {
					return false;
				}
			} else if (!street.equals(other.street)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "RawAddressData [street=" + street + ", postcode="
					+ postcode + ", country=" + country + "]";
		}

		public String getNeighborhood() {
			return neighborhood;
		}

		public String getCity() {
			return city;
		}

		public String getStreet() {
			return street;
		}

		public String getPobox() {
			return pobox;
		}

		public String getRegion() {
			return region;
		}

		public String getPostcode() {
			return postcode;
		}

		public String getCountry() {
			return country;
		}
	}

	public static class RawImData extends ListRawData<ImType> {

		private ImProtocolType protType;
		private String customProtocolName;

		public RawImData(String address, ImType type, String label,
				boolean isPrimary, boolean isSuperPrimary,
				ImProtocolType protType, String customProtocolName) {
			super(address, type, label, isPrimary, isSuperPrimary);
			this.protType = protType;
			this.customProtocolName = customProtocolName;
		}

		public ImProtocolType getProtType() {
			return protType;
		}

		public String getCustomProtocolName() {
			return customProtocolName;
		}

		@Override
		public int hashCode() {
			if (data == null) {
				return type != null ? type.getVal() : -1;
			}
			return data.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!super.equals(obj)) {
				return false;
			}
			RawImData other = (RawImData) obj;
			if (customProtocolName == null) {
				if (other.customProtocolName != null) {
					return false;
				}
			} else if (!customProtocolName.equals(other.customProtocolName)) {
				return false;
			}
			if (protType != other.protType) {
				return false;
			}
			return true;
		}

	}

	public static class RawOrganizationData extends
			ListRawData<OrganizationType> {

		private String title;
		private String department;
		private String jobDescription;

		public RawOrganizationData(String name, OrganizationType type,
				String label, boolean isPrimary, boolean isSuperPrimary,
				String title, String department, String jobDescription) {
			super(name, type, label, isPrimary, isSuperPrimary);
			this.title = title;
			this.department = department;
			this.jobDescription = jobDescription;
		}

		public String getDepartment() {
			return department;
		}

		public String getJobDescription() {
			return jobDescription;
		}

		public String getTitle() {
			return title;
		}

		@Override
		public int hashCode() {
			if (data == null) {
				return type != null ? type.getVal() : -2;
			}
			return data.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!super.equals(obj)) {
				return false;
			}
			RawOrganizationData other = (RawOrganizationData) obj;
			if (title == null) {
				if (other.title != null) {
					return false;
				}
			} else if (!title.equals(other.title)) {
				return false;
			}
			if (department == null) {
				if (other.department != null) {
					return false;
				}
			} else if (!department.equals(other.department)) {
				return false;
			}
			if (jobDescription == null) {
				if (other.jobDescription != null) {
					return false;
				}
			} else if (!jobDescription.equals(other.jobDescription)) {
				return false;
			}
			return true;
		}
	}
}