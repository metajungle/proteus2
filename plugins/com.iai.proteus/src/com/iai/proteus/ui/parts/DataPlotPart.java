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
package com.iai.proteus.ui.parts;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.experimental.swt.SWTUtils;
import org.jfree.ui.HorizontalAlignment;

/**
 * Data viewer 
 * 
 * @author Jakob Henriksson 
 *
 */
public class DataPlotPart {
	
	private JFreeChart chartTimeSeries;
	private TimeSeriesCollection datasetTimeSeries;

	
	/**
	 * Constructor 
	 */
	public DataPlotPart() {
		// create empty data set
		datasetTimeSeries = new TimeSeriesCollection();
		// chart 
		chartTimeSeries =
				ChartFactory.createTimeSeriesChart("", "", "",
						datasetTimeSeries, true, true, false);
	}
	
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		Composite chartComposite = createTimeSeriesChart(parent);

		chartComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	/**
	 * Create a Time Series chart
	 *
	 * @param parent
	 * @return
	 */
	private Composite createTimeSeriesChart(Composite parent) {

		Color colorBg = SWTUtils.toAwtColor(parent.getBackground());
		chartTimeSeries.setBackgroundPaint(colorBg);
		// for performance
		chartTimeSeries.setAntiAlias(false);

		XYPlot plot = (XYPlot) chartTimeSeries.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
//		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);

		plot.setDomainCrosshairLockedOnData(true);
//		plot.setRangeCrosshairVisible(false);
		plot.setRangeCrosshairVisible(true);

		plot.setNoDataMessage("There is currently no data to plot.");

		// legend
		LegendTitle legend = chartTimeSeries.getLegend();
		legend.setFrame(BlockBorder.NONE);
		legend.setBackgroundPaint(colorBg);
		legend.setHorizontalAlignment(HorizontalAlignment.CENTER);
		
		// date/time axis
		DateAxis dateAxis = new DateAxis();
		dateAxis.setAutoTickUnitSelection(true);
//		dateAxis.setVerticalTickLabels(true);
		DateFormat chartFormatter = new SimpleDateFormat("MM/dd/yy Ka");
		dateAxis.setDateFormatOverride(chartFormatter);
		plot.setDomainAxis(dateAxis);

		// modify the renderer
		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
//			renderer.setBaseShapesFilled(true);

//			Shape shape = renderer.getBaseLegendShape();
//			shape.getBounds().width += 20;
//
		}

		Composite chartComposite =
			new ChartComposite(parent, SWT.NONE , chartTimeSeries,
				false, true, true, true, true);

		((ChartComposite) chartComposite).setDomainZoomable(true);
		((ChartComposite) chartComposite).setRangeZoomable(false);

		return chartComposite;
	}
	

	@Focus
	public void setFocus() {
		
	}
}