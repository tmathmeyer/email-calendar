/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.ui.views.week;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.tmathmeyer.sentinel.AbstractCalendar;
import com.tmathmeyer.sentinel.models.client.net.EventClient;
import com.tmathmeyer.sentinel.models.client.net.CommitmentClient;
import com.tmathmeyer.sentinel.models.data.Displayable;
import com.tmathmeyer.sentinel.models.data.Event;
import com.tmathmeyer.sentinel.ui.main.MainPanel;
import com.tmathmeyer.sentinel.ui.views.day.DayGridLabel;
import com.tmathmeyer.sentinel.ui.views.day.collisiondetection.DayItem;
import com.tmathmeyer.sentinel.ui.views.day.collisiondetection.DayPanel;
import com.tmathmeyer.sentinel.utils.Colors;
import com.tmathmeyer.sentinel.utils.Months;

/**
 * Calendar to display the 7 days of the selected week. A timetable and 7 day
 * views are scaled to fit in the current window size.
 */
public class WeekCalendar extends AbstractCalendar
{
	private static final long serialVersionUID = 1L;
	private DateTime time;
	private DateTime weekStartTime;
	private DateTime weekEndTime;
	private DayItem selected;
	private MainPanel mainPanel;

	private Displayable lastSelection;

	private DayPanel[] daysOfWeekArray = new DayPanel[7];
	private List<Displayable> displayableList = new ArrayList<Displayable>();
	private List<WeekMultidayEventItem> multidayItemList = new ArrayList<WeekMultidayEventItem>();
	private DateTimeFormatter monthDayFmt = DateTimeFormat.forPattern("MMM d");
	private DateTimeFormatter dayYearFmt = DateTimeFormat.forPattern("d, yyyy");
	private DateTimeFormatter monthDayYearFmt = DateTimeFormat.forPattern("MMM d, yyyy");
	private DateTimeFormatter dayTitleFmt = DateTimeFormat.forPattern("E M/d");
	private JLabel weekTitle = new JLabel();
	private JScrollPane headerScroller = new JScrollPane();
	private ScrollableBox headerBox = new ScrollableBox();
	private JScrollPane smithsonianScroller = new JScrollPane();
	private JPanel smithsonian = new JPanel();
	private JLabel dayHeaders[] = new JLabel[7];
	private JPanel hourLabels;
	private boolean scrolled = false;

	/**
	 * 
	 * @param on the DateTime that the Week Calendar is focused/centered on
	 */
	public WeekCalendar(DateTime on)
	{
		this.selected = null;
		this.mainPanel = MainPanel.getInstance();
		this.time = on;
		updateWeekStartAndEnd(time);

		// ui layout
		String rs = (new JScrollBar().getPreferredSize().width) + "px";
		setLayout(new MigLayout(
		        "insets 0,gap 0",
		        "[60px:60px:60px][sizegroup a,grow][sizegroup a,grow][sizegroup a,grow][sizegroup a,grow][sizegroup a,grow][sizegroup a,grow][sizegroup a,grow]["
		                + rs + ":" + rs + ":" + rs + "]", "[][][::100px,grow][grow]"));

		weekTitle.setFont(new Font("DejaVu Sans", Font.BOLD, 25));
		add(weekTitle, "cell 0 0 9 1,alignx center");

		for (int i = 0; i < 7; i++)
		{
			add(dayHeaders[i] = new JLabel("", JLabel.CENTER), "cell " + (i + 1) + " 1,alignx center");
		}

		headerScroller.setBorder(BorderFactory.createEmptyBorder());
		headerScroller.setViewportBorder(BorderFactory.createEmptyBorder());
		headerScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(headerScroller, "cell 1 2 7 1,grow"); // default to 7 as no
												  // scrollbars

		headerBox.setBorder(BorderFactory.createEmptyBorder());
		headerScroller.setViewportView(headerBox);

		smithsonianScroller.setBorder(null);
		smithsonianScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		smithsonianScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		smithsonianScroller.getVerticalScrollBar().setUnitIncrement(20);
		add(smithsonianScroller, "cell 0 3 9 1,grow");

		smithsonianScroller.setViewportView(smithsonian);
		smithsonian
		        .setLayout(new MigLayout(
		                "insets 0,gap 0",
		                "[60px:60px:60px][sizegroup a,grow][sizegroup a,grow][sizegroup a,grow][sizegroup a,grow][sizegroup a,grow][sizegroup a,grow][sizegroup a,grow]",
		                "[grow]"));

		generateDay();
		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e)
			{
				// TODO Auto-gene// silently ignore as this is apparently not in
				// the viewrated method stub

			}

			@Override
			public void mouseDragged(MouseEvent e)
			{
				if (selected != null)
					revalidate();
			}

		});
	}

	/**
	 * Generates the day displays in the week panel
	 */
	private void generateDay()
	{
		// clear out the specifics
		smithsonian.removeAll();
		headerBox.removeAll();

		// add the day grid back in
		hourLabels = new DayGridLabel();
		smithsonian.add(hourLabels, "cell 0 0,grow");

		MutableDateTime increment = new MutableDateTime(weekStartTime);
		increment.setMillisOfDay(0);
		DateTime now = DateTime.now().withMillisOfDay(0);

		displayableList = getVisibleDisplayables();

		for (int i = 0; i < 7; i++)
		{
			// add day views to the day grid
			this.daysOfWeekArray[i] = new DayPanel(true, this);
			this.daysOfWeekArray[i].setEvents(
			        getDisplayablesInInterval(increment.toDateTime(), increment.toDateTime().plusDays(1)),
			        increment.toDateTime());
			this.daysOfWeekArray[i].setBorder(BorderFactory.createMatteBorder(1, 0, 1, i == 6 ? 0 : 1, Colors.BORDER));

			this.smithsonian.add(this.daysOfWeekArray[i], "cell " + (i + 1) + " 0,grow");

			// add day titles to the title grid
			dayHeaders[i].setText(increment.toDateTime().toString(dayTitleFmt));
			dayHeaders[i].setFont(dayHeaders[i].getFont().deriveFont(increment.isEqual(now) ? Font.BOLD : Font.PLAIN));

			increment.addDays(1);
		}

		// populate and set up the multiDayEventGrid
		populateMultidayEventGrid();

		// setup week title
		increment.addDays(-1);

		// smart titles
		if (weekStartTime.getYear() != increment.getYear())
			weekTitle.setText(weekStartTime.toString(monthDayYearFmt) + " - " + increment.toString(monthDayYearFmt));
		else if (weekStartTime.getMonthOfYear() != increment.getMonthOfYear())
			weekTitle.setText(weekStartTime.toString(monthDayFmt) + " - " + increment.toString(monthDayYearFmt));
		else
			weekTitle.setText(weekStartTime.toString(monthDayFmt) + " - " + increment.toString(dayYearFmt));

		// notify mini-calendar to change
		mainPanel.miniMove(time);
	}

	/**
	 * Adds all multiday event items into grids corresponding to the days of the
	 * week then adds them to the top bar display
	 */
	private void populateMultidayEventGrid()
	{
		List<Event> multidayEvents = getMultidayEvents();
		multidayItemList.clear();
		Collections.sort(multidayEvents, new Comparator<Event>() {

			@Override
			public int compare(Event o1, Event o2)
			{
				return o1.getEnd().compareTo(o2.getEnd());
			}

		});
		int rows = 0;

		while (!multidayEvents.isEmpty())
		{
			JPanel multiGrid = new JPanel();
			multiGrid.setBorder(BorderFactory.createEmptyBorder());
			multiGrid
			        .setLayout(new MigLayout(
			                "insets 0,gap 0",
			                "[sizegroup a,grow][sizegroup a,grow][sizegroup a,grow][sizegroup a,grow][sizegroup a,grow][sizegroup a,grow][sizegroup a,grow]",
			                "[]"));

			int gridIndex = 0;

			next: while (gridIndex < 7)
			{
				Interval mInterval = new Interval(daysOfWeekArray[gridIndex].getDisplayDate(),
				        daysOfWeekArray[gridIndex].getDisplayDate().plusDays(1));

				for (Event currEvent : multidayEvents)
				{
					if (isDisplayableInInterval(currEvent, mInterval))
					{
						boolean firstPanel = true;
						WeekMultidayEventItem multidayPanel;
						do
						{
							if (firstPanel)
							{
								multidayPanel = new WeekMultidayEventItem(currEvent, MultidayEventItemType.Start, " "
								        + currEvent.getName());
								multidayPanel.setMinimumSize(new Dimension(0, 0));
								multidayPanel.setBackground(currEvent.getColor());
								if (multiGrid.getComponentCount() > 0)
									((JComponent) multiGrid.getComponents()[multiGrid.getComponentCount() - 1])
									        .setBorder(BorderFactory.createMatteBorder(rows == 0 ? 1 : 0,
									                (gridIndex == 1) ? 1 : 0, 1, 0, Colors.BORDER));
								multidayPanel.setRows(rows);
								multidayPanel.setDynamicBorder(currEvent.getColor().darker(), false);
								multiGrid.add(multidayPanel, "cell " + gridIndex + " 0, grow");
								multidayItemList.add(multidayPanel);
								firstPanel = false;
							} else
							{
								multidayPanel = new WeekMultidayEventItem(currEvent, MultidayEventItemType.Middle);
								multidayPanel.setBackground(currEvent.getColor());
								multidayPanel.setRows(rows);
								multidayPanel.setDynamicBorder(currEvent.getColor().darker(), false);
								multiGrid.add(multidayPanel, "cell " + gridIndex + " 0, grow");
								multidayItemList.add(multidayPanel);
							}
							gridIndex++;
						} while (gridIndex < 7
						        && daysOfWeekArray[gridIndex].getDisplayDate().isBefore(currEvent.getEnd()));

						if (multidayPanel.getType() == MultidayEventItemType.Start)
							multidayPanel.setType(MultidayEventItemType.Single);
						else
							multidayPanel.setType(MultidayEventItemType.End);

						multidayPanel.setDynamicBorder(currEvent.getColor().darker(), false);
						multidayEvents.remove(currEvent);
						continue next;
					}
				}

				// if we don't find anything, add spacer and go to next day
				gridIndex++;
				WeekMultidayEventItem spacer = new WeekMultidayEventItem(null,
				        gridIndex == 1 ? MultidayEventItemType.Start : gridIndex == 7 ? MultidayEventItemType.End
				                : MultidayEventItemType.Middle);
				spacer.setBackground(Colors.TABLE_BACKGROUND);
				spacer.setRows(rows);
				spacer.setSpacer(true);
				spacer.setDynamicBorder(Colors.BORDER, false);
				multiGrid.add(spacer, "cell " + (gridIndex - 1) + " 0, grow");
			}

			if (multiGrid.getComponentCount() > 0)
				headerBox.add(multiGrid);
			rows++;
		}
		// need to set this manually, silly because scrollpanes are silly and
		// don't resize
		if (rows > 3)
		{
			add(headerScroller, "cell 1 2 8 1,grow");
			rows = 3;
		} else
		{
			add(headerScroller, "cell 1 2 7 1,grow");
		}
		((MigLayout) getLayout()).setRowConstraints("[][][" + (rows * 23 + 1) + "px:n:80px,grow]"
		        + (rows > 0 ? "6" : "") + "[grow]");
	}

	private List<Displayable> getVisibleDisplayables()
	{
		List<Displayable> visibleDisplayables = new ArrayList<Displayable>();
		visibleDisplayables.addAll(EventClient.getInstance().getEvents(weekStartTime, weekEndTime));
		visibleDisplayables.addAll(CommitmentClient.getInstance().getCommitments(weekStartTime, weekEndTime));

		Collections.sort(visibleDisplayables, new Comparator<Displayable>() {
			public int compare(Displayable d1, Displayable d2)
			{
				return d1.getStart().getMinuteOfDay() < d2.getStart().getMinuteOfDay() ? -1 : d1.getStart()
				        .getMinuteOfDay() > d2.getStart().getMinuteOfDay() ? 1 : 0;
			}
		});

		// Return list of displayables to be displayed
		return visibleDisplayables;
	}

	@Override
	public void next()
	{
		display(Months.nextWeek(this.time));
	}

	@Override
	public void previous()
	{
		display(Months.prevWeek(this.time));
	}

	@Override
	public void display(DateTime newTime)
	{
		this.time = newTime;
		updateWeekStartAndEnd(time);

		this.generateDay();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				if (scrolled)
					return;
				scrolled = true;
				// Scroll to now
				BoundedRangeModel jsb = smithsonianScroller.getVerticalScrollBar().getModel();

				double day;

				if (!displayableList.isEmpty())
				{
					day = displayableList.get(0).getStart().getMinuteOfDay();
				} else
				{
					day = DateTime.now().getMinuteOfDay();
				}

				day -= (day > 60) ? 60 : day;

				day /= time.minuteOfDay().getMaximumValue();
				day *= (jsb.getMaximum()) - jsb.getMinimum();
				jsb.setValue((int) day);
			}
		});

		// repaint
		mainPanel.revalidate();
		mainPanel.repaint();
	}

	/**
	 * updates the week start and end variables
	 * 
	 * @param time a time in the week
	 */
	private void updateWeekStartAndEnd(DateTime time)
	{
		MutableDateTime mdt = new MutableDateTime(time);
		mdt.addDays(-(time.getDayOfWeek() % 7));
		mdt.setMillisOfDay(0);
		this.weekStartTime = mdt.toDateTime();
		mdt.addDays(7);
		this.weekEndTime = mdt.toDateTime();
	}

	@Override
	public void updateDisplayable(Displayable events, boolean added)
	{
		// at the moment, we don't care, and just re-pull from the DB. TODO:
		// this should change
		this.generateDay();
	}

	@Override
	public void select(Displayable item)
	{
		Displayable oitem = item;
		DayPanel day;
		if (item == null && lastSelection == null)
			return;
		if (item == null)
			oitem = lastSelection;

		if (lastSelection != null)
		{
			if (lastSelection instanceof Event)
				selectEvents((Event) lastSelection, null);
			else
			{
				day = null;
				for (DayPanel lt : daysOfWeekArray)
				{
					if (lastSelection.getStart().getDayOfYear() == lt.getDisplayDate().getDayOfYear())
					{
						day = lt;
						break;
					}
				}

				if (day != null)
				{
					day.select(null);
				}
			}
		}

		if (item != null && item instanceof Event)
			selectEvents((Event) item, item);
		else
		{
			day = null;
			for (DayPanel lt : daysOfWeekArray)
			{
				if (oitem.getStart().getDayOfYear() == lt.getDisplayDate().getDayOfYear())
				{
					day = lt;
					break;
				}
			}

			if (day != null)
			{
				day.select(item);
			}
		}
		lastSelection = item;
	}

	/**
	 * Selects an event's corresponding Displayable
	 * 
	 * @param on Event being selected
	 * @param setTo Displayable of Event being selected
	 */
	private void selectEvents(Event on, Displayable setTo)
	{
		// TODO: refactor this pattern
		DayPanel mLouvreTour;
		MutableDateTime startDay = new MutableDateTime(on.getStart());
		MutableDateTime endDay = new MutableDateTime(on.getEnd());

		endDay.setMillisOfDay(0);
		endDay.addDays(1);
		endDay.addMillis(-1);
		startDay.setMillisOfDay(0);

		Interval eventLength = new Interval(startDay, endDay);
		if (setTo == null || eventLength.toDuration().getStandardHours() > 24)
		{
			for (WeekMultidayEventItem multidayItem : multidayItemList)
			{
				if (setTo != null && multidayItem.getEvent().getUuid().equals(((Event) on).getUuid()))
					multidayItem.setSelected(true);
				else
					multidayItem.setSelected(false);
			}
			return;
		}

		// TODO: can be simplified now that multiday events are handled
		// elsewhere
		int index = 0;
		for (int i = 0; i < 7; i++)
		{
			if (startDay.getDayOfYear() == daysOfWeekArray[i].getDisplayDate().getDayOfYear())
			{
				index = i;
				break;
			}
		}

		while (index < 7 && !endDay.isBefore(daysOfWeekArray[index].getDisplayDate()))
		{
			mLouvreTour = daysOfWeekArray[index];
			try
			{
				mLouvreTour.select(setTo);
			} catch (NullPointerException ex)
			{
				// silently ignore as this is apparently not in the view
			}
			index++;
		}
	}

	/**
	 * Get the time from a Date Time
	 * 
	 * @return
	 */
	public DateTime getTime()
	{
		return time;
	}

	/**
	 * Gets all the events in the week that also are in the given interval
	 * 
	 * @param intervalStart start of the interval to check
	 * @param intervalEnd end of the interval to check
	 * @return list of events that are both in the week and interval
	 */
	private List<Displayable> getDisplayablesInInterval(DateTime intervalStart, DateTime intervalEnd)
	{
		List<Displayable> retrievedDisplayables = new ArrayList<>();
		Interval mInterval = new Interval(intervalStart, intervalEnd);

		for (Displayable d : displayableList)
		{
			if (new Interval(d.getStart(), d.getEnd()).toDuration().getStandardHours() > 24)
				continue;

			if (isDisplayableInInterval(d, mInterval))
			{
				retrievedDisplayables.add(d);
			}
		}

		return retrievedDisplayables;
	}

	/**
	 * Gets the multiday events in the scope of the week
	 * 
	 * @return list of multiday events
	 */
	private List<Event> getMultidayEvents()
	{
		List<Event> retrievedEvents = new ArrayList<>();

		for (Displayable d : displayableList)
		{
			if (!(d instanceof Event))
				continue;

			DateTime intervalStart = d.getStart();
			DateTime intervalEnd = d.getEnd();
			Interval mInterval = new Interval(intervalStart, intervalEnd);
			if (mInterval.toDuration().getStandardHours() > 24)
				retrievedEvents.add(((Event) d));
		}

		return retrievedEvents;
	}

	private boolean isDisplayableInInterval(Displayable mDisplayable, Interval mInterval)
	{
		DateTime s = mDisplayable.getStart(), e = mDisplayable.getEnd();
		if (this.weekStartTime.isAfter(s))
			s = weekStartTime;

		return (s.isBefore(e) && mInterval.contains(s));
	}

	public void passTo(DayItem toPass)
	{
		selected = toPass;
		if (selected != null)
		{
			this.daysOfWeekArray[selected.getDisplayable().getStart().getDayOfWeek() % 7].add(selected.createPuppet());
			selected.createPuppet().day = selected.getDisplayable().getStart().getDayOfWeek() % 7;
		}
	}

	public void mouseOverDay(int day)
	{
		if (selected != null)
		{
			int previous = selected.createPuppet().day;
			if (previous != day)
			{
				selected.createPuppet().day += day - previous;
				this.daysOfWeekArray[day].add(selected.createPuppet());
				revalidate();
				repaint();
			}
		}
	}

	@Override
	public void setSelectedDay(DateTime time)
	{

	}
}
