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
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.joda.time.DateTime;

import com.tmathmeyer.sentinel.AbstractCalendar;
import com.tmathmeyer.sentinel.CalendarLogger;
import com.tmathmeyer.sentinel.models.client.net.CategoryClient;
import com.tmathmeyer.sentinel.models.client.net.CommitmentClient;
import com.tmathmeyer.sentinel.models.client.net.EventClient;
import com.tmathmeyer.sentinel.models.client.net.ICategoryRegister;
import com.tmathmeyer.sentinel.models.data.Category;
import com.tmathmeyer.sentinel.models.data.Commitment;
import com.tmathmeyer.sentinel.models.data.Displayable;
import com.tmathmeyer.sentinel.models.data.Event;
import com.tmathmeyer.sentinel.ui.tabs.AddCommitmentDisplay;
import com.tmathmeyer.sentinel.ui.tabs.AddEventDisplay;
import com.tmathmeyer.sentinel.ui.tabs.CategoryManager;
import com.tmathmeyer.sentinel.ui.navigation.CalendarSelector;
import com.tmathmeyer.sentinel.ui.navigation.GoToPanel;
import com.tmathmeyer.sentinel.ui.navigation.MainCalendarNavigation;
import com.tmathmeyer.sentinel.ui.navigation.MiniCalendarHostIface;
import com.tmathmeyer.sentinel.ui.navigation.MiniCalendarPanel;
import com.tmathmeyer.sentinel.ui.navigation.SidebarTabbedPane;
import com.tmathmeyer.sentinel.ui.navigation.ViewSize;
import com.tmathmeyer.sentinel.ui.views.day.DayCalendar;
import com.tmathmeyer.sentinel.ui.views.month.MonthCalendar;
import com.tmathmeyer.sentinel.ui.views.week.WeekCalendar;
import com.tmathmeyer.sentinel.ui.views.year.YearCalendar;

/**
 * The main UI of the Calendar module. This singleton is basically the
 * controller for everything in the calendar module. It manages most resources.
 */
public class MainPanel extends JTabbedPane implements MiniCalendarHostIface
{
	private static final long serialVersionUID = 1L;
	private JTabbedPane mTabbedPane;
	private MiniCalendarPanel mMiniCalendarPanel;
	private JPanel mainPaneContainer;
	private JPanel centerPanel;
	private JPanel centerPanelTop;
	private JPanel centerPanelBottom;
	private JPanel sidePanel;
	private JPanel sidePanelTop;
	private SidebarTabbedPane sideTabbedPanel;
	private MainCalendarNavigation mainCalendarNavigationPanel;
	private GoToPanel mGoToPanel;
	private AbstractCalendar mCalendar, monthCal, dayCal, yearCal, weekCal;
	private DateTime lastTime = DateTime.now();
	private CalendarSelector mCalendarSelector;
	private JPopupMenu popup = new JPopupMenu();
	private JMenuItem closeAll = new JMenuItem("Close All Tabs");
	private int tabPosition;
	private final HashMap<Integer, JComponent> tabs = new HashMap<Integer, JComponent>();
	private int tab_id = 0;
	private EventClient events;
	private CategoryClient categories;
	private CommitmentClient commitments;
	private ViewSize view = ViewSize.Month;
	private static MainPanel instance;
	private Displayable currentSelected;
	private List<ICategoryRegister> registered = new ArrayList<ICategoryRegister>();

	// Left these as public variables as they are updated & read in refresh
	// loops so encapsulation makes no sense at all (just overhead)
	public boolean showPersonal = true;
	public boolean showTeam = true;

	/**
	 * Tabbed main panel to display in the calendar module. This pane will
	 * contain the rest of the elements in the calendar module, including the
	 * calendar view, add event view, add commitment view, and so on.
	 */
	public MainPanel()
	{
		if (instance != null)
		{
			throw new RuntimeException("Trying to create more than one calendar panel!");
		}

		instance = this; // Variable for creating new tabs in addTopLevelTab

	}

	@Override
	public void paint(Graphics g)
	{
		if (mTabbedPane != this)
		{
			((JFrame) SwingUtilities.getWindowAncestor(this)).addWindowListener(new WindowListener() {

				@Override
				public void windowOpened(WindowEvent arg0)
				{

				}

				@Override
				public void windowIconified(WindowEvent arg0)
				{

				}

				@Override
				public void windowDeiconified(WindowEvent arg0)
				{

				}

				@Override
				public void windowDeactivated(WindowEvent arg0)
				{

				}

				@Override
				public void windowClosing(WindowEvent arg0)
				{

				}

				@Override
				public void windowClosed(WindowEvent arg0)
				{

				}

				@Override
				public void windowActivated(WindowEvent arg0)
				{

				}
			});
			finishInit();
		}
		super.paint(g);
	}

	/**
	 * This is called AFTER login, because for some reason janeway inits all the
	 * panels before the network (aka login) is setup. If we initialize before
	 * then, we crash as there is no network session.
	 */
	void finishInit()
	{
		if (mTabbedPane == this)
			return;
		mTabbedPane = this;
		this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		// Side tabbed panel needs to be initialized here because event model
		// references the
		// category filters. If panel is initialized after events are
		// references, a null pointer
		// exception will be thrown
		sideTabbedPanel = new SidebarTabbedPane();

		categories = CategoryClient.getInstance();
		events = EventClient.getInstance(); // used for accessing events
		commitments = CommitmentClient.getInstance();
		this.mainPaneContainer = new JPanel(); // Container for the navigation
											   // and calendars
		this.sidePanel = new JPanel(); // Container to hold the top and bottom
									   // side sub-panels
		this.sidePanelTop = new JPanel(); // Panel to hold the mini calendar and
										  // the goto date
		this.centerPanel = new JPanel(); // Container for top and bottom
										 // sub-panels
		this.centerPanelTop = new JPanel(); // Container for navigation and
											// calendar selector
		this.centerPanelBottom = new JPanel(); // Container for calendar itself

		// mMiniCalendarPanel must be initialized before monthCal and dayCal
		// because they call miniMove() in their constructors
		this.mMiniCalendarPanel = new MiniCalendarPanel(DateTime.now(), this); // Mini
																			   // calendar

		// Components of center panel
		this.mCalendar = monthCal = new MonthCalendar(DateTime.now()); // Monthly
																	   // calendar

		this.dayCal = new DayCalendar(DateTime.now()); // Day calendar (hidden)
		this.yearCal = new YearCalendar(DateTime.now(), events); // Year
																 // calendar
																 // (hidden)
		this.weekCal = new WeekCalendar(DateTime.now()); // Year calendar
														 // (hidden)

		this.mainCalendarNavigationPanel = new MainCalendarNavigation(this, mCalendar); // Navigation
																						// bar

		// Components of side panel
		this.mGoToPanel = new GoToPanel(DateTime.now()); // Go to date

		// Calendar selector
		this.mCalendarSelector = new CalendarSelector();

		// Set up side panel
		sidePanel.setPreferredSize(new Dimension(200, 1024));
		sidePanel.setLayout(new BorderLayout());
		sidePanel.setBorder(new EmptyBorder(5, 5, 0, 0));

		sidePanelTop.setLayout(new BorderLayout());
		sidePanelTop.add(mMiniCalendarPanel, BorderLayout.NORTH);
		sidePanelTop.add(mGoToPanel, BorderLayout.CENTER);

		sidePanel.add(sidePanelTop, BorderLayout.NORTH);
		sidePanel.add(sideTabbedPanel, BorderLayout.CENTER);

		// Set up center panel elements
		centerPanelTop.setLayout(new BorderLayout());
		centerPanelTop.add(mainCalendarNavigationPanel, BorderLayout.WEST);
		centerPanelTop.add(mCalendarSelector, BorderLayout.EAST);

		centerPanelBottom.setLayout(new BorderLayout());
		centerPanelBottom.add(mCalendar, BorderLayout.CENTER);

		// Add top bar and monthly calendar to center panel
		centerPanel.setLayout(new BorderLayout());
		centerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		centerPanel.add(centerPanelTop, BorderLayout.NORTH);
		centerPanel.add(centerPanelBottom, BorderLayout.CENTER);

		// Set up the main panel
		mainPaneContainer.setLayout(new BorderLayout());
		mainPaneContainer.add(sidePanel, BorderLayout.WEST);
		mainPaneContainer.add(centerPanel, BorderLayout.CENTER);

		// Add default tabs to main panel
		addTopLevelTab(mainPaneContainer, "Calendar", false);

		// add context menu
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.isPopupTrigger() && indexAtLocation(e.getX(), e.getY()) != -1)
					popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
		popup.add(closeAll);
		closeAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// remove all but calendar
				while (getTabCount() > 1)
				{
					removeTabAt(1);
					MainPanel.getInstance().mainCalendarNavigationPanel.grabFocus();
					tabs.clear();
				}
			}
		});

		// Get focus for arrow key input
		mainCalendarNavigationPanel.grabFocus();

	}

	/**
	 * gives external access for adding tabs (that can be closed!)
	 * 
	 * @param component the content of the tab, usually a calendar or an event
	 *            creation/editing page
	 * @param name the name of the tab
	 * @param closeable whether the tab can be closed
	 */
	public int addTopLevelTab(JComponent component, String name, boolean closeable)
	{

		if (!closeable)
		{
			mTabbedPane.addTab(name, component);
			return -1;
		} else
		{
			class Title extends JButton
			{
				private static final long serialVersionUID = 1L;
				public final int ID;

				public Title(String name, int ID)
				{
					super(name);
					this.ID = ID;
				}
			}
			mTabbedPane.addTab(null, component);
			tabPosition = mTabbedPane.indexOfComponent(component);
			JPanel tabInformation = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
			JLabel tabInfoName = new JLabel(name);
			Title tabInfoClose = new Title("\u2716", tab_id++); // we need an
																// icon for this
																// eventually
																// //for
																// historarical
																// purposes
			tabInfoClose.setFont(tabInfoClose.getFont().deriveFont((float) 8));
			tabInfoClose.setMargin(new Insets(0, 0, 0, 0));
			tabInfoClose.setPreferredSize(new Dimension(20, 17));

			tabInformation.setOpaque(false);

			tabInfoClose.setFocusable(false);
			tabInfoClose.setBorder(null);

			tabInformation.add(tabInfoName);
			tabInformation.add(tabInfoClose);

			tabInformation.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
			mTabbedPane.setTabComponentAt(tabPosition, tabInformation);

			tabs.put(tabInfoClose.ID, component);

			ActionListener listener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					int ID = ((Title) e.getSource()).ID;

					JComponent jc = tabs.get(ID);
					mTabbedPane.remove(jc);
					tabs.remove(ID);
					mainCalendarNavigationPanel.grabFocus();
					if (jc instanceof ICategoryRegister)
					{
						unregisterCategory((ICategoryRegister) jc);
					}
				}
			};

			tabInfoClose.addActionListener(listener);
			mTabbedPane.setSelectedIndex(tabPosition);
			return tabInfoClose.ID;
		}
	}

	public AbstractCalendar getMOCA()
	{
		return mCalendar;
	}

	/**
	 * Changes the date being displayed on the calendar
	 * 
	 * @param newTime time to set the calendar to
	 */
	public void display(DateTime newDate)
	{
		mCalendar.display(newDate);
		refreshView();
		lastTime = newDate;
	}

	/**
	 * Changes the date being displayed on the mini calendar
	 * 
	 * @param date the date to move the mini calendar to
	 */
	public void miniMove(DateTime date)
	{
		mMiniCalendarPanel.display(date);
		lastTime = date;
	}

	/**
	 * Adds a new event to the database and refreshes the UI
	 * 
	 * @param newEvent The event to add
	 */
	public void addEvent(Event newEvent)
	{
		events.put(newEvent);
		mCalendar.updateDisplayable(newEvent, true);
	}

	/**
	 * Updates an event as long as it retains its ID
	 * 
	 * @param updateEvent event to update
	 */
	public void updateEvent(Event updateEvent)
	{
		if ((currentSelected instanceof Event) && updateEvent.getUuid().equals(((Event) currentSelected).getUuid()))
			clearSelected();
		events.put(updateEvent);
	}

	/**
	 * Adds a new commitment to the database and refreshes the UI
	 * 
	 * @param newCommitment The commitment to add
	 */
	public void addCommitment(Commitment newCommitment)
	{
		commitments.put(newCommitment);
	}

	/**
	 * Updates a commitment as long both commitments have the same ID
	 * 
	 * @param updateCommitment
	 */
	public void updateCommitment(Commitment updateCommitment)
	{
		commitments.update(updateCommitment);
	}

	/**
	 * Adds a new category to the database and refreshes the UI
	 * 
	 * @param newCategory The category to add
	 */
	public void addCategory(Category newCategory)
	{
		categories.put(newCategory);
	}

	/**
	 * Updates a category as long both categories have the same ID
	 * 
	 * @param updateCategory
	 */
	public void updateCategory(Category updateCategory)
	{
		categories.update(updateCategory);
	}

	/**
	 * Returns the category model of the main panel
	 * 
	 * @return category model of the main panel instance
	 */
	public CategoryClient getCategoryModel()
	{
		return this.categories;
	}

	/**
	 * Gets the singleton instance of this panel to avoid passing it everywhere
	 * 
	 * @return the instance
	 */
	public static MainPanel getInstance()
	{
		if (instance == null)
			instance = new MainPanel();
		return instance;
	}

	/**
	 * Toggle monthly calendar view
	 */
	public void viewMonth()
	{
		view = ViewSize.Month;
		refreshView(monthCal);
	}

	/**
	 * Toggle daily calendar view
	 */
	public void viewDay()
	{
		view = ViewSize.Day;
		refreshView(dayCal);
		this.mCalendarSelector.toDay();
	}

	public void viewYear()
	{
		view = ViewSize.Month;
		refreshView(yearCal);
	}

	public Displayable getSelectedDisplayable()
	{
		return currentSelected;
	}

	public void viewWeek()
	{
		view = ViewSize.Week;
		refreshView(weekCal);
	}

	/**
	 * Updates calendar in view and sets navigation panel to act on the active
	 * view
	 * 
	 * @param absCalendar
	 */
	private void refreshView(final AbstractCalendar absCalendar)
	{
		clearSelected();
		centerPanelBottom.remove(mCalendar);
		mCalendar = absCalendar;
		mainCalendarNavigationPanel.updateCalendar(mCalendar);
		centerPanelBottom.add(mCalendar, BorderLayout.CENTER);
		mCalendar.display(lastTime);

		mainCalendarNavigationPanel.grabFocus();

		revalidate();
		repaint();
	}

	/**
	 * Refresh the view to properly show additions and navigation
	 */
	public void refreshView()
	{
		mCalendar.display(lastTime);
		revalidate();
		repaint();
	}

	/**
	 * Unregister category
	 * 
	 * @param sa the category that was deleted
	 */
	public void unregisterCategory(ICategoryRegister e)
	{
		registered.remove(e);
	}

	/**
	 * Register category
	 * 
	 * @param sa the category that was added
	 */
	public void registerCategory(ICategoryRegister e)
	{
		registered.add(e);
	}

	/**
	 * Refresh categories
	 */
	public void refreshCategories(Category.SerializedAction sa)
	{
		for (ICategoryRegister e : registered)
		{
			e.fire(sa);
		}
		refreshView();
	}

	/**
	 * @return current view
	 */
	public ViewSize getView()
	{
		return view;
	}

	/**
	 * Get calendar navigation panel Used to give focus back to arrow key
	 * listeners
	 * 
	 * @return the calendar navigation panel
	 */
	public MainCalendarNavigation getCalNav()
	{
		return this.mainCalendarNavigationPanel;
	}

	/**
	 * Close specified tab
	 * 
	 * @param id
	 */
	public void closeTab(int id)
	{
		mTabbedPane.remove(tabs.get(id));
		tabs.remove(id);
	}

	/**
	 * Highlights the selected monthItem on the calendar
	 * 
	 * @param Item the month item to highlight
	 */
	public void updateSelectedDisplayable(Displayable item)
	{
		mCalendar.select(item);
		this.sideTabbedPanel.selectDetailTab();
		this.currentSelected = item;
		sideTabbedPanel.showDetails(item);
	}

	/**
	 * Edits the selected displayable
	 * 
	 * @param Item the month item containing the displayable to edit
	 */
	public void editSelectedDisplayable(Displayable item)
	{
		updateSelectedDisplayable(item);

		if (item instanceof Event)
		{
			AddEventDisplay mAddEventDisplay = new AddEventDisplay((Event) item);
			boolean openNewTab = true;
			JComponent tabToOpen = null;

			for (JComponent c : tabs.values())
			{
				if (openNewTab && c instanceof AddEventDisplay)
				{
					openNewTab = !((AddEventDisplay) c).matchingEvent(mAddEventDisplay);
					tabToOpen = c;
				}
			}
			if (openNewTab)
			{
				mAddEventDisplay.setTabId(instance.addTopLevelTab(mAddEventDisplay, "Edit Event", true));
			} else if (tabToOpen != null)
			{
				setSelectedTab(tabToOpen);
			}

		} else if (item instanceof Commitment)
		{
			AddCommitmentDisplay mAddCommitmentDisplay = new AddCommitmentDisplay((Commitment) item);
			boolean openNewTab = true;
			JComponent tabToOpen = null;

			for (JComponent c : tabs.values())
			{
				if (openNewTab && c instanceof AddCommitmentDisplay)
				{
					openNewTab = !((AddCommitmentDisplay) c).matchingCommitment(mAddCommitmentDisplay);
					tabToOpen = c;
				}
			}
			if (openNewTab)
			{
				mAddCommitmentDisplay.setTabId(instance.addTopLevelTab(mAddCommitmentDisplay, "Edit Commitment", true));
			} else if (tabToOpen != null)
			{
				setSelectedTab(tabToOpen);
			}
		}
	}

	/**
	 * Clears selected MonthItem from calendar
	 */
	public void clearSelected()
	{
		updateSelectedDisplayable(null);
		sideTabbedPanel.clearDetails();
		this.mainCalendarNavigationPanel.grabFocus();
	}

	/**
	 * Will set the currently viewed tab to the category tab
	 */
	public CategoryManager getCategoryManagerTab()
	{
		for (JComponent c : tabs.values())
		{
			if (c instanceof CategoryManager)
			{
				return (CategoryManager) c;
			}
		}
		return null;
	}

	/**
	 * Will set the currently viewed tab to the calendar tab
	 */
	public void openCalendarViewTab()
	{
		mTabbedPane.setSelectedComponent(mainPaneContainer);
	}

	public void setSelectedTab(JComponent tabToFocus)
	{
		try
		{
			this.mTabbedPane.setSelectedComponent(tabToFocus);
		} catch (IllegalArgumentException e)
		{
			CalendarLogger.LOGGER.severe(e.toString()); // tab not found
		}
	}

	/**
	 * Delete specified category
	 * 
	 * @param categoryToDelete the category to delete
	 */
	public void deleteCategory(Category categoryToDelete)
	{
		categoryToDelete.delete();
	}

	/**
	 * deletes the displayable, then repaints
	 * 
	 * @param displayableToDelete the displayable to delete
	 */
	public void deleteDisplayable(Displayable displayableToDelete)
	{
		if (this.currentSelected == displayableToDelete)
		{
			this.clearSelected();
		}
		displayableToDelete.delete();
		this.refreshView();
	}

	public void setSelectedDay(DateTime time)
	{
		lastTime = time;
		mCalendar.setSelectedDay(time);
	}

	/**
	 * Updates category filter tab to show additions and deletions via the
	 * category manager
	 */
	public void refreshCategoryFilterTab()
	{
		this.sideTabbedPanel.refreshFilterTab();
	}

	/**
	 * Gets the selected categories by the filters in side pane
	 */
	public Collection<UUID> getSelectedCategories()
	{
		return this.sideTabbedPanel.getSelectedCategories();
	}

	/**
	 * Gets the selected statuses by the filters in side pane
	 */
	public Collection<String> getSelectedStatuses()
	{
		return this.sideTabbedPanel.getSelectedStatuses();
	}

	/**
	 * Determines whether commitments should be displayed or not
	 * 
	 * @return boolean indicating whether or not to display commitments
	 */
	public boolean showCommitments()
	{
		return this.sideTabbedPanel.showCommitments();
	}

	/**
	 * Determines whether events should be displayed or not
	 * 
	 * @return boolean indicating whether or not to display events
	 */
	public boolean showEvents()
	{
		return this.sideTabbedPanel.showEvents();
	}

	/**
	 * 
	 * @return the currently selected displayable
	 */
	public Displayable getSelectedEvent()
	{
		return this.currentSelected;
	}

	/**
	 * 
	 * @return the currently selected day
	 */
	public DateTime getSelectedDay()
	{
		return this.lastTime;
	}
}
