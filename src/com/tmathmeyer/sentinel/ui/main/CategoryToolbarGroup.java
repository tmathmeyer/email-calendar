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

import com.tmathmeyer.sentinel.models.ToolTipListener;
import com.tmathmeyer.sentinel.ui.janeway.container.toolbar.ToolbarGroupView;
import com.tmathmeyer.sentinel.ui.tabs.CategoryManager;

public class CategoryToolbarGroup extends ToolbarGroupView
{
	private static final long serialVersionUID = 1L;
	private final JButton editCategory;

	public CategoryToolbarGroup(final MainPanel mMainPanel)
	{
		super("Categories");
		this.content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));

		editCategory = new JButton("<html>Manage<br/>Categories</html>") {
			private static final long serialVersionUID = 1L;
			JToolTip toolTip;

			@Override
			public JToolTip createToolTip()
			{
				if (toolTip == null)
				{
					JPanel panel = new JPanel(new GridLayout(0, 1));
					JLabel label = new JLabel(" Lets you manage your categories");
					panel.add(label);
					toolTip = super.createToolTip();
					// toolTip.setFocusable(false);
					toolTip.setLayout(new BorderLayout());
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
		editCategory.addMouseListener(new ToolTipListener());
		editCategory.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				CategoryManager cat = MainPanel.getInstance().getCategoryManagerTab();

				if (cat == null)
				{
					cat = new CategoryManager();
					cat.setTabId(mMainPanel.addTopLevelTab(cat, "Manage Categories", true));
				} else
				{
					MainPanel.getInstance().setSelectedTab(cat);
				}

				// TODO: use selected times. ned.display(DateTime.now());
			}
		});

		try
		{
			Image img = ImageIO.read(getClass().getResource("/com/tmathmeyer/sentinel/img/category.png"));
			editCategory.setIcon(new ImageIcon(img));
		} catch (IOException ex)
		{
		}

		// Disable focus to allow arrow keys to respond to navigation requests
		editCategory.setFocusable(false);

		this.getContent().add(editCategory);
	}
}
