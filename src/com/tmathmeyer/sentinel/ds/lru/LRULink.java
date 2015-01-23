package com.tmathmeyer.sentinel.ds.lru;

public class LRULink<K, T>
{
	private LRULink<K, T> killbefore, killafter;
	private final T t;
	private final K k;
	
	public LRULink(K k, T t, LRULink<K, T> next)
	{
		this.t = t;
		this.k = k;
		this.setNext(next);
	}

	public LRULink<K, T> getNext()
    {
	    return killbefore;
    }

	public void setNext(LRULink<K, T> next)
    {
	    this.killbefore = next;
    }

	public LRULink<K, T> getPrev()
    {
	    return killafter;
    }

	public void setPrev(LRULink<K, T> prev)
    {
	    this.killafter = prev;
    }

	public void fuseSurroundings()
    {
		if (getPrev()!=null) {
	    	getPrev().setNext(getNext());
	    }
		if (getNext()!=null) {
			getNext().setPrev(getPrev());
	    }
		
		setPrev(null);
		setNext(null);
    }

	public T getT()
    {
	    return t;
    }

	public K getK()
    {
	    return k;
    }
	
}
