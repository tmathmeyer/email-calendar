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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.UUID;

import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import com.tmathmeyer.sentinel.models.data.Category;
import com.tmathmeyer.sentinel.models.data.Commitment;
import com.tmathmeyer.sentinel.ui.DatePickerListener;
import com.tmathmeyer.sentinel.ui.main.MainPanel;

/**
 * UI for adding and editing a commitment
 */
public class AddCommitmentDisplay extends DisplayableEditorView
{
	private static final long serialVersionUID = 1L;
	private int tabid;
	private Commitment commitmentToEdit;
	private UUID existingCommitmentID; // UUID of event being edited
	private boolean isEditingCommitment;
	private DateTime selectedTime;

	// For a new commitment.
	public AddCommitmentDisplay()
	{
		super(false);
		this.isEditingCommitment = false;
		this.selectedTime = new DateTime();
		setCurrentDateAndTime();
		setUpListeners();
	}

	// For editing a commitment.
	public AddCommitmentDisplay(Commitment mCommitment)
	{
		super(false);
		this.isEditingCommitment = true;
		this.commitmentToEdit = mCommitment;
		this.existingCommitmentID = commitmentToEdit.getUuid();
		populateCommitmentFields(commitmentToEdit);
		setUpListeners();
	}

	/**
	 * Fill fields of commitment UI with existing data of commitment
	 * 
	 * @param mCommitment the commitment being edited
	 */
	private void populateCommitmentFields(Commitment mCommitment)
	{
		nameTextField.setText(mCommitment.getName());
		startTimeDatePicker.setDateTime(mCommitment.getStart());
		participantsTextField.setText(mCommitment.getParticipants());
		this.rdbtnPersonal.setSelected(!mCommitment.isProjectwide());
		this.rdbtnTeam.setSelected(mCommitment.isProjectwide());
		descriptionTextArea.setText(mCommitment.getDescription());
		if (mCommitment.getAssociatedCategory() != null)
		{
			this.eventCategoryPicker.setSelectedItem(mCommitment.getAssociatedCategory());
		} else
		{
			this.eventCategoryPicker.setSelectedItem(Category.DEFAULT_CATEGORY);
		}
		this.commitmentStatusPicker.setSelectedItem(mCommitment.getStatus().toString());
	}

	/**
	 * Set up listeners for UI operation
	 */
	private void setUpListeners()
	{
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				attemptSave();

			}
		});
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				MainPanel.getInstance().closeTab(tabid);
			}
		});

		nameTextField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e)
			{
				nameErrorLabel.setVisible(!validateText(nameTextField.getText(), nameErrorLabel));
				saveButton.setEnabled(isSaveable());
			}

			@Override
			public void focusGained(FocusEvent e)
			{
			}
		});

		nameTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				nameErrorLabel.setVisible(!validateText(nameTextField.getText(), nameErrorLabel));
				saveButton.setEnabled(isSaveable());
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				nameErrorLabel.setVisible(!validateText(nameTextField.getText(), nameErrorLabel));
				saveButton.setEnabled(isSaveable());
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				// Not triggered by plaintext fields
			}
		});

		startTimeDatePicker.addChangeListener(new DatePickerListener() {

			@Override
			public void datePickerUpdate(DateTime mDateTime)
			{
				dateErrorLabel.setVisible(!validateDate(startTimeDatePicker.getDateTime(), dateErrorLabel));
				saveButton.setEnabled(isSaveable());
			}
		});

		// this should be called in updateSaveable() and thus isnt necessary
		// here
		// but error msg didn't start visible unless I called it directly
		validateDate(startTimeDatePicker.getDateTime(), dateErrorLabel);
		saveButton.setEnabled(isSaveable());
	}

	/**
	 * Checks to see if fields need saving, if so, saves to server database
	 */
	public void attemptSave()
	{
		if (!isSaveable())
			return;
		Commitment e = new Commitment();
		e.setName(nameTextField.getText().trim());
		e.setDescription(descriptionTextArea.getText());
		e.setStart(startTimeDatePicker.getDateTime());
		e.setProjectCommitment(rdbtnTeam.isSelected());
		e.setParticipants(participantsTextField.getText().trim());
		e.setCategory(((Category) eventCategoryPicker.getSelectedItem()).getUuid());
		if (commitmentStatusPicker.getSelectedItem() == "Not Started")
			e.setStatus(Commitment.Status.NOT_STARTED);
		else if (commitmentStatusPicker.getSelectedItem() == "In Progress")
			e.setStatus(Commitment.Status.IN_PROGRESS);
		else
			e.setStatus(Commitment.Status.COMPLETE);

		if (isEditingCommitment)
		{
			e.setUuid(existingCommitmentID);
			MainPanel.getInstance().updateCommitment(e);
		} else
			MainPanel.getInstance().addCommitment(e);

		saveButton.setEnabled(false);
		saveButton.setText("Saved!");
		MainPanel.getInstance().closeTab(tabid);
		MainPanel.getInstance().refreshView();
	}

	/**
	 * Checks to see if a field can be saved, or if it is incorrect
	 * 
	 * @return True/False
	 */
	public boolean isSaveable()
	{
		return validateText(nameTextField.getText(), nameErrorLabel)
		        && validateDate(startTimeDatePicker.getDateTime(), dateErrorLabel);
	}

	/**
	 * Checks to see if the commitment is being edited (as opposed to added)
	 * 
	 * @return True/False
	 */
	public boolean editingCommitment()
	{
		return this.isEditingCommitment;
	}

	/**
	 * 
	 * @param dueDate first DatePicker to validate and compare
	 * @param mEndTime second DatePicker to validate and compare
	 * @param mErrorLabel text field to be updated with any error message
	 * @return true if all validation checks pass, else returns false
	 */
	private boolean validateDate(DateTime dueDate, JLabel mErrorLabel)
	{
		if (dueDate == null)// Make sure that a date has been selected
		{
			mErrorLabel.setText("That does not look like a valid date & time");
			return false;
		}
		// no errors found
		return true;

	}

	/**
	 * 
	 * @param mText text to be validated
	 * @param mErrorLabel JLabel to display resulting error message
	 * @return true if all pass, else return true
	 */
	private boolean validateText(String mText, JLabel mErrorLabel)
	{
		if (mText == null || mText.trim().length() == 0)
		{
			mErrorLabel.setText("This field is required");
			return false;

		}
		return true;
	}

	/**
	 * Sets the ID of the tab
	 * 
	 * @param id ID to be set
	 */
	public void setTabId(int id)
	{
		tabid = id;
	}

	/**
	 * Checks if this display has the same commitment as the display provided
	 * 
	 * @param other the other display to compare
	 * @return true if the displays' commitments match
	 */
	public boolean matchingCommitment(AddCommitmentDisplay other)
	{
		return this.commitmentToEdit != null && this.commitmentToEdit.equals(other.commitmentToEdit);
	}

	/**
	 * Sets the default date and time text fields to the current date and time
	 * 
	 * Should be only called if creating a new commitment, not when editing
	 * since edit event already has a date and time to fill the text fields with
	 */
	public void setCurrentDateAndTime()
	{
		this.startTimeDatePicker.setDate(selectedTime);
		MutableDateTime mdt = DateTime.now().toMutableDateTime();
		int quarterHours = mdt.getMinuteOfHour() / 15;
		int minutes = quarterHours < 4 ? (quarterHours + 1) * 15 : (quarterHours) * 15;
		if (minutes == 60)
		{
			mdt.addHours(1);
			mdt.setMinuteOfHour(0);
		} else
			mdt.setMinuteOfHour(minutes);
		this.startTimeDatePicker.setTime(mdt.toDateTime());
	}
}
