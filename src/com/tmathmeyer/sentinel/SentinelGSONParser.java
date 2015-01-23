package com.tmathmeyer.sentinel;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.tmathmeyer.sentinel.models.client.local.EventClient;
import com.tmathmeyer.sentinel.models.data.Event;

public class SentinelGSONParser
{
	static class EventClientInstanceCreator implements JsonSerializer<EventClient>
	{

		@Override
        public JsonElement serialize(EventClient e, Type t, JsonSerializationContext jsc)
        {
	        JsonArray jsonArray = new JsonArray();
	        for(Event ev : e.getAll())
	        {
	        	jsonArray.add(jsc.serialize(ev));
	        }
	        return jsonArray;
        }
		
	}
	
	public static Gson getGson()
	{
		GsonBuilder gsb = new GsonBuilder();
		
		gsb.registerTypeAdapter(EventClient.class, new EventClientInstanceCreator());
		
		return gsb.create();
	}
}
