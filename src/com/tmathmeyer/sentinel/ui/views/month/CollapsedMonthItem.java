/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.ui.views.month;

import java.awt.Font;

import javax.swing.JLabel;

import com.tmathmeyer.sentinel.utils.Colors;

/**
 * UI for x more.. when there is not enough space for them to display on the
 * month view
 */
public class CollapsedMonthItem extends JLabel
{
	private static final long serialVersionUID = 1L;
	public CollapsedMonthItem(int more)
	{
		setBackground(Colors.TABLE_BACKGROUND);
		setMaximumSize(new java.awt.Dimension(32767, 24));

		setFont(new java.awt.Font("DejaVu Sans", Font.BOLD, 12));

		setText(Integer.toString(more) + " more...");
		setMinimumSize(new java.awt.Dimension(10, 15));
	}
}
