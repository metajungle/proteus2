/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.queryset;


import com.iai.proteus.common.sos.model.SensorOffering;
import com.iai.proteus.model.services.Service;

public class SosSensorOffering {

	private Service service;
	private SensorOffering sensorOffering;

	/**
	 * Constructor
	 *
	 * @param service
	 * @param sensorOffering
	 */
	public SosSensorOffering(Service service, SensorOffering sensorOffering) {
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sensorOffering == null) ? 0 : sensorOffering.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SosSensorOffering other = (SosSensorOffering) obj;
		if (sensorOffering == null) {
			if (other.sensorOffering != null)
				return false;
		} else if (!sensorOffering.equals(other.sensorOffering))
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		return true;
	}
}
