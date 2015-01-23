package com.tmathmeyer.sentinel.utils.cache;

import java.util.Iterator;

public class AccessOrderedList<V> implements Iterable<V>
{

	AccessOrderedList<V> above; // above = the elements accessed after this
	AccessOrderedList<V> below; // below = the elements accessed before this

	V element;

	/**
	 * 
	 * @param value the value at this place in the list
	 */
	public AccessOrderedList(V value)
	{
		this.element = value;
	}

	/**
	 * Sets the current element
	 * 
	 * @param value new value
	 */
	public void setValue(V value)
	{
		element = value;
	}

	/**
	 * 
	 * @param currentLeadingEdge the current head of the list that this will be
	 *            added to
	 * @return a pair of our element and ourself
	 */
	public Pair<V, AccessOrderedList<V>> access(AccessOrderedList<V> currentLeadingEdge)
	{
		this.mergeSurrounding(); // removes it from place
		this.above = null; // sets it as top;
		this.mergeTwo(this, currentLeadingEdge); // fuses it with top

		return new Pair<V, AccessOrderedList<V>>(this.element, this);
	}

	/**
	 * merges the surrounding nodes together, effectively popping this one out
	 * of the list
	 */
	public void mergeSurrounding()
	{
		this.mergeTwo(this.above, this.below);
	}

	/**
	 * 
	 * @param above2 the node that goes on top
	 * @param below2 the node that goes on bottom
	 */
	public void mergeTwo(AccessOrderedList<V> above2, AccessOrderedList<V> below2)
	{
		if (above2 == null && below2 == null)
		{
			return;
		}
		if (above2 == null)
		{
			below2.above = null;
			return;
		}
		if (below2 == null)
		{
			above2.below = null;
			return;
		}
		above2.below = below2;
		below2.above = above2;
	}

	@Override
	public Iterator<V> iterator()
	{
		return new AccessOrderedListIterator<V>(this);
	}

	private class AccessOrderedListIterator<K> implements Iterator<K>
	{
		AccessOrderedList<K> current;

		/**
		 * 
		 * @param accessOrderedList the order list at which to start our
		 *            iterator
		 */
		public AccessOrderedListIterator(AccessOrderedList<K> accessOrderedList)
		{
			this.current = accessOrderedList;
		}

		@Override
		public boolean hasNext()
		{
			return this.current != null;
		}

		@Override
		public K next()
		{
			K elem = this.current.element;
			remove();
			return elem;
		}

		@Override
		public void remove()
		{
			this.current = this.current.above;
		}

	}

}
