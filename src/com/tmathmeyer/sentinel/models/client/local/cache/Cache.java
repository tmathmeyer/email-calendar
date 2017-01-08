package com.tmathmeyer.sentinel.models.client.local.cache;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

public class Cache<K, V>{
	
	Map<K, AccessOrderedList<TimeOrderedList<V, K>>> cache = 
			new HashMap<K, AccessOrderedList<TimeOrderedList<V, K>>>();
	
	AccessOrderedList<TimeOrderedList<V, K>> head;
	TimeOrderedList<V, K> latest;
	TimeOrderedList<V, K> oldest;
	
	public Cache(V value)
	{
		TimeOrderedList<V, K> newLatest = new TimeOrderedList<V, K>(value, latest);
		oldest = latest;
		latest = newLatest;
		AccessOrderedList<TimeOrderedList<V, K>> newHead = new AccessOrderedList<TimeOrderedList<V, K>>(newLatest);
		head = newHead.access(head).getB();
	}
	
	
	/**
	 * insert a key value pair into the cache
	 * set it as most recently accessed as well
	 * as most recently inserted
	 * 
	 * @param key the key to store
	 * @param value the value to get
	 */
	public void put(K key, V value)
	{
		TimeOrderedList<V, K> newLatest = new TimeOrderedList<V, K>(value, latest);
		latest = newLatest;
		AccessOrderedList<TimeOrderedList<V, K>> newHead = new AccessOrderedList<TimeOrderedList<V, K>>(newLatest);
		cache.put(key, newHead);
		head = newHead.access(head).getB();
	}
	
	/**
	 * removes an item from the AccessOrderedList,
	 * but not from the TimeOrderedList
	 * 
	 * 
	 * @param key the key from the KVP to remove
	 */
	public void remove(K key)
	{
		AccessOrderedList<TimeOrderedList<V, K>> fromMap = this.cache.get(key);
		if (fromMap != null)
		{ //move it to the front so that the whole is cleaned up, then delete it from the map
			fromMap.access(head);
			cache.remove(key);
		}
	}
	
	/**
	 * Adds a new change at the top of the queue
	 * @param value the value to add to the list
	 */
	public void pushChange(V value)
	{
		TimeOrderedList<V, K> newLatest = new TimeOrderedList<V, K>(value, null);
		latest.addLater(newLatest);
		latest = newLatest;
	}
	
	/**
	 * get a value by key from the cache
	 * set it as most recently accessed
	 * but not as most recently inserted
	 * 
	 * @param key they key to pull data from the cache
	 * @return
	 */
	public V access(K key)
	{
		AccessOrderedList<TimeOrderedList<V, K>> fromMap = this.cache.get(key);
		if (fromMap != null)
		{
			Pair<TimeOrderedList<V, K>, AccessOrderedList<TimeOrderedList<V, K>>> result = fromMap.access(head);
			head = result.getB();
			return result.getA().getValue();
		}
		return null;
	}
	
	/**
	 * Brings the session key up to the latest in the queue
	 * @param key session to bring up
	 */
	public void bringUpToHead(K key)
	{
		cache.get(key).setValue(latest);
	}
	
	/**
	 * Get an iterator over the elements in the order that they were last accessed or inserted
	 * NOTE: 
	 * 	this iterator will start at the least recently used element and end at the
	 *  most recently used element. iterating over the elements in this way will not
	 *  update their order
	 * 
	 * @param key the key from the KVP to start the iterator at
	 * @return an iterator over the values in access order
	 */
	public Iterable<TimeOrderedList<V, K>> accessOrderedCallIterator(K key)
	{
		return cache.get(key);
	}
	
	/**
	 * get an iterator over the elements in the order that they were inserted
	 * NOTE:
	 *  this iterator will start at the least recently inserted element
	 *  it would be advisable, though not required, to apply each element
	 *  to the AccessOrderedList so that the tail events will simply drop
	 *  off and get scooped up by the garbage collection
	 * 
	 * @param key the key from the KVP to start the iterator at
	 * @return an iterator over the values in time order
	 */
	public TimeOrderedList<V, K> timeOrderedCallIterator(K key)
	{
		if (cache.get(key) == null)
			cache.put(key, new AccessOrderedList<TimeOrderedList<V, K>>(latest));
		return cache.get(key).access(head).getA().boundOn(this, key);
	}


	public void removeOldByMinute(int i)
	{
		MutableDateTime bef = new MutableDateTime(DateTime.now());
		bef.addMinutes(-i);
		while(this.oldest!=null && this.oldest.isBefore(bef.toDateTime()))
		{
			this.oldest = this.oldest.getLater();
			bef = new MutableDateTime(DateTime.now());
			bef.addMinutes(-i);
		}
	}
	
}
