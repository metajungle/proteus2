/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.queryset;

import java.util.ArrayList;
import java.util.Collection;

public class SensorOfferingsHolder {

	/*
	 * Holds the sensor offerings
	 */
	private Collection<SosOfferingObject> sensorOfferings;

	/**
	 * Constructor
	 *
	 */
	public SensorOfferingsHolder() {

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
