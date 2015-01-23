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

import com.tmathmeyer.sentinel.models.data.Category;

public interface ICategoryRegister
{
	/**
	 * Forces update for cache to refresh categories
	 */
	public void fire(Category.SerializedAction sa);
}
