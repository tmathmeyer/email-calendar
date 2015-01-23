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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.joda.time.DateTime;

import com.tmathmeyer.sentinel.CalendarLogger;
import com.tmathmeyer.sentinel.models.data.Commitment;
import com.tmathmeyer.sentinel.models.data.Displayable;
import com.tmathmeyer.sentinel.models.data.Event;
import com.tmathmeyer.sentinel.ui.main.MainPanel;
import com.tmathmeyer.sentinel.utils.Colors;

/**
 * UI for displaying individual events/commitments in MonthDays. These are
 * collapsed into CollapsedMonthItem's when there is not enough space
 */
public class MonthItem extends JPanel
{
    private static final long serialVersionUID = 1L;
	public static final String UNSTARTED_COMMITMENT_ICON = "/com/tmathmeyer/sentinel/img/commitment_unstarted.png";
	public static final String COMMITMENT_IN_PROGRESS_ICON = "/com/tmathmeyer/sentinel/img/commitment_in_progress.png";
	public static final String FINISHED_COMMITMENT = "/com/tmathmeyer/sentinel/img/commitment_complete.png";

	private JLabel time = new JLabel(), name = new JLabel(), arrow = new JLabel("");
	private JPanel categoryColor = new JPanel();
	private DateTime currentTime;
	private Displayable mDisplayable;

	/**
	 * Month Item constructor. When called without time, set time to current
	 * time
	 * 
	 * @param ndisp displayable to show on month item
	 */
	public MonthItem(Displayable ndisp, MonthDay parent)
	{
		this(ndisp, DateTime.now(), parent);
	}

	/**
	 * MonthItem Constructor
	 * 
	 * @param ndisp displayable to display
	 * @param day time of month item
	 */
	public MonthItem(Displayable ndisp, DateTime day, final MonthDay parent)
	{
		currentTime = day;
		this.mDisplayable = ndisp;

		// Set up Month Item Layout
		setBackground(Colors.TABLE_BACKGROUND);
		setMaximumSize(new java.awt.Dimension(32767, 24));
		setMinimumSize(new java.awt.Dimension(0, 0));
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS));

		// Time label
		time.setFont(new java.awt.Font("DejaVu Sans", Font.BOLD, 12));
		time.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 3));

		// Name label
		name.putClientProperty("html.disable", true);
		name.setText(mDisplayable.getName() + " ");
		name.setFont(new java.awt.Font("DejaVu Sans", Font.PLAIN, 12));
		name.setMinimumSize(new java.awt.Dimension(10, 15));

		// Arrow label (for multi-day events)
		arrow.setFont(new java.awt.Font("DejaVu Sans", Font.BOLD, 12));
		arrow.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		arrow.setHorizontalAlignment(JLabel.CENTER);
		arrow.setVerticalAlignment(JLabel.CENTER);

		// Get the appropriate size for the arrow container based on the max
		// possible value
		FontMetrics arrowMetrics = getFontMetrics(arrow.getFont());
		int width = arrowMetrics.stringWidth("\u2194");
		int height = arrowMetrics.getHeight();

		arrow.setPreferredSize(new Dimension(width, height));
		arrow.setMaximumSize(new Dimension(width, height));
		arrow.setMinimumSize(new Dimension(width, height));

		// Box for category color and arrow label
		categoryColor.setLayout(new GridLayout(1, 1));
		categoryColor.setPreferredSize(new Dimension(width + 3, height));
		categoryColor.setMaximumSize(new Dimension(width + 3, height));
		categoryColor.setMinimumSize(new Dimension(width + 3, height));

		/** Display displayable based on whether it is an event or a commitment */

		// If displayable is commitment, show commitment sign and name
		if (ndisp instanceof Commitment)
		{
			arrow.setForeground(Colors.COMMITMENT_NOT_STARTED);
			arrow.setText("\uFF01");

			try
			{
				// Get the appropriate image based on the commitment's status
				// and put it on the label.
				Commitment castedDisp = (Commitment) ndisp;
				Image img = ImageIO.read(getClass().getResource(UNSTARTED_COMMITMENT_ICON));

				if (Commitment.Status.IN_PROGRESS == castedDisp.getStatus())
				{
					img = ImageIO.read(getClass().getResource(COMMITMENT_IN_PROGRESS_ICON));
				} else if (Commitment.Status.COMPLETE == castedDisp.getStatus())
				{
					img = ImageIO.read(getClass().getResource(FINISHED_COMMITMENT));
				}

				arrow = new JLabel(new ImageIcon(img));
			} catch (IOException ex)
			{
				CalendarLogger.LOGGER.severe(ex.toString());
			}

			categoryColor.setBackground((Colors.TABLE_BACKGROUND));
			categoryColor.setBorder(new EmptyBorder(0, 0, 0, 0));
			time.setText(simpleTime(mDisplayable.getStart()));
		} else if (ndisp instanceof Event) // Else, show date, name, category,
										   // and whether event is multiple day
		{
			categoryColor.setBackground(((Event) ndisp).getColor());
			categoryColor.setBorder(new EmptyBorder(0, 0, 0, 0));

			// Temporary variables for shorthand
			Event tmpEvent = ((Event) ndisp);
			DateTime eventStart = tmpEvent.getStart();
			DateTime eventEnd = tmpEvent.getEnd();

			if (isStartBeforeCurrent(day, eventStart) && isEndAfterCurrent(day, eventEnd))
			{
				arrow.setText("\u2194");// the event goes before and after
			} else if ((isStartBeforeCurrent(day, eventStart)))
			{
				arrow.setText("\u2190");
			} else if (isEndAfterCurrent(day, eventEnd))
			{
				time.setText(simpleTime(mDisplayable.getStart()));
				arrow.setText("\u2192");
			} else
			{
				time.setText(simpleTime(mDisplayable.getStart()));
			}
		}

		// Add elements to UI
		categoryColor.add(arrow);
		add(categoryColor);
		add(time);
		add(name);

		// Set up click listener
		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e)
			{
				parent.dispatchEvent(e);
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				parent.dispatchEvent(e);
				MainPanel.getInstance().setSelectedDay(currentTime);
				if (e.getClickCount() > 1)
				{
					MainPanel.getInstance().editSelectedDisplayable(mDisplayable);
				} else
				{
					MainPanel.getInstance().updateSelectedDisplayable(mDisplayable);
				}
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				parent.dispatchEvent(e);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
			}
		});

		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e)
			{
				parent.dispatchEvent(e);
			}

			@Override
			public void mouseMoved(MouseEvent e)
			{
				parent.dispatchEvent(e);
			}

		});
	}

	/**
	 * Generate small version of the time
	 * 
	 * @param when
	 * @return
	 */
	public String simpleTime(DateTime when)
	{
		String ret;
		boolean pm = when.getHourOfDay() >= 12;
		if (when.getHourOfDay() == 0)
			ret = "12";
		else if (when.getHourOfDay() > 12)
			ret = Integer.toString(when.getHourOfDay() - 12);
		else
			ret = Integer.toString(when.getHourOfDay());

		if (when.getMinuteOfHour() != 0)
		{
			ret += ":";
			if (when.getMinuteOfHour() < 10)
				ret += "0";
			ret += Integer.toString(when.getMinuteOfHour());
		}

		if (pm)
			ret += "p";

		return ret;
	}

	/**
	 * Generate month item
	 * 
	 * @param elt displayable to display
	 * @param selected selected displayable by Main Panel
	 * @param day current date
	 * @return month item
	 */
	public static Component generateFrom(Displayable elt, Displayable selected, DateTime day, MonthDay parent)
	{
		MonthItem mi = new MonthItem(elt, day, parent);
		mi.setSelected(elt == selected);
		return mi;
	}

	/**
	 * Get current displayable
	 * 
	 * @return the selected displayable
	 */
	public Displayable getDisplayable()
	{
		return this.mDisplayable;
	}

	/**
	 * Sets selected month item in month view
	 * 
	 * @param selected whether this is a selected event
	 */
	public void setSelected(boolean selected)
	{
		if (selected)
			this.setBackground(Colors.SELECTED_BACKGROUND);
		else
			this.setBackground(Colors.TABLE_BACKGROUND);
	}

	/**
	 * Check if start date of event is before the specified day
	 * 
	 * @param currentDay day to compare event time with
	 * @param eventStart event time
	 * @return boolean determining whether the event start date is before
	 *         current day
	 */
	private boolean isStartBeforeCurrent(DateTime currentDay, DateTime eventStart)
	{
		if (eventStart.getYear() < currentDay.getYear())// if the year is less
														// than its always true
			return true;
		else if (eventStart.getYear() > currentDay.getYear())
			return false;
		else if (eventStart.getDayOfYear() < currentDay.getDayOfYear())// year
																	   // is the
																	   // same,
																	   // so
																	   // only
																	   // day
																	   // matters
			return true;
		return false;
	}

	/**
	 * Check if end date of event is after the specified day
	 * 
	 * @param currentDay day to compare event time with
	 * @param eventEnd event time
	 * @return boolean determining whether the event end date is after current
	 *         day
	 */
	private boolean isEndAfterCurrent(DateTime currentDay, DateTime eventEnd)
	{
		if (eventEnd.getYear() > currentDay.getYear())// if the year is less
													  // than its always true
			return true;
		else if (eventEnd.getYear() < currentDay.getYear())
			return false;
		else if (eventEnd.getDayOfYear() > currentDay.getDayOfYear())// year is
																	 // the
																	 // same, so
																	 // only day
																	 // matters
			return true;
		return false;
	}

}
