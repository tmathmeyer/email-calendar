package com.tmathmeyer.sentinel.models.client.local;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.tmathmeyer.sentinel.models.Model;
import com.tmathmeyer.sentinel.models.client.local.cache.Cache;
import com.tmathmeyer.sentinel.models.client.net.NetworkCachingClient.SerializedAction;

public class FilesystemBlockingProvider implements BlockingProvider
{
	private final long timeout;
	
	public FilesystemBlockingProvider(long timeout)
	{
		this.timeout = timeout;
	}
	
	public FilesystemBlockingProvider()
	{
		this(30000);
	}
	
	Map<Class<? extends Model>, Cache<UUID, SerializedAction<? extends Model>>> caches = 
			new HashMap<>();
	Map<Class<? extends Model>, Map<UUID, AsyncListener<? extends Model>>> waitingThreads = new HashMap<>();
	Map<Class<? extends Model>, Set<? extends Model>> allModels = new HashMap<>();
	
	private Cache<UUID, SerializedAction<? extends Model>> getCacheForType(Class<? extends Model> type)
	{
		Cache<UUID, SerializedAction<? extends Model>> res = caches.get(type);
		if (res == null)
		{
			res = new Cache<UUID, SerializedAction<? extends Model>>(null);
			caches.put(type, res);
		}
		return res;
	}
	
	private Map<UUID, AsyncListener<? extends Model>> getWaitingThreadsforType(Class<? extends Model> type)
	{
		Map<UUID, AsyncListener<? extends Model>> res = waitingThreads.get(type);
		if (res == null)
		{
			res = new HashMap<>();
			waitingThreads.put(type, res);
		}
		return res;
	}
	
	private Set<? extends Model> getModelSet(Class<? extends Model> type)
	{
		Set<? extends Model> res = allModels.get(type);
		if (res == null)
		{
			String file = System.getProperty("user.home")+"/.calendar/"+type.getSimpleName()+".set";
			res = new HashSet<>();
			try(Reader r = new FileReader(file))
			{
				Object o = new Gson().fromJson(r, Array.newInstance(type, 0).getClass());
				Object[] itr = (Object[]) o;
				for(Object e : itr)
				{
					res.add(new Caster(e).get());
				}
			}
			catch (Exception e)
			{
				
			}
			allModels.put(type, res);
		}
		return res;
	}
	
	private static class Caster
	{
		private final Object obj;
		public Caster(Object e)
		{
			this.obj = e;
		}

		@SuppressWarnings("unchecked")
		public <T extends Model> T get()
		{
			return (T) obj;
		}
		
	}
	
	private static class NoModelsException extends Exception
	{
		private static final long serialVersionUID = 1L;
	}
	
	private static interface AsyncListener<T>
	{
		void takeUpdate(Set<SerializedAction<T>> action);
		Set<SerializedAction<T>> getActions();
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Model> Set<SerializedAction<T>> listenAndBlock(UUID session, AsyncListener<T> listener, Class<T> clazz) throws NoModelsException
	{
		Cache<UUID, SerializedAction<? extends Model>> actionCache = getCacheForType(clazz);
		actionCache.removeOldByMinute(2);
		Iterable<SerializedAction<? extends Model>> iter = actionCache.timeOrderedCallIterator(session);
		if (!iter.iterator().hasNext())
		{
			getWaitingThreadsforType(clazz).put(session, listener);
			throw new NoModelsException();
		}
		else
		{
			Set<SerializedAction<T>> result = new HashSet<>();
			Iterator<SerializedAction<? extends Model>> elemIter = iter.iterator();
			elemIter.forEachRemaining(E -> {
				result.add((SerializedAction<T>) E);
			});
			return result;
		}
	}
	
	@Override
	public <T extends Model> Set<SerializedAction<T>> get(UUID session, Class<T> clazz)
	{
		final Thread self = Thread.currentThread();
		AsyncListener<T> listener = new AsyncListener<T>()
		{
			private Set<SerializedAction<T>> actions;
			@Override
			public void takeUpdate(Set<SerializedAction<T>> actions)
			{
				this.actions = actions;
				self.interrupt();
			}
			
			@Override
			public Set<SerializedAction<T>> getActions()
			{
				if (actions == null)
				{
					return Collections.emptySet();
				}
				return actions;
			}
		};
		try
		{
			return listenAndBlock(session, listener, clazz);
		}
		catch(NoModelsException nme)
		{
			try
			{
				Thread.sleep(timeout);
				getWaitingThreadsforType(clazz).remove(session);
				return listener.getActions();
			}
			catch(InterruptedException e)
			{
				return Collections.emptySet();
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private synchronized <T extends Model> void update(UUID session, Class<? extends Model> clazz, SerializedAction<? extends T> change)
	{
		getModelSet(clazz).add(change.objectAsModel());
		Cache<UUID, SerializedAction<? extends Model>> actionCache = getCacheForType(clazz);
		actionCache.pushChange(change);
		Set<SerializedAction<? extends Model>> actions = new HashSet<>();
		actions.add(change);
		for(AsyncListener<? extends Model> at : getWaitingThreadsforType(clazz).values())
		{
			Set unchecked = actions;
			actionCache.bringUpToHead(session);
			at.takeUpdate(unchecked);
		}
	}

	@Override
	public <T extends Model> boolean post(UUID session, T elem)
	{
		try
		{
			update(session, elem.getClass(), elem.getSerializedAction(false));
	
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	@Override
	public <T extends Model> boolean delete(UUID session, T elem)
	{
		try
		{
			update(session, elem.getClass(), elem.getSerializedAction(true));
	
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	@Override
	public <T extends Model> boolean put(UUID session, T elem)
	{
		try
		{
			update(session, elem.getClass(), elem.getSerializedAction(false));
	
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Model> Set<T> getAll(UUID session, Class<T> clazz)
	{
		Set<T> res = new HashSet<>();
		res.addAll((Collection<? extends T>) getModelSet(clazz));
		
		
		Cache<UUID, SerializedAction<? extends Model>> actionCache = getCacheForType(clazz);
		actionCache.removeOldByMinute(2);
		Iterable<SerializedAction<? extends Model>> iter = actionCache.timeOrderedCallIterator(session);
		if (iter.iterator().hasNext())
		{
			Iterator<SerializedAction<? extends Model>> elemIter = iter.iterator();
			elemIter.forEachRemaining(E -> {
				res.add(((SerializedAction<T>) E).object);
			});
		}

		return res;
	}

	private final class ModelSet implements JsonSerializer<ModelSet>
	{
		private final Set<? extends Model> models;
		
		public ModelSet(Set<? extends Model> models)
		{
			this.models = models;
		}
		
		public ModelSet()
		{
			this(Collections.emptySet());
		}

		@Override
		public JsonElement serialize(ModelSet ms, Type t, JsonSerializationContext jsc)
		{
			JsonArray jsonArray = new JsonArray();
	        for(Model ev : ms.models)
	        {
	        	jsonArray.add(jsc.serialize(ev));
	        }
	        return jsonArray;
		}
	}
	
	public void writeToFile(File directory)
	{
		if (!directory.isDirectory())
		{
			directory.mkdir();
		}
		GsonBuilder gsb = new GsonBuilder();
		
		gsb.registerTypeAdapter(ModelSet.class, new ModelSet());
		
		Gson serializer = gsb.create();
		
		for(Set<? extends Model> models : allModels.values())
		{
			serializer.toJson(new ModelSet(models)).getBytes();
		}
		
		for(Class<?> clazz : allModels.keySet())
		{
			String file = directory.getAbsolutePath()+"/"+clazz.getSimpleName()+".set";
			
			try (FileOutputStream outputStream = new FileOutputStream(file))
			{
				outputStream.write(serializer.toJson(new ModelSet(allModels.get(clazz))).getBytes());
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public void flush()
	{
		writeToFile(new File(System.getProperty("user.home")+"/.calendar/"));
	}
	

}
