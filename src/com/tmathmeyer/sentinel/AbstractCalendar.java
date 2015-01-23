/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel;

import javax.swing.JComponent;

import org.joda.time.DateTime;

import com.tmathmeyer.sentinel.models.data.Displayable;

/**
 * Abstract calendar is used to abstract month/week/day/year view in such a way
 * to enable any view to implement it and be controllable from the mini-calendar
 * and navigation arrows.
 */
public abstract class AbstractCalendar extends JComponent
{
	/**
	 * generated serialID
	 */
    private static final long serialVersionUID = -1331721503295229243L;

	/**
	 * Requests the next logical display. This should correspond to the period
	 * of the view, ie day view moves one day, week view one week, etc..
	 */
	public abstract void next();

	/**
	 * Requests the previous logical display. This should correspond to the
	 * period of the view, ie day view moves one day, week view one week, etc..
	 */
	public abstract void previous();

	/**
	 * Requests the view to adjust such that the provided date/time is visible.
	 * 
	 * @param newTime The instant to show
	 */
	public abstract void display(DateTime newTime);

	/**
	 * Notifies the calendar that the local client has changed an
	 * event/commitment. NOTE: updateDisplayable is only used when
	 * updating/creating/deleting events and is not used to pass ALL the events
	 * in. The AC should get the model and request from it all events.
	 * 
	 * @param event The modified displayable
	 * @param added Did we add/edit it or was it deleted (false)
	 */
	public abstract void updateDisplayable(Displayable event, boolean added);

	/**
	 * Select the given displayable via UI changes
	 * 
	 * @param item the event to select
	 */
	public abstract void select(Displayable item);

	public abstract void setSelectedDay(DateTime time);
}
