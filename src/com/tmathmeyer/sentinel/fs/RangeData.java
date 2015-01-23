package com.tmathmeyer.sentinel.fs;

import java.io.Serializable;
import java.util.UUID;

public interface RangeData<T> extends Comparable<RangeData<T>>, Serializable
{
	long getStartUnix();
	long getEndUnix();
	UUID getUUID();
	T copyInternal(T t);
}
