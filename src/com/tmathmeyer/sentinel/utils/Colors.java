/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.utils;

import java.awt.Color;

import javax.swing.UIManager;

/**
 * Place for consistent UI colors. Compensates for platform issues. Supports all
 * LAF's
 */
public class Colors
{
	public static final Color TABLE_BACKGROUND = UIManager.getDefaults().getColor("Table.background"),
	        SELECTED_BACKGROUND = UIManager.getDefaults().getColor("textHighlight"), SELECTED_TEXT = UIManager
	                .getDefaults().getColor("textHighlightText"), BORDER = UIManager.getDefaults().getColor(
	                "Separator.foreground"), TABLE_GRAY_TEXT = UIManager.getDefaults().getColor("Label.foreground"),
	        COMMITMENT_NOT_STARTED = Color.RED, COMMITMENT_IN_PROGRESS = new Color(252, 255, 30),
	        COMMITMENT_COMPLETE = new Color(42, 175, 21), TABLE_TEXT = UIManager.getDefaults().getColor(
	                "Label.foreground");

	public static final Color TABLE_GRAY_HEADER = UIManager.getColor("Panel.background");
}
