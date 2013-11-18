/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.ui;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.iai.proteus.common.Labeling;
import com.iai.proteus.queryset.Category;
import com.iai.proteus.queryset.SosSensorOffering;
import com.iai.proteus.ui.model.ObservedProperty;

/**
 * Simple label provider for viewers 
 * 
 * @author Jakob Henriksson
 *
 */
public class LabelProvider implements ILabelProvider {

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		/*
		 * Sensor offerings
		 */
		if (element instanceof SosSensorOffering) {
			SosSensorOffering item = (SosSensorOffering) element;
			return item.getSensorOffering().getGmlId();
		}
		/*
		 * Observed properties
		 */
		else if (element instanceof Category)
			return element.toString();
		else if (element instanceof ObservedProperty) {
			return Labeling.labelProperty(element.toString());
		}
		
		// default 
		return element.toString();
	}

	@Override
    public void dispose() {
	}

	@Override
	public void addListener(ILabelProviderListener listener) {

	}

	@Override
	public void removeListener(ILabelProviderListener listener) {

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return true;
	}
}
