package com.tmathmeyer.sentinel.ui.views.week;

import javax.swing.JPanel;

import javax.swing.BoxLayout;
import javax.swing.Scrollable;

import java.awt.Rectangle;

import java.awt.Dimension;

/**
 * Custom JPanel which does not scroll horizontally
 */
public class ScrollableBox extends JPanel implements Scrollable
{
	private static final long serialVersionUID = 1L;
	/**
	 * Create the panel.
	 */
	public ScrollableBox()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 20;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 5;
	}

	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}
}
