/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.queryset;


import com.iai.proteus.common.sos.model.SensorOffering;
import com.iai.proteus.model.services.Service;

public class SosOfferingObject {

	private Service service;
	private SensorOffering sensorOffering;

	/**
	 * Constructor
	 *
	 * @param service
	 * @param sensorOffering
	 */
	public SosOfferingObject(Service service, SensorOffering sensorOffering) {
		this.service = service;
		this.sensorOffering = sensorOffering;
	}

	/**
	 * @return the service
	 */
	public Service getService() {
		return service;
	}

	/**
	 * @return the sensorOffering
	 */
	public SensorOffering getSensorOffering() {
		return sensorOffering;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SosOfferingObject) {
			SosOfferingObject item = (SosOfferingObject) obj;
			if (item.getSensorOffering().getGmlId().equals(getSensorOffering().getGmlId()))
					return true;
		}
		return false;
	}
}
