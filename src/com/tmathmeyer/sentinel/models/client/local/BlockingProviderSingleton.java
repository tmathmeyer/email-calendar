package com.tmathmeyer.sentinel.models.client.local;

public class BlockingProviderSingleton
{
	private final BlockingProvider provider;
	
	private BlockingProviderSingleton(BlockingProvider provider)
	{
		this.provider = provider;
	}
	
	
	private static BlockingProviderSingleton instance;
	public static BlockingProvider getLocalProvider()
	{
		if (instance == null)
		{
			instance = new BlockingProviderSingleton(new FilesystemBlockingProvider());
		}
		return instance.provider;
	}
}
