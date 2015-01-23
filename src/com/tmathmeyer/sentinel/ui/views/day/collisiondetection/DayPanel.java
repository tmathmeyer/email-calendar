/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.ui.views.day.collisiondetection;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import com.tmathmeyer.sentinel.models.data.Displayable;
import com.tmathmeyer.sentinel.ui.main.MainPanel;
import com.tmathmeyer.sentinel.ui.views.week.WeekCalendar;
import com.tmathmeyer.sentinel.utils.Colors;

/**
 * All the VanGoghPaintings are visible in the LouvreTour you saw in the day
 */
public class DayPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	HashMap<Displayable, DayItem> guides = new HashMap<>();
	private DateTime date;
	private DayItem selected;
	private boolean isSomethingDragging;
	private DateTime displayDate;
	private boolean inWeekView;
	private WeekCalendar inWeek;
	private int offset;

	public DayPanel()
	{
		inWeekView = false;
		inWeek = null;
		isSomethingDragging = false;
		setLayout(null);
		setPreferredSize(new Dimension(1, 1440));
		setBackground(Colors.TABLE_BACKGROUND);
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0)
			{
				if (isSomethingDragging)
				{
					MainPanel.getInstance().display(selected.getDisplayable().getStart());
				}
				isSomethingDragging = false;
				selected = null;
				if (inWeekView)
				{
					inWeek.passTo(selected);
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0)
			{

			}

			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				if (inWeekView)
				{
					inWeek.mouseOverDay(displayDate.getDayOfWeek() % 7);
				}
			}

			public void mousePressed(MouseEvent e)
			{
				MainPanel.getInstance().setSelectedDay(displayDate);
				MainPanel.getInstance().clearSelected();
			}

			@Override
			public void mouseClicked(MouseEvent arg0)
			{

			}
		});

		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent arg0)
			{
			}

			@Override
			public void mouseDragged(MouseEvent arg0)
			{
				if (selected != null)
				{
					DayPanel.this.setComponentZOrder(selected, 0);
					selected.updateTime(getTimeAtCursor());
					if (!isSomethingDragging)
					{
						isSomethingDragging = true;
						offset = arg0.getY();
						if (inWeekView)
						{
							inWeek.passTo(selected);
							inWeek.dispatchEvent(arg0);
						}

					}
					revalidate();
					repaint();
				}
			}
		});
	}

	public DayPanel(boolean inWeekView, WeekCalendar inWeek)
	{
		this();
		this.inWeek = inWeek;
		this.inWeekView = inWeekView;
	}

	public void setEvents(List<Displayable> events, DateTime displayedDay)
	{
		this.date = displayedDay;
		List<DayItem> collidingEvents = CollisionAlgorithms.createEventsReallyNicely(events, displayedDay);
		this.displayDate = displayedDay;
		removeAll();
		guides.clear();
		for (DayItem dayItem : collidingEvents)
		{
			guides.put(dayItem.getDisplayable(), dayItem);
			add(dayItem);
		}
		revalidate();
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		int distanceBetweenBorders = (this.getHeight() - 23) / 24;
		g.setColor(Colors.BORDER);
		int currentPosition = distanceBetweenBorders + 1;

		for (int i = 0; i < 23; i++)
		{
			g.drawLine(0, currentPosition, this.getWidth(), currentPosition);
			currentPosition++;
			currentPosition += distanceBetweenBorders;
		}

		currentPosition = distanceBetweenBorders / 2 + 1;
		g.setColor(Colors.BORDER.brighter());
		for (int i = 0; i < 24; i++)
		{
			g.drawLine(0, currentPosition, this.getWidth(), currentPosition);
			currentPosition++;
			currentPosition += distanceBetweenBorders;
		}

	}

	public void select(Displayable item)
	{
		for (DayItem v : guides.values())
		{
			if (item instanceof Displayable && v.getDisplayable().getUuid().equals(((Displayable) item).getUuid()))
				item = v.getDisplayable();
			v.setSelected(false);
		}
		if (item instanceof Displayable)
		{
			selected = guides.get(item);
			if (selected != null)
			{
				selected.setSelected(true);
			}
		}
	}

	public DateTime getDisplayDate()
	{
		return displayDate;
	}

	private DateTime getTimeAtCursor()
	{
		Point m = MouseInfo.getPointerInfo().getLocation();
		Point p = getLocationOnScreen();
		MutableDateTime d = date.toMutableDateTime();
		m.y -= offset;
		if (m.y < p.y)
			m.y = p.y;
		if (m.y > 1440)
			m.y = 1440;

		d.setHourOfDay((m.y - p.y) / 60);
		int min = (m.y - p.y) % 60;

		if (min >= 7 && min < 23)
			min = 15;
		if (min < 7)
			min = 0;
		if (min >= 23 && min < 38)
			min = 30;
		if (min >= 38 && min < 55)
			min = 45;
		if (min >= 55)
		{
			min = 0;
			d.addHours(1);
		}
		d.setMinuteOfHour(min);
		return d.toDateTime();
	}
}
