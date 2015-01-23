/*******************************************************************************
 * Copyright (c) 2012 -- WPI Suite
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    twack
 *******************************************************************************/

package com.tmathmeyer.sentinel.models;

/**
 * Model : The interface for all data models. Prototypes methods for model
 * handling and serializing.
 * 
 * @author twack
 * @author bgaffey
 *
 */
public interface Model
{

	public void save();

	public void delete();

	/**
	 * toString : enforce an override. May simply call serializeToJSON.
	 * 
	 * @return The string representation of this Model
	 */
	public String toString();

	/**
	 * identify: true if the argument o is equal this object's unique identifier
	 * or this object this method was created for use with the mock database
	 * 
	 * implementations overriding this method should check if o is either a
	 * unique identifier, or an instance of this class if o is an instance of
	 * this class, this method should check if it contains the same unique
	 * identifier
	 * 
	 * @param o - a unique identifier belonging to an object
	 * @return true if the o is equal to this Model's unique identifier, else
	 *         false
	 */
	public Boolean identify(Object o);
}