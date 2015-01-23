/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.ui.views.day;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.tmathmeyer.sentinel.utils.Colors;

public class DayGridLabel extends JPanel
{
	private static final long serialVersionUID = 1L;
	public DayGridLabel()
	{
		this.setLayout(new GridLayout(24, 1));
		this.setBackground(Colors.TABLE_BACKGROUND);
		this.setPreferredSize(new Dimension(60, 1440));
		this.setMaximumSize(new Dimension(60, 1440));

		for (int i = 0; i < 24; i++)
		{
			int hour = i % 12 == 0 ? 12 : i % 12;
			String padding = (hour < 10) ? "  " : " ";
			StringBuilder currtime = new StringBuilder();
			currtime.append(" ").append(hour);
			if (i <= 11)
				currtime.append("am");
			else
				currtime.append("pm");
			JLabel text = new JLabel(currtime.append(padding).toString());
			text.setBackground(Colors.TABLE_BACKGROUND);
			text.setBorder(BorderFactory.createMatteBorder(1, 1, i == 23 ? 1 : 0, 1, Colors.BORDER));
			this.add(text);
		}
	}
}
