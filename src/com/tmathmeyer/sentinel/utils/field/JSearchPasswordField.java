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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.UIManager;

public class JSearchPasswordField extends JIconPasswordField implements FocusListener
{
    private static final long serialVersionUID = -7353260091165012133L;
	private String textWhenNotFocused;

	/**
	 * creates a zero length password field with the provided background text
	 * 
	 * @param greyMSG the default grey message
	 */
	public JSearchPasswordField(String greyMSG)
	{
		this(greyMSG, 0);
	}

	/**
	 * creates an X length password field with no background text
	 * 
	 * @param i the length of the password box
	 */
	public JSearchPasswordField(int i)
	{
		this("password", i);
	}

	/**
	 * creates an 8 length password field with the text "password" in the
	 * background
	 */
	public JSearchPasswordField()
	{
		this("password", 8);
	}

	/**
	 * 
	 * @param greyMSG the default grey message
	 * @param i the length of the password box
	 */
	public JSearchPasswordField(String greyMSG, int i)
	{
		super(i);
		this.textWhenNotFocused = greyMSG;
		this.addFocusListener(this);
	}

	/**
	 * return the text that is shown when the field isn't focused and has no
	 * user-input text
	 * 
	 * @return get the background text
	 */
	public String getTextWhenNotFocused()
	{
		return this.textWhenNotFocused;
	}

	/**
	 * update the text that is shown in the background of the field
	 * 
	 * @param newText the background text
	 */
	public void setTextWhenNotFocused(String newText)
	{
		this.textWhenNotFocused = newText;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (!this.hasFocus() && new String(getPassword()).equals(""))
		{
			int height = this.getHeight();
			Font prev = g.getFont();
			Font italic = prev.deriveFont(Font.ITALIC);
			Color prevColor = g.getColor();
			g.setFont(italic);
			g.setColor(UIManager.getColor("textInactiveText"));
			int h = g.getFontMetrics().getHeight();
			int textBottom = (height - h) / 2 + h - 4;
			int x = this.getInsets().left;
			Graphics2D g2d = (Graphics2D) g;
			RenderingHints hints = g2d.getRenderingHints();
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.drawString(textWhenNotFocused, x, textBottom);
			g2d.setRenderingHints(hints);
			g.setFont(prev);
			g.setColor(prevColor);
		}

	}

	@Override
	public void focusGained(FocusEvent e)
	{
		this.repaint();
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		this.repaint();
	}
}
