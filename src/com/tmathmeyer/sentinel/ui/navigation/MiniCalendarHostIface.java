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

import org.joda.time.DateTime;

/**
 * This is an interface for items that can display dates but not events.
 */
public interface MiniCalendarHostIface
{
	/**
	 * Requests the view to adjust such that the provided date/time is visible.
	 * 
	 * @param newTime The instant to show
	 */
	public void display(DateTime newtime);
}
