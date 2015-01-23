/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/

package com.tmathmeyer.sentinel.models.data;

import java.awt.Color;
import java.util.UUID;

import com.tmathmeyer.sentinel.models.Model;
import com.tmathmeyer.sentinel.models.client.net.NetworkCachingClient;
import com.tmathmeyer.sentinel.models.client.net.CategoryClient;

public class Category implements Model
{
	private UUID uuid = UUID.randomUUID();
	private String name;
	private Color color;
	private boolean isProjectCategory;

	public static final Category DEFAULT_CATEGORY = new Category("Uncategorized");
	public static final Category DEFAULT_DISPLAY_CATEGORY = new Category("No Categories");
	public static final Category COMMITMENT_CATEGORY = new Category("Commitments");
	public static final Category EVENT_CATEGORY = new Category("Events");
	public static final Category GOOGLE_EVENT_DEFAULT = new Category("GCal Event", new Color(41, 82, 163));
	
	private Category(String s)
	{
		name = s;
		color = null;
		uuid = new UUID(0, 0);
	}

	public Category()
	{
	}

	/**
	 * makes a new category for google events
	 * 
	 * @param string the name
	 * @param color2 the color
	 */
	public Category(String string, Color color2)
	{
		name = string;
		color = color2;
		uuid = new UUID(0, 1);
	}

	/**
	 * 
	 * @param name the name of the category
	 * @return the category after name has been set
	 */
	public Category addName(String name)
	{
		setName(name);
		return this;
	}

	/**
	 * Sets the name of the category
	 * 
	 * @param name the name to set to the category
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the name of the category
	 * 
	 * @return name the name of the category
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * 
	 * @param color the color of the category
	 * @return the category after color has been set
	 */
	public Category addColor(Color color)
	{
		setColor(color);
		return this;
	}

	/**
	 * Sets the color of the category
	 * 
	 * @param color the color to set the category
	 */
	public void setColor(Color color)
	{
		this.color = color;
	}

	/**
	 * Gets the color of the category
	 * 
	 * @return color the color of the category
	 */
	public Color getColor()
	{
		return this.color;
	}

	/**
	 * Gets the UUID of the given category
	 * 
	 * @return categoryID the UUID of the given category
	 */
	public UUID getUuid()
	{
		return uuid;
	}

	@Override
	public void save()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void delete()
	{
		CategoryClient.getInstance().delete(this);
	}

	@Override
	public Boolean identify(Object o)
	{
		if (o instanceof Category)
		{
			return ((Category) o).name.equals(this.name);
		}
		return false;
	}

	/**
	 * Checks to see if the given category is a ProjectCategory
	 * 
	 * @return boolean if the category is a ProjectCategory
	 */

	public boolean isProjectCategory()
	{
		return isProjectCategory;
	}

	@Override
	public String toString()
	{
		return this.getName();
	}

	/**
	 * @param categoryID the categoryID to set
	 */
	public void setUuid(UUID categoryID)
	{
		this.uuid = categoryID;
	}

	public static class SerializedAction extends NetworkCachingClient.SerializedAction<Category>
	{
		public SerializedAction(Category e, UUID eventID, boolean b)
		{
			object = e;
			uuid = eventID;
			isDeleted = b;
		}
	}
}
