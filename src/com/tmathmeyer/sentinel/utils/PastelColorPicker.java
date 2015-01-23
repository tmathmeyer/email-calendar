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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class PastelColorPicker extends JPanel implements MouseListener, MouseMotionListener
{
    private static final long serialVersionUID = 2969448143269015689L;

	public static final int DEFAULT_SELECTOR_LOCATION = -9000;

	private int lastMousePosition = DEFAULT_SELECTOR_LOCATION;
	private Color currentColor = null;
	private HSLColor inputColor;

	/**
	 * a color picker for category colors. uses pastel colors only
	 */
	public PastelColorPicker()
	{
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	@Override
	public void paintComponent(Graphics g)
	{
		int width = this.getWidth();
		for (float i = 0; i < 360; i++)
		{
			int start = (int) (width * i / 360);
			int end = (int) (width * (i + 1) / 360);

			g.setColor(new HSLColor(i, 54f, 82f).getRGB());
			g.fillRect(start, 0, end - start, this.getHeight());
		}

		if (this.lastMousePosition > 0)
		{
			if (lastMousePosition > this.getWidth())
				lastMousePosition = this.getWidth();
			float colorAtMouse = 360f * (((float) this.lastMousePosition) / ((float) this.getWidth()));

			this.currentColor = new HSLColor(colorAtMouse, 64f, 82f).getRGB();

		} else if (lastMousePosition == DEFAULT_SELECTOR_LOCATION)
		{
			lastMousePosition = this.getWidth() / 2;
			float colorAtMouse = 360f * (((float) this.lastMousePosition) / ((float) this.getWidth()));
			this.currentColor = new HSLColor(colorAtMouse, 64f, 82f).getRGB();
		} else
			lastMousePosition = 0;

		g.setColor(Color.BLACK);
		g.fillRect(lastMousePosition - 20, 12, 40, this.getHeight() - 24);
		g.setColor(this.currentColor);
		g.fillRect(lastMousePosition - 18, 14, 36, this.getHeight() - 28);
	}

	/**
	 * 
	 * @return the current selected color of the color picker
	 */
	public Color getCurrentColorState()
	{
		return this.currentColor;
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{
	}

	@Override
	public void mouseMoved(MouseEvent arg0)
	{
	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
		this.lastMousePosition = arg0.getX();
		this.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent arg0)
	{
		this.lastMousePosition = arg0.getX();
		this.repaint();
	}

	/**
	 * Moves the color selector to the specified location
	 * 
	 * @param color RGB color upon which to base the calculation
	 */
	public void moveColorSelector(Color color)
	{
		inputColor = new HSLColor(color);
		this.lastMousePosition = (int) ((this.getWidth() * inputColor.getHue()) / 360f);
		this.repaint();
	}

	/**
	 * Moves the color selector to the specified location
	 * 
	 * @param location integer location to move the cursor to
	 */
	public void moveColorSelector(int location)
	{
		this.lastMousePosition = location;
		this.repaint();
	}

}
