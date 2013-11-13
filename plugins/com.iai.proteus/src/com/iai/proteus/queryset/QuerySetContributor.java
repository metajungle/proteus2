/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.queryset;

import java.util.Collection;

public interface QuerySetContributor {

	public Collection<SosOfferingObject> getSensorOfferingsContribution();
	public FacetData getObservedPropertiesContribution();

}
