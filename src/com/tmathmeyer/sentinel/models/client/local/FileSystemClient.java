package com.tmathmeyer.sentinel.models.client.local;

import java.io.FileOutputStream;
import java.util.Set;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.tmathmeyer.sentinel.SentinelGSONParser;
import com.tmathmeyer.sentinel.fs.BlockAllocator;
import com.tmathmeyer.sentinel.fs.RangeData;

public abstract class FileSystemClient<T extends RangeData<T>>
{
	public final BlockAllocator<T> blocks = new BlockAllocator<>(604800);

	public abstract boolean filter(T t);

	public boolean put(T t)
	{
		System.out.println("adding something");
		return blocks.registerWithBlocksInRange(t);
	}

	public boolean update(T t)
	{
		System.out.println("updating something");
		return blocks.update(t);
	}

	public boolean delete(T t)
	{
		System.out.println("deleting something");
		return blocks.remove(t) != null;
	}

	public Set<T> getWithinRange(long start, long end)
	{
		System.out.println("requesting interval: "+start+" to "+end);
		Set<T> result = blocks.findAllWithinRange(start, end);
		for(T t : result) {
			System.out.println("\tretrieved items @("+t.getStartUnix()+":"+t.getEndUnix()+")");
		}
		return result;
	}

	public Set<T> getWithinRange(DateTime from, DateTime to)
	{
		return getWithinRange(from.getMillis() / 1000l, to.getMillis() / 1000l);
	}

	public void writeToFile(String filepath)
	{
		System.out.println("WRITING TO FILE BITCH");
		Gson gson = SentinelGSONParser.getGson();

		try (FileOutputStream outputStream = new FileOutputStream(filepath))
		{
			outputStream.write(gson.toJson(getSelf()).getBytes());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	protected abstract <X extends FileSystemClient<T>> X getSelf();

	public abstract Set<T> getAll();
}
