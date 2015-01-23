/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.utils;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Several handy methods related to DateTime calculations
 */
public class Months
{
	/**
	 * Used for Month and MiniMonth formatting
	 */
	final public static DateTimeFormatter monthLblFormat = DateTimeFormat.forPattern("MMMM y");

	/**
	 * 
	 * @param year the year to test
	 * @return whether the year provided is a leap year
	 */
	@Deprecated
	public static boolean isLeapYear(int year)
	{
		if (year % 4 != 0)
			return false;
		if (year % 400 == 0)
			return true;
		if (year % 100 == 0)
			return false;
		return true;
	}

	/**
	 * 
	 * @param month the month's number (jan = 1, dec = 12)
	 * @param year
	 * @return the number of days in a given month in a given year
	 */
	@Deprecated
	public static int getDaysInMonth(int month, int year)
	{
		if (month == 2)
			return 28 + (isLeapYear(year) ? 1 : 0);

		if ((month == 4) || (month == 6) || (month == 9) || (month == 11))
			return 30;

		else
			return 31;
	}

	/**
	 * 
	 * @param year they year
	 * @param month the month
	 * @param day the day
	 * @return on which day of the week this date falls, ie: 1-sundae (mmmmm)
	 *         2-monday
	 */
	@Deprecated
	public static int getDayOfMonth(int year, int month, int day)
	{
		if (month < 3)
		{
			month += 12;
			year--;
		}
		int d = (day + 2 * month + (3 * (month + 1) / 5) + year + (year / 4) - (year / 100) + (year / 400) + 2) % 7;
		return (d == 0 ? 7 : d);

	}

	/**
	 * 
	 * @param year the year
	 * @param month the month
	 * @return the day (same format as getDayOfMonth)
	 */
	@Deprecated
	public static int getStartingDay(int year, int month)
	{
		return Months.getDayOfMonth(year, month, 1);
	}

	public static DateTime nextMonth(DateTime time)
	{
		MutableDateTime mdt = new MutableDateTime(time);
		mdt.addMonths(1);
		return mdt.toDateTime();
	}

	public static DateTime prevMonth(DateTime time)
	{
		MutableDateTime mdt = new MutableDateTime(time);
		mdt.addMonths(-1);
		return mdt.toDateTime();
	}

	public static DateTime nextDay(DateTime time)
	{
		MutableDateTime mdt = new MutableDateTime(time);
		mdt.addDays(1);
		return mdt.toDateTime();
	}

	public static DateTime prevDay(DateTime time)
	{
		MutableDateTime mdt = new MutableDateTime(time);
		mdt.addDays(-1);
		return mdt.toDateTime();
	}

	public static DateTime nextWeek(DateTime time)
	{
		MutableDateTime mdt = new MutableDateTime(time);
		mdt.addDays(7);
		return mdt.toDateTime();
	}

	public static DateTime prevWeek(DateTime time)
	{
		MutableDateTime mdt = new MutableDateTime(time);
		mdt.addDays(-7);
		return mdt.toDateTime();
	}

	/**
	 * 
	 * @param d any DateTime
	 * @return get's the Sundae of the provided day's week
	 */
	public static DateTime getWeekStart(DateTime d)
	{
		MutableDateTime t = new MutableDateTime(d);
		t.addDays(-(d.getDayOfWeek() % 7));
		return t.toDateTime();
	}

	/**
	 * adds descriptive letters to numbers: 1 -> 1st 2 -> 2nd 11 -> 11th etc
	 * 
	 * @param i the number to convert
	 * @return a String
	 */
	public static String getDescriptiveNumber(int i)
	{
		if (i % 100 < 20 && i % 100 > 10)
		{
			return i + "th";
		}

		if (i % 10 == 1)
		{
			return i + "st";
		}
		if (i % 10 == 2)
		{
			return i + "nd";
		}
		if (i % 10 == 3)
		{
			return i + "rd";
		}

		return i + "th";
	}

}
