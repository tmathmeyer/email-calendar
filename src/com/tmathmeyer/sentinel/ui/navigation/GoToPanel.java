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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.tmathmeyer.sentinel.CalendarLogger;
import com.tmathmeyer.sentinel.ui.main.MainPanel;

public class GoToPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	final private static DateTimeFormatter gotoExampleField = DateTimeFormat.forPattern("M/d/yyyy");
	final private static DateTimeFormatter gotoField = DateTimeFormat.forPattern("M/d/yy");
	final private static DateTimeFormatter gotoFieldShort = DateTimeFormat.forPattern("M/d");
	private JLabel gotoErrorText;
	private JLabel gotoDateText;
	private JTextField gotoDateField;
	private JButton updateGotoButton;
	private DateTime currentDate;

	public GoToPanel(DateTime date)
	{

		JPanel top = new JPanel();
		JPanel bot = new JPanel();

		this.currentDate = date;
		this.setBorder(new EmptyBorder(5, 0, 0, 0));

		// Go to field
		try
		{
			this.gotoDateField = new JFormattedTextField(new MaskFormatter("##/##/####"));
		} catch (ParseException e1)
		{
			CalendarLogger.LOGGER.severe(e1.toString());
		}
		this.gotoDateField.setText(currentDate.toString(gotoExampleField));
		// Go to label
		gotoDateText = new JLabel("Go to: ");

		// Go to error label
		gotoErrorText = new JLabel(" ");
		gotoErrorText.setHorizontalAlignment(SwingConstants.CENTER);
		gotoErrorText.setForeground(Color.RED);

		// Go to button
		updateGotoButton = new JButton(">");
		updateGotoButton.setFocusable(false);
		updateGotoButton.setBackground(UIManager.getDefaults().getColor("Panel.background"));
		updateGotoButton.setBorder(new EmptyBorder(5, 5, 5, 5));

		// Set up listener
		updateGotoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				parseGoto(gotoDateField.getText());
			}
		});

		gotoDateField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				parseGoto(gotoDateField.getText());
			}
		});

		// Set up pane
		top.setLayout(new BorderLayout());
		bot.setLayout(new BorderLayout());

		this.setLayout(new BorderLayout());

		top.add(gotoDateText, BorderLayout.WEST);
		top.add(gotoDateField, BorderLayout.CENTER);
		top.add(updateGotoButton, BorderLayout.EAST);

		bot.add(gotoErrorText);
		top.add(bot, BorderLayout.SOUTH);

		this.add(top, BorderLayout.NORTH);

		// Give focus to navigation after focus is lost
		gotoDateField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e)
			{
				MainPanel.getInstance().getCalNav().grabFocus();
			}

			@Override
			public void focusGained(FocusEvent e)
			{
				// TODO Auto-generated method stub
			}

		});

	}

	/**
	 * Parses the input to the goTo field to ensure proper formatting and handle
	 * syntax errors
	 * 
	 * @param text string to parse
	 */
	public void parseGoto(String text)
	{

		DateTime dt;
		boolean isValidYear = true;

		try
		{
			dt = gotoField.parseDateTime(text);
			if (dt.getYear() < 1900 || dt.getYear() > 2100)
			{
				isValidYear = false;
				dt = null;
			}
		}

		catch (IllegalArgumentException illArg)
		{
			try
			{
				MutableDateTime mdt = gotoFieldShort.parseMutableDateTime(text);
				mdt.setYear(currentDate.getYear()); // this format does not
													// provide years. add it
				dt = mdt.toDateTime();
			} catch (IllegalArgumentException varArg)
			{
				dt = null;
			}
		}
		if (dt != null)
		{
			MainPanel.getInstance().display(dt);
			MainPanel.getInstance().refreshView();
			gotoErrorText.setText(" ");
		} else
		{
			if (isValidYear)
				gotoErrorText.setText("* Use format: mm/dd/yyyy");
			else
				gotoErrorText.setText("* Year out of range (1900-2100)");
		}
	}

	public void displayGoto(DateTime mDateTime)
	{
		gotoDateField.setText(mDateTime.toString(gotoExampleField));
	}
}
