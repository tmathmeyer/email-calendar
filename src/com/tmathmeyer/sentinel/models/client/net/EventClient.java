package com.tmathmeyer.sentinel.models.client.net;

import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import com.tmathmeyer.sentinel.models.data.Event;
import com.tmathmeyer.sentinel.ui.main.MainPanel;

public class EventClient extends CachingDisplayableClient<Event, Event.SerializedAction>
{
	private static EventClient instance;

	protected EventClient()
	{
		super("events", Event.SerializedAction[].class, Event[].class);
	}
	
	@Override
	protected Event buildUuidOnlyObject(UUID uuid)
	{
		Event e = new Event();
		e.setUuid(uuid);
		return e;
	}

	public static EventClient getInstance()
	{
		if (instance == null)
		{
			instance = new EventClient();
		}
		return instance;
	}

	/**
	 * Gets all visible events in the range [from..to]
	 * @param from
	 * @param to
	 * @return list of visible events
	 */
	public List<Event> getEvents(DateTime from, DateTime to)
	{
		return getRange(from, to);
	}

	/**
	 * Gets event by id
	 * @param id id of the event to get
	 * @return event with given id
	 */
	public Event getEventById(UUID id)
	{
		return getByUUID(id);
	}

	/**
	 * Gets all events
	 * @return all events in database
	 */
	public List<Event> getAllEvents()
	{
		return getAll();
	}
	
	/**
	 * Get all events by category
	 * @param id id of the category
	 * @return all events with given category id
	 */
	public List<Event> getEventsByCategory(UUID id)
	{
		return getByCategory(id);
	}
	
	@Override
	protected boolean visibleCategory(Event obj)
	{
		return MainPanel.getInstance().showEvents() && super.visibleCategory(obj);
	}
}