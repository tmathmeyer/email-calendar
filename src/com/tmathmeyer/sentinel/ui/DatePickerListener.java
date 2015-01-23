/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.ui;

import org.joda.time.DateTime;

/**
 * Event listener used when the DatePicker changes.
 */
public interface DatePickerListener
{
	/**
	 * Called whenever a DatePicker changes. Null dateTime means invalid date.
	 * 
	 * @param mDateTime parsed DateTime or null if not parsable.
	 */
	public void datePickerUpdate(DateTime dateTime);
}
