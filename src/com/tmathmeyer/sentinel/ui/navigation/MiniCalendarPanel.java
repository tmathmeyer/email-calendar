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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.joda.time.DateTime;

import com.tmathmeyer.sentinel.utils.Months;

public class MiniCalendarPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private CalendarNavigationModule calendarPreloader;
	private JComponent miniCalendar;
	private JLabel monthName;
	private DateTime currentDate;
	private MiniCalendarHostIface mainPanel;
	private boolean monthOnly = false;

	public MiniCalendarPanel(DateTime date, MiniCalendarHostIface mainPanel)
	{
		init(date, mainPanel);
	}

	public MiniCalendarPanel(DateTime date, MiniCalendarHostIface mainPanel, boolean monthonly)
	{
		monthOnly = monthonly;
		init(date, mainPanel);
	}

	// Initialize variables.
	public void init(DateTime date, MiniCalendarHostIface mainPanel)
	{
		this.setPreferredSize(new Dimension(200, 250));
		currentDate = date;
		this.mainPanel = mainPanel;
		display(date);
	}

	public void display(DateTime date)
	{
		// Title Bar Pane
		monthName = new JLabel(date.toString(Months.monthLblFormat), JLabel.CENTER);
		monthName.setFont(new Font("DejaVu Sans", Font.BOLD, 12));
		monthName.setMaximumSize(new Dimension(4400, 30));
		this.removeAll();
		this.setLayout(new BorderLayout());

		JPanel titlePane = new JPanel();
		JButton nextButton = new JButton(">");
		JButton prevButton = new JButton("<");

		titlePane.setLayout(new BoxLayout(titlePane, BoxLayout.X_AXIS));

		titlePane.add(prevButton);
		titlePane.add(nextButton);

		prevButton.setFocusable(false);
		prevButton.setBackground(UIManager.getDefaults().getColor("Panel.background"));
		nextButton.setFocusable(false);
		nextButton.setBackground(UIManager.getDefaults().getColor("Panel.background"));

		prevButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		nextButton.setBorder(new EmptyBorder(5, 5, 5, 5));

		titlePane.add(monthName);

		calendarPreloader = new CalendarNavigationModule(date, mainPanel, monthOnly);
		this.miniCalendar = this.calendarPreloader.renderComponent();

		this.add(miniCalendar, BorderLayout.CENTER);
		this.add(titlePane, BorderLayout.NORTH);

		// add event listeners
		nextButton.addActionListener(nextListener);
		prevButton.addActionListener(prevListener);

		currentDate = date;

		this.revalidate();
		this.repaint();
	}

	ActionListener prevListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e)
		{
			display(Months.prevMonth(currentDate));
		}
	};

	ActionListener nextListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e)
		{
			display(Months.nextMonth(currentDate));
		}
	};

}
