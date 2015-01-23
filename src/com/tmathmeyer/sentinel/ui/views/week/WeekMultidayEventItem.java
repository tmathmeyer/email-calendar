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

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.tmathmeyer.sentinel.models.data.Event;
import com.tmathmeyer.sentinel.ui.main.MainPanel;

public class WeekMultidayEventItem extends JLabel
{
	private static final long serialVersionUID = 1L;
	Event mEvent;
	boolean isSpacer = false;
	MultidayEventItemType type;

	int row = 0;

	/**
	 * Constructor for WeekMultidayEventItem
	 * 
	 * @param event event associated with the item
	 * @param type type of item
	 */
	public WeekMultidayEventItem(Event event, MultidayEventItemType type)
	{
		this.mEvent = event;
		this.type = type;
		this.setOpaque(true);

		setupListeners();
	}

	/**
	 * Constructor for WeekMultidayEventItem with text
	 * 
	 * @param event event associated with the item
	 * @param type type of item
	 * @param itemText the text to display on the item
	 */
	public WeekMultidayEventItem(Event event, MultidayEventItemType type, String itemText)
	{
		this.mEvent = event;
		this.type = type;
		this.setText(itemText);
		this.setOpaque(true);

		setupListeners();
	}

	/**
	 * Sets up the listeners for the item
	 */
	private void setupListeners()
	{
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.getClickCount() > 1)
				{
					MainPanel.getInstance().editSelectedDisplayable(mEvent);
				} else
				{
					MainPanel.getInstance().updateSelectedDisplayable(mEvent);
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
			}
		});
	}

	/**
	 * gets the event associated with this item
	 * 
	 * @return the associated event
	 */
	public Event getEvent()
	{
		return mEvent;
	}

	/**
	 * sets the event associated with this item
	 * 
	 * @param mEvent the event to set
	 */
	public void setEvent(Event mEvent)
	{
		this.mEvent = mEvent;
	}

	/**
	 * Sets the border depending on the type and location of the item in the
	 * grid
	 * 
	 * @param mColor the color to set the border to
	 * @param isSelected whether the item is currently selected
	 */
	public void setDynamicBorder(Color mColor, boolean isSelected)
	{
		if (isSpacer)
			this.setBorder(BorderFactory.createMatteBorder(row == 0 ? 1 : 0,
			        (type == MultidayEventItemType.Start || type == MultidayEventItemType.Single) ? 1 : 0, 1,
			        (type == MultidayEventItemType.End || type == MultidayEventItemType.Single) ? 1 : 0, mColor));
		else
			this.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(row == 0 ? 1 : 0,
			        (type == MultidayEventItemType.Start || type == MultidayEventItemType.Single) ? 1 : 0, 1,
			        (type == MultidayEventItemType.End || type == MultidayEventItemType.Single) ? 1 : 0, mColor),
			        new CompoundBorder(BorderFactory.createMatteBorder(1,
			                (type == MultidayEventItemType.Start || type == MultidayEventItemType.Single) ? 1 : 0, 1,
			                (type == MultidayEventItemType.End || type == MultidayEventItemType.Single) ? 1 : 0,
			                isSelected ? mEvent.getColor().darker() : mEvent.getColor()), new EmptyBorder(1, 1, 1, 1))));
	}

	/**
	 * updates the border corresponding to the item's selected status
	 * 
	 * @param selected the item's selected status
	 */
	public void setSelected(boolean selected)
	{
		setDynamicBorder(mEvent.getColor().darker(), selected);
	}

	/**
	 * gets the row number in the parent grid array
	 * 
	 * @param rows the row in the grid array that this item is stored in
	 */
	public int getRows()
	{
		return row;
	}

	/**
	 * sets the row number in the parent grid array
	 * 
	 * @param rows the row number to set
	 */
	public void setRows(int row)
	{
		this.row = row;
	}

	/**
	 * setter for whether or not the item is a spacer
	 * 
	 * @param isSpacer whether or not the item is a spacer
	 */
	public boolean isSpacer()
	{
		return isSpacer;
	}

	/**
	 * sets whether or not the item is a spacer
	 * 
	 * @param isSpacer whether or not the item is a spacer
	 */
	public void setSpacer(boolean isSpacer)
	{
		this.isSpacer = isSpacer;
	}

	/**
	 * gets the item's type
	 * 
	 * @return the item's type
	 */
	public MultidayEventItemType getType()
	{
		return type;
	}

	/**
	 * sets the items type
	 * 
	 * @param type the type to set
	 */
	public void setType(MultidayEventItemType type)
	{
		this.type = type;
	}
}
