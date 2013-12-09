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
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
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
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.experimental.swt.SWTUtils;
import org.jfree.ui.HorizontalAlignment;

import com.iai.proteus.common.TimeUtils;
import com.iai.proteus.common.sos.data.Field;
import com.iai.proteus.common.sos.data.SensorData;
import com.iai.proteus.events.DataPreviewEvent;
import com.iai.proteus.events.EventConstants;
import com.iai.proteus.ui.UIUtil;

/**
 * Data viewer 
 * 
 * @author Jakob Henriksson 
 *
 */
public class DataPlotPart {
	
	private static final Logger log = Logger.getLogger(DataPlotPart.class);
	
	@Inject
	UISynchronize sync;
	
	private JFreeChart chartTimeSeries;
	private TimeSeriesCollection datasetTimeSeries;
	
	private Composite chartComposite; 

	private static String sep = " - ";
	
	/*
	 * Holds the currently plotted data
	 */
	private SensorData currentSensorData;
	
	
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

		chartComposite = createTimeSeriesChart(parent);

		chartComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}
	
	/**
	 * Handle setting of the bounding box selection 
	 * 
	 * @param sector
	 */
	@Inject
	@Optional
	private void receiveEvent(
			@UIEventTopic(EventConstants.EVENT_PLOT_DATA) DataPreviewEvent event) {
		if (event != null) {
			plot(event);
		}
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
	
	/**
	 * Plot the sensor data available
	 *
	 * @param plotData
	 */
	private void plot(DataPreviewEvent plotData) {

		currentSensorData = plotData.getSensorData();
		
		// provide title or information for plot legend
		String title = plotData.getOfferingId() + 
				sep + 
				plotData.getObservedProperty();
//				Labeling.labelProperty(plotData.getObservedProperty());
				
		plot(currentSensorData, title, 
				plotData.getDomainVariable(), plotData.getRangeVariasbles());
	}
	
	/**
	 * Plots a time series
	 *
	 * @param sensorData
	 * @param title 
	 * @param domainVariable
	 * @param rangeVariables
	 */
	private void plot(SensorData sensorData, final String title, Field domainVariable,
			Collection<Field> rangeVariables)
	{

		String description = "";

		// get the time series for the data of this request
		final List<TimeSeries> timeSeries =
				generateTimeSeries(sensorData, description, title, domainVariable,
						rangeVariables);

		clearPlot();

		if (timeSeries.size() > 0) {

			/*
			 * Update the data underlying the chart
			 */
			sync.asyncExec(new Runnable() {
				public void run() {

					// set title 
					TextTitle tt = new TextTitle();
					tt.setFont(new Font("SansSerif", SWT.BOLD, 14));
					tt.setText(title);
					chartTimeSeries.setTitle(tt);
					
					for (TimeSeries series : timeSeries) {
						datasetTimeSeries.addSeries(series);
					}
					chartTimeSeries.fireChartChanged();
				}
			});
		}

		autoZoom();
	}


	/**
	 * Generates a time series data set for the given data and variables
	 * (domain and range)
	 *
	 * @param sensorData
	 * @param description
	 * @param title
	 * @param domainVariable
	 * @param rangeVariables
	 * @return
	 */
	private List<TimeSeries>
		generateTimeSeries(SensorData sensorData,
				String description, String title,
				Field domainVariable, Collection<Field> rangeVariables)
	{

		if (rangeVariables.size() > 0) {

//			System.out.println("Range variables: " + rangeVariables);

			List<Field> allVariables = new ArrayList<Field>();
			// domain
			allVariables.add(domainVariable);
			// ranges
			for (Field field : rangeVariables) {
				allVariables.add(field);
			}

			/*
			 * Get data
			 */
			List<String[]> data = sensorData.getData(allVariables);

//			System.out.println("DATA ROWS: " + data.size());

			// create as many time series as we have variables
			TimeSeries[] allSeries = new TimeSeries[rangeVariables.size()];
			for (int i = 0; i < allSeries.length; i++) {
				// create label for time series
				String variable = allVariables.get(i + 1).getName();
				String label = variable;
				allSeries[i] = new TimeSeries(label);
				allSeries[i].setDescription(description + "##" + variable);
			}

			/*
			 * Update labels
			 *
			 * TODO: what should this be?
			 */
//			chartSettings.labelYAxis = "";
//				Util.readableLocalURL(rangeVariables[0]);

			for (String[] row : data) {

				Date timestamp = TimeUtils.parseDefault(row[0]);
				if (timestamp != null) {

					// add to each time series in order
					for (int i = 0; i < allSeries.length; i++) {
						try {
							double value = Double.parseDouble(row[i + 1]);
							allSeries[i].addOrUpdate(new Minute(timestamp), value);
						} catch (NumberFormatException e) {
							log.warn("Could not parse '" + row[1] +
									"' as Double");
						}
					}
				}
			}

			return Arrays.asList(allSeries);

		} else {
			log.warn("Could not find appropriate values to plot");
		}

		// default
		return null;
	}	

	/**
	 * Removes all the series from the chart
	 *
	 */
	private void clearPlot() {

		log.trace("Clearing plot.");

		sync.syncExec(new Runnable() {
			public void run() {
				// clear all
				datasetTimeSeries.removeAllSeries();
			}
		});
	}
	
	/**
	 *
	 */
	private void autoZoom() {
		sync.syncExec(new Runnable() {
			public void run() {
				((ChartComposite) chartComposite).restoreAutoBounds();
			}
		});
	}
	
	@Focus
	public void setFocus() {
		
	}
}