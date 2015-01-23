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

import com.tmathmeyer.sentinel.fs.RangeData;
import com.tmathmeyer.sentinel.models.Model;
import com.tmathmeyer.sentinel.models.client.net.NetworkCachingClient;
import com.tmathmeyer.sentinel.models.client.net.CategoryClient;
import com.tmathmeyer.sentinel.models.client.net.CommitmentClient;
import com.tmathmeyer.sentinel.ui.views.month.MonthCalendar;
import com.tmathmeyer.sentinel.utils.Months;

/**
 * Basic Commitment class that contains the information required to represent a
 * Commitment on a calendar.
 * 
 */
public class Commitment implements Displayable, Model, RangeData<Commitment>
{
    private static final long serialVersionUID = 7711318582487098682L;
	private UUID uuid = UUID.randomUUID();
	private String name;
	private String description;
	private Date duedate;
	private UUID category;
	private String participants;
	private boolean isProjectCommitment;
	private Status status;
	// Default status for new commitments.
	public static final Status DEFAULT_STATUS = Status.NOT_STARTED;

	@Override
    public Commitment copyInternal(Commitment t)
    {
	    throw new RuntimeException("AHHHHHHH");
    }
	
	@Override
    public UUID getUUID()
    {
	    return uuid;
    }
	
	/**
	 * @param name the name of the event
	 * @return this event after having it's name set
	 */
	public Commitment addName(String name)
	{
		setName(name);
		return this;
	}

	/**
	 * 
	 * @param description the event's description
	 * @return this event after having it's description set
	 */
	public Commitment addDescription(String description)
	{
		setDescription(description);
		return this;
	}

	/**
	 * This does the same things as setDate, it is only kept for compatibility
	 * with older code.
	 * 
	 * @param date the starting time
	 * @return this event after having its start date set
	 */
	public Commitment setDueDate(DateTime date)
	{
		setStart(date);
		return this;
	}

	@Override
	public UUID getCategory()
	{
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(UUID category)
	{
		this.category = category;
	}

	/**
	 * Create an event with the default characteristics.
	 */
	public Commitment()
	{
		super();
		status = DEFAULT_STATUS;
	}

	@Override
	public void save()
	{
		// This is never called by the core ?
	}

	@Override
	public void delete()
	{
		CommitmentClient.getInstance().delete(this);
	}

	@Override
	public Boolean identify(Object o)
	{
		if (o instanceof String)
			return getUuid().toString().equals((String) (o));
		else if (o instanceof UUID)
			return getUuid().equals((UUID) (o));
		else if (o instanceof Commitment)
			return getUuid().equals(((Commitment) (o)).getUuid());
		return false;
	}

	/**
	 * @param CommitmentID the CommitmentID to set
	 */
	public void setUuid(UUID commitmentID)
	{
		this.uuid = commitmentID;
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
		return new DateTime(duedate);
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(DateTime start)
	{
		this.duedate = start.toDate();
	}

	/**
	 * @return the end (only used for displaying on day calendar, commitments
	 *         don't actually hve duration)
	 */
	public DateTime getEnd()
	{
		return new DateTime(duedate).plusMinutes(45);
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

	public boolean isProjectwide()
	{
		return isProjectCommitment;
	}

	/**
	 * @param isProjectCommitment the isProjectCommitment to set
	 */
	public void setProjectCommitment(boolean isProjectCommitment)
	{
		this.isProjectCommitment = isProjectCommitment;
	}

	public Category getAssociatedCategory()
	{
		return CategoryClient.getInstance().getCategoryByUUID(category);
	}

	public Color getColor()
	{
		Color fallbackColor = isProjectCommitment ? new Color(125, 157, 227) : new Color(227, 125, 147);
		Category cat = CategoryClient.getInstance().getCategoryByUUID(category);
		if (cat == null)
		{
			return fallbackColor;
		}
		Color commitmentColor = cat.getColor();
		if (commitmentColor != null)
		{
			return commitmentColor;
		}
		return fallbackColor;
	}

	@Override
	public void setTime(DateTime newTime)
	{
		MutableDateTime mdt = new MutableDateTime(this.duedate);
		mdt.setDayOfYear(newTime.getDayOfYear());
		mdt.setYear(newTime.getYear());
		this.duedate = mdt.toDate();
	}

	@Override
	public Interval getInterval()
	{
		return new Interval(getStart(), getStart());
	}

	@Override
	public void update()
	{
		CommitmentClient.getInstance().update(this);
	}

	@Override
	public String getFormattedHoverTextTime()
	{
		return new DateTime(this.duedate).toString(DateTimeFormat.forPattern("h:mma"));
	}

	@Override
	public String getFormattedDateRange()
	{
		DateTime s = new DateTime(this.duedate);
		StringBuilder timeFormat = new StringBuilder().append(s.monthOfYear().getAsShortText()).append(", ")
		        .append(Months.getDescriptiveNumber(s.getDayOfMonth()));
		return timeFormat.toString();
	}

	@Override
	public UUID getUuid()
	{
		return uuid;
	}

	public static class SerializedAction extends NetworkCachingClient.SerializedAction<Commitment>
	{
		public SerializedAction(Commitment e, UUID eventID, boolean b)
		{
			object = e;
			uuid = eventID;
			isDeleted = b;
		}
	}

	public void select(MonthCalendar monthCalendar)
	{
		monthCalendar.select(this);
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
		if (this.getStart().plusMinutes(30).isAfter(mDisplayedDay))
		{
			return mDisplayedDay.toDateTime();
		} else
			return this.getStart().plusMinutes(30);
	}

	/**
	 * Gets the current status the commitment is at.
	 * 
	 * @return the current commitment status
	 */
	public Status getStatus()
	{
		return this.status;
	}

	public Commitment addStatus(Status status)
	{
		this.status = status;
		return this;
	}

	/**
	 * Set the status to a given status input.
	 * 
	 * @param status
	 */
	public void setStatus(Status status)
	{
		this.status = status;
	}

	/**
	 * an enum to describe the commitment type
	 */
	public enum Status
	{
		NOT_STARTED("Not Started"), IN_PROGRESS("In Progress"), COMPLETE("Completed");

		private String status;

		private Status(String s)
		{
			this.status = s;
		}

		@Override
		public String toString()
		{
			return status;
		}
	}

	@Override
    public void setEnd(DateTime newEnd)
    {
	    throw new IllegalArgumentException("can't do that");
    }

	@Override
    public long getStartUnix()
    {
	    return duedate.getTime() / 1000l;
    }

	@Override
    public long getEndUnix()
    {
	    return -1;
    }

	@Override
    public int compareTo(RangeData<Commitment> o)
    {
	    return (int) (getStartUnix() - o.getStartUnix());
    }
}
