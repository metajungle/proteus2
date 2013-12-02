/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.queryset;

/**
 * Valid time facet values  
 * 
 * @author Jakob Henriksson
 *
 */
public enum TimeFacet {

	ALL("No restriction"),
	ONEDAY("Last 24 hours"),
	ONEWEEK("Last one week");

	private String time;

	/**
	 * Constructor 
	 * 
	 * @param time
	 */
	private TimeFacet(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return time;
	}

	/**
	 * Parses from string value  
	 * 
	 * @param value
	 * @return
	 */
	public TimeFacet parse(String value) {
		for (TimeFacet facet : TimeFacet.values()) {
			if (facet.toString().equals(value))
				return facet;
		}
		// default
		return null;
	}

}
