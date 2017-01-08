/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.ui.views.year;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.MutableDateTime;

import com.tmathmeyer.sentinel.AbstractCalendar;
import com.tmathmeyer.sentinel.models.client.net.EventClient;
import com.tmathmeyer.sentinel.models.data.Displayable;
import com.tmathmeyer.sentinel.models.data.Event;
import com.tmathmeyer.sentinel.ui.main.MainPanel;
import com.tmathmeyer.sentinel.utils.Colors;
import com.tmathmeyer.sentinel.utils.VerticalLabelUI;

/**
 * View for showing the entire year
 */
public class YearCalendar extends AbstractCalendar
{
	private static final long serialVersionUID = 1L;
	private MutableDateTime calendarStart;
	private Map<Integer, Integer> events = new HashMap<Integer, Integer>();
	private EventClient eventModel;

	/**
	 * 
	 * @param dt any date time in the year that you wish to display
	 * @param eventModel the event model so that this can access the database
	 */
	public YearCalendar(DateTime dt, EventClient eventModel)
	{
		this.setLayout(new BorderLayout());
		drawCalendar(new MutableDateTime(dt));
		this.eventModel = eventModel;
	}

	/**
	 * 
	 * @param dt any date time
	 * @return the date time that corresponds to january first of the year of
	 *         the date time provided
	 */
	private MutableDateTime getStartOfYearCalendar(DateTime dt)
	{
		MutableDateTime jan1 = new MutableDateTime(new DateTime(dt.getYear(), 1, 1, 1, 1));
		jan1.addDays(0 - (jan1.getDayOfWeek()));
		return jan1;
	}

	/**
	 * 
	 * @param mdt a mutable date time on which to start the calendar. this can
	 *            be used to make the dynamic month scrolling work if needed.
	 */
	private void drawCalendar(MutableDateTime mdt)
	{
		this.removeAll();
		mdt = this.calendarStart = getStartOfYearCalendar(mdt.toDateTime());

		MutableDateTime upperBound = mdt.copy();
		upperBound.addDays(378);
		getVisibleEvents(mdt.toDateTime(), upperBound.toDateTime());

		MutableDateTime yearDate = mdt.copy();
		yearDate.addDays(20);
		int year = yearDate.getYear();

		JLabel title = new JLabel(year + "");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("DejaVu Sans", Font.BOLD, 25));
		JPanel content = new JPanel();

		this.add(title, BorderLayout.NORTH);
		this.add(content, BorderLayout.CENTER);

		MutableDateTime monthCounter = mdt.copy();

		content.setLayout(new BoxLayout(content, 0));
		for (int i = 0; i < 3; i++)
		{
			content.add(new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(65566, 0)));
			content.add(getFourMonthLabel(monthCounter.copy()));
			content.add(getFourMonthGrid(mdt.copy()));
			content.add(new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(65566, 0)));
			mdt.addDays(18 * 7);
			monthCounter.addMonths(4);
		}
	}

	/**
	 * get the vertical text labels for the months
	 * 
	 * @param start the first day in the series
	 * @return the JPanel that contains the 4 month labels
	 */
	private JPanel getFourMonthLabel(MutableDateTime start)
	{
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(4, 1));
		for (int i = 0; i < 4; i++)
		{
			start.addMonths(1);
			JLabel l = new JLabel(start.monthOfYear().getAsText());
			l.setHorizontalAlignment(SwingConstants.CENTER);
			l.setFont(new Font("DejaVu Sans", Font.BOLD, 16));
			l.setUI(new VerticalLabelUI(false));

			p.add(l);

		}

		int width = 50;
		int height = 570;

		p.setMinimumSize(new Dimension(width, height - 150));
		p.setPreferredSize(new Dimension(width, height));
		p.setMaximumSize(new Dimension(width, height + 150));

		return p;
	}

	/**
	 * gets the drawn year
	 * 
	 * @param start the date time that starts the series.
	 * @return the jpanel that contains the colored days and the S/M/T/W/R/F/S
	 *         labels
	 */
	private JPanel getFourMonthGrid(MutableDateTime start)
	{
		int gridHeight = 19;
		int gridWidth = 7;

		JPanel p = new JPanel();
		p.setLayout(new GridLayout(gridHeight, gridWidth));

		String[] days = { "S", "M", "T", "W", "R", "F", "S" };
		for (int i = 0; i < 7; i++)
		{
			JLabel header = new JLabel(days[i]);
			header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER));
			header.setHorizontalAlignment(SwingConstants.CENTER);
			header.setFont(new Font("DejaVu Sans", Font.ITALIC, 12));
			p.add(header);
		}

		for (int j = 0; j < 18 * 7; j++)
		{
			Color dayBackground = start.getMonthOfYear() % 2 == 0 ? Colors.TABLE_BACKGROUND : Colors.TABLE_GRAY_HEADER;

			Integer eventCount = events.get(start.getDayOfYear());
			eventCount = eventCount == null ? 0 : eventCount;

			YearlyDayHolder day = new YearlyDayHolder(start.toDateTime(), dayBackground);
			MutableDateTime today = new MutableDateTime(DateTime.now());
			today.setMillisOfDay(0);
			MutableDateTime checking = new MutableDateTime(start);
			start.setMillisOfDay(0);
			if (checking.toDateTime().isEqual(today))
			{
				day.setBackground(Colors.SELECTED_BACKGROUND);
				day.setForeground(Colors.SELECTED_TEXT);
			}
			JLabel dayLabel = new JLabel(start.getDayOfMonth() + "");
			dayLabel.setHorizontalAlignment(SwingConstants.CENTER);

			day.setLayout(new GridLayout(1, 1));
			day.add(dayLabel);
			day.setBorder(BorderFactory.createMatteBorder(0, start.getDayOfWeek() % 7 == 0 ? 1 : 0, 1, 1, Colors.BORDER));

			day.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent me)
				{
					YearlyDayHolder event = (YearlyDayHolder) (me.getSource());
					MainPanel.getInstance().miniMove(event.getDateTime());
					MainPanel.getInstance().viewDay();
				}

				@Override
				public void mouseEntered(MouseEvent me)
				{
					// this is just a demo of what it can do
				}

				@Override
				public void mouseExited(MouseEvent me)
				{
				}

				@Override
				public void mousePressed(MouseEvent me)
				{
					// TODO: something? maybe nothing? have to decide with
					// team/steakholders
				}

				@Override
				public void mouseReleased(MouseEvent me)
				{
					YearlyDayHolder event = (YearlyDayHolder) (me.getSource());
					MainPanel.getInstance().miniMove(event.getDateTime());
				}

			});

			p.add(day);
			start.addDays(1);
		}

		int width = 280;
		int height = 570;

		p.setMinimumSize(new Dimension(0, height - 150));
		p.setPreferredSize(new Dimension(width, height));
		p.setMaximumSize(new Dimension(width + 350, height + 150));

		return p;
	}

	/**
	 * 
	 * inner class that holds the day (and can draw dots if there are events
	 * that day)
	 *
	 */
	public class YearlyDayHolder extends JPanel
	{
		private static final long serialVersionUID = 1L;
		private DateTime dt;
		private Color defaultBackground;

		/**
		 * 
		 * @param dt the date time that this panel represents
		 * @param dayBackground the background color (alternates by month)
		 * @param hasEvent whether to draw the dot signifying an event
		 */
		public YearlyDayHolder(DateTime dt, Color dayBackground)
		{
			this.dt = dt;
			this.defaultBackground = dayBackground;
			this.resetColor();
		}

		/**
		 * resets the color to the default background
		 */
		public void resetColor()
		{
			this.setBackground(this.defaultBackground);
		}

		/**
		 * 
		 * @return the date time that this day represents graphically
		 */
		public DateTime getDateTime()
		{
			return this.dt;
		}

		/**
		 * redraw
		 */
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
		}
	}

	// goes to the NEXT YEAR
	@Override
	public void next()
	{
		this.display(this.calendarStart.toDateTime());
	}

	// goes to the PREVIOUS year
	@Override
	public void previous()
	{
		MutableDateTime mdt = new MutableDateTime(this.calendarStart);
		mdt.addYears(-2);
		this.display(mdt.toDateTime());
	}

	@Override
	public void display(DateTime newTime)
	{
		this.events.clear();
		this.drawCalendar(new MutableDateTime(newTime));
		MainPanel.getInstance().miniMove(newTime);
		this.revalidate();
		this.repaint();
	}

	@Override
	public void updateDisplayable(Displayable event, boolean added)
	{
		Interval ival = event.getInterval();
		MutableDateTime start = new MutableDateTime(ival.getStart());

		while (start.isBefore(ival.getEnd()))
		{
			if (!added)
			{
				int day = start.getDayOfYear();
				Integer ec = this.events.get(day);
				int eventCount = (ec == null || ec <= 0) ? 0 : ec - 1;
				this.events.put(day, eventCount);
			} else
			{
				int day = start.getDayOfYear();
				Integer ec = this.events.get(day);
				int eventCount = (ec == null) ? 1 : ec + 1;
				this.events.put(day, eventCount);
			}
			start.addDays(1);
		}
	}

	/**
	 * gets all the events for every day that is shown on the calendar, then
	 * adds them to the map of days with events.
	 * 
	 * @param from the beginning of the year
	 * @param to the end of the year
	 */
	private void getVisibleEvents(DateTime from, DateTime to)
	{
		if (this.eventModel == null)
		{
			return;
		}
		Set<Event> events = this.eventModel.getEvents(from, to);
		for (Event e : events)
		{
			this.updateDisplayable(e, true);
		}
	}

	@Override
	public void select(Displayable item)
	{

	}

	@Override
	public void setSelectedDay(DateTime time)
	{

	}
}
