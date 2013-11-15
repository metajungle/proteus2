/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

import com.iai.proteus.queryset.Category;

/**
 * Model for observed properties UI viewers  
 * 
 * @author b0kaj
 *
 */
public class ObservedPropertyModel {

	// the observed properties in the model 
	private Collection<ObservedProperty> observedProperties;

	// holds the structure used by the UI viewers 
	private Collection<Category> categories;

	private Map<String, String> rules;
	
	/*
	 * Name of a miscellaneous category
	 */
	public final static String categoryOtherName = "Other";
	
	private Category categoryOther;  

	/**
	 * Constructor
	 *
	 */
	public ObservedPropertyModel() {

		observedProperties = new HashSet<>();
		categories = new ArrayList<>();
		
		// create default category
		categoryOther = new Category(categoryOtherName);
		categories.add(categoryOther);

		rules = new HashMap<>();
		// default rules 
		rules.put("Weather", "wind,gust,barometic,precipitation,rain,solar");
		rules.put("Climate", "temp,air,atmosphere,humidity");
		rules.put("Water", "water,salinity,sea,wave,smell,currents");
	}

	/**
	 * Clears the model 
	 * 
	 */
	public void clearAll() {
		// clear 
		observedProperties.clear();
		categories.clear();
		// re-add the default category
		categories.add(categoryOther);
	}
	
	/**
	 * Clear the categorization rules 
	 * 
	 */
	public void clearCategoryRules() {
		rules.clear();
	}
	
	/**
	 * Adds categorization rule for a named category. 
	 * 
	 * Any observed property that matches on of the terms in the comma-separated
	 * term list will be added to the category 
	 * 
	 * @param category The name of the category 
	 * @param terms Comma-separated list of simple terms.  
	 */
	public void addCategoryRule(String category, String terms) {
		rules.put(category, terms);
	}

	/**
	 * Returns the observed properties, organized by categories
	 *
	 * @return
	 */
	public Collection<Category> getCategories() {
		return categories;
	}
	
	/**
	 * Adds all the observed property names. 
	 * 
	 * @param observedPropertyNames
	 * @return Returns true if an observed property was added, false otherwise
	 */
	public boolean addObservedProperties(Collection<String> observedPropertyNames) {
		boolean change = false;
		for (String property : observedPropertyNames) {
			if (addObservedProperty(property)) {
				change = true;
			}
		}
		return change; 
	}
	
	/**
	 * Add observed property 
	 * 
	 * @param observedPropertyName
	 * @return
	 */
	public boolean addObservedProperty(String observedPropertyName) {
		
		// clean-up property name 
		if (observedPropertyName == null) {
			return false;
		}
		observedPropertyName = observedPropertyName.trim();
		
		// create the property 
		ObservedProperty observedProperty = 
				new ObservedProperty(observedPropertyName);
		
		// add and categorize the observed property 
		if (observedProperties.add(observedProperty)) {
			// if it was added, we need to categorize the property
			for (String categoryName : rules.keySet()) {
				// each category has a list of regular expressions
				// that defines what goes into the category
				String termsStr = rules.get(categoryName);
				if (termsStr == null || termsStr.equals("")) {
					continue;
				}
				String[] terms = termsStr.split(",");
				for (String term : terms) {
					term = term.trim();
					// create primitive regular expression 
					String regexp = "(.)*" + term + "(.)*";
					Pattern pattern = 
							Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
					// if we find a match, categorize and add it 
					if (pattern.matcher(observedPropertyName).matches()) {
						boolean categoryFound = false;
						// find category to add property to 
						for (Category category : categories) {
							if (category.getName().equals(categoryName)) {
								category.addObservedProperty(observedProperty);
								categoryFound = true;
							}
						}
						// add the category entry if we need to 
						if (!categoryFound) {
							Category category = new Category(categoryName);
							category.addObservedProperty(observedProperty);
							categories.add(category);
						}
						// don't try and match any more, return 
						return true;
					}
				}
			}
			// if there was no match at all, put in "Other" category
			categoryOther.addObservedProperty(observedProperty);
		}
		// nothing was added 
		return false;
	}
	
	/**
	 * Returns true if the observed property with the given name is 
	 * found and selected, false otherwise 
	 * 
	 * @param observedPropertyName
	 * @return
	 */
	public boolean isObservedPropertySelected(String observedPropertyName) {
		ObservedProperty observedProperty = 
				new ObservedProperty(observedPropertyName);
		for (ObservedProperty p : observedProperties) {
			if (p.equals(observedProperty)) {
				return p.isSelected();
			}
		}
		// default 
		return false;
	}
	
	/**
	 * Returns the selected observed properties 
	 * 
	 * @return
	 */
	public Collection<ObservedProperty> getSelectedObservedProperties() {
		Collection<ObservedProperty> selected = new ArrayList<>();
		for (ObservedProperty p : observedProperties) {
			if (p.isSelected()) {
				selected.add(p);
			}
		}
		return selected;
	}

	/**
	 * Updates the selected status on the given observed properties 
	 * 
	 * @param properties
	 */
	public void setSelectedObservedProperties(Collection<ObservedProperty> properties) {
		for (ObservedProperty p : observedProperties) {
			if (properties.contains(p)) {
				p.select();
			} else {
				p.deselect();
			}
		}
	}
	
	/**
	 * Returns true if there are some observed properties selected, 
	 * but not all, false otherwise 
	 * 
	 * Can be used for indicating "grayed" check boxes 
	 * 
	 * @param category
	 * @return
	 */
	public boolean hasSomeSelected(Category category) {
		boolean some = false;
		boolean all = true;
		for (ObservedProperty property : category.getObservedProperties()) {
			if (property.isSelected()) {
				some = true;
			} else {
				all = false;
			}
		}
		return some && !all;
	}
	
	/**
	 * Returns true if all observed properties are selected, false otherwise
	 *  
	 * Can be used for check box indicators  
	 *  
	 * @param category
	 * @return
	 */
	public boolean hasAllSelected(Category category) {
		Collection<ObservedProperty> properties = 
				category.getObservedProperties();
		if (properties != null && !properties.isEmpty()) {
			for (ObservedProperty property : properties) {
				if (!property.isSelected()) {
					// something was not selected
					return false;
				}
			}
			// there were properties and none were found to not be selected
			return true;
		} 
		// default 
		return false;
	}
	
	/**
	 * De-selects all observed properties 
	 * 
	 */
	public void deselectAll() {
		for (Category category : categories) {
			category.deselect();
		}
	}
}
