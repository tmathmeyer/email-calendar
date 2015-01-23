package com.tmathmeyer.sentinel.ds.lru;

import java.util.HashMap;
import java.util.Map;

public class LRU<K, V>
{
	private final Map<K, LRULink<K, V>> map = new HashMap<>();
	private LRULink<K, V> mru=null, lru=null;
	private int remainingSpots;
	
	public LRU(int size)
	{
		remainingSpots = size;
	}
	
	public void put(K k, V v)
	{
		if (remainingSpots == 0)
		{
			evict();
		}
		remainingSpots --;
		LRULink<K, V> link = map.get(k);
		if (link == null)
		{
			link = new LRULink<>(k, v, mru);
		}
		else
		{
			link.fuseSurroundings();
			link.setNext(mru);
		}
		mru = link;
		findmin();
	}
	
	private void findmin()
    {
	    if (lru == null)
	    {
	    	lru = mru;
	    }
	    else
	    {
	    	while(lru.getNext() != null)
	    	{
	    		lru = lru.getNext();
	    	}
	    }
    }

	public V remove(K k)
	{
		LRULink<K, V> link = map.get(k);
		if (link != null)
		{
			link.fuseSurroundings();
			map.remove(k);
			remainingSpots ++;
			findmin();
			return link.getT();
		}
		findmin();
		return null;
		
	}
	
	public void evict()
	{
		if (lru == null)
		{
			findmin();
			return;
		}
		LRULink<K, V> temp = lru.getPrev();
		lru.fuseSurroundings();
		map.remove(lru.getK());
		lru = temp;
		findmin();
	}

	public V get(K k)
    {
		LRULink<K, V> link = map.get(k);
		if (link != null)
		{
			link.fuseSurroundings();
			link.setNext(mru);
			mru = link;
			findmin();
			return link.getT();
		}
		findmin();
		return null;
    }
	
}
