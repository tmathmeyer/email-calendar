/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import com.tmathmeyer.sentinel.models.client.local.EventClient;
import com.tmathmeyer.sentinel.ui.janeway.container.JanewayTabModel;
import com.tmathmeyer.sentinel.ui.janeway.container.TabPanel;
import com.tmathmeyer.sentinel.ui.main.MainPanel;
import com.tmathmeyer.sentinel.ui.main.RibbonToolbar;

public class Calendar extends JFrame implements WindowListener
{
    private static final long serialVersionUID = -6889334929889129532L;
	private final MainPanel mainPanel = new MainPanel();
	private final RibbonToolbar toolbar = new RibbonToolbar(mainPanel, true);

	public Calendar()
	{
		JanewayTabModel master = new JanewayTabModel("Calendar", new ImageIcon(), toolbar, mainPanel);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(800, 600));
		add(new TabPanel(master).getTabbedPane(), BorderLayout.CENTER);
		pack();
		
		addWindowListener(this);
	}

	public static void main(String... args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		for (LookAndFeelInfo x : UIManager.getInstalledLookAndFeels())
		{
			if (x != null && x.getName().equals("GTK+"))
			{
				UIManager.setLookAndFeel(x.getClassName());
			}
		}
		
		new Calendar().setVisible(true);
	}

	@Override
    public void windowActivated(WindowEvent arg0)
    {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void windowClosed(WindowEvent arg0)
    {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void windowClosing(WindowEvent arg0)
    {
		EventClient.getInstance().writeToFile(System.getProperty("user.home")+"/.sentinel-events");
    }

	@Override
    public void windowDeactivated(WindowEvent arg0)
    {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void windowDeiconified(WindowEvent arg0)
    {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void windowIconified(WindowEvent arg0)
    {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void windowOpened(WindowEvent arg0)
    {
	    // TODO Auto-generated method stub
	    
    }

}
