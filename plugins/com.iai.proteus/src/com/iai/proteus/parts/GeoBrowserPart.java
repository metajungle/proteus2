/*******************************************************************************
 * Copyright (c) 2010 - 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <lars.Vogel@gmail.com> - Bug 419770
 *******************************************************************************/
package com.iai.proteus.parts;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.iai.proteus.ui.UIUtil;


public class GeoBrowserPart {
	
	final static WorldWindowGLCanvas world = new WorldWindowGLCanvas();
	
	private Image imgSectorSelect;
	private Image imgSectorClear;


	/**
	 * Constructor 
	 * 
	 */
	public GeoBrowserPart() {
		imgSectorSelect = UIUtil.getImage("resources/icons/fugue/zone--plus.png");
		imgSectorClear = UIUtil.getImage("resources/icons/fugue/zone--minus.png");
	}

	/**
	 * Initialize the default WW layers
	 */
	static {
		initWorldWindLayerModel();
	}
	
	@PostConstruct
	public void createComposite(Composite parent) {
		
		parent.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (imgSectorSelect != null) {
					imgSectorSelect.dispose();
				}
				if (imgSectorClear != null) {
					imgSectorClear.dispose();
				}
			}
		});
		
		parent.setLayout(new GridLayout(1, true));
		
		ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		
		ToolItem tltmSelectRegion = new ToolItem(toolBar, SWT.NONE);
		tltmSelectRegion.setText("Select region");
		tltmSelectRegion.setImage(imgSectorSelect);
		
		ToolItem tltmClearRegion = new ToolItem(toolBar, SWT.NONE);
		tltmClearRegion.setText("Clear region");
		tltmClearRegion.setImage(imgSectorClear);
		
		// GUI: an SWT composite on top
		Composite top = new Composite(parent, SWT.EMBEDDED);
		top.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Swing Frame and Panel
		java.awt.Frame worldFrame = SWT_AWT.new_Frame(top);
		java.awt.Panel panel = new java.awt.Panel(new java.awt.BorderLayout());
		worldFrame.add(panel);

		// Add the WWJ 3D OpenGL Canvas to the Swing Panel
		panel.add(world, BorderLayout.CENTER);
				
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	@Inject
	@Optional
	void receiveEvent(@UIEventTopic("viewcommunication/*") String elmt) {
		System.out.println("From WW: " + elmt);
	}	
	
	/*
	 * Initialize WW model with default layers
	 */
	static void initWorldWindLayerModel () 
	{
		Model m = (Model) WorldWind.createConfigurationComponent(
				AVKey.MODEL_CLASS_NAME);

		world.setModel(m);
	}

	@Focus
	public void setFocus() {

	}

}