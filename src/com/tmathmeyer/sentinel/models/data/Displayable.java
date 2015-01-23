/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.models.data;

import java.awt.Color;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Any object that is displayable on the calendar with a date and time, such as
 * events and commitments.
 */
public interface Displayable
{
	/**
	 * Gets the name of the event/commitment.
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Get the description for the event/commitment.
	 * 
	 * @return the respective description
	 */
	public String getDescription();

	/**
	 * Gets the participants for the event/commitment.
	 * 
	 * @return the participants for said event/commitment.
	 */
	public String getParticipants();

	/**
	 * The date to display. If there are more than one, the default date (start)
	 * 
	 * @return the start time
	 */
	public DateTime getStart();

	/**
	 * Sets the date to display. If there are more than one, the default date
	 * (start)
	 * 
	 * @param newDate the date to set the start date to
	 */
	public void setStart(DateTime newDate);

	/**
	 * Get the end date for the event/commitment.
	 * 
	 * @return the end date
	 */
	public DateTime getEnd();

	/**
	 * Get the time interval
	 * 
	 * @return the interval
	 */
	public Interval getInterval();

	/**
	 * See if event/commitment pertains to the project.
	 * 
	 * @return true if it pertains to the project.
	 */
	public boolean isProjectwide();

	/**
	 * deletes this Displayable
	 */
	public void delete();

	/**
	 * sets the time (for easy updating)
	 */
	public void setTime(DateTime newTime);

	/**
	 * updates this event (sends call to db layer)
	 */
	public void update();

	/**
	 * gets the duration of this event as a string
	 * 
	 * @return a String
	 */
	public String getFormattedHoverTextTime();

	/**
	 * returns the dateRange for this event (an empty string for single day
	 * events and commitments)
	 * 
	 * @return a String
	 */
	public String getFormattedDateRange();

	/**
	 * gets the displayables identification UUID
	 * 
	 * @return a UUID
	 */
	public UUID getUuid();

	/**
	 * @return the category
	 */
	public UUID getCategory();

	/**
	 * get the color that this event wants to be drawn
	 * 
	 * @return a Color Object
	 */
	public Color getColor();

	/**
	 * Gets the start time of the event on a given day.
	 * 
	 * @param givenDay the day to check
	 * @return the start time for that day
	 */
	public DateTime getStartTimeOnDay(DateTime givenDay);

	/**
	 * Gets the end time of the event on a given day.
	 * 
	 * @param givenDay the day to check
	 * @return the end time for that day
	 */
	public DateTime getEndTimeOnDay(DateTime givenDay);

	public void setEnd(DateTime newEnd);

}
