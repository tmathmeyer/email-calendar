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

import java.util.ArrayList;
import java.util.List;

import com.tmathmeyer.sentinel.models.data.Displayable;

public class OverlappedDisplayable implements Comparable<OverlappedDisplayable>
{
	private int collisionCount = 0;
	private Displayable displayable;
	private Rational xpos = new Rational(1, 1);
	private List<OverlappedDisplayable> collisions = new ArrayList<>();

	/**
	 * 
	 * @param Displayable the Displayable that this encapsulates
	 */
	public OverlappedDisplayable(Displayable displayable)
	{
		this.displayable = displayable;
	}

	/**
	 * 
	 * @return the maximum number of collisions this Displayable has at any
	 *         given time
	 */
	public int getCollisions()
	{
		return collisionCount;
	}

	/**
	 * 
	 * @param collisions set the max num of collisions
	 */
	public void setCollisions(int collisions)
	{
		this.collisionCount = collisions;
	}

	/**
	 * 
	 * @return get this Displayable's horizontal position
	 */
	public Rational getXpos()
	{
		return xpos;
	}

	/**
	 * 
	 * @param xpos set this Displayable's horizontal position
	 */
	public void setXpos(Rational xpos)
	{
		this.xpos = xpos;
	}

	/**
	 * 
	 * @return the Displayable we encapsulate
	 */
	public Displayable getEvent()
	{
		return displayable;
	}

	/**
	 * 
	 * @param overlapped add an overlapping Displayable to the list of events
	 *            that this overlaps with
	 */
	public void addOverlappedEvent(OverlappedDisplayable overlapped)
	{
		if (!collisions.contains(overlapped))
			collisions.add(overlapped);
	}

	/**
	 * 
	 * @return all of the events that we overlap with
	 */
	public List<OverlappedDisplayable> getOverlappedEvents()
	{
		return collisions;
	}

	@Override
	public int compareTo(OverlappedDisplayable toCompare)
	{
		int res = Integer.compare(toCompare.xpos.toInt(10000), xpos.toInt(10000));
		if (res == 0)
		{
			return toCompare.displayable.getStart().compareTo(displayable.getStart());
		}
		return res;
	}

	@Override
	public String toString()
	{
		return super.toString() + "{" + getEvent().getName() + "@" + getEvent().getStart().toString() + " collisions: "
		        + collisionCount + "}";
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof OverlappedDisplayable)
		{
			return this.compareTo((OverlappedDisplayable) o) == 0;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return displayable.hashCode() ^ xpos.hashCode() ^ collisions.hashCode() ^ collisionCount;
	}
}
