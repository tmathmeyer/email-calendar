/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.ui;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.tmathmeyer.sentinel.ui.navigation.MiniCalendarHostIface;

/**
 * This is the DateTime picker that enables picking dates, and datetimes. Has
 * awesome drop-down mini-month
 */
public class DatePicker extends JPanel implements MiniCalendarHostIface
{
    private static final long serialVersionUID = 1622348186175933977L;
	private DateTimeFormatter dateFmt;
	private DateTimeFormatter timeFmt;
	private DateTimeFormatter dateTimeFmt;
	private JComboBox<String> AMPM;
	private JFormattedTextField date;
	private JFormattedTextField time;
	private DatePicker linked;
	private ArrayList<DatePickerListener> changeListeners = new ArrayList<DatePickerListener>();
	private JFrame cal;

	private boolean instance;

	public DatePicker(boolean showTime, DatePicker mLinked)
	{
		super();
		linked = mLinked;
		dateFmt = DateTimeFormat.forPattern("MM/dd/yy");
		timeFmt = DateTimeFormat.forPattern("hh:mm");
		dateTimeFmt = DateTimeFormat.forPattern("MM/dd/yy hh:mm aa");
		try
		{
			date = new JFormattedTextField(new MaskFormatter("##/##/##"));
			date.setFont(new Font("Monospaced", Font.PLAIN, 12));

			date.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent arg0)
				{
					notifyListeners();
				}

				@Override
				public void insertUpdate(DocumentEvent arg0)
				{
					notifyListeners();
				}

				@Override
				public void changedUpdate(DocumentEvent arg0)
				{
					notifyListeners();
				}
			});
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			this.add(date);
			this.add(Box.createHorizontalStrut(6));
			if (showTime)
			{
				MaskFormatter mask = new MaskFormatter("##:##");
				time = new JFormattedTextField(mask);
				time.setFont(new Font("Monospaced", Font.PLAIN, 12));
				time.getDocument().addDocumentListener(new DocumentListener() {

					@Override
					public void removeUpdate(DocumentEvent arg0)
					{
						notifyListeners();
					}

					@Override
					public void insertUpdate(DocumentEvent arg0)
					{
						notifyListeners();
					}

					@Override
					public void changedUpdate(DocumentEvent arg0)
					{
						notifyListeners();
					}
				});

				time.addFocusListener(new FocusListener() {

					@Override
					public void focusLost(FocusEvent arg0)
					{
						String input = time.getText().replace(":", "").trim();
						// If less than 4 characters are entered, intelligently
						// fill textbox to fit format
						switch (input.length())
						{
							case 4:
								if (Integer.valueOf(input) > 1259)
									time.setValue(time.getValue());
								break;

							case 3:
								time.setValue("0" + input.charAt(0) + ":" + input.substring(1));
								break;

							case 2:
								if (Integer.valueOf(input) < 13)
									time.setValue(input + ":00");
								else
									time.setValue(time.getValue());
								break;

							case 1:
								if (Integer.valueOf(input) == 0)
									time.setValue(time.getValue());
								else
									time.setValue("0" + input + ":00");
								break;

							default:
						}
					}

					// Select all when user clicks text box
					@Override
					public void focusGained(FocusEvent arg0)
					{
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run()
							{
								time.selectAll();
							}
						});
					}
				});

				this.add(time);
				AMPM = new JComboBox<>();
				AMPM.addItem("AM");
				AMPM.addItem("PM");
				AMPM.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						notifyListeners();
					}
				});
				this.add(AMPM);
			} else
			{
				MaskFormatter mask = new MaskFormatter("##:##");
				time = new JFormattedTextField(mask);
				time.setText("12:00");
				AMPM = new JComboBox<>();
				AMPM.addItem("PM");
				AMPM.setSelectedIndex(0);
			}
			date.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e)
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void keyReleased(KeyEvent e)
				{
					hideMiniCalendar();
				}

				@Override
				public void keyTyped(KeyEvent e)
				{
					// TODO Auto-generated method stub

				}

			});
			date.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent e)
				{

				}

				public void mousePressed(MouseEvent e)
				{

				}

				public void mouseReleased(MouseEvent e)
				{
					showMiniCalendar();
				}

				public void mouseEntered(MouseEvent e)
				{

				}

				public void mouseExited(MouseEvent e)
				{

				}

			});

		} catch (java.text.ParseException e)
		{
		}
	}

	/**
	 * This pulls up the mini calendar date picker for the date
	 */
	private void showMiniCalendar()
	{
		if (!instance)
		{
			JFrame cal;
			if (getDate() != null)
				cal = new PopupCalendar(getDate(), this);
			else
				cal = new PopupCalendar(DateTime.now(), this);
			cal.setUndecorated(true);
			cal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Point loc = date.getLocationOnScreen();
			loc.setLocation(loc.x, loc.y + 26);
			cal.setLocation(loc);
			cal.setSize(220, 220);
			this.cal = cal;
			this.cal.setAlwaysOnTop(true);
			this.miniCalendarInstance(true);
			this.cal.setVisible(true);
		}
	}

	/**
	 * Hides the calendar popup.
	 */
	public void hideMiniCalendar()
	{
		this.miniCalendarInstance(false);
		this.cal.setVisible(false);
	}

	/**
	 * Set this to true when there is an instance of a popup already open.
	 * 
	 * @param status
	 */
	public void miniCalendarInstance(boolean status)
	{
		this.instance = status;
	}

	/**
	 * Make the date field display the selected date.
	 */
	public void display(DateTime value)
	{
		this.miniCalendarInstance(false);
		if (this.cal != null)
		{
			this.cal.setVisible(false);
		}
		if (this.date != null)
		{
			date.setText(value.toString(dateFmt));
		}
		if (linked != null)
		{
			linked.display(value);
		}
	}

	/**
	 * Return the Date in the date field.
	 * 
	 * @return DateTime
	 */
	public DateTime getDate()
	{
		try
		{
			return dateFmt.parseDateTime(date.getText());
		} catch (IllegalArgumentException e)
		{
			return null;
		}
	}

	/**
	 * Return the Date and Time from their respective fields.
	 * 
	 * @return DateTime
	 */
	public DateTime getDateTime()
	{
		try
		{
			return dateTimeFmt.parseDateTime(date.getText() + " " + time.getText() + " " + AMPM.getSelectedItem());
		} catch (IllegalArgumentException e)
		{
			return null;
		}
	}

	/**
	 * Sets date and time of DatePicker to specified value
	 * 
	 * @param previous the DateTime object from which to obtain values
	 */
	public void setDateTime(DateTime previous)
	{
		this.date.setValue(previous.toString(dateFmt));
		this.time.setValue(previous.toString(timeFmt));
		if (previous.getHourOfDay() >= 12)
		{
			this.AMPM.setSelectedIndex(1);
		} else
		{
			this.AMPM.setSelectedIndex(0);
		}
	}

	/**
	 * Sets date of DatePicker to specified value
	 * 
	 * @param previous the DateTime object from which to obtain values
	 */
	public void setDate(DateTime previous)
	{
		this.date.setValue(previous.toString(dateFmt));
	}

	/**
	 * Sets time of DatePicker to specified value
	 * 
	 * @param previous the DateTime object from which to obtain values
	 */
	public void setTime(DateTime previous)
	{
		this.time.setValue(previous.toString(timeFmt));
		if (previous.getHourOfDay() >= 12)
		{
			this.AMPM.setSelectedIndex(1);
		} else
		{
			this.AMPM.setSelectedIndex(0);
		}
	}

	/**
	 * Add a listener to the changeListeners
	 * 
	 * @param newListener
	 */
	public void addChangeListener(DatePickerListener newListener)
	{
		changeListeners.add(newListener);
	}

	/**
	 * Date field gets focus and opens a calendar popup.
	 */
	public void requestDateFocus()
	{
		date.requestFocus();
		showMiniCalendar();
	}

	/**
	 * Request focus for date field.
	 */
	public void requestDateFocusPost()
	{
		date.requestFocus();
	}

	/**
	 * Request focus for time field.
	 */
	public void requestTimeFocus()
	{
		time.requestFocus();
	}

	public void notifyListeners()
	{
		for (DatePickerListener d : changeListeners)
		{
			d.datePickerUpdate(this.getDateTime());
		}
	}
}
