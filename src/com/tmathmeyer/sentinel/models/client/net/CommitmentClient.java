/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.models.client.net;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import com.tmathmeyer.sentinel.models.data.Commitment;
import com.tmathmeyer.sentinel.ui.main.MainPanel;

public class CommitmentClient extends CachingDisplayableClient<Commitment, Commitment.SerializedAction>
{
	private static CommitmentClient instance;

	/**
	 * singleton constructor
	 */
	protected CommitmentClient()
	{
		super("commitments", Commitment.SerializedAction[].class, Commitment[].class);
	}

	/**
	 * singleton method
	 * 
	 * @return the singleton instance of the CommitmentClient
	 */
	public static CommitmentClient getInstance()
	{
		if (instance == null)
		{
			instance = new CommitmentClient();
		}
		return instance;
	}

	@Override
	protected Commitment buildUuidOnlyObject(UUID uuid)
	{
		Commitment c = new Commitment();
		c.setUuid(uuid);
		return c;
	}

	/**
	 * gets a list of commitments within a specified range
	 * 
	 * @param from the start time of the range
	 * @param to the end time of the range
	 * @return all commitments within this range
	 */
	public List<Commitment> getCommitments(DateTime from, DateTime to)
	{
		return getRange(from, to);
	}

	/**
	 * 
	 * @return get all the commitments that the user has access to
	 */
	public List<Commitment> getAllCommitments()
	{
		return getAll();
	}

	/**
	 *
	 * checks if the given commitment's status is among the visible commitment
	 * statuses
	 * 
	 * @param commitment to be checked
	 * @return if the commitment is visible or not
	 */
	public boolean visibleStatus(Commitment obj)
	{
		Collection<String> statuses = MainPanel.getInstance().getSelectedStatuses();
		return statuses.contains(obj.getStatus().toString());
	}

	/**
	 * Get all events by category
	 * 
	 * @param id id of the category
	 * @return all events with given category id
	 */
	public List<Commitment> getCommitmentsByCategory(UUID id)
	{
		return getByCategory(id);
	}

	@Override
	protected boolean visibleCategory(Commitment obj)
	{
		return MainPanel.getInstance().showCommitments() && this.visibleStatus(obj) && super.visibleCategory(obj);
	}

}
