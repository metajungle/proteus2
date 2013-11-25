/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.iai.proteus.common.sos.SupportedResponseFormats;

/**
 * Model for response formats 
 * 
 * @author Jakob Henriksson 
 *
 */
public class ResponseFormatsModel {

	// Holds the selected formats
	private Collection<ResponseFormat> responseFormats;
	
	/**
	 * Constructor
	 *
	 */
	public ResponseFormatsModel() {
		responseFormats = new HashSet<>();
	}
	
	/**
	 * Clears all response formats 
	 * 
	 */
	public void clear() {
		responseFormats.clear();
	}

	/**
	 * Adds a response format
	 *
	 * @param format
	 */
	public boolean addResponseFormat(String format) {
		if (format != null) {
			return responseFormats.add(new ResponseFormat(format.trim()));
		}
		return false;
	}
	
	/**
	 * Adds collection of response formats 
	 * 
	 * @param formats
	 * @return
	 */
	public boolean addResponseFormats(Collection<String> formats) {
		boolean added = false;
		for (String format : formats) {
			if (addResponseFormat(format)) {
				added = true;
			}
		}
		return added;
	}

	/**
	 * Returns the available formats
	 *
	 * @return
	 */
	public Collection<ResponseFormat> getResponseFormats() {
		return responseFormats;
	}
	
	/**
	 * Returns the selected response formats 
	 * 
	 * @return
	 */
	public Collection<ResponseFormat> getSelectedResponseFormats() {
		Collection<ResponseFormat> selected = new ArrayList<>();
		for (ResponseFormat format : getResponseFormats()) {
			if (format.isSelected()) {
				selected.add(format);
			}
		}
		return selected;
		
	}
	
	/**
	 * Returns true one of the given formats is available in this
	 * model, false otherwise  
	 * 
	 * @param formats
	 * @return
	 */
	public boolean containsSelectedFormat(Collection<String> formats) {
		for (String format : formats) {
			ResponseFormat f = new ResponseFormat(format);
			// set to make sure equals() works the right way
			f.select();
			if (getSelectedResponseFormats().contains(f)) {
				return true;
			}
		}
		// default 
		return false;
	}

	/**
	 * De-selects all response formats 
	 * 
	 */
	public void deselectAll() {
		for (ResponseFormat f : getResponseFormats()) {
			f.deselect();
		}
	}
	
	/**
	 * Selects supported formats 
	 * 
	 */
	public void selectSupported() {
		Collection<String> supported = new ArrayList<>();
		for (SupportedResponseFormats f : SupportedResponseFormats.values()) {
			supported.add(f.toString());
		}
		selectSupported(supported);
	}
	
	/**
	 * Selects only the given supported formats 
	 * 
	 * @param supported
	 */
	public void selectSupported(Collection<String> supported) {
		for (ResponseFormat f : getResponseFormats()) {
			if (supported.contains(f.getResponseFormat())) {
				f.select();
			} else {
				f.deselect();
			}
		}
	}
}
