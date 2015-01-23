package com.tmathmeyer.sentinel.fs;

import java.util.Iterator;
import java.util.UUID;

import com.tmathmeyer.sentinel.ds.AVL;
import com.tmathmeyer.sentinel.ds.EmptyAVLTree;

public class MemoryUnitBlock<T extends RangeData<T>> implements UnitBlock<T>
{
    private static final long serialVersionUID = 1040776676032875682L;
	private AVL<RangeData<T>> backing = new EmptyAVLTree<>();
	private final long startUNIX;
	private final long durationUNIX;
	
	private transient MemoryUnitBlock<T> next;
	
	public MemoryUnitBlock(long start, long duration)
    {
	    startUNIX = start;
	    durationUNIX = duration;
    }
	
	@Override
	@SuppressWarnings("unchecked")
    public Iterator<T> iterator()
    {
	   return (Iterator<T>) backing.iterator();
    }

	@Override
    public boolean linkData(T t)
    {
		backing = backing.add(t);
		return true;
    }

	@Override
    public UnitBlock<T> getNextPopulated(BlockAllocator<T> blockAllocator)
    {
	    if (next == null)
	    {
	    	next = blockAllocator.register(new MemoryUnitBlock<>(getEndUnix(), durationUNIX));
	    }
	    return next;
    }

	@Override
    public long getStartUnix()
    {
	    return startUNIX;
    }

	@Override
    public long getEndUnix()
    {
	    return startUNIX + durationUNIX;
    }

	@Override
    public int compareTo(UnitBlock<T> other)
    {
	    return (int) (startUNIX - other.getStartUnix());
    }

    @Override
	@SuppressWarnings("unchecked")
    public T getByUUID(UUID uuid)
    {
	    for(RangeData<T> rd : backing)
	    {
	    	if (rd.getUUID().equals(uuid))
	    	{
	    		return (T) rd;
	    	}
	    }
	    return null;
    }

	@Override
    public boolean unlinkData(T t)
    {
	    int oldbackingSize = backing.size();
	    backing = backing.remove(t);
	    return backing.size() == oldbackingSize;
    }
}
