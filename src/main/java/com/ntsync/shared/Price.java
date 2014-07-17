/**
 * 
 */
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

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Product-Price for buying a ntsync-subscription 
 */
public class Price {

	private String currency;

	private int duration;

	private BigDecimal price;

	private int discount;

	private boolean hit;

	private UUID priceId;

	/**
	 * Date when this Price was been diabled. A Price could still be used for a
	 * short time after its disabled.
	 **/
	private Date disabledDate;

	public Price() {

	}

	public Price(String currency, int duration, BigDecimal price, int discount,
			boolean hit, UUID priceId, Date disabledDate) {
		super();
		this.currency = currency;
		this.duration = duration;
		this.price = price;
		this.discount = discount;
		this.hit = hit;
		this.priceId = priceId;
		this.disabledDate = disabledDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

	public boolean isHit() {
		return hit;
	}

	public void setHit(boolean hit) {
		this.hit = hit;
	}

	public UUID getPriceId() {
		return priceId;
	}

	public void setPriceId(UUID priceId) {
		this.priceId = priceId;
	}

	public Date getDisabledDate() {
		return disabledDate;
	}

	public void setDisabledDate(Date disabledDate) {
		this.disabledDate = disabledDate;
	}

	@Override
	public int hashCode() {
		return priceId != null ? priceId.hashCode() : 0;
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
		Price other = (Price) obj;
		if (priceId == null) {
			if (other.priceId != null) {
				return false;
			}
		} else if (!priceId.equals(other.priceId)) {
			return false;
		}
		if (price == null) {
			if (other.price != null) {
				return false;
			}
		} else if (!price.equals(other.price)) {
			return false;
		}
		if (currency == null) {
			if (other.currency != null) {
				return false;
			}
		} else if (!currency.equals(other.currency)) {
			return false;
		}
		if (disabledDate == null) {
			if (other.disabledDate != null) {
				return false;
			}
		} else if (!disabledDate.equals(other.disabledDate)) {
			return false;
		}
		if (discount != other.discount) {
			return false;
		}
		if (duration != other.duration) {
			return false;
		}
		if (hit != other.hit) {
			return false;
		}
		return true;
	}
}
