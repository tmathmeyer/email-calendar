/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.ui.navigation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.joda.time.DateTime;

import com.tmathmeyer.sentinel.AbstractCalendar;

public class MainCalendarNavigation extends JPanel
{
	private static final long serialVersionUID = 1L;
	private JButton nextButton = new JButton(">");
	private JButton previousButton = new JButton("<");
	private JButton todayButton = new JButton("Today");
	private JPanel navigationButtonPanel = new JPanel();
	private AbstractCalendar currentCalendar;

	public MainCalendarNavigation(JComponent parent, final AbstractCalendar mAbstractCalendar)
	{

		// Disable focus to allow arrow keys to respond to navigation requests
		nextButton.setFocusable(false);
		previousButton.setFocusable(false);
		todayButton.setFocusable(false);
		navigationButtonPanel.setFocusable(false);

		navigationButtonPanel.setLayout(new BoxLayout(navigationButtonPanel, BoxLayout.X_AXIS));
		navigationButtonPanel.add(todayButton);
		navigationButtonPanel.add(Box.createHorizontalStrut(6));
		navigationButtonPanel.add(previousButton);
		navigationButtonPanel.add(nextButton);

		// Set current calendar
		this.currentCalendar = mAbstractCalendar;

		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				currentCalendar.next();
			}
		});
		previousButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				currentCalendar.previous();

			}
		});
		todayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				currentCalendar.display(DateTime.now());
			}
		});

		// Set up UI
		this.setLayout(new BorderLayout());
		this.add(navigationButtonPanel, BorderLayout.WEST);

		// Listens for arrow keyboard input
		this.setFocusable(true);
		this.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "prevMonth");
		this.getActionMap().put("prevMonth", prevMonth);
		this.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "nextMonth");
		this.getActionMap().put("nextMonth", nextMonth);

	}

	/**
	 * Update the calendar referenced by the calendar navigation panel
	 * 
	 * @param newCalendar calendar to reference
	 */
	public void updateCalendar(AbstractCalendar newCalendar)
	{
		this.currentCalendar = newCalendar;
	}

	/**
	 * Action to execute upon press of left arrow key
	 */
	private Action prevMonth = new AbstractAction("prevMonth") {
        private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e)
		{
			currentCalendar.previous();
		}
	};

	/**
	 * Action to execute upon press of right arrow key
	 */
	private Action nextMonth = new AbstractAction("prevMonth") {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e)
		{
			currentCalendar.next();
		}
	};
}
