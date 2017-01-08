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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.tmathmeyer.sentinel.CalendarLogger;
import com.tmathmeyer.sentinel.models.Model;
import com.tmathmeyer.sentinel.models.client.local.BlockingProviderSingleton;

/**
 * CachingClient is a base class to enable long polling and caching on all
 * server access
 * 
 * @param <T> Model type
 * @param <SA> SerializedAction type for restoring data
 */
public abstract class NetworkCachingClient<T extends Model>
{
	protected HashMap<UUID, T> cache = new HashMap<>();
	protected boolean valid = false;
	final private Class<T> singleClass;
	private final UUID sessionID;

	/**
	 * Build a new Caching client
	 * 
	 * @param urlname the url path of the type
	 * @param serializedActionClass class instance of SA
	 * @param singleClass class of T
	 */
	public NetworkCachingClient(final Class<T> singleClass)
	{
		this.singleClass = singleClass;
		this.sessionID = UUID.randomUUID();
		Thread t = new Thread(new Runnable() {

			@Override
			public void run()
			{
				while (true)
				{
					try
					{
						for (SerializedAction<T> serializedAction : BlockingProviderSingleton.getLocalProvider().get(sessionID, singleClass))
						{
							applySerializedChange(serializedAction);
						}
					} catch (Exception ex)
					{
						invalidateCache();
						CalendarLogger.LOGGER.severe(ex.toString());
						try
						{
							Thread.sleep(20000);
						} catch (InterruptedException e)
						{

						}
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Called when a new item is pushed from remote clients
	 * 
	 * @param serializedAction the change event
	 */
	protected abstract void applySerializedChange(SerializedAction<T> serializedAction);

	/**
	 * Pulls a unique id from an object
	 * 
	 * @param obj the object to pull the ID from
	 * @return the ID
	 */
	protected abstract UUID getUuidFrom(T obj);

	/**
	 * Called when clients access this. Should change based on UI
	 * 
	 * @param obj object to filter
	 * @return if this event should be filtered out or kept (true)
	 */
	protected abstract boolean filter(T obj);

	/**
	 * Finds the element associated with the ID. Supports filters
	 * 
	 * @param id ID to find
	 * @return the element with the id given or null
	 */
	public T getByUUID(UUID id)
	{
		validateCache();
		T e = cache.get(id);
		return filter(e) ? e : null;
	}

	/**
	 * Gets all visible events
	 * 
	 * @return
	 */
	protected List<T> getAll()
	{
		validateCache();
		List<T> filteredEvents = new ArrayList<T>();

		for (T e : cache.values())
		{
			if (filter(e))
				filteredEvents.add(e);
		}
		return filteredEvents;
	}

	/**
	 * Caches the object
	 * 
	 * @param obj object to cache
	 */
	protected void cache(T obj)
	{
		cache.put(getUuidFrom(obj), obj);
	}

	/**
	 * Marks the cache as invalid to hit the server next time
	 */
	public void invalidateCache()
	{
		valid = false;
	}

	/**
	 * Pulls everything from database to revalidate the cache
	 */
	protected void validateCache()
	{
		if (valid)
			return;
		cache.clear();
		Set<T> all = BlockingProviderSingleton.getLocalProvider().getAll(sessionID, singleClass);
		for (T event : all)
		{
			cache(event);
		}
		valid = true;
	}

	/**
	 * Saves a new item to the database
	 * 
	 * @param toAdd item to add
	 * @return did we succeed?
	 */
	public boolean put(T toAdd)
	{
		cache(toAdd);
		return BlockingProviderSingleton.getLocalProvider().put(sessionID, toAdd);
	}

	/**
	 * Updates an existing item to the database, the ID's must be the same
	 * 
	 * @param toAdd item to update
	 * @return did we succeed?
	 */
	public boolean update(T toUpdate)
	{
		cache(toUpdate);
		return BlockingProviderSingleton.getLocalProvider().post(sessionID, toUpdate);
	}

	/**
	 * Deletes item from the database with the given ID
	 * 
	 * @param toAdd item to add
	 * @return did we succeed?
	 */
	public boolean delete(T toRemove)
	{
		cache(toRemove);
		return BlockingProviderSingleton.getLocalProvider().delete(sessionID, toRemove);
	}

	/**
	 * Used to serialize actions (deletes vs updates) across the network
	 */
	public static abstract class SerializedAction<T>
	{
		public T object;
		public UUID uuid;
		public boolean isDeleted;
		
		@SuppressWarnings("unchecked")
		public <X extends Model> X objectAsModel()
		{
			return (X) object;
		}
	}
}
