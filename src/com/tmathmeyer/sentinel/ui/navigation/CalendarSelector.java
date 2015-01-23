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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.Box.Filler;

import com.tmathmeyer.sentinel.ui.main.MainPanel;

public class CalendarSelector extends JPanel
{
	private static final long serialVersionUID = 1L;
	JToggleButton day, week, month, year;

	public CalendarSelector()
	{
		// create components
		JToggleButton personalCalendar = new JToggleButton("Personal");
		JToggleButton teamCalendar = new JToggleButton("Team"), bothCalendar = new JToggleButton("Both");
		month = new JToggleButton("Month");
		day = new JToggleButton("Day");
		year = new JToggleButton("Year");
		week = new JToggleButton("Week");
		Filler filler1 = new Filler(new Dimension(30, 0), new Dimension(30, 0), new Dimension(30, 32767));

		// build button groups
		ButtonGroup view = new ButtonGroup();
		view.add(day);
		view.add(week);
		view.add(month);
		view.add(year);
		month.setSelected(true);
		ButtonGroup cal = new ButtonGroup();
		cal.add(personalCalendar);
		cal.add(teamCalendar);
		cal.add(bothCalendar);
		bothCalendar.setSelected(true);

		// layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(day);
		add(week);
		add(month);
		add(year);
		add(filler1);
		this.add(personalCalendar);
		this.add(teamCalendar);
		this.add(bothCalendar);

		// "logic"

		day.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				MainPanel.getInstance().viewDay();
			}
		});
		week.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				MainPanel.getInstance().viewWeek();
			}
		});
		month.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				MainPanel.getInstance().viewMonth();
			}
		});
		year.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				MainPanel.getInstance().viewYear();
			}
		});

		personalCalendar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				MainPanel mp = MainPanel.getInstance();
				mp.showPersonal = true;
				mp.showTeam = false;
				mp.refreshView();
			}
		});
		teamCalendar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				MainPanel mp = MainPanel.getInstance();
				mp.showPersonal = false;
				mp.showTeam = true;
				mp.refreshView();
			}
		});
		bothCalendar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				MainPanel mp = MainPanel.getInstance();
				mp.showPersonal = true;
				mp.showTeam = true;
				mp.refreshView();
			}
		});

		// Disable focus to allow arrow keys to respond to navigation requests
		personalCalendar.setFocusable(false);
		teamCalendar.setFocusable(false);
		bothCalendar.setFocusable(false);
		month.setFocusable(false);
		day.setFocusable(false);
		year.setFocusable(false);
		week.setFocusable(false);

	}

	public void toDay()
	{
		day.setSelected(true);
		week.setSelected(false);
		month.setSelected(false);
		year.setSelected(false);
	}

}
