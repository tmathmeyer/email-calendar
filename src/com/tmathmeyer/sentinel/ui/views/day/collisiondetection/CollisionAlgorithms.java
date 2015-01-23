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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;

import com.tmathmeyer.sentinel.models.data.Displayable;

/**
 * Detects collisions in the events and sets positional values for them
 * accordingly, so that we can quickly draw them on the calendar.
 */
public class CollisionAlgorithms
{
	/**
	 * turns a list of events into a list of positional DayItems
	 * 
	 * @param events the events that may or may not conflict with one another.
	 * @param displayedDay the day on which these events occur
	 * @return the list of DayItems containing events
	 */
	public static List<DayItem> createEventsReallyNicely(List<Displayable> events, DateTime displayedDay)
	{
		EventEndpoints[] particles = splitEvents(events, displayedDay);
		List<OverlappedDisplayable> travellers = collideEvents(particles);
		xSort(particles);
		Collections.sort(travellers);
		return generateUI(travellers, displayedDay);
	}

	/**
	 * get the endpoints for the events (start and end)
	 * 
	 * @param events all the events for the day
	 * @param displayedDay the day that these events are to displayed on
	 * @return an array of starting and ending points for these events
	 */
	private static EventEndpoints[] splitEvents(List<Displayable> events, DateTime displayedDay)
	{
		EventEndpoints re[] = new EventEndpoints[events.size() * 2];
		for (int i = 0; i < events.size(); i++)
		{
			re[i * 2] = new EventEndpoints(events.get(i), false, displayedDay);
			re[i * 2 + 1] = new EventEndpoints(events.get(i), true, displayedDay);
		}
		Arrays.sort(re);
		return re;
	}

	/**
	 * takes the ends of the events and uses information to determine how they
	 * overlapp; in what quantity and for what duration
	 * 
	 * @param eventEndpoints the endpoints of all the events
	 * @return a list of overlapping events
	 */
	private static List<OverlappedDisplayable> collideEvents(EventEndpoints[] eventEndpoints)
	{
		int counter = -1;
		List<OverlappedDisplayable> out = new ArrayList<OverlappedDisplayable>(eventEndpoints.length / 2);
		HashMap<Displayable, OverlappedDisplayable> active = new HashMap<Displayable, OverlappedDisplayable>();

		for (EventEndpoints c : eventEndpoints)
		{
			if (!c.isEnd())
			{
				OverlappedDisplayable t = new OverlappedDisplayable(c.getEvent());
				active.put(c.getEvent(), c.setResult(t));
				counter++;
				// max active
				for (OverlappedDisplayable who : active.values())
				{
					// count the number of hits we register
					who.setCollisions(Math.max(who.getCollisions(), counter));
					if (t != who)
					{
						who.addOverlappedEvent(t);
						t.addOverlappedEvent(who);
					}
				}
			} else
			{
				OverlappedDisplayable who = active.remove(c.getEvent());
				out.add(who);
				c.setResult(who);
				counter--;
			}
		}
		return out;
	}

	/**
	 * sorts the events by their horizontal position in the day. made static so
	 * that the list passed in can be mutated.
	 * 
	 * @param endpoints the endpoints of the events
	 */
	private static void xSort(EventEndpoints[] endpoints)
	{
		ArrayList<Boolean> state = new ArrayList<>();
		for (EventEndpoints x : endpoints)
		{
			if (!x.isEnd())
			{
				int i = 0;
				boolean foundPos = false;
				for (; i < state.size(); i++)
				{
					if (!state.get(i))
					{
						state.set(i, true);
						foundPos = true;
						break;
					}
				}
				x.getResult().setXpos(new Rational(i, 1 + x.getResult().getCollisions()));
				if (!foundPos)
				{
					state.add(true);
				}
			} else
			{
				state.set(x.getResult().getXpos().getNumerator(), false);
			}
		}
	}

	/**
	 * Take the UI of the Items
	 * 
	 * @param overlappingInformation the information regarding the position of
	 *            the events
	 * @return a list of day Items with the position set on them based on the
	 *         overlapping information
	 */
	private static List<DayItem> generateUI(List<OverlappedDisplayable> overlappingInformation, DateTime displayedDay)
	{
		List<DayItem> paintings = new ArrayList<>(overlappingInformation.size());
		for (OverlappedDisplayable t : overlappingInformation)
		{
			paintings.add(new DayItem(t, displayedDay));
		}
		return paintings;
	}

}
