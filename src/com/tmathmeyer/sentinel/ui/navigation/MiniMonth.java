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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import com.tmathmeyer.sentinel.ui.main.MainPanel;
import com.tmathmeyer.sentinel.utils.Colors;
import com.tmathmeyer.sentinel.utils.Months;

/**
 * Sidebar sidekick month view to enable quick navigation to other
 * months/days/weeks. Scrolls independely of main view
 */
public class MiniMonth extends JPanel
{
	private static final long serialVersionUID = 1L;
	public MiniMonth(DateTime time, final MiniCalendarHostIface mc, boolean monthOnly)
	{
		this.setLayout(new GridLayout(7, 7));
		MutableDateTime prevMonth = new MutableDateTime(time);
		prevMonth.setDayOfMonth(1);
		prevMonth.addMonths(-1); // What is prevMonth for?
		String[] dayLabel = { "S", "M", "T", "W", "R", "F", "S" };

		MouseListener monthChanger = new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent me)
			{
			}

			@Override
			public void mouseEntered(MouseEvent me)
			{
			}

			@Override
			public void mouseExited(MouseEvent me)
			{
			}

			@Override
			public void mousePressed(MouseEvent me)
			{
			}

			@Override
			public void mouseReleased(MouseEvent me)
			{
				DayLabel d = (DayLabel) (me.getSource());
				if (!(d instanceof DescriptiveDayLabel))
				{
					mc.display(d.getMonth());
				}
			}
		};

		MutableDateTime referenceDay = new MutableDateTime(time);
		// reset to the first of the month at midnight, then find Sunday
		referenceDay.setDayOfMonth(1);
		referenceDay.setMillisOfDay(0);
		int first = referenceDay.getDayOfWeek();
		referenceDay.addDays(-first);
		boolean flipFlop = false;

		// add day labels
		for (int i = 0; i < 7; i++)
		{
			DayLabel day = new DescriptiveDayLabel(dayLabel[i], time);
			day.borderize((i % 7) == 0, i >= 5 * 7, (i % 7) == 6);
			add(day);
			day.addMouseListener(monthChanger);
		}

		// generate days, 6*7 covers all possible months, so we just loop
		// through and add each day
		for (int i = 0; i < (6 * 7); i++)
		{
			DayLabel day;
			if (monthOnly || MainPanel.getInstance().getView() == ViewSize.Month)
			{
				if (referenceDay.getDayOfMonth() == 1)
					flipFlop ^= true; // flops the flip flop flappity flip
			} else if (MainPanel.getInstance().getView() == ViewSize.Day)
				flipFlop = referenceDay.getDayOfYear() == time.getDayOfYear()
				        && referenceDay.getYear() == time.getYear();
			else if (MainPanel.getInstance().getView() == ViewSize.Week)
			{
				if (Months.getWeekStart(time).getMonthOfYear() == 12 && Months.getWeekStart(time).getDayOfMonth() >= 26) // Exception
																														 // case
																														 // for
																														 // weeks
																														 // between
																														 // years
					flipFlop = time.getMonthOfYear() == 12 ? i >= 35 : i <= 6;
				else
					flipFlop = referenceDay.getDayOfYear() >= Months.getWeekStart(time).getDayOfYear()
					        && referenceDay.getDayOfYear() <= Months.getWeekStart(time).getDayOfYear() + 6;
			}

			if (flipFlop)
				day = new ActiveDayLabel(referenceDay.toDateTime());
			else
				day = new InactiveDayLabel(referenceDay.toDateTime());

			day.borderize((i % 7) == 0, i >= 5 * 7, (i % 7) == 6);
			add(day);
			day.addMouseListener(monthChanger);
			referenceDay.addDays(1); // go to next day
		}
	}

	private class DayLabel extends JLabel
	{
		private static final long serialVersionUID = 1L;
		private DateTime day;
		private Font font;
		private Map<TextAttribute, Object> fontAttributes;

		public DayLabel(DateTime time, boolean todayable)
		{
			this.setForeground(Color.BLACK);
			this.setText(Integer.toString(time.getDayOfMonth()));
			this.setHorizontalAlignment(SwingConstants.CENTER);

			if (todayable)
			{
				DateTime now = DateTime.now();
				if (now.getDayOfYear() == time.getDayOfYear() && now.getYear() == time.getYear())
				{
					font = this.getFont();
					fontAttributes = new HashMap<TextAttribute, Object>();
					fontAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
					fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);

					this.setFont(font.deriveFont(fontAttributes));
				}
			}

			this.day = time;
		}

		public DateTime getMonth()
		{
			return day;
		}

		public void borderize(boolean left, boolean bottom, boolean right)
		{
			setBorder(javax.swing.BorderFactory.createMatteBorder(0, left ? 1 : 0, bottom ? 1 : 0, right ? 1 : 0,
			        Colors.BORDER));
		}
	}

	private class ActiveDayLabel extends DayLabel
	{
		private static final long serialVersionUID = 1L;
		public ActiveDayLabel(DateTime time)
		{
			super(time, true);
			setForeground(Colors.TABLE_TEXT);
			setBackground(Colors.TABLE_BACKGROUND);
			setOpaque(true);
		}
	}

	private class InactiveDayLabel extends DayLabel
	{
		private static final long serialVersionUID = 1L;
		public InactiveDayLabel(DateTime time)
		{
			super(time, true);
			setBackground(Colors.TABLE_GRAY_HEADER);
			setForeground(Colors.TABLE_GRAY_TEXT);
			this.setOpaque(true);
		}
	}

	private class DescriptiveDayLabel extends DayLabel
	{
		private static final long serialVersionUID = 1L;
		public DescriptiveDayLabel(String text, DateTime time)
		{
			super(time, false);
			setText(text);
			this.setFont(getFont().deriveFont(Font.ITALIC));
			setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER));
		}

		@Override
		public void borderize(boolean left, boolean bottom, boolean right)
		{
			// don't screw with borders. we don't need them here
		}
	}

}
