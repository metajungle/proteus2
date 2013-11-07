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

import java.awt.Color;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.experimental.swt.SWTUtils;

import com.iai.proteus.events.EventConstants;

public class TimeSeriesPart {

	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		TimeSeriesCollection datasetTimeSeries = new TimeSeriesCollection();
		
		JFreeChart chartTimeSeries =
				ChartFactory.createTimeSeriesChart("", "", "",
						datasetTimeSeries, true, true, false);

		Color colorBg = SWTUtils.toAwtColor(parent.getBackground());
		chartTimeSeries.setBackgroundPaint(colorBg);
		
//		// GUI: an SWT composite on top
//		Composite top = new Composite(parent, SWT.EMBEDDED);
//		top.setLayoutData(new GridData(GridData.FILL_BOTH));
//		
//		// Swing Frame and Panel
//		java.awt.Frame worldFrame = SWT_AWT.new_Frame(top);
//		java.awt.Panel panel = new java.awt.Panel(new java.awt.BorderLayout());
//		worldFrame.add(panel);
//		
//		java.awt.Frame frame = new ChartFrame("Time Series", chartTimeSeries);
//
//		panel.add(frame, BorderLayout.CENTER);
		
		Composite chartComposite =
				new ChartComposite(parent, SWT.NONE , chartTimeSeries,
					false, true, true, true, true);
		
		chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	@Inject
	@Optional
	void receiveEvent(@UIEventTopic(EventConstants.EVENT) String elmt) {
		System.out.println("From TimeSeries: " + elmt);
	}	
	

	@Focus
	public void setFocus() {
		
	}
}