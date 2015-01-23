package com.tmathmeyer.sentinel.models.client.local;

import java.io.FileReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.tmathmeyer.sentinel.SentinelGSONParser;
import com.tmathmeyer.sentinel.models.data.Event;

public class EventClient extends FileSystemClient<Event> implements Serializable
{
	private static final long serialVersionUID = -5430654606975346330L;
	private static EventClient INSTANCE = null;

	@Override
	public boolean filter(Event t)
	{
		return true;
	}

	public static EventClient getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new EventClient();
			try
			(
				Reader r = new FileReader(System.getProperty("user.home")+"/.sentinel-events");
			)
			{
				 Event[] events = SentinelGSONParser.getGson().fromJson(r, Event[].class);
				 for(Event e : events)
				 {
					 INSTANCE.put(e);
				 }
			}
			catch (Exception e)
			{
				
			}
		}

		return INSTANCE;
	}

	public List<Event> getEventsByCategory(UUID categoryID)
	{
		List<Event> events = new ArrayList<>();

		blocks.doWithAll(new Consumer<Event>() {
			public void accept(Event e)
			{
				if (e.getCategory().equals(categoryID))
				{
					events.add(e);
				}
			}
		});

		return events;
	}
	
	@Override
	public Set<Event> getAll()
	{
		Set<Event> all = new HashSet<Event>();
		blocks.doWithAll(new Consumer<Event>(){

			@Override
            public void accept(Event arg0)
            {
	            all.add(arg0);
            }
			
		});
		return all;
	}

    @Override
	@SuppressWarnings("unchecked")
    protected <X extends FileSystemClient<Event>> X getSelf()
    {
	    return (X) this;
    }
}
