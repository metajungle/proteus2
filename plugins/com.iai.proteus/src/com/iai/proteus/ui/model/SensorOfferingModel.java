/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.ui.model;

import java.util.ArrayList;
import java.util.Collection;

import com.iai.proteus.common.model.sos_v1.SensorOffering;
import com.iai.proteus.model.services.Service;
import com.iai.proteus.queryset.SosSensorOffering;

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
	private Collection<SosSensorOffering> sensorOfferings;

	/**
	 * Constructor
	 *
	 */
	public SensorOfferingModel() {

		sensorOfferings = new ArrayList<SosSensorOffering>();
	}

	/**
	 * Clears the model 
	 * 
	 */
	public void clear() {
		sensorOfferings.clear();
	}
	
	/**
	 * Adds an offering using {@link Service} and {@link SensorOffering} objects
	 * 
	 * @param service
	 * @param sensorOffering
	 * @return
	 */
	public boolean addSosSensorOffering(Service service, SensorOffering sensorOffering) {
		return addSosSensorOffering(new SosSensorOffering(service, sensorOffering));
	}
	
	/**
	 * Adds an offering to the model 
	 * 
	 * @param sensorOffering
	 * @return True if the offering was added, false otherwise 
	 */
	public boolean addSosSensorOffering(SosSensorOffering sensorOffering) {
		if (sensorOffering == null) {
			System.err.println("It was null!");
		}
		if (!sensorOfferings.contains(sensorOffering)) {
			return sensorOfferings.add(sensorOffering);
		}
		return false;
	}
	
	/**
	 * Returns the sensor offerings
	 *
	 * @return
	 */
	public Collection<SosSensorOffering> getSosSensorOfferings() {
		return sensorOfferings;
	}

}
