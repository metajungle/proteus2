/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.queryset;

/**
 * Used to control how facet selection are displayed 
 * 
 * @author Jakob Henriksson
 *
 */
public enum FacetDisplayStrategy {

	// show all sensor offerings when no facet is selected
	SHOW_ALL, 
	// only show sensor offerings when a facet is selected 
	SHOW_SELECTED	

}
