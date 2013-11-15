/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.queryset;

import java.util.Collection;
import java.util.HashSet;

import com.iai.proteus.ui.model.ObservedProperty;

/**
 * Model object for categorizes that are used for organizing 
 * observed properties 
 * 
 * @author Jakob Henriksson
 *
 */
public class Category {

	private String name;
	private Collection<ObservedProperty> observedProperties;

	/**
	 * Constructor 
	 * 
	 * @param name
	 */
	public Category(String name) {
		this.name = name;
		observedProperties = new HashSet<>();
	}
	
	/**
	 * Sets the name of the category 
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the category 
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Adds an observed property to the category 
	 * 
	 * @param p
	 * @return
	 */
	public boolean addObservedProperty(ObservedProperty p) {
		return observedProperties.add(p);
	}

	/**
	 * Returns the observed properties in the category 
	 * 
	 * @return
	 */
	public Collection<ObservedProperty> getObservedProperties() {
		return observedProperties;
	}

	/**
	 * Selects all observed properties in the category 
	 * 
	 */
	public void select() {
		for (ObservedProperty property : getObservedProperties()) {
			property.select();
		}
	}
	
	/**
	 * De-selects all observed properties in the category  
	 * 
	 */
	public void deselect() {
		for (ObservedProperty property : getObservedProperties()) {
			property.deselect();
		}
	}

	@Override
	public String toString() {
		return getName();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
