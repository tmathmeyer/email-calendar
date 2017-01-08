/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.models.client.net;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.tmathmeyer.sentinel.models.Model;
import com.tmathmeyer.sentinel.models.data.Displayable;
import com.tmathmeyer.sentinel.ui.main.MainPanel;

/**
 * CachingClient specialized for Displayable items (Events and Commitments)
 * 
 * @param <T> Displayable item
 * @param <SA> SerializedAction to represent the changes pushed from the server
 */
public abstract class CachingDisplayableClient<T extends Model & Displayable>
        extends NetworkCachingClient<T>
{
	/**
	 * Creates new client
	 * 
	 * @param urlname name of the url fragment
	 * @param serializedActionClass the class object of the SA
	 * @param singleClass the class object for T
	 */
	public CachingDisplayableClient(Class<T> singleClass)
	{
		super(singleClass);
	}

	@Override
	protected void applySerializedChange(SerializedAction<T> serializedAction)
	{

		T obj = serializedAction.object;
		if (serializedAction.isDeleted)
		{
			obj = buildUuidOnlyObject(serializedAction.uuid);
			cache.remove(serializedAction.uuid);
		} else
		{
			cache.put(serializedAction.uuid, obj);
		}
		MainPanel.getInstance().getMOCA().updateDisplayable(obj, !serializedAction.isDeleted);
	}

	/**
	 * Builds new displayable from UUID for deletion
	 * 
	 * @param uuid
	 * @return new displayable item with given uuid
	 */
	protected abstract T buildUuidOnlyObject(UUID uuid);

	@Override
	protected UUID getUuidFrom(T obj)
	{
		return obj.getUuid();
	}

	protected boolean visibleCategory(T obj)
	{
		Collection<UUID> categories = MainPanel.getInstance().getSelectedCategories();
		return categories.contains(obj.getCategory());
	}

	@Override
	protected boolean filter(T obj)
	{
		return (obj.isProjectwide() ? MainPanel.getInstance().showTeam : MainPanel.getInstance().showPersonal)
		        && visibleCategory(obj);
	}

	/**
	 * Gets all visible events in the range [from..to]
	 * 
	 * @param from
	 * @param to
	 * @return list of visible events
	 */
	public Set<T> getRange(DateTime from, DateTime to)
	{
		validateCache();
		// set up to filter events based on booleans in MainPanel
		Set<T> filteredEvents = new HashSet<T>();
		final Interval range = new Interval(from, to);

		// loop through and add only if isProjectEvent() matches corresponding
		// boolean
		for (T e : cache.values())
		{
			if (range.overlaps(e.getInterval()) && filter(e))
			{
				filteredEvents.add(e);
			}
		}
		return filteredEvents;
	}

	/**
	 * gets events with specified category
	 * 
	 * @param categoryUUID the category's UUID to get
	 * @return a list containing all events with that category
	 */
	protected Set<T> getByCategory(UUID categoryUUID)
	{
		validateCache();

		Set<T> retrievedEvents = new HashSet<>();

		for (T e : cache.values())
		{
			if (filter(e) && e.getCategory() != null && e.getCategory().equals(categoryUUID))
			{
				retrievedEvents.add(e);
			}
		}

		return retrievedEvents;
	}
}
