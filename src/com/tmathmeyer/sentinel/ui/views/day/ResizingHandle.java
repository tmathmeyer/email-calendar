package com.tmathmeyer.sentinel.ui.views.day;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

import com.tmathmeyer.sentinel.ui.views.day.collisiondetection.DayItem;

public class ResizingHandle extends JComponent
{
    private static final long serialVersionUID = 1L;
	private DayItem linked;
	int currIncrement;
	int startPos;
	boolean isTopBar;

	public ResizingHandle(DayItem d, boolean isTop)
	{
		this.isTopBar = isTop;
		linked = d;
		currIncrement = 0;
		startPos = -1;
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0)
			{
				currIncrement = 0;
				startPos = -1;
				getParent().dispatchEvent(arg0);
			}

			@Override
			public void mousePressed(MouseEvent arg0)
			{
				// TODO Auto-generated method stub

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

		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent arg0)
			{

			}

			@Override
			public void mouseDragged(MouseEvent arg0)
			{
				if (startPos == -1)
				{
					startPos = ResizingHandle.this.getLocationOnScreen().y;
				}
				int y;
				do
				{
					y = arg0.getYOnScreen() - startPos - currIncrement;
					if (y > 14)
					{
						if (!isTopBar)
						{
							linked.addMinutesToEnd(15);
							currIncrement += 15;
						}
						if (isTopBar)
						{
							linked.addMinutesToStart(15);
							currIncrement += 15;
						}
					}
					if (y < -14)
					{
						if (!isTopBar)
						{
							linked.addMinutesToEnd(-15);
							currIncrement -= 15;
						}
						if (isTopBar)
						{
							linked.addMinutesToStart(-15);
							currIncrement -= 15;
						}
					}
				} while (Math.abs(y) > 14);
			}
		});
	}
}
