/*******************************************************************************
 * Copyright (c) 2013 -- WPI Suite
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andrew Hurle
 ******************************************************************************/

package com.tmathmeyer.sentinel.ui.janeway.container.toolbar;

/**
 * Implementations of this interface provide button groups to display when they
 * are relevant. For example, defect views might provide buttons to save or
 * discard changes.
 */
public interface IToolbarGroupProvider
{

	/**
	 * @return a ToolbarGroupView containing buttons to display.
	 */
	public ToolbarGroupView getGroup();
}
