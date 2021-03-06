/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.ui.model;

/**
 * Model object for observed properties 
 * 
 * @author Jakob Henriksson
 *
 */
public class ObservedProperty {

	private String observedProperty;
	private boolean selected; 

	/**
	 * Constructor 
	 * 
	 * @param observedProperty
	 */
	public ObservedProperty(String observedProperty) {
		this.observedProperty = observedProperty;
		selected = false;
	}
	
	public String getObservedProperty() {
		return observedProperty;
	}
	
	/**
	 * @param observedProperty the observedProperty to set
	 */
	public void setObservedProperty(String observedProperty) {
		this.observedProperty = observedProperty;
	}

	/**
	 * @return the checked
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the checked to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 * Convenience 
	 * 
	 */
	public void select() {
		setSelected(true);
	}
	
	/**
	 * Convenience 
	 * 
	 */
	public void deselect() {
		setSelected(false);
	}

	@Override
	public String toString() {
		return observedProperty;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((observedProperty == null) ? 0 : observedProperty.hashCode());
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
		ObservedProperty other = (ObservedProperty) obj;
		if (observedProperty == null) {
			if (other.observedProperty != null)
				return false;
		} else if (!observedProperty.equals(other.observedProperty))
			return false;
		return true;
	}

}
