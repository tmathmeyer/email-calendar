package com.tmathmeyer.sentinel.models.client.net;

import java.util.ArrayList;
public class ServerClient
{
	
	public static <T> ArrayList<T> get(String path, final Class<?> classType, String... args)
	{
		return new ArrayList<T>();
	}

	public static <T> boolean post(String path, String... args)
	{
		return true;
	}

	public static <T> boolean delete(String path, String... args)
	{
		return true;
	}

	public static <T> boolean put(String path, String... args)
	{
		return true;
	}
}
