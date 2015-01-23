package com.tmathmeyer.sentinel.fs;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.tmathmeyer.sentinel.ds.AVL;
import com.tmathmeyer.sentinel.ds.EmptyAVLTree;
import com.tmathmeyer.sentinel.ds.lru.LRU;

public class BlockAllocator<T extends RangeData<T>> implements Serializable
{
    private static final long serialVersionUID = -1655969961416767177L;
	private AVL<UnitBlock<T>> blockset = new EmptyAVLTree<>();
	private transient LRU<UUID, T> sizedCache;
	private final long stdUnixDur;
	
	public BlockAllocator(long standardDuration)
    {
	    stdUnixDur = standardDuration;
    }
	
	public <X extends UnitBlock<T>> X register(X block)
	{
		blockset = blockset.add(block);
		return block;
	}
	
	public boolean registerWithBlocksInRange(T t)
	{
		UnitBlock<T> current = findBlockContaining(t.getStartUnix());
		if (sizedCache == null)
		{
			sizedCache = new LRU<>(10);
		}
		sizedCache.put(t.getUUID(), t);
		do
		{
			if (!current.linkData(t))
			{
				return false;
			}
			current = current.getNextPopulated(this);
		} while(t.getEndUnix() > current.getEndUnix());
		
		return true;
	}
	
	public boolean update(T t)
	{
		sizedCache.put(t.getUUID(), t);
		return registerWithBlocksInRange(remove(getByUUID(t.getUUID())).copyInternal(t));
	}
	
	public T remove(T t)
	{
		if (sizedCache == null)
		{
			sizedCache = new LRU<>(10);
		}
		sizedCache.remove(t.getUUID());
		UnitBlock<T> current = findBlockContaining(t.getStartUnix());
		while(t.getEndUnix() >= current.getEndUnix())
		{
			current.unlinkData(t);
			current = current.getNextPopulated(this);
		}
		
		return t;
	}
	
	public T getByUUID(UUID uuid)
	{
		if (sizedCache == null)
		{
			sizedCache = new LRU<>(10);
		}
		T t = sizedCache.get(uuid);
		if (t == null)
		{
			for(UnitBlock<T> ub : blockset)
			{
				T tt = ub.getByUUID(uuid);
				if (tt != null)
				{
					sizedCache.put(uuid, tt);
					return tt;
				}
			}
			return null;
		}
		return t;
			
	}
	
	public void doWithInRange(long startunix, long endunix, Consumer<T> consumer)
	{
		UnitBlock<T> current = findBlockContaining(startunix);
		
		while(endunix >= current.getEndUnix())
		{
			for(T t : current)
			{
				if (t.getEndUnix() <= startunix)
				{
					continue;
				}
				if (t.getStartUnix() > endunix)
				{
					return;
				}
				consumer.accept(t);
			}
			current = current.getNextPopulated(this);
		}
	}
	
	public void doWithAll(Consumer<T> consumer)
	{	
		for(UnitBlock<T> current : blockset)
		{
			for(T t : current)
			{
				consumer.accept(t);
			}
		}
	}
	
	public Set<T> findAllWithinRange(long start, long end)
	{
		Set<T> result = new HashSet<>();
		doWithInRange(start, end, new Consumer<T>(){
			@Override
            public void accept(T t)
            {
	            result.add(t);
            }
		});
		return result;
	}
	
	private UnitBlock<T> findBlockContaining(long startUnix)
	{
		AVL<UnitBlock<T>> dup = blockset;
		while(!dup.isEmpty())
		{
			if (startUnix < dup.getNode().getStartUnix())
			{
				dup = dup.getLeft();
			}
			else if (startUnix >= dup.getNode().getEndUnix())
			{
				dup = dup.getRight();
			}
			else
			{
				return dup.getNode();
			}
		}
		
		return register(new MemoryUnitBlock<>(round(startUnix), stdUnixDur));
	}
	
	private long round(long start)
	{
		return start - start%stdUnixDur;
	}
}
