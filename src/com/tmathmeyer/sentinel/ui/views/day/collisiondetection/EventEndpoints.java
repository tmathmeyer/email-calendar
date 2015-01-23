/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.ui.views.day.collisiondetection;

import org.joda.time.DateTime;

import com.tmathmeyer.sentinel.models.data.Commitment;
import com.tmathmeyer.sentinel.models.data.Displayable;
import com.tmathmeyer.sentinel.models.data.Event;

/**
 * Class used for day calendar collisions
 */
public class EventEndpoints implements Comparable<EventEndpoints>
{
	private Displayable displayable;
	private DateTime time;
	private boolean isEnd;
	private OverlappedDisplayable result;

	/**
	 * 
	 * @param displayable the Displayable to getEndpoints from
	 * @param isEnd whether this is the end of an Displayable (for multiday)
	 * @param displayedDay the day on which the Displayable is being displayed
	 */
	public EventEndpoints(Displayable displayable, boolean isEnd, DateTime displayedDay)
	{
		this.displayable = displayable;
		this.isEnd = isEnd;
		if (displayable instanceof Event)
		{
			if (!isEnd)
			{
				this.time = ((Event) displayable).getStartTimeOnDay(displayedDay);
			} else
			{
				this.time = ((Event) displayable).getEndTimeOnDay(displayedDay);
			}
		} else if (displayable instanceof Commitment)
		{
			if (!isEnd)
			{
				this.time = displayable.getStart();
			} else
			{
				this.time = displayable.getEnd();
			}
		}
	}

	/**
	 * 
	 * @return this Displayable
	 */
	public Displayable getEvent()
	{
		return displayable;
	}

	/**
	 * 
	 * @return this time
	 */
	public DateTime getTime()
	{
		return time;
	}

	/**
	 * 
	 * @return return whether this is the end
	 */
	public boolean isEnd()
	{
		return isEnd;
	}

	/**
	 * 
	 * @param isEnd set whether this is the end
	 */
	public void setEnd(boolean isEnd)
	{
		this.isEnd = isEnd;
	}

	/**
	 * 
	 * @return get the overlappingEvent that we encapsulate
	 */
	public OverlappedDisplayable getResult()
	{
		return result;
	}

	/**
	 * 
	 * @param result the overlap to set
	 * @return this overlapping Displayable once it has been set
	 */
	public OverlappedDisplayable setResult(OverlappedDisplayable result)
	{
		this.result = result;
		return result;
	}

	@Override
	public int compareTo(EventEndpoints o)
	{
		int res = time.compareTo(o.time);
		if (res == 0 && isEnd != o.isEnd)
		{
			res = isEnd ? -1 : 1;
		}
		if (res == 0 && !isEnd) // sort by start, and if they are the same, by
								// last end time
		{
			// at this point, will always return 0 for commitments
			res = o.displayable.getEnd().compareTo(displayable.getEnd());
		}
		return res;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof EventEndpoints)
		{
			return this.compareTo((EventEndpoints) o) == 0;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return displayable.hashCode() ^ time.hashCode() ^ result.hashCode() ^ (isEnd ? 0 : 1);
	}
}
