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

import org.junit.Assert;
import org.junit.Test;

public class PriceTest {

	@Test
	public void testEquals() {

		Price price1 = new Price("CHF", 3, new BigDecimal("1.20"), 0, false,
				UUID.randomUUID(), null);
		Price price2 = new Price("CHF", 3, new BigDecimal("1.20"), 0, false,
				price1.getPriceId(), null);

		Price price3 = new Price("EUR", 3, new BigDecimal("1.20"), 0, false,
				price1.getPriceId(), null);
		Price price4 = new Price("CHF", 3, new BigDecimal("1.20"), 0, false,
				UUID.randomUUID(), null);
		Price price5 = new Price("EUR", 4, new BigDecimal("1.20"), 0, false,
				price1.getPriceId(), null);
		Price price6 = new Price("CHF", 3, new BigDecimal("1.30"), 0, false,
				price1.getPriceId(), null);
		Price price7 = new Price("CHF", 3, new BigDecimal("1.20"), 1, false,
				price1.getPriceId(), null);
		Price price8 = new Price("CHF", 3, new BigDecimal("1.20"), 0, true,
				price1.getPriceId(), null);
		Date disabledDate = new Date();
		Price price9 = new Price("CHF", 3, new BigDecimal("1.20"), 0, false,
				price1.getPriceId(), disabledDate);
		Price price10 = new Price("CHF", 3, new BigDecimal("1.20"), 0, false,
				price1.getPriceId(), disabledDate);

		Assert.assertNotEquals(price1, null);
		Assert.assertNotEquals(price1, "");
		Assert.assertNotEquals(price1, price3);
		Assert.assertNotEquals(price1, price4);
		Assert.assertNotEquals(price1, price5);
		Assert.assertNotEquals(price1, price6);
		Assert.assertNotEquals(price1, price7);
		Assert.assertNotEquals(price1, price8);
		Assert.assertNotEquals(price1, price9);
		Assert.assertEquals(price1, price2);
		Assert.assertEquals(price10, price10);

		Assert.assertEquals("CHF", price1.getCurrency());
		Assert.assertEquals(3, price1.getDuration());
		Assert.assertEquals(new BigDecimal("1.20"), price1.getPrice());
		Assert.assertEquals(1, price7.getDiscount());
		Assert.assertEquals(false, price1.isHit());
		Assert.assertEquals(true, price8.isHit());
		Assert.assertEquals(disabledDate, price9.getDisabledDate());
	}
}
