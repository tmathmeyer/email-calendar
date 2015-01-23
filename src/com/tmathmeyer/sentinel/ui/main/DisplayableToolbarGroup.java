/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.ui.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;

import com.tmathmeyer.sentinel.ui.janeway.container.toolbar.ToolbarGroupView;
import com.tmathmeyer.sentinel.models.ToolTipListener;
import com.tmathmeyer.sentinel.ui.tabs.AddCommitmentDisplay;
import com.tmathmeyer.sentinel.ui.tabs.AddEventDisplay;

public class DisplayableToolbarGroup extends ToolbarGroupView
{
	private static final long serialVersionUID = 1L;
	private final JButton addEventButton, removeEventButton, addCommitmentButton;

	public DisplayableToolbarGroup(final MainPanel mMainPanel)
	{
		super("Events & Commitments");
		setPreferredWidth(600);

		this.content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));

		addEventButton = new JButton("<html>Add<br/>Event</html>") {
			private static final long serialVersionUID = 1L;
			private JToolTip toolTip;

			@Override
			public JToolTip createToolTip()
			{
				if (toolTip == null)
				{
					JPanel panel = new JPanel(new GridLayout(0, 1));
					JLabel label = new JLabel("<html>Lets you make a new event in<br/>the calendar</html>");
					panel.add(label);
					toolTip = super.createToolTip();
					toolTip.setLayout(new BorderLayout());
					toolTip.setFocusable(false);
					Insets insets = toolTip.getInsets();
					Dimension panelSize = panel.getPreferredSize();
					panelSize.width += insets.left + insets.right + 5;
					panelSize.height += insets.top + insets.bottom;
					toolTip.setPreferredSize(panelSize);
					label.setBackground(toolTip.getBackground());
					toolTip.add(panel);
				}
				return toolTip;
			}

			@Override
			public Point getToolTipLocation(MouseEvent e)
			{
				return new Point(95, 40);
			}
		};
		addEventButton.addMouseListener(new ToolTipListener());
		addEventButton.setToolTipText(" ");

		addEventButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				AddEventDisplay ned = new AddEventDisplay();
				ned.setTabId(mMainPanel.addTopLevelTab(ned, "New Event", true));
			}
		});

		addCommitmentButton = new JButton("<html>Add<br/>Commitment</html>") {
			private static final long serialVersionUID = 1L;
			JToolTip toolTip;

			@Override
			public JToolTip createToolTip()
			{
				if (toolTip == null)
				{
					JPanel panel = new JPanel(new GridLayout(0, 1));
					JLabel label = new JLabel("<html>Lets you make a new commitment in<br/>the calendar</html>");
					panel.add(label);
					toolTip = super.createToolTip();
					toolTip.setLayout(new BorderLayout());
					toolTip.setFocusable(false);
					Insets insets = toolTip.getInsets();
					Dimension panelSize = panel.getPreferredSize();
					panelSize.width += insets.left + insets.right + 5;
					panelSize.height += insets.top + insets.bottom;
					toolTip.setPreferredSize(panelSize);
					label.setBackground(toolTip.getBackground());
					toolTip.add(panel);
				}
				return toolTip;
			}

			@Override
			public Point getToolTipLocation(MouseEvent e)
			{
				return new Point(95, 40);
			}
		};
		addCommitmentButton.setToolTipText(" ");
		addCommitmentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				AddCommitmentDisplay newCommitment = new AddCommitmentDisplay();
				newCommitment.setTabId(mMainPanel.addTopLevelTab(newCommitment, "New Commitment", true));
			}
		});
		addCommitmentButton.addMouseListener(new ToolTipListener());
		removeEventButton = new JButton("<html>Remove<br/>Event</html>");
		removeEventButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				mMainPanel.addTopLevelTab(new JPanel(), "test", true);
			}
		});

		try
		{
			Image img = ImageIO.read(getClass().getResource("/com/tmathmeyer/sentinel/img/add_event.png"));
			addEventButton.setIcon(new ImageIcon(img));
			addCommitmentButton.setIcon(new ImageIcon(img));

			img = ImageIO.read(getClass().getResource("/com/tmathmeyer/sentinel/img/del_event.png"));
			removeEventButton.setIcon(new ImageIcon(img));
		} catch (IOException ex)
		{
		}

		// Disable focus to allow arrow keys to respond to navigation requests
		addEventButton.setFocusable(false);
		addCommitmentButton.setFocusable(false);
		removeEventButton.setFocusable(false);

		// eventContentPanel.add(addEventButton);
		// eventContentPanel.add(removeEventButton);
		this.getContent().add(addEventButton);
		this.getContent().add(addCommitmentButton);
	}

	public void disableRemoveEventButton()
	{
		removeEventButton.setEnabled(false);
	}
}
