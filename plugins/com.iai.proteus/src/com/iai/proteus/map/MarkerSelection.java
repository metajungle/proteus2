/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.map;

import java.util.ArrayList;
import java.util.List;

import com.iai.proteus.queryset.SosSensorOffering;

/**
 * Represents a World Wind marker selection 
 * 
 * @author Jakob Henriksson
 *
 */
public class MarkerSelection {

	private List<SosSensorOffering> markers;
	
	/**
	 * Constructor 
	 * 
	 */
	public MarkerSelection() {
		markers = new ArrayList<>(); 
	}
	
	/**
	 * Add a selection
	 * 
	 * @param marker
	 */
	public void add(SosSensorOffering marker) {
		markers.add(marker);
	}
	
	/**
	 * Returns the selection 
	 * 
	 * @return
	 */
	public List<SosSensorOffering> getSelection() {
		return markers; 
	}
}
