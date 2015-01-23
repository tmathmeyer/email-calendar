package com.tmathmeyer.sentinel.fs;

import java.io.Serializable;
import java.util.UUID;

public interface UnitBlock<T extends RangeData<T>> extends Iterable<T>, Comparable<UnitBlock<T>>, Serializable
{
	boolean linkData(T t);
	boolean unlinkData(T t);
	UnitBlock<T> getNextPopulated(BlockAllocator<T> blockAllocator);
	long getStartUnix();
	long getEndUnix();
	T getByUUID(UUID uuid);
}
