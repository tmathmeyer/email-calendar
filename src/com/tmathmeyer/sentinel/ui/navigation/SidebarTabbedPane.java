/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.ui.navigation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.tmathmeyer.sentinel.CalendarLogger;
import com.tmathmeyer.sentinel.ui.main.MainPanel;
import com.tmathmeyer.sentinel.utils.Colors;
import com.tmathmeyer.sentinel.models.client.net.CategoryClient;
import com.tmathmeyer.sentinel.models.client.net.ICategoryRegister;
import com.tmathmeyer.sentinel.models.data.Category;
import com.tmathmeyer.sentinel.models.data.Commitment;
import com.tmathmeyer.sentinel.models.data.Displayable;
import com.tmathmeyer.sentinel.models.data.Event;

public class SidebarTabbedPane extends JTabbedPane implements ICategoryRegister
{
	private static final long serialVersionUID = 1L;
	private JPanel detailTab;
	private JTextArea detailTextPane;
	private Document detailTextDoc;
	private SimpleAttributeSet normalTextStyle;
	private SimpleAttributeSet boldBlueTextStyle;
	private SimpleAttributeSet boldRedTextStyle;
	private JScrollPane detailScrollPane;
	private JLabel detailTitleLabel;
	private JPanel detailButtonPane;
	private JButton detailEditButton;
	private JButton detailDeleteButton;
	private JTextPane commitmentTab;
	private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("h:mm aa");
	private DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MM/dd/yy");
	private Displayable currentDisplayable;

	// Category filter tab
	private JPanel categoryFilterTab;
	private JPanel categoryList;
	private JPanel categoryButtonPanel;
	private JPanel eventCommitmentTab;
	private JButton selectAllButton;
	private JButton clearAllButton;
	private boolean showCommitments = true; // Show events and commitments by
											// default
	private boolean showEvents = true;
	private boolean isUser = true; // Avoid extra db calls when
								   // selecting/unselecting all
	private JScrollPane categoryScroll;
	private List<Category> allPlusDefault = new ArrayList<Category>();
	private HashMap<JCheckBox, Category> checkBoxCategoryMap = new HashMap<JCheckBox, Category>();
	private Collection<UUID> selectedCategories = new ArrayList<UUID>();
	private Collection<String> selectedStatuses = new ArrayList<String>();
	private int catsLeft; // The # of categories left before there are none
						  // selected

	/**
	 * Tabbed panel in the navigation sidebar to hold additional details of
	 * selected items
	 */
	public SidebarTabbedPane()
	{

		// setup
		this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		this.setFocusable(false);

		setupTextStyles();
		setupDetailTab();
		setupCommitementTab();
		setUpCategoryFilterTab();

		selectedStatuses = new ArrayList<String>();
		selectedStatuses.add("Not Started");
		selectedStatuses.add("In Progress");
		selectedStatuses.add("Complete");

		// add tabs
		this.addTab("Details", detailTab);
		// this.addTab("Commitments", commitmentTab);
		this.addTab("Filters", categoryFilterTab);

		MainPanel.getInstance().registerCategory(this);
	}

	/**
	 * Initializes some text styles to be used in the JTextAreas
	 */
	private void setupTextStyles()
	{
		normalTextStyle = new SimpleAttributeSet();
		StyleConstants.setFontFamily(normalTextStyle, "Tahoma");
		StyleConstants.setFontSize(normalTextStyle, 12);

		// These are unused in current formatting
		boldBlueTextStyle = new SimpleAttributeSet(normalTextStyle);
		StyleConstants.setBold(boldBlueTextStyle, true);
		StyleConstants.setForeground(boldBlueTextStyle, Colors.SELECTED_BACKGROUND);

		boldRedTextStyle = new SimpleAttributeSet(normalTextStyle);
		StyleConstants.setBold(boldRedTextStyle, true);
		StyleConstants.setForeground(boldRedTextStyle, Color.MAGENTA);
	}

	/**
	 * initializes all the components of the commitment tab
	 */
	private void setupCommitementTab()
	{
		commitmentTab = new JTextPane();
		commitmentTab.setEditable(false);
		commitmentTab.setCursor(null);
		commitmentTab.setFocusable(false);
		commitmentTab.setOpaque(false);
		commitmentTab.setFont(new Font("Tahoma", Font.PLAIN, 12));
		commitmentTab.putClientProperty("html.disable", true); // prevents html
															   // parsing
		DefaultCaret caret = (DefaultCaret) commitmentTab.getCaret(); // prevents
																	  // scrollpane
																	  // from
																	  // autoscrolling
																	  // to
																	  // bottom
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
	}

	/**
	 * initializes all the components of the details tab
	 */
	private void setupDetailTab()
	{
		// setup container panel
		detailTab = new JPanel();
		detailTab.setLayout(new BorderLayout());
		detailTab.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		detailTab.setFocusable(false);

		// setup text area
		detailTextPane = new JTextArea();
		detailTextPane.setWrapStyleWord(true);
		detailTextPane.setLineWrap(true);
		detailTextPane.setMaximumSize(new Dimension(180, Short.MAX_VALUE));
		detailTextPane.setEditable(false);
		detailTextPane.setCursor(null);
		detailTextPane.setFocusable(false);
		detailTextPane.setOpaque(false);
		detailTextPane.setFont(new Font("Tahoma", Font.PLAIN, 12));
		detailTextPane.putClientProperty("html.disable", true); // prevents html
																// parsing
		DefaultCaret caret = (DefaultCaret) detailTextPane.getCaret(); // prevents
																	   // scrollpane
																	   // from
																	   // autoscrolling
																	   // to
																	   // bottom
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

		detailTextDoc = detailTextPane.getDocument();

		// setup title label
		detailTitleLabel = new JLabel();
		detailTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		detailTitleLabel.putClientProperty("html.disable", true); // prevents
																  // html
																  // parsing
		detailTitleLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		detailTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// setup buttons and listeners
		detailEditButton = new JButton("Edit");
		detailEditButton.setFocusable(false);
		detailEditButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				MainPanel instance = MainPanel.getInstance();
				instance.editSelectedDisplayable(currentDisplayable);
			}
		});

		detailDeleteButton = new JButton("Delete");
		detailDeleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				MainPanel.getInstance().deleteDisplayable(currentDisplayable);
			}
		});

		// buttons disabled by default
		setButtonsEnabled(false);

		// add buttons to button container
		detailButtonPane = new JPanel();
		detailButtonPane.setLayout(new FlowLayout());
		detailButtonPane.add(detailEditButton);
		detailButtonPane.setFocusable(false);
		detailButtonPane.add(detailDeleteButton);

		// put entire tab into a scroll pane
		detailScrollPane = new JScrollPane(detailTextPane);
		detailScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		detailScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		detailScrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		// add text area and button container to detail tab
		detailTab.add(detailTitleLabel, BorderLayout.NORTH);
		detailTab.add(detailScrollPane, BorderLayout.CENTER);
		detailTab.add(detailButtonPane, BorderLayout.SOUTH);
	}

	/**
	 * Sets up the components of the category filter tab
	 */
	private void setUpCategoryFilterTab()
	{

		// Set up container panel
		categoryFilterTab = new JPanel();
		categoryFilterTab.setLayout(new BoxLayout(categoryFilterTab, BoxLayout.Y_AXIS));
		categoryFilterTab.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		categoryFilterTab.putClientProperty("html.disable", true);
		categoryFilterTab.setAlignmentY(LEFT_ALIGNMENT);

		// Set up panel with selecition for events and commitments
		eventCommitmentTab = new JPanel();
		eventCommitmentTab.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		eventCommitmentTab.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		eventCommitmentTab.putClientProperty("html.disable", true);
		eventCommitmentTab.setAlignmentY(CENTER_ALIGNMENT);
		eventCommitmentTab.setAlignmentX(CENTER_ALIGNMENT);

		JCheckBox showEvent = new JCheckBox("Events");
		showEvent.setSelected(true);
		showEvent.addItemListener(new CheckBoxListener(null));
		showEvent.setMinimumSize(getPreferredSize());

		JCheckBox showCommitments = new JCheckBox("Commits");
		showCommitments.setSelected(true);
		showCommitments.addItemListener(new CheckBoxListener(null));
		showCommitments.setMinimumSize(getPreferredSize());

		eventCommitmentTab.add(showEvent);
		eventCommitmentTab.add(showCommitments);
		eventCommitmentTab.setMaximumSize(new Dimension(1000, 25));
		eventCommitmentTab.setMinimumSize(new Dimension(1000, 25));
		// Set up panel with categories
		categoryList = new JPanel();
		categoryList.setLayout(new BoxLayout(categoryList, BoxLayout.Y_AXIS));
		categoryList.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		categoryList.putClientProperty("html.disable", true);
		categoryList.setAlignmentX(TOP_ALIGNMENT);
		categoryList.setAlignmentY(TOP_ALIGNMENT);

		// Add categories to panel
		populateCategoryList(categoryList);

		// Set up scroll panel
		categoryScroll = new JScrollPane(categoryList);
		categoryScroll.putClientProperty("html.disable", true);
		categoryScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		categoryScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		categoryScroll.getVerticalScrollBar().setUnitIncrement(10);
		categoryScroll.setBorder(new EmptyBorder(5, 5, 5, 5));
		categoryScroll.setAlignmentY(LEFT_ALIGNMENT);

		// Set up selection buttons
		categoryButtonPanel = new JPanel();
		categoryButtonPanel.setLayout(new GridLayout());
		categoryButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		categoryButtonPanel.putClientProperty("html.disable", true);

		selectAllButton = new JButton("Select All");
		selectAllButton.putClientProperty("html.disable", true);
		selectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				selectAllCategories();
			}
		});

		clearAllButton = new JButton("Clear");
		clearAllButton.putClientProperty("html.disable", true);
		clearAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				deselectAllCategories();
			}
		});

		categoryButtonPanel.add(selectAllButton);
		categoryButtonPanel.add(clearAllButton);

		// Set up UI
		categoryFilterTab.add(eventCommitmentTab);
		categoryFilterTab.add(Box.createVerticalStrut(3));
		categoryButtonPanel.setMaximumSize(new Dimension(1000, 20));
		categoryFilterTab.add(categoryButtonPanel);
		categoryFilterTab.add(categoryScroll);
		categoryFilterTab.setFocusable(false); // Keep tab form grabbing focus
											   // from arrow keys

	}

	/**
	 * Sets the enabled status of the edit and cancel buttons
	 * 
	 * @param enabled flag to set enabled status
	 */
	private void setButtonsEnabled(boolean enabled)
	{
		detailEditButton.setEnabled(enabled);
		detailDeleteButton.setEnabled(enabled);
	}

	/**
	 * Updates the text area to display information about a displayable
	 * 
	 * @param mDisplayable the displayable to show
	 */
	public void showDetails(Displayable mDisplayable)
	{
		clearDetails();
		currentDisplayable = mDisplayable;

		if (mDisplayable instanceof Event)
		{
			try
			{
				detailTitleLabel.setOpaque(true);
				detailTitleLabel.setBackground(((Event) mDisplayable).getColor());
				detailTitleLabel.setText(mDisplayable.getName());
				if (!((Event) mDisplayable).isMultiDayEvent())
					detailTextDoc.insertString(detailTextDoc.getLength(), "Time:\n   "
					        + ((Event) mDisplayable).getStart().toString(timeFormatter) + " - "
					        + ((Event) mDisplayable).getEnd().toString(timeFormatter) + "\n", normalTextStyle);
				else
				{
					detailTextDoc.insertString(detailTextDoc.getLength(), "Starts:\n   "
					        + ((Event) mDisplayable).getStart().toString(dateFormatter) + " "
					        + ((Event) mDisplayable).getStart().toString(timeFormatter) + "\n", normalTextStyle);
					detailTextDoc.insertString(detailTextDoc.getLength(), "Ends:\n   "
					        + ((Event) mDisplayable).getEnd().toString(dateFormatter) + " "
					        + ((Event) mDisplayable).getEnd().toString(timeFormatter) + "\n", normalTextStyle);
				}

				detailTextDoc.insertString(detailTextDoc.getLength(),
				        "Description:\n   " + mDisplayable.getDescription() + "\n", normalTextStyle);
				if (((Event) mDisplayable).getAssociatedCategory() != null)
					detailTextDoc.insertString(detailTextDoc.getLength(), "Category:\n   "
					        + ((Event) mDisplayable).getAssociatedCategory().getName() + "\n", normalTextStyle);

			} catch (Exception e)
			{
				CalendarLogger.LOGGER.severe(e.toString());
			}

			setButtonsEnabled(true);
		} else if (mDisplayable instanceof Commitment)
		{
			try
			{
				detailTextDoc.insertString(detailTextDoc.getLength(), "Date:\n   "
				        + ((Commitment) mDisplayable).getStart().toString(dateFormatter) + "\n", normalTextStyle);
				detailTextDoc.insertString(detailTextDoc.getLength(), "Time:\n   "
				        + ((Commitment) mDisplayable).getStart().toString(timeFormatter) + "\n", normalTextStyle);
				detailTextDoc.insertString(detailTextDoc.getLength(),
				        "Description:\n   " + mDisplayable.getDescription() + "\n", normalTextStyle);
				if (((Commitment) mDisplayable).getAssociatedCategory() != null)
				{
					detailTextDoc.insertString(detailTextDoc.getLength(), "Category:\n   "
					        + ((Commitment) mDisplayable).getAssociatedCategory().getName() + "\n", normalTextStyle);
				}
				detailTextDoc.insertString(detailTextDoc.getLength(), "Status:\n   "
				        + ((Commitment) mDisplayable).getStatus().toString() + "\n", normalTextStyle);
			} catch (Exception e)
			{
				CalendarLogger.LOGGER.severe(e.toString());
			}

			setButtonsEnabled(true);
		}
	}

	/**
	 * clears the text area of any details
	 */
	public void clearDetails()
	{
		detailTitleLabel.setOpaque(false);
		detailTitleLabel.setText("");
		detailTextPane.setText("");
		setButtonsEnabled(false);
	}

	/**
	 * Refreshes the category filter tab
	 */
	public void refreshFilterTab()
	{
		populateCategoryList(categoryList);
		if (catsLeft != 0)
		{
			clearAllButton.setSelected(true);
		}
		categoryScroll.getVerticalScrollBar().setValue(0); // Scroll to top
														   // after adding
														   // element
		this.categoryFilterTab.revalidate();
		this.categoryFilterTab.repaint();
	}

	/**
	 * Populate provided JPanel with list of categories
	 * 
	 * @param categoryListHolder the JPanel to populate
	 */
	public void populateCategoryList(JPanel categoryListHolder)
	{
		// Clear category list panel
		categoryListHolder.removeAll();

		// Clear category list array
		selectedCategories.clear();
		checkBoxCategoryMap.clear();

		List<Category> allCategories = CategoryClient.getInstance().getAllCategories();

		// Use different list to avoid commitment and uncategorized from
		// displaying in other places
		// since the allCategories list is passed by reference
		allPlusDefault.clear();
		allPlusDefault.add(Category.DEFAULT_CATEGORY);

		// Adding categories to represent the commitment statuses
		Category notStarted = new Category();
		notStarted.setName("Not Started");
		allPlusDefault.add(notStarted);
		Category incomplete = new Category();
		incomplete.setName("In Progress");
		allPlusDefault.add(incomplete);
		Category complete = new Category();
		complete.setName("Complete");
		allPlusDefault.add(complete);
		allPlusDefault.addAll(allCategories);

		for (Category c : allPlusDefault)
		{
			// Check box for current category
			JCheckBox categoryCheckBox = new JCheckBox(c.getName());
			categoryCheckBox.setAlignmentX(BOTTOM_ALIGNMENT);
			categoryCheckBox.setFocusable(false);
			categoryCheckBox.setSelected(true);
			categoryCheckBox.putClientProperty("html.disable", true);
			categoryCheckBox.addItemListener(new CheckBoxListener(c));

			// Category color indicator for current category
			JPanel categoryColor = new JPanel();
			categoryColor.setPreferredSize(new Dimension(16, 15));
			categoryColor.setMaximumSize(new Dimension(16, 15));
			categoryColor.putClientProperty("html.disable", true);
			categoryColor.setMinimumSize(new Dimension(16, 15));
			categoryColor.setLayout(new GridLayout(1, 1));
			categoryColor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

			if ("Uncategorized".equals(c.getName())) // If uncategorized
			{
				// Show both colors (team and personal events)
				JPanel doubleColor = new JPanel();
				doubleColor.putClientProperty("html.disable", true);
				doubleColor.setLayout(new GridLayout(1, 2));
				JPanel blue = new JPanel();
				blue.putClientProperty("html.disable", true);
				blue.setBackground(new Color(125, 157, 227));
				JPanel red = new JPanel();
				red.putClientProperty("html.disable", true);
				red.setBackground(new Color(227, 125, 147));
				doubleColor.add(blue);
				doubleColor.add(red);
				categoryColor.add(doubleColor);
			} else if ("Not Started".equals(c.getName())) // If not started
			{
				categoryColor.setBackground(Color.RED);
			} else if ("In Progress".equals(c.getName())) // If in progress
			{
				categoryColor.setBackground(Color.YELLOW);
			} else if ("Complete".equals(c.getName())) // If complete
			{
				categoryColor.setBackground(Color.GREEN);
			} else
			// If not, get category color
			{
				categoryColor.setBackground(c.getColor());
			}

			categoryColor.setAlignmentX(BOTTOM_ALIGNMENT);

			// Container for category color and check box
			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
			container.putClientProperty("html.disable", true);
			container.setAlignmentY(LEFT_ALIGNMENT);
			container.setAlignmentX(BOTTOM_ALIGNMENT);
			container.setMaximumSize(new Dimension(10000, 20));

			// Store reference to check boxes and categories
			if (categoryCheckBox.isSelected()
			        && !(selectedCategories.contains(c.getUuid()) && !c.getName().equals("Not Started")
			                && !c.getName().equals("In Progress") && !c.getName().equals("Complete")))
			{
				selectedCategories.add(c.getUuid());
			}

			if (!checkBoxCategoryMap.containsKey(categoryCheckBox))
			{
				checkBoxCategoryMap.put(categoryCheckBox, c);
			}

			// Set up container UI
			container.add(categoryColor);
			container.add(Box.createHorizontalStrut(2));
			container.add(categoryCheckBox);
			container.add(Box.createHorizontalGlue());

			// Add container to category list holder
			categoryListHolder.add(container);
		}

		categoryListHolder.add(Box.createVerticalGlue());

		catsLeft = this.selectedCategories.size() + this.selectedStatuses.size();
	}

	/**
	 * Get the collection of selected categories
	 * 
	 * @return the collection of selected UUIDs
	 */
	public Collection<UUID> getSelectedCategories()
	{
		return this.selectedCategories;
	}

	/**
	 * Get the collection of selected commitment statuses
	 * 
	 * @return the collection of selected UUIDs
	 */
	public Collection<String> getSelectedStatuses()
	{
		return this.selectedStatuses;
	}

	/**
	 * Custom listener for check boxes
	 *
	 * Maintains the selected category collection updated
	 */
	private class CheckBoxListener implements ItemListener
	{

		private Category referencedCategory;

		public CheckBoxListener(Category c)
		{
			this.referencedCategory = c;
		}

		public void itemStateChanged(ItemEvent e)
		{
			JCheckBox tmp = ((JCheckBox) e.getSource());

			if (tmp.isSelected())
			{
				if (referencedCategory == null && tmp.getText().equals("Events"))
				{
					showEvents = true;
				} else if (referencedCategory == null && tmp.getText().equals("Commits"))
				{
					showCommitments = true;
				} else
				{
					if ("Not Started".equals(referencedCategory.getName()))
					{
						selectedStatuses.add("Not Started");
					} else if ("In Progress".equals(referencedCategory.getName()))
					{
						selectedStatuses.add("In Progress");
					} else if ("Complete".equals(referencedCategory.getName()))
					{
						selectedStatuses.add("Complete");
					} else if (!selectedCategories.contains(referencedCategory.getUuid()))
					{
						selectedCategories.add(referencedCategory.getUuid());
					}
					catsLeft++;
					clearAllButton.setEnabled(true);
				}
			} else
			{
				if (referencedCategory == null && tmp.getText().equals("Events"))
				{
					showEvents = false;
				} else if (referencedCategory == null && tmp.getText().equals("Commits"))
				{
					showCommitments = false;
				} else
				{
					if ("Not Started".equals(referencedCategory.getName()))
					{
						selectedStatuses.remove("Not Started");
					} else if ("In Progress".equals(referencedCategory.getName()))
					{
						selectedStatuses.remove("In Progress");
					} else if ("Complete".equals(referencedCategory.getName()))
					{
						selectedStatuses.remove("Complete");
					} else if (selectedCategories.contains(referencedCategory.getUuid()))
					{
						selectedCategories.remove(referencedCategory.getUuid());
					}
					catsLeft--;
					if (catsLeft <= 0)
					{
						clearAllButton.setEnabled(false);
					}
				}
			}
			if (isUser)
				MainPanel.getInstance().refreshView(); // Update view for
													   // selected filters
		}
	}

	/**
	 * Checks all category check boxes and adds UUID to selectedCategories
	 * collection
	 */
	public void selectAllCategories()
	{
		isUser = false; // Do not call db upon single each check and uncheck

		// Iterate over check boxes and categories, checking them and adding to
		// list
		for (Map.Entry<JCheckBox, Category> entry : checkBoxCategoryMap.entrySet())
		{
			JCheckBox key = entry.getKey();
			Category value = entry.getValue();

			if (!key.isSelected())
			{
				key.setSelected(true);
				if ("Not Started".equals(value.getName()))
				{
					selectedStatuses.add("Not Started");
				} else if ("In Progress".equals(value.getName()))
				{
					selectedStatuses.add("In Progress");
				} else if ("Complete".equals(value.getName()))
				{
					selectedStatuses.add("Complete");
				} else if (!selectedCategories.contains(value.getUuid()))
				{
					selectedCategories.add(value.getUuid());
				}
			}
		}

		catsLeft = this.selectedCategories.size() + 3;
		clearAllButton.setEnabled(true);

		MainPanel.getInstance().refreshView(); // Update all events
		isUser = true; // set is user back to true
	}

	/**
	 * Un-checks all category check boxes and clears selectedCategories
	 * collection
	 */
	public void deselectAllCategories()
	{
		// Clear previous selected categories
		selectedCategories.clear();
		selectedStatuses.clear();

		isUser = false; // Do not call db upon single each check and uncheck

		// Iterate over check boxes and uncheck them
		for (JCheckBox key : checkBoxCategoryMap.keySet())
			key.setSelected(false);

		MainPanel.getInstance().refreshView(); // Update all events
		isUser = true; // set is user back to true

		catsLeft = 0;
		clearAllButton.setEnabled(false);

	}

	/**
	 * Returns whether commitments should be shown or not
	 * 
	 * @return boolean indicating whether commitments should be shown in current
	 *         calendar view
	 */
	public boolean showCommitments()
	{
		return this.showCommitments;
	}

	/**
	 * Returns whether events should be shown or not
	 * 
	 * @return boolean indicating whether events should be shown in current
	 *         calendar view
	 */
	public boolean showEvents()
	{
		return this.showEvents;
	}

	/**
	 * Focuses the details tab
	 */
	public void selectDetailTab()
	{
		this.setSelectedComponent(detailTab);
	}

	/**
	 * Focuses the filter tab
	 */
	public void selectFilterTab()
	{
		this.setSelectedComponent(categoryFilterTab);
	}

	@Override
	public void fire(Category.SerializedAction sa)
	{
		populateCategoryList(categoryList);
	}

}
