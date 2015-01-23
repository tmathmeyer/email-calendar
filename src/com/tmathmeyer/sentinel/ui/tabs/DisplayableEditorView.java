/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.ui.tabs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.UUID;

import javax.swing.Box;
import javax.swing.Box.Filler;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import net.miginfocom.swing.MigLayout;

import com.tmathmeyer.sentinel.models.SelectableField;
import com.tmathmeyer.sentinel.models.client.net.CategoryClient;
import com.tmathmeyer.sentinel.models.client.net.ICategoryRegister;
import com.tmathmeyer.sentinel.models.data.Category;
import com.tmathmeyer.sentinel.models.data.Category.SerializedAction;
import com.tmathmeyer.sentinel.models.data.Commitment;
import com.tmathmeyer.sentinel.ui.DatePicker;
import com.tmathmeyer.sentinel.ui.main.MainPanel;
import com.tmathmeyer.sentinel.utils.Colors;
import com.tmathmeyer.sentinel.utils.RequestFocusListener;

/**
 * The UI for AddEvent & AddCommitment
 */
public class DisplayableEditorView extends JPanel implements ICategoryRegister
{
	private static final long serialVersionUID = 1L;
	protected JTextField nameTextField, participantsTextField;
	protected final ButtonGroup buttonGroup = new ButtonGroup();
	protected JLabel nameLabel, nameErrorLabel, dateAndTimeLabel, lblUntil, dateErrorLabel, participantsLabel,
	        lblCategory, lblStatus, lblCalendar, descriptionLabel;
	protected JRadioButton rdbtnTeam, rdbtnPersonal;
	protected JTextArea descriptionTextArea;
	protected DatePicker startTimeDatePicker, endTimeDatePicker;
	protected JComboBox<Category> eventCategoryPicker;
	protected HashMap<UUID, Category> savedMap = new HashMap<>();
	protected JComboBox<String> commitmentStatusPicker;
	protected JButton cancelButton, saveButton;

	public DisplayableEditorView(boolean showEnd)
	{
		nameTextField = new JTextField();
		nameTextField.setColumns(30);
		nameTextField.addAncestorListener(new RequestFocusListener());
		nameTextField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e)
			{

			}

			@Override
			public void keyReleased(KeyEvent e)
			{

			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER)
					saveButton.doClick();
			}
		});

		nameLabel = new JLabel("Name:");
		this.setLayout(new MigLayout(
		        "",
		        "[45px][334px,grow]",
		        "[sizegroup 1line][sizegroup 1line][sizegroup 1line][sizegroup 1line][sizegroup 1line][sizegroup 1line][30px:n,grow][grow][25px]"));
		this.add(nameLabel, "cell 0 0,alignx right,growy");
		this.add(nameTextField, "cell 1 0,alignx left,aligny baseline");

		nameErrorLabel = new JLabel("Please name this event");
		nameErrorLabel.setForeground(Color.RED);
		nameErrorLabel.setVisible(false);
		this.add(nameErrorLabel, "cell 1 0");

		dateAndTimeLabel = new JLabel("When:");
		this.add(dateAndTimeLabel, "cell 0 1,alignx right,aligny baseline");

		endTimeDatePicker = new DatePicker(true, null);
		startTimeDatePicker = new DatePicker(true, showEnd ? endTimeDatePicker : null);
		this.add(startTimeDatePicker, "flowx,cell 1 1,alignx left,growy");

		if (showEnd)
		{
			lblUntil = new JLabel("until");
			this.add(Box.createHorizontalStrut(6), "flowx,cell 1 1,alignx left,growy");
			this.add(lblUntil, "flowx,cell 1 1,alignx left,growy");
			this.add(Box.createHorizontalStrut(7), "flowx,cell 1 1,alignx left,growy");
			this.add(endTimeDatePicker, "flowx,cell 1 1,alignx left,growy");
		}

		dateErrorLabel = new JLabel("Event can't start after it ends");
		dateErrorLabel.setForeground(Color.RED);
		dateErrorLabel.setVisible(false);
		this.add(dateErrorLabel, "flowx,cell 1 1,alignx left,growy");

		participantsLabel = new JLabel("Participants:");
		this.add(participantsLabel, "cell 0 2,alignx right,aligny baseline");

		participantsTextField = new JTextField();
		this.add(participantsTextField, "cell 1 2,alignx left,aligny baseline");
		participantsTextField.setColumns(40);

		lblCategory = new JLabel("Category:");
		this.add(lblCategory, "cell 0 3,alignx right,aligny baseline");

		eventCategoryPicker = new JComboBox<>();
		eventCategoryPicker.setRenderer(new CategoryComboBoxRenderer());
		this.eventCategoryPicker.addItem(Category.DEFAULT_CATEGORY);
		for (Category c : CategoryClient.getInstance().getAllCategories())
		{
			this.eventCategoryPicker.addItem(c);
			savedMap.put(c.getUuid(), c);
		}

		this.add(eventCategoryPicker, "cell 1 3,alignx left,aligny baseline");

		if (!showEnd)
		{
			lblStatus = new JLabel("Status:");
			this.add(lblStatus, "cell 0 4,alignx right,aligny baseline");

			commitmentStatusPicker = new JComboBox<>();
			this.commitmentStatusPicker.addItem(Commitment.DEFAULT_STATUS.toString());
			this.commitmentStatusPicker.addItem(Commitment.Status.IN_PROGRESS.toString());
			this.commitmentStatusPicker.addItem(Commitment.Status.COMPLETE.toString());

			this.add(commitmentStatusPicker, "cell 1 4,alignx left,aligny baseline");
		}

		lblCalendar = new JLabel("Calendar:");
		this.add(lblCalendar, "cell 0 5,alignx right,aligny baseline");

		rdbtnPersonal = new JRadioButton("Personal");
		buttonGroup.add(rdbtnPersonal);
		this.add(rdbtnPersonal, "flowx,cell 1 5,alignx left,growy");

		rdbtnTeam = new JRadioButton("Team");
		rdbtnTeam.setSelected(true);
		buttonGroup.add(rdbtnTeam);
		this.add(rdbtnTeam, "cell 1 5");

		descriptionLabel = new JLabel("Description:");
		this.add(descriptionLabel, "cell 0 6,alignx right,aligny top");

		descriptionTextArea = new JTextArea();
		descriptionTextArea.setLineWrap(true);
		descriptionTextArea.setWrapStyleWord(true);
		descriptionTextArea.setBorder(nameTextField.getBorder());

		JScrollPane descriptionScrollPane = new JScrollPane(descriptionTextArea);
		descriptionScrollPane.setBorder(nameTextField.getBorder());
		this.add(descriptionScrollPane, "cell 1 6,grow");

		cancelButton = new JButton("Cancel");
		cancelButton.setMinimumSize(new Dimension(80, 0));
		this.add(cancelButton, "flowx,cell 1 8,alignx right,aligny bottom,tag cancel");

		saveButton = new JButton("Save");
		saveButton.setMinimumSize(new Dimension(80, 0));
		this.add(saveButton, "cell 1 8,alignx right,aligny bottom,tag ok");

		MainPanel.getInstance().registerCategory(this);
	}

	/**
	 * Sets the selected field based on user selection
	 * 
	 * @param field the field that was selected
	 */
	public void setSelected(SelectableField field)
	{
		if (field == SelectableField.NAME)
			nameTextField.requestFocus();
		else if ((field == SelectableField.START_DATE) || (field == SelectableField.DATE))
			startTimeDatePicker.requestDateFocus();
		else if (field == SelectableField.END_DATE)
			endTimeDatePicker.requestDateFocus();
		else if ((field == SelectableField.START_TIME) || (field == SelectableField.TIME))
			startTimeDatePicker.requestTimeFocus();
		else if (field == SelectableField.END_TIME)
			endTimeDatePicker.requestTimeFocus();
		else if (field == SelectableField.DESCRIPTION)
			descriptionTextArea.requestFocus();
		else if (field == SelectableField.PARTICIPANTS)
			participantsTextField.requestFocus();
		else if (field == SelectableField.CATEGORY)
			eventCategoryPicker.showPopup();
		else if (field == SelectableField.STATUS)
			commitmentStatusPicker.showPopup();
	}

	/**
	 * Renders the fields for the category drop-down menu (Necessary because of
	 * the colored squares for each category)
	 */
	private class CategoryComboBoxRenderer implements ListCellRenderer<Category>
	{
		@Override
		public Component getListCellRendererComponent(JList<? extends Category> list, Category value, int index,
		        boolean isSelected, boolean cellHasFocus)
		{
			JPanel jPanel1 = new javax.swing.JPanel();
			JPanel jPanel2 = new javax.swing.JPanel();
			Filler filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 0),
			        new java.awt.Dimension(6, 0));
			JLabel jLabel1 = new javax.swing.JLabel();
			jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 6, 3, 0));
			jPanel1.setAlignmentX(0.0F);
			jPanel1.setAlignmentY(0.0F);
			jPanel1.setOpaque(false);
			jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));

			jPanel2.setBackground(value.getColor());
			jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(Colors.BORDER));
			jPanel2.setMaximumSize(new java.awt.Dimension(15, 15));
			jPanel2.setMinimumSize(new java.awt.Dimension(15, 15));
			jPanel2.setName(""); // NOI18N
			jPanel2.setPreferredSize(new java.awt.Dimension(15, 15));

			jPanel1.add(jPanel2);
			jPanel1.add(filler1);
			jLabel1.putClientProperty("html.disable", true);
			jLabel1.setText(value.getName());
			jLabel1.setMaximumSize(new java.awt.Dimension(32767, 15));
			jPanel1.add(jLabel1);

			jPanel1.setName("ComboBox.listRenderer");
			if (isSelected)
			{
				jPanel1.setOpaque(true);
				jPanel1.setBackground(list.getSelectionBackground());
				jPanel1.setForeground(list.getSelectionForeground());
			} else
			{
				jPanel1.setBackground(list.getBackground());
				jPanel1.setForeground(list.getForeground());
			}

			jLabel1.setFont(list.getFont());

			return jPanel1;
		}
	}

	@Override
	public void fire(SerializedAction sa)
	{
		if (savedMap.get(sa.uuid) != null)
		{
			eventCategoryPicker.removeItem(savedMap.get(sa.uuid));
		}
		if (!sa.isDeleted)
		{
			eventCategoryPicker.addItem(sa.object);
			savedMap.put(sa.uuid, sa.object);
		}
	}
}
