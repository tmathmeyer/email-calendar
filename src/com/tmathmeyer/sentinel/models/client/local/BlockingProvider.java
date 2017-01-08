package com.tmathmeyer.sentinel.models.client.local;

import java.util.Set;
import java.util.UUID;

import com.tmathmeyer.sentinel.models.Model;
import com.tmathmeyer.sentinel.models.client.net.NetworkCachingClient;

public interface BlockingProvider
{
	<T extends Model> Set<NetworkCachingClient.SerializedAction<T>> get(UUID session, Class<T> clazz);
	<T extends Model> Set<T> getAll(UUID session, Class<T> clazz);

	<T extends Model> boolean post(UUID session, T elem);

	<T extends Model> boolean delete(UUID session, T elem);

	<T extends Model> boolean put(UUID session, T elem);
	
	void flush();
}
