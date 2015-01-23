/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.ui.navigation;

import javax.swing.JComponent;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

/**
 * This is the controller for the entire side panel
 */
public class CalendarNavigationModule
{

	private DateTime time;
	private MiniMonth calendar;
	private MiniCalendarHostIface mc;
	private boolean monthOnly;

	public CalendarNavigationModule(DateTime time, MiniCalendarHostIface mc, boolean monthOnly)
	{
		this.mc = mc;
		this.time = time;
		this.monthOnly = monthOnly;
	}

	public CalendarNavigationModule getPrevious()
	{
		MutableDateTime next = new MutableDateTime(time);
		next.addMonths(-1);
		return new CalendarNavigationModule(next.toDateTime(), mc, monthOnly);
	}

	public CalendarNavigationModule getFollowing()
	{
		MutableDateTime next = new MutableDateTime(time);
		next.addMonths(1);
		return new CalendarNavigationModule(next.toDateTime(), mc, monthOnly);
	}

	public JComponent renderComponent()
	{
		if (calendar == null)
		{
			calendar = new MiniMonth(time, mc, monthOnly);
		}
		return calendar;
	}
}
