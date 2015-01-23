/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.models;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import com.tmathmeyer.sentinel.ui.main.MainPanel;

/*
 * This handles enabling and disabling the tooltips when the mouse hovers over a button
 */
public class ToolTipListener implements MouseListener
{
	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		if (SwingUtilities.getWindowAncestor(MainPanel.getInstance()).isActive())// only
																				 // enable
																				 // tool
																				 // tips
																				 // is
																				 // its
																				 // the
																				 // active
																				 // window
		{
			ToolTipManager.sharedInstance().setEnabled(true);
		} else
		{
			ToolTipManager.sharedInstance().setEnabled(false);
		}

		ToolTipManager.sharedInstance().setDismissDelay(1500);

	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
	}

}
