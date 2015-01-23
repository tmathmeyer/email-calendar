/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.ui.main;

import com.tmathmeyer.sentinel.ui.janeway.container.toolbar.DefaultToolbarView;

public class RibbonToolbar extends DefaultToolbarView
{
	private static final long serialVersionUID = 1L;
	public DisplayableToolbarGroup eventButtonGroup;
	public CategoryToolbarGroup categoryButtonGroup;

	public RibbonToolbar(final MainPanel mMainPanel, boolean visible)
	{
		eventButtonGroup = new DisplayableToolbarGroup(mMainPanel);
		categoryButtonGroup = new CategoryToolbarGroup(mMainPanel);

		this.setFocusable(false);

		this.addGroup(eventButtonGroup);
		this.addGroup(categoryButtonGroup);

		eventButtonGroup.disableRemoveEventButton();
	}
}
