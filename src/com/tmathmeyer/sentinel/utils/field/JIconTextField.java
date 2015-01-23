/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.utils.field;

import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class JIconTextField extends JTextField
{
    private static final long serialVersionUID = 1111174178658547787L;
	private Icon icon;
	private Insets dummyInsets;

	/**
	 * sets up a zero-length text field with no background
	 */
	public JIconTextField()
	{
		this(0);
	}

	/**
	 * creates an X length text field with no background
	 * 
	 * @param size the size (in characters) of the panel
	 */
	public JIconTextField(int size)
	{
		super(size);
		this.icon = null;

		Border border = UIManager.getBorder("TextField.border");
		JTextField dummy = new JTextField();
		this.dummyInsets = border.getBorderInsets(dummy);
	}

	/**
	 * sets the Icon for the text field
	 * 
	 * @param icon the field icon
	 */
	public void setIcon(Icon icon)
	{
		this.icon = icon;
	}

	/**
	 * get the icon that is being shown on the field
	 * 
	 * @return the icon for the field
	 */
	public Icon getIcon()
	{
		return this.icon;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		int textX = 2;

		if (this.icon != null)
		{
			int iconWidth = icon.getIconWidth();
			int iconHeight = icon.getIconHeight();
			int x = dummyInsets.left + 5;// this is our icon's x
			textX = x + iconWidth + 2; // this is the x where text should start
			int y = (this.getHeight() - iconHeight) / 2;
			icon.paintIcon(this, g, x, y);
		}

		setMargin(new Insets(2, textX, 2, 2));
	}
}
