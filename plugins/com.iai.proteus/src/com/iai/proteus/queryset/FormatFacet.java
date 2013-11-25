/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.queryset;

/**
 * Defines the available facet values for format selection 
 * 
 * @author Jakob Henriksson
 *
 */
public enum FormatFacet {

	ALL("No restriction"),
	SUPPORTED("Only supported formats");

	private String time;

	private FormatFacet(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return time;
	}

	public FormatFacet parse(String value) {
		for (FormatFacet facet : FormatFacet.values()) {
			if (facet.toString().equals(value))
				return facet;
		}
		// default
		return null;
	}

}
