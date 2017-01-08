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
import java.util.Date;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;

import com.tmathmeyer.sentinel.models.Model;
import com.tmathmeyer.sentinel.models.client.net.NetworkCachingClient;
import com.tmathmeyer.sentinel.models.client.net.EventClient;
import com.tmathmeyer.sentinel.models.client.net.CategoryClient;
import com.tmathmeyer.sentinel.utils.Months;

/**
 * Basic event class that contains the information required to represent an
 * event on a calendar.
 * 
 */
public class Event implements Model, Displayable
{
    @SuppressWarnings("unused")
	private static final long serialVersionUID = 5687571978423959740L;
	private UUID uuid = UUID.randomUUID();
	private String name;
	private String description;
	private Date start;
	private Date end;
	private boolean isProjectEvent;
	private boolean isAllDay;
	private UUID category;
	private String participants;

	/**
	 * 
	 * @param name the name of the event
	 * @return this event after having it's name set
	 */
	public Event addName(String name)
	{
		setName(name);
		return this;
	}

	/**
	 * 
	 * @param description the event's description
	 * @return this event after having it's description set
	 */
	public Event addDescription(String description)
	{
		setDescription(description);
		return this;
	}

	/**
	 * 
	 * @param date the starting time
	 * @return this event after having its start date set
	 */
	public Event addStartTime(DateTime date)
	{
		setStart(date);
		return this;
	}

	/**
	 * 
	 * @param date the end time of this event
	 * @return this event after having it's end time set
	 */
	public Event addEndTime(DateTime date)
	{
		setEnd(date);
		return this;
	}

	/**
	 * 
	 * @param pe whether this is a project event
	 * @return this event after having it's project flag set
	 */
	public Event addIsProjectEvent(boolean pe)
	{
		setProjectEvent(pe);
		return this;
	}

	/**
	 * Create an event with the default characteristics.
	 */
	public Event()
	{
		super();
	}

	@Override
	public void save()
	{
		// This is never called by the core ?
	}

	@Override
	public void delete()
	{
		EventClient.getInstance().delete(this);
	}

	@Override
	public Boolean identify(Object o)
	{
		if (o instanceof String)
		{
			return getUuid().toString().equals((String) (o));
		} else if (o instanceof UUID)
		{
			return getUuid().equals((UUID) (o));
		} else if (o instanceof Event)
		{
			return getUuid().equals(((Event) (o)).getUuid());
		}
		return false;
	}

	@Override
	public UUID getUuid()
	{
		return uuid;
	}

	/**
	 * @param eventID the eventID to set
	 */
	public void setUuid(UUID eventID)
	{
		this.uuid = eventID;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return the start
	 */
	public DateTime getStart()
	{
		return new DateTime(start);
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(DateTime start)
	{
		this.start = start.toDate();
	}

	/**
	 * @return the end
	 */
	public DateTime getEnd()
	{
		return new DateTime(end);
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(DateTime end)
	{
		this.end = end.toDate();
	}

	/**
	 * @return the isProjectEvent
	 */
	public boolean isProjectwide()
	{
		return isProjectEvent;
	}

	/**
	 * @param isProjectEvent the isProjectEvent to set
	 */
	public void setProjectEvent(boolean isProjectEvent)
	{
		this.isProjectEvent = isProjectEvent;
	}

	@Override
	public UUID getCategory()
	{
		return category;
	}

	/**
	 * 
	 */
	public Event addCategory(UUID category)
	{
		setCategory(category);
		return this;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(UUID category)
	{
		this.category = category;
	}

	/**
	 * @return the participants
	 */
	public String getParticipants()
	{
		return participants;
	}

	/**
	 * @param participants the participants to set
	 */
	public void setParticipants(String participants)
	{
		this.participants = participants;
	}

	/**
	 * 
	 * @return whether this is an all day event or not
	 */
	boolean isAllDay()
	{
		return isAllDay;
	}

	/**
	 * set this event to be all day or not all day
	 * 
	 * @param isAllDay a boolean representing whether this event is all day
	 */
	void setAllDay(boolean isAllDay)
	{
		this.isAllDay = isAllDay;
	}

	/**
	 * returns the category that this event is associated with
	 * 
	 * @return a Category Object
	 */
	public Category getAssociatedCategory()
	{
		return CategoryClient.getInstance().getCategoryByUUID(category);
	}

	@Override
	public Color getColor()
	{
		Color fallbackColor = isProjectEvent ? new Color(125, 157, 227) : new Color(227, 125, 147);
		Category cat = CategoryClient.getInstance().getCategoryByUUID(category);
		if (cat == null)
		{
			return fallbackColor;
		}
		Color eventColor = cat.getColor();
		if (eventColor != null)
		{
			return eventColor;
		}
		return fallbackColor;
	}

	/**
	 * 
	 * @return whether this day is a multiday event
	 */
	public boolean isMultiDayEvent()
	{
		return (getEnd().getYear() != getStart().getYear() || getEnd().getDayOfYear() != getStart().getDayOfYear());
	}

	@Override
	public Interval getInterval()
	{
		return new Interval(getStart(), getEnd());
	}

	/**
	 * this is primarily used for multiday events
	 * 
	 * @param givenDay gets the time that this event starts on a given day
	 * @return when this event starts
	 */
	public DateTime getStartTimeOnDay(DateTime givenDay)
	{
		MutableDateTime mDisplayedDay = new MutableDateTime(givenDay);
		mDisplayedDay.setMillisOfDay(1);
		// if it starts before the beginning of the day then its a multi day
		// event, or all day event
		if (this.getStart().isBefore(mDisplayedDay))
		{
			mDisplayedDay.setMillisOfDay(0);
			return (mDisplayedDay.toDateTime());
		} else
			return this.getStart();
	}

	/**
	 * this is primarily used for multiday events
	 * 
	 * @param givenDay gets the time that this event ends on a given day
	 * @return when this event ends
	 */
	public DateTime getEndTimeOnDay(DateTime givenDay)
	{
		MutableDateTime mDisplayedDay = new MutableDateTime(givenDay);
		;
		mDisplayedDay.setMillisOfDay(86400000 - 2);
		if (this.getEnd().isAfter(mDisplayedDay))
		{
			return mDisplayedDay.toDateTime();
		} else
			return this.getEnd();
	}

	@Override
	public void setTime(DateTime newTime)
	{
		if (new Interval(new DateTime(this.start), new DateTime(this.end)).contains(newTime))
		{
			// this is what stops the events from being dragged to the next day.
			// leaving it in case we might want it later
			// return;
		}

		Interval i;
		int daysBetween = 0;
		if (new DateTime(this.start).isAfter(newTime))
		{
			i = new Interval(newTime, new DateTime(this.start));
			daysBetween = 0 - (int) i.toDuration().getStandardDays();
		} else
		{
			i = new Interval(new DateTime(this.start), newTime);
			daysBetween = (int) i.toDuration().getStandardDays();
		}

		MutableDateTime newEnd = new MutableDateTime(this.end);
		newEnd.addDays(daysBetween);

		MutableDateTime newStart = new MutableDateTime(this.start);
		newStart.addDays(daysBetween);

		this.end = newEnd.toDate();
		this.start = newStart.toDate();

	}

	@Override
	public void update()
	{
		EventClient.getInstance().put(this);
	}

	@Override
	public String getFormattedHoverTextTime()
	{
		StringBuilder timeFormat = new StringBuilder().append(getStart().toString(DateTimeFormat.forPattern("h:mma")))
		        .append(" - ").append(getEnd().toString(DateTimeFormat.forPattern("h:mma")));
		return timeFormat.toString();
	}

	@Override
	public String getFormattedDateRange()
	{
		if (this.isMultiDayEvent())
		{
			DateTime s = new DateTime(this.start);
			DateTime e = new DateTime(this.end);
			StringBuilder timeFormat = new StringBuilder().append(s.monthOfYear().getAsShortText()).append(", ")
			        .append(Months.getDescriptiveNumber(s.getDayOfMonth())).append(" - ")
			        .append(e.monthOfYear().getAsShortText()).append(", ")
			        .append(Months.getDescriptiveNumber(e.getDayOfMonth()));
			return timeFormat.toString();
		} else
		{
			DateTime s = new DateTime(this.start);
			StringBuilder timeFormat = new StringBuilder().append(s.monthOfYear().getAsShortText()).append(", ")
			        .append(Months.getDescriptiveNumber(s.getDayOfMonth()));
			return timeFormat.toString();
		}
	}

	@Override
	public String toString()
	{
		return new StringBuilder(super.toString()).append("{name: ").append(getName()).append(", from: ")
		        .append(getStart().toString()).append(", to: ").append(getEnd().toString()).append("}").toString();
	}

	public static class SerializedAction extends NetworkCachingClient.SerializedAction<Event>
	{
		public SerializedAction(Event e, UUID eventID, boolean b)
		{
			object = e;
			uuid = eventID;
			isDeleted = b;
		}
	}
	
	@Override
	public int hashCode()
	{
		return getUuid().hashCode();
	}

	@Override
	public NetworkCachingClient.SerializedAction<? extends Model> getSerializedAction(
			boolean isDeleted) {
		return new SerializedAction(this, this.getUuid(), isDeleted);
	}
}
