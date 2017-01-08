package com.tmathmeyer.sentinel.models.client.local;

import static org.junit.Assert.*;

import java.io.File;
import java.util.UUID;

import org.junit.Test;

import com.tmathmeyer.sentinel.models.data.Commitment;
import com.tmathmeyer.sentinel.models.data.Event;

public class BLockingProviderTest {

	@Test
	public void test()
	{
		BlockingProvider bp = new FilesystemBlockingProvider(1000);
		
		UUID session1 = UUID.randomUUID();
		UUID session2 = UUID.randomUUID();
		
		Event e1 = new Event().addName("This is an Event!");
		Event e2 = new Event().addName("This is another Event!");
		
		bp.get(session2, Event.class);
		
		bp.put(session1, e1);
		bp.put(session1, e2);

		assertEquals(bp.get(session1, Event.class).size(), 0);
		assertEquals(bp.get(session2, Event.class).size(), 2);
		assertEquals(bp.get(session2, Event.class).size(), 0);
	}
	
	
	public void testFile()
	{
		FilesystemBlockingProvider bp = new FilesystemBlockingProvider(1000);
		
		UUID session1 = UUID.randomUUID();
		UUID session2 = UUID.randomUUID();
		
		Event e1 = new Event().addName("This is an Event!");
		Event e2 = new Event().addName("This is another Event!");
		
		bp.get(session2, Event.class);
		
		bp.put(session1, e1);
		bp.put(session1, e2);
		
		File f = new File(System.getProperty("user.home")+"/.calendar");
		
		bp.writeToFile(f);
	}
	
	@Test
	public void testGet()
	{
		FilesystemBlockingProvider bp = new FilesystemBlockingProvider(1000);
		
		UUID session1 = UUID.randomUUID();
		
		assertEquals(bp.getAll(session1, Event.class).size(), 2);
		assertEquals(bp.getAll(session1, Commitment.class).size(), 0);
	}

}
