/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/

package com.tmathmeyer.sentinel.models.client.net;

import java.util.List;
import java.util.UUID;

import com.tmathmeyer.sentinel.models.data.Category;
import com.tmathmeyer.sentinel.ui.main.MainPanel;

public class CategoryClient extends NetworkCachingClient<Category>
{

	private static CategoryClient instance;

	/**
	 * private singleton constructor
	 */
	private CategoryClient()
	{
		super(Category.class);
	}

	/**
	 * 
	 * @return the singleton category model
	 */
	public static CategoryClient getInstance()
	{
		if (instance == null)
		{
			instance = new CategoryClient();
			instance.put(Category.GOOGLE_EVENT_DEFAULT);
		}
		return instance;
	}

	/**
	 * @return filteredCategories List of all categories from the database
	 */
	public List<Category> getAllCategories()
	{
		return getAll();
	}

	/**
	 * 
	 * @param categoryID the ID of the category
	 * @return the category with this ID
	 */
	public Category getCategoryByUUID(UUID categoryID)
	{
		return getByUUID(categoryID);
	}

	@Override
	protected void applySerializedChange(SerializedAction<Category> serializedAction)
	{
		if (serializedAction.isDeleted)
		{
			cache.remove(serializedAction.uuid);
		} else
		{
			cache.put(serializedAction.uuid, serializedAction.object);
		}
		MainPanel.getInstance().refreshCategories((com.tmathmeyer.sentinel.models.data.Category.SerializedAction) serializedAction);
	}

	@Override
	protected UUID getUuidFrom(Category obj)
	{
		return obj.getUuid();
	}

	@Override
	protected boolean filter(Category obj)
	{
		return true;
	}
}
