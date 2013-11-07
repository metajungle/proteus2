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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.iai.proteus.events.EventConstants;
import com.iai.proteus.ui.QuerySetTabOld;
import com.iai.proteus.ui.QuerySetTab;
import com.iai.proteus.ui.UIUtil;

public class QuerySetPart {

	// the tab folder object
	private CTabFolder tabFolder;
	
	private Image imgChart;
	private Image imgMap; 
	private Image imgQuestion;

	@Inject
	private IEventBroker eventBroker;

	@Inject
	private MDirtyable dirty;
	
	/**
	 * Constructor 
	 * 
	 */
	public QuerySetPart() {
		// images
		imgChart = UIUtil.getImage("icons/fugue/chart.png");
		imgMap = UIUtil.getImage("icons/fugue/map.png");
		imgQuestion = UIUtil.getImage("icons/fugue/question-white.png");
	}
	
	@PostConstruct
	public void createComposite(Composite parent) {

		// dispose listener 
		parent.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (imgChart != null)
					imgChart.dispose();
				if (imgMap != null)
					imgMap.dispose();
				if (imgQuestion != null)
					imgQuestion.dispose();
			}
		});
		
		parent.setLayout(new GridLayout(1, false));
		
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));		

		QuerySetTab querySet = new QuerySetTab(composite);
		Composite master = querySet.getMaster();
		master.setLayoutData(new GridData(GridData.FILL_BOTH));		
		
		
//		tabFolder = new CTabFolder(composite, SWT.BORDER);
//		tabFolder.setSimple(false);
//		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
//		
//		// create a new tab 
//		CTabItem item = new CTabItem(tabFolder, SWT.CLOSE);
//		item.setText("Item 0");
//
//		QuerySetTab2 querySet = new QuerySetTab2(tabFolder);
//		
//		Composite master = querySet.getMaster();
//		master.setLayoutData(new GridData(GridData.FILL_BOTH));
//		
//		item.setControl(master);
//
//		tabFolder.setSelection(item);
//		
//		Button btn = new Button(parent, SWT.NONE);
//		btn.setText("Test me");
//		btn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		btn.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				eventBroker.send(EventConstants.EVENT, "Hello!");
//			}
//		});
	}

	@Focus
	public void setFocus() {
		
	}

	@Persist
	public void save() {
		dirty.setDirty(false);
	}
}