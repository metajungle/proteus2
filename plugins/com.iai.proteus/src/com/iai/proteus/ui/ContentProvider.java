/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.ui;

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.iai.proteus.queryset.Category;
import com.iai.proteus.ui.model.ResponseFormatsModel;
import com.iai.proteus.ui.model.ObservedPropertyModel;
import com.iai.proteus.ui.model.SensorOfferingModel;

/**
 * Content provider for UI viewers  
 * 
 * @author Jakob Henriksson 
 *
 */
public class ContentProvider implements ITreeContentProvider {

	Object[] EMPTY_ARRAY = new Object[0];

	@Override
    public Object[] getChildren(Object parent) {

		// handles collections 
		if (parent instanceof Collection<?>) {
			return ((Collection<?>) parent).toArray();
		} 
		// sensor offerings
		else if (parent instanceof SensorOfferingModel) {
			return ((SensorOfferingModel) parent).getSosSensorOfferings().toArray();
		}
		// observed properties
		else if (parent instanceof ObservedPropertyModel) {
			ObservedPropertyModel holder =
					(ObservedPropertyModel) parent;
			return holder.getCategories().toArray();
		} else if (parent instanceof Category) {
			Category category = (Category) parent;
			return category.getObservedProperties().toArray();
		}
		// formats
		else if (parent instanceof ResponseFormatsModel) {
			return ((ResponseFormatsModel) parent).getResponseFormats().toArray();
		}

        return EMPTY_ARRAY;
    }

	@Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

	@Override
    public Object getParent(Object element) {
        return null;
    }

	@Override
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

	@Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

	@Override
	public void dispose() {
	}
}
