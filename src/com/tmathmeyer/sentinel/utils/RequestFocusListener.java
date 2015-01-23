/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.utils;

import javax.swing.*;
import javax.swing.event.*;

/**
 * Convenience class to request focus on a component.
 *
 * When the component is added to a realized Window then component will request
 * focus immediately, since the ancestorAdded event is fired immediately.
 *
 * When the component is added to a non realized Window, then the focus request
 * will be made once the window is realized, since the ancestorAdded event will
 * not be fired until then.
 *
 * Using the default constructor will cause the listener to be removed from the
 * component once the AncestorEvent is generated. A second constructor allows
 * you to specify a boolean value of false to prevent the AncestorListener from
 * being removed when the event is generated. This will allow you to reuse the
 * listener each time the event is generated.
 * 
 * url: http://www.camick.com/java/source/RequestFocusListener.java
 */
public class RequestFocusListener implements AncestorListener
{
	private boolean removeListener;

	/*
	 * Convenience constructor. The listener is only used once and then it is
	 * removed from the component.
	 */
	public RequestFocusListener()
	{
		this(true);
	}

	/*
	 * Constructor that controls whether this listen can be used once or
	 * multiple times.
	 * 
	 * @param removeListener when true this listener is only invoked once
	 * otherwise it can be invoked multiple times.
	 */
	private RequestFocusListener(boolean removeListener)
	{
		this.removeListener = removeListener;
	}

	@Override
	public void ancestorAdded(AncestorEvent e)
	{
		JComponent component = e.getComponent();
		component.requestFocusInWindow();

		if (removeListener)
			component.removeAncestorListener(this);
	}

	@Override
	public void ancestorMoved(AncestorEvent e)
	{
	}

	@Override
	public void ancestorRemoved(AncestorEvent e)
	{
	}
}
