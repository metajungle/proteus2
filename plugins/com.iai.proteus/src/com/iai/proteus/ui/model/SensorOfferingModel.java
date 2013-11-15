/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.ui.model;

import java.util.ArrayList;
import java.util.Collection;

import com.iai.proteus.queryset.SosOfferingObject;

/**
 * Model for sensor offering UI viewers 
 * 
 * @author b0kaj
 *
 */
public class SensorOfferingModel {

	/*
	 * Holds the sensor offerings
	 */
	private Collection<SosOfferingObject> sensorOfferings;

	/**
	 * Constructor
	 *
	 */
	public SensorOfferingModel() {

		sensorOfferings = new ArrayList<SosOfferingObject>();
	}

	/**
	 * Sets the sensor offerings
	 *
	 * @param sensorOfferings
	 */
	public void setSensorOfferings(Collection<SosOfferingObject> sensorOfferings) {
		this.sensorOfferings = sensorOfferings;
	}

	/**
	 * Returns the sensor offerings
	 *
	 * @return
	 */
	public Collection<SosOfferingObject> getSensorOfferings() {
		return sensorOfferings;
	}

}
