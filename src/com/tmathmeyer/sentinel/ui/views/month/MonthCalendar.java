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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.IllegalComponentStateException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;

import com.tmathmeyer.sentinel.AbstractCalendar;
import com.tmathmeyer.sentinel.DayStyle;
import com.tmathmeyer.sentinel.models.client.local.EventClient;
import com.tmathmeyer.sentinel.models.client.net.CommitmentClient;
import com.tmathmeyer.sentinel.models.data.Displayable;
import com.tmathmeyer.sentinel.ui.main.MainPanel;
import com.tmathmeyer.sentinel.utils.Colors;
import com.tmathmeyer.sentinel.utils.HSLColor;
import com.tmathmeyer.sentinel.utils.Lambda;
import com.tmathmeyer.sentinel.utils.Months;

/**
 * Main Month view.
 */
public class MonthCalendar extends AbstractCalendar
{
	private static final long serialVersionUID = 1L;
	private JPanel inside = new JPanel(), top = new JPanel(), mainCalendarView = new JPanel(),
	        calendarTitlePanel = new JPanel();

	private JLabel monthLabel = new JLabel();
	private DateTime time;
	private MainPanel mainPanel;
	private Displayable lastSelection;
	private DateTime firstOnMonth;
	private DateTime lastOnMonth;
	private HashMap<Integer, MonthDay> days = new HashMap<Integer, MonthDay>();
	private HashMap<UUID, Displayable> emap = new HashMap<>();

	private boolean escaped; // has the user dragged an event off it's starting
							 // day?
	private boolean external; // has the user dragged an event off of the
							  // calendar?
	private boolean tooltip;

	public MonthCalendar(DateTime on)
	{
		this.mainPanel = MainPanel.getInstance();
		this.time = on;

		this.setLayout(new BorderLayout());
		this.add(calendarTitlePanel, BorderLayout.NORTH);

		// add the UI
		generateDays(new MutableDateTime(on));
		generateHeaders(new MutableDateTime(on));

		// add drag and drop listeners
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e)
			{
				MainPanel p = MainPanel.getInstance();
				Displayable d = p.getSelectedEvent();

				MonthDay md = getMonthDayAtCursor();

				if (d != null && md != null)
				{
					md.setBackground(new Color(255, 255, 200));
					d.setTime(md.getDay());
				} else
				{
					escaped = false;
					e.consume();
					repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e)
			{
				if (tooltip)
				{
					repaint();
				}
				setEscaped(false);
			}

		});

		// add drag and drop listeners
		addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0)
			{
			}

			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				external = false;
			}

			@Override
			public void mouseExited(MouseEvent arg0)
			{
				external = true;
			}

			@Override
			public void mousePressed(MouseEvent arg0)
			{
				external = false;
			}

			@Override
			public void mouseReleased(MouseEvent arg0)
			{
				if (external)
				{
					System.out.println("drop the events");
					display(null);
					repaint();
					setEscaped(false);
				} else
				{
					Displayable selected = MainPanel.getInstance().getSelectedEvent();
					if (selected != null && escaped)
					{
						display(selected.getStart());
					}
				}
			}

		});
	}

	/**
	 * Finds the MonthDay UI object under the cursor
	 * 
	 * @return MonthDay UI object under cursor
	 */
	public MonthDay getMonthDayAtCursor()
	{
		try
		{
			Point l = MouseInfo.getPointerInfo().getLocation();
			Point pp = inside.getLocationOnScreen();
	
			int x = l.x - pp.x;
			int y = l.y - pp.y;
	
			Component jc = inside.getComponentAt(x, y);
			if (jc instanceof MonthDay)
			{
				return (MonthDay) jc;
			}
		}
		catch(IllegalComponentStateException icse)
		{
			// sometimes the UI updates faster then the event propagates -- catch it
		}
		return null;
	}

	/**
	 * 
	 * @param fom the mutable date time
	 */
	public void generateHeaders(MutableDateTime fom)
	{
		// Set up label for month title
		monthLabel.setHorizontalAlignment(JLabel.CENTER);
		monthLabel.setFont(new java.awt.Font("DejaVu Sans", Font.BOLD, 25));

		// Set up the container title panel (only holds monthLabel for now)
		calendarTitlePanel.setLayout(new BorderLayout());
		calendarTitlePanel.add(monthLabel, BorderLayout.CENTER);

		// layout code
		mainCalendarView.setBackground(Colors.TABLE_BACKGROUND);
		mainCalendarView.setLayout(new BorderLayout());
		top.setLayout(new GridLayout(1, 7));

		mainCalendarView.add(top, BorderLayout.NORTH);
		mainCalendarView.add(inside, BorderLayout.CENTER);

		this.add(mainCalendarView, BorderLayout.CENTER);
		this.add(calendarTitlePanel, BorderLayout.NORTH);
		// end layout code

		fom.setDayOfMonth(1);
		fom.setMillisOfDay(0);
		int first = (fom.getDayOfWeek() % 7);
		fom.addDays(-first);

		// generate days on top
		for (int i = 0; i < 7; i++)
		{
			JLabel jl = new JLabel(fom.dayOfWeek().getAsText());
			jl.setHorizontalAlignment(SwingConstants.CENTER);
			fom.addDays(1);
			top.add(jl);
		}
	}

	/**
	 * clears and sets a list of events
	 * 
	 * @param events
	 */
	public void setDisplayables(List<Displayable> disps)
	{
		clearDisplayables();
		for (Displayable displayable : disps)
		{
			addDisplayable(displayable);
		}
	}

	/**
	 * Adds a commitment or event to the calendar
	 * 
	 * @param disp Displayable to add
	 */
	public void addDisplayable(final Displayable disp)
	{
		emap.put(disp.getUuid(), disp);

		traverseDisplayable(disp, new Lambda<MonthDay>() {

			@Override
			public void call(MonthDay md)
			{
				md.addDisplayable(disp);
			}
		});
	}

	/**
	 * Adds or deletes a displayable from the days its occupies
	 * 
	 * @param e
	 * @param add
	 */
	protected void traverseDisplayable(Displayable e, Lambda<MonthDay> func)
	{
		// get and normalize the range of the disp
		Interval ival = e.getInterval();
		MutableDateTime startDay = new MutableDateTime(ival.getStart());
		MutableDateTime endDay = new MutableDateTime(ival.getEnd());
		endDay.setMillisOfDay(0);
		startDay.setMillisOfDay(0);

		// bound it inside this visible month
		if (startDay.isBefore(firstOnMonth))
			startDay = new MutableDateTime(firstOnMonth);
		if (endDay.isAfter(lastOnMonth))
			endDay = new MutableDateTime(lastOnMonth);

		// go from start to end and add it to all the days in the way
		while (!endDay.isBefore(startDay))
		{
			MonthDay md = this.days.get(startDay.getDayOfYear());
			if (md != null)
				func.call(md);
			startDay.addDays(1);
		}
	}

	/**
	 * Remove a single event
	 * 
	 * @param disp the event/commitment to remove
	 */
	public void removeDisplayable(final Displayable disp)
	{
		if (disp == null)
			return;
		traverseDisplayable(disp, new Lambda<MonthDay>() {

			@Override
			public void call(MonthDay md)
			{
				md.removeDisplayable(disp);
			}
		});
		emap.remove(disp.getUuid());
	}

	/**
	 * Removes all displayable items inside this month
	 */
	public void clearDisplayables()
	{
		for (Component i : inside.getComponents())
		{
			((MonthDay) i).clearDisplayable();
		}
		emap.clear();
	}

	public boolean isToday(ReadableDateTime fom)
	{
		DateTime now = DateTime.now();
		return fom.getYear() == now.getYear() && fom.getDayOfYear() == now.getDayOfYear();
	}

	public void display(DateTime newTime)
	{
		this.escaped = false;
		time = newTime;
		generateDays(new MutableDateTime(time));
		mainPanel.miniMove(time);
	}

	public void next()
	{
		MutableDateTime fom = new MutableDateTime(time);
		fom.addMonths(1);
		time = fom.toDateTime();
		generateDays(fom);
	}

	public void previous()
	{
		MutableDateTime fom = new MutableDateTime(time);
		fom.addMonths(-1);
		time = fom.toDateTime();
		generateDays(fom);
	}

	/**
	 * Fill calendar with month in referenceDay
	 * 
	 * @param referenceDay what month should we display
	 */
	protected void generateDays(MutableDateTime referenceDay)
	{
		// reset to the first of the month at midnight, then find Sunday
		referenceDay.setDayOfMonth(1);
		referenceDay.setMillisOfDay(0);
		int first = (referenceDay.getDayOfWeek() % 7);
		int daysInView = first + referenceDay.dayOfMonth().getMaximumValue();
		int weeks = (int) Math.ceil(daysInView / 7.0);

		inside.setLayout(new java.awt.GridLayout(weeks, 7));
		referenceDay.addDays(-first);

		firstOnMonth = new DateTime(referenceDay);

		// remove all old days
		inside.removeAll();

		DateTime from = referenceDay.toDateTime();

		// generate days, weeks*7 covers all possible months, so we just loop
		// through and add each day
		for (int i = 0; i < (weeks * 7); i++)
		{
			MonthDay md = new MonthDay(referenceDay.toDateTime(), getMarker(referenceDay), this);
			inside.add(md);
			md.reBorder(i < 7, (i % 7) == 0, i >= (weeks - 1) * 7);
			this.days.put(referenceDay.getDayOfYear(), md);
			referenceDay.addDays(1); // go to next day
		}

		referenceDay.addDays(-1);// go back one to counteract last add one

		lastOnMonth = new DateTime(referenceDay);
		setDisplayables(getVisibleItems(from, referenceDay.toDateTime()));

		monthLabel.setText(this.getTime().toString(Months.monthLblFormat));

		// notify mini-calendar to change
		mainPanel.miniMove(time);

		// repaint when changed
		mainPanel.revalidate();
	}

	private List<Displayable> getVisibleItems(DateTime from, DateTime to)
	{
		List<Displayable> visible = new ArrayList<>();
		visible.addAll(EventClient.getInstance().getWithinRange(from, to));
		visible.addAll(CommitmentClient.getInstance().getCommitments(from, to));
		return visible;
	}

	/**
	 * Gets the DayStyle of given date
	 * 
	 * @param date
	 * @return
	 */
	protected DayStyle getMarker(ReadableDateTime date)
	{
		if (date != null && time != null && date.getMonthOfYear() == time.getMonthOfYear())
		{
			return (isToday(date) ? DayStyle.Today : DayStyle.Normal);
		}

		return DayStyle.OutOfMonth;
	}

	// Added for testing purposes
	public DateTime getTime()
	{
		return time;
	}

	@Override
	public void updateDisplayable(Displayable events, boolean added)
	{
		removeDisplayable(emap.get(events.getUuid()));
		if (added)
			addDisplayable(events);
		revalidate();
		repaint();
	}

	@Override
	public void select(final Displayable item)
	{
		if (this.lastSelection != null)
		{
			traverseDisplayable(lastSelection, new Lambda<MonthDay>() {

				@Override
				public void call(MonthDay md)
				{
					md.clearSelected();
				}
			});
		}

		if (item != null)
		{
			traverseDisplayable(item, new Lambda<MonthDay>() {

				@Override
				public void call(MonthDay md)
				{
					md.select(item);
				}
			});
			lastSelection = item;
		}
	}

	/**
	 * @return the escaped
	 */
	public boolean isEscaped()
	{
		return escaped;
	}

	/**
	 * @param escaped the escaped to set
	 */
	public void setEscaped(boolean escaped)
	{
		this.escaped = escaped;
	}

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		tooltip = escaped;
		if (escaped)
		{
			Displayable dp = MainPanel.getInstance().getSelectedEvent();
			if (dp != null)
			{
				String name = dp.getName();
				if (name != null)
				{
					// get Cursor info
					Point l = MouseInfo.getPointerInfo().getLocation();
					Point pp = inside.getLocationOnScreen();
					int x = l.x - pp.x;
					int y = l.y - pp.y;

					// get String properties of displayables
					String time = dp.getFormattedHoverTextTime();
					String days = dp.getFormattedDateRange();

					// get widths of stringProperties and find longest
					int timeSize = g.getFontMetrics().stringWidth(time);
					int nameSize = g.getFontMetrics().stringWidth(name);
					int daysSize = g.getFontMetrics().stringWidth(days);

					int width = Math.max(Math.max(timeSize + 10, nameSize + 10), Math.max(daysSize + 10, 60));

					// generate the polygon
					Polygon dropdown = getDropTextPolygon(width, x, y);

					HSLColor background = new HSLColor(dp.getColor());
					Color c = background.adjustLuminance(90);

					// draw the polygon
					// g.setColor(new Color(255,255,255,160));
					g.setColor(c);
					g.fillPolygon(dropdown);
					g.setColor(Color.BLACK);
					g.drawPolygon(dropdown);

					// draw the text
					g.drawString(name, x + (width - nameSize) / 2, y + 90);
					g.drawString(time, x + (width - timeSize) / 2, y + 110);
					g.drawString(days, x + (width - daysSize) / 2, y + 130);
				}
			}
		}
	}

	/**
	 * 
	 * @param width the width of the box (for text)
	 * @param x the mouse's position (x)
	 * @param y the mouse's position (y)
	 * @return the drawing polygon
	 */
	private Polygon getDropTextPolygon(int width, int x, int y)
	{
		y += 40;
		int[] xs = { x + 0, x + 30, x + 0, x + 0, x + width, x + width, x + 60 };
		int[] ys = { y + 0, y + 30, y + 30, y + 100, y + 100, y + 30, y + 30 };

		return new Polygon(xs, ys, 7);
	}

	@Override
	public void setSelectedDay(DateTime time)
	{
		for (Component c : inside.getComponents())
		{
			if (c instanceof MonthDay)
			{
				MonthDay day = (MonthDay) c;
				if (day.isSelected())
					day.setSelected(false);

				day.setSelected(day.getDay().getDayOfYear() == time.getDayOfYear()
				        && day.getDay().getYear() == time.getYear());
			}
		}
	}
}
