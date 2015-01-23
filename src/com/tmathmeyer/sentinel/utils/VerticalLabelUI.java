/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.utils;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicLabelUI;

/**
 * Used for the Year view to turn the month names on their sides.
 */
public class VerticalLabelUI extends BasicLabelUI
{
	static
	{
		labelUI = new VerticalLabelUI(false);
	}

	private boolean clockwise;

	public VerticalLabelUI(boolean clockwise)
	{
		super();
		this.clockwise = clockwise;
	}

	@Override
	public Dimension getPreferredSize(JComponent c)
	{
		Dimension dim = super.getPreferredSize(c);
		return new Dimension(dim.height, dim.width);
	}

	private static Rectangle paintIconR = new Rectangle();
	private static Rectangle paintTextR = new Rectangle();
	private static Rectangle paintViewR = new Rectangle();
	private static Insets paintViewInsets = new Insets(0, 0, 0, 0);

	public void paint(Graphics g, JComponent c)
	{
		JLabel label = (JLabel) c;
		String text = label.getText();
		Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();

		if ((icon == null) && (text == null))
		{
			return;
		}

		FontMetrics fm = g.getFontMetrics();
		paintViewInsets = c.getInsets(paintViewInsets);

		paintViewR.x = paintViewInsets.left;
		paintViewR.y = paintViewInsets.top;

		paintViewR.height = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
		paintViewR.width = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);

		paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height;
		paintTextR.x = paintTextR.y = paintIconR.width = paintTextR.height;

		String clippedText = layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);

		Graphics2D g2 = (Graphics2D) g;
		AffineTransform tr = g2.getTransform();
		if (clockwise)
		{
			g2.rotate(Math.PI / 2);
			g2.translate(0, -c.getWidth());
		} else
		{
			g2.rotate(-Math.PI / 2);
			g2.translate(-c.getHeight(), 0);
		}

		if (icon != null)
		{
			icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
		}

		if (text != null)
		{
			int textX = (int) (paintTextR.x * 1.21);
			int textY = paintTextR.y + fm.getAscent();

			if (label.isEnabled())
			{
				paintEnabledText(label, g, clippedText, textX, textY);
			} else
			{
				paintDisabledText(label, g, clippedText, textX, textY);
			}
		}

		g2.setTransform(tr);
	}
}
