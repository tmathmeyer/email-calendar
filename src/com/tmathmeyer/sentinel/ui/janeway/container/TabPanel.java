/*******************************************************************************
 * Copyright (c) 2013 -- WPI Suite
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Chris Casola
 *    Andrew Hurle
 ******************************************************************************/

package com.tmathmeyer.sentinel.ui.janeway.container;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

/**
 * The main application panel. Holds each modules' tabs, each of which contain a
 * toolbar and main component.
 */
@SuppressWarnings("serial")
public class TabPanel extends JPanel
{

	/** The tab pane */
	protected JTabbedPane tabbedPane;
	private List<JanewayTabModel> models = new ArrayList<>();

	public TabPanel(List<IJanewayModule> modules)
	{
		for(IJanewayModule ijm : modules)
		{
			models.addAll(ijm.getTabs());
		}
		this.tabbedPane = new JTabbedPane();

		// Populate tabs
		addModules();

		// Set the layout and add the tabs to the panel
		this.setLayout(new BorderLayout());
		this.add(tabbedPane, BorderLayout.CENTER);
	}
	
	public TabPanel(JanewayTabModel ... modules)
	{
		models = Arrays.asList(modules);
		this.tabbedPane = new JTabbedPane();

		// Populate tabs
		addModules();

		// Set the layout and add the tabs to the panel
		this.setLayout(new BorderLayout());
		this.add(tabbedPane, BorderLayout.CENTER);
	}

	/**
	 * Return the JTabbedPane holding the module tabs
	 * 
	 * @return
	 */
	public JTabbedPane getTabbedPane()
	{
		return tabbedPane;
	}

	/**
	 * Called by the constructor to add a tab for each module
	 */
	private void addModules()
	{
		for (JanewayTabModel jtm : models)
		{
			// Create a panel to hold the buttons and tab contents
			JPanel newPanel = new JPanel();
			SpringLayout newPanelLayout = new SpringLayout();
			newPanel.setLayout(newPanelLayout);

			// Constrain the toolbar
			newPanelLayout.putConstraint(SpringLayout.NORTH, jtm.getToolbar(), 0, SpringLayout.NORTH, newPanel);
			newPanelLayout.putConstraint(SpringLayout.WEST, jtm.getToolbar(), 0, SpringLayout.WEST, newPanel);
			newPanelLayout.putConstraint(SpringLayout.EAST, jtm.getToolbar(), 0, SpringLayout.EAST, newPanel);
			newPanelLayout.putConstraint(SpringLayout.SOUTH, jtm.getToolbar(), 100, SpringLayout.NORTH,
			        jtm.getToolbar());

			// Constrain the main component
			newPanelLayout.putConstraint(SpringLayout.NORTH, jtm.getMainComponent(), 0, SpringLayout.SOUTH,
			        jtm.getToolbar());
			newPanelLayout.putConstraint(SpringLayout.WEST, jtm.getMainComponent(), 0, SpringLayout.WEST, newPanel);
			newPanelLayout.putConstraint(SpringLayout.EAST, jtm.getMainComponent(), 0, SpringLayout.EAST, newPanel);
			newPanelLayout.putConstraint(SpringLayout.SOUTH, jtm.getMainComponent(), 0, SpringLayout.SOUTH,
			        newPanel);

			// Add the toolbar and main component to the new panel
			newPanel.add(jtm.getToolbar());
			newPanel.add(jtm.getMainComponent());
			newPanel.setName(jtm.getName());

			// Add a tab to the tabbed pane containing the new panel
			tabbedPane.addTab(jtm.getName(), jtm.getIcon(), newPanel);
		}
	}
}
