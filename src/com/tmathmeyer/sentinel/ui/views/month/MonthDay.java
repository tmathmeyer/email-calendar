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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import com.tmathmeyer.sentinel.DayStyle;
import com.tmathmeyer.sentinel.models.data.Commitment;
import com.tmathmeyer.sentinel.models.data.Displayable;
import com.tmathmeyer.sentinel.models.data.Event;
import com.tmathmeyer.sentinel.ui.main.MainPanel;
import com.tmathmeyer.sentinel.utils.Colors;

/**
 * UI for showing events on a day in a month view. This is the entire day block.
 */
public class MonthDay extends JPanel
{
	private static final long serialVersionUID = 1L;
	private boolean borderTop;
	JLabel header = new JLabel();
	private Displayable selected;
	private List<Displayable> allitems = new ArrayList<>();
	private final DateTime day;
	private DayStyle style;
	private boolean isSelected = false;

	Color grayit, textit = Colors.TABLE_TEXT, bg = Colors.TABLE_BACKGROUND;

	public MonthDay(DateTime initDay, DayStyle style, final MonthCalendar parent)
	{
		this.day = initDay;
		this.style = style;
		Color grayit = Colors.TABLE_GRAY_HEADER, textit = Colors.TABLE_TEXT, bg = Colors.TABLE_BACKGROUND;
		switch (style)
		{
			case Normal:
				grayit = Colors.TABLE_GRAY_HEADER;
				textit = Colors.TABLE_GRAY_TEXT;
				break;
			case OutOfMonth:
				grayit = bg;
				break;
			case Today:
				grayit = Colors.TABLE_GRAY_HEADER;
				textit = Colors.TABLE_GRAY_TEXT;
				break;
			default:
				throw new IllegalStateException("DayStyle is not a valid DayStyle!");
		}
		setBackground(bg);
		setForeground(textit);
		borderTop = grayit.equals(bg);
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

		header.setBackground(grayit);
		header.setForeground(textit);
		header.setFont(new java.awt.Font("DejaVu Sans", style == DayStyle.Today ? Font.BOLD : Font.PLAIN, 12));
		header.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		header.setText(Integer.toString(initDay.getDayOfMonth()));
		header.setAutoscrolls(true);
		header.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
		header.setMaximumSize(new java.awt.Dimension(10000, 17));
		header.setOpaque(true);

		if (style == DayStyle.Today)
		{
			Font font = header.getFont();
			Map<TextAttribute, ?> attributes = font.getAttributes();
			Map<TextAttribute, Object> newtributes = new HashMap<>();
			newtributes.putAll(attributes);
			newtributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			header.setFont(font.deriveFont(newtributes));
		}

		add(header);

		addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e)
			{
				parent.dispatchEvent(e);
				MainPanel.getInstance().setSelectedDay(day);
				MainPanel.getInstance().clearSelected();
				parent.setEscaped(false);
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{

			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (parent.isEscaped())
				{
					MonthDay releasedDay = parent.getMonthDayAtCursor();
					Displayable selected = MainPanel.getInstance().getSelectedEvent();
					if (selected != null && releasedDay != null)
					{
						MutableDateTime newTime = new MutableDateTime(selected.getStart());

						newTime.setYear(releasedDay.day.getYear());
						newTime.setDayOfYear(releasedDay.day.getDayOfYear());

						selected.setTime(newTime.toDateTime());

						selected.update();
					}
				}
				parent.dispatchEvent(e);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				parent.dispatchEvent(e);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				setBackground(Colors.TABLE_BACKGROUND);
				parent.setEscaped(true);

			}
		});

		header.addMouseListener(new MouseListener() {

			@Override
			public void mousePressed(MouseEvent e)
			{
				MainPanel.getInstance().setSelectedDay(day);
				MainPanel.getInstance().clearSelected();
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				MainPanel.getInstance().miniMove(day);
				MainPanel.getInstance().viewDay();
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				setBackground(Colors.TABLE_BACKGROUND);
			}
		});

		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e)
			{
				parent.repaint();
				parent.dispatchEvent(e);
			}

			@Override
			public void mouseMoved(MouseEvent e)
			{
				parent.dispatchEvent(e);
			}

		});
	}

	public void reBorder(boolean top, boolean left, boolean bottom)
	{
		setBorder(javax.swing.BorderFactory.createMatteBorder((top || borderTop) ? 1 : 0, left ? 1 : 0, bottom ? 1 : 0,
		        1, Colors.BORDER));
	}

	/**
	 * Add an event or commitment to a given day of the month
	 * 
	 * @param d the displayable to add
	 */
	public void addDisplayable(Displayable d)
	{
		allitems.add(d);
		this.removeAll();
		revalidate();
		repaint();
	}

	/**
	 * removes a displayable from this monthday
	 * 
	 * @param d the commitment or event to remove
	 */
	public void removeDisplayable(Displayable d)
	{
		allitems.remove(d);
		this.removeAll();
		revalidate();
		repaint();
	}

	// call revalidate, not this method directly, it is an override
	@Override
	public void doLayout()
	{
		int total = this.getHeight();
		int hidden = 0;
		removeAll();
		add(header);
		total -= header.getHeight();

		Collections.sort(allitems, new Comparator<Displayable>() {

			@Override
			public int compare(Displayable o1, Displayable o2)
			{
				if ((o1 instanceof Event) && (o2 instanceof Commitment))
					return -1;
				else if ((o1 instanceof Commitment) && (o2 instanceof Event))
					return 1;
				else if ((o1 instanceof Event) && (o2 instanceof Event))// both
																		// events
																		// so
																		// check
																		// multiday
																		// event
				{
					if (((Event) o1).isMultiDayEvent() && !((Event) o2).isMultiDayEvent())
						return -1;
					else if (!((Event) o1).isMultiDayEvent() && ((Event) o2).isMultiDayEvent())
						return 1;
				}
				// if it gets to this poing then they are both commitments, or
				// both multi day events, or both single day events
				if (o1.getStart().isBefore(o2.getStart()))
					return -1;
				else
					// will default to 1, no need to check if they start at the
					// same time....
					return 1;
			}
		});

		for (Displayable elt : allitems)
		{
			if (hidden > 0)
			{
				hidden++;
			} else
			{
				total -= 24; // TODO: don't use constant. getHeight fails when
				             // slow resizing to min though...
				if (total <= 10)
				{
					hidden = 1;
				} else
				{
					this.add(MonthItem.generateFrom(elt, selected, day, this));
				}
			}
		}

		if (hidden == 1) // silly, add it anyway
		{
			this.add(MonthItem.generateFrom(allitems.get(allitems.size() - 1), selected, day, this));
		} else if (hidden > 1)
		{
			this.add(new CollapsedMonthItem(hidden));
		}

		super.doLayout();
	}

	/**
	 * remove all events from the monthday
	 */
	public void clearDisplayable()
	{
		allitems.clear();
		removeAll();
		revalidate();
		repaint();
	}

	/**
	 * "selects" an item by keeping a special reference to and and highlighting
	 * it
	 * 
	 * @param item the displayable item that the user has clicked on to select
	 */
	public void select(Displayable item)
	{
		selected = item;
		for (Component c : getComponents())
		{
			if (c instanceof MonthItem)
			{
				MonthItem mi = ((MonthItem) c);
				mi.setSelected(mi.getDisplayable().equals(item));
			}
		}
	}

	/**
	 * clears all selected displayables
	 */
	public void clearSelected()
	{
		selected = null;
		for (Component c : getComponents())
		{
			if (c instanceof MonthItem)
			{
				MonthItem mi = ((MonthItem) c);
				mi.setSelected(false);
			}
		}
	}

	/**
	 * gets this DateTime's day
	 * 
	 * @return a DateTime
	 */
	public DateTime getDay()
	{
		return this.day;
	}

	/**
	 * Updates header and text to show selected status
	 * 
	 * @param b the selected status of the day
	 */
	public void setSelected(boolean b)
	{
		isSelected = b;
		this.header.setBackground(b ? Colors.SELECTED_BACKGROUND : (this.style == DayStyle.OutOfMonth ? grayit
		        : Colors.TABLE_GRAY_HEADER));
		this.header.setForeground(b ? Color.WHITE : Colors.TABLE_GRAY_TEXT);
	}

	/**
	 * Getter for isSelected
	 * 
	 * @return whether the day is selected
	 */
	public boolean isSelected()
	{
		return isSelected;
	}
}
