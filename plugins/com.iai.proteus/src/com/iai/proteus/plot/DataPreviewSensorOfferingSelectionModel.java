package com.iai.proteus.plot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import com.iai.proteus.common.sos.data.Field;

/**
 * Maintains information about data preview selection parameters for a given 
 * sensor offering  
 * 
 * @author Jakob Henriksson 
 *
 */
public class DataPreviewSensorOfferingSelectionModel {
	
	// maps observed-property -> fields 
	private Map<String, Collection<Field>> fields;
	// holds the currently selected observed property 
	private String selectedObservedProperty;
	
	/**
	 * Constructor 
	 * 
	 */
	public DataPreviewSensorOfferingSelectionModel() {
		fields = new HashMap<>();
		selectedObservedProperty = null;
	}

	/**
	 * Add a new field for a particular observed property  
	 * 
	 * @param observedProperty
	 * @param field
	 */
	public void addField(String observedProperty, Field field) {
		if (fields.containsKey(observedProperty)) {
			fields.get(observedProperty).add(field);
		} else {
			Collection<Field> fs = new LinkedHashSet<>();
			fs.add(field);
			fields.put(observedProperty, fs);
		}
		// automatically set observed property if not set
		if (selectedObservedProperty == null) {
			selectedObservedProperty = observedProperty;
		}
	}
	
	/**
	 * Adds multiple fields for a particular observed property
	 * 
	 * @param observedProperty
	 * @param fields
	 */
	public void addFields(String observedProperty, Collection<Field> fields) {
		for (Field f : fields) {
			addField(observedProperty, f);
		}
	}

	/**
	 * Returns the selected observed property, null if it is not set 
	 * 
	 * @return
	 */
	public String getSelectedObservedProperty() {
		return selectedObservedProperty;
	}

	/**
	 * Sets the selected observed property 
	 * 
	 * @param selectedObservedProperty
	 */
	public void setSelectedObservedProperty(String selectedObservedProperty) {
		this.selectedObservedProperty = selectedObservedProperty;
	}
	
	/**
	 * Returns the fields for the given observed property, null if the 
	 * observed property does not exist 
	 * 
	 * @param observedProperty
	 * @return
	 */
	public Collection<Field> getFields(String observedProperty) {
		Collection<Field> fs = fields.get(observedProperty);
		if (fs != null) {
			return fs;
		}
		// default
		return new ArrayList<>();
	}
	
	/**
	 * Selects the given field
	 * 
	 * @param observedProperty
	 * @param field
	 */
	public void selectField(String observedProperty, Field field) {
		setFieldSelection(observedProperty, field, true);
	}
	
	/**
	 * De-selects the given field 
	 * 
	 * @param observedProperty
	 * @param field
	 */
	public void deselectVariable(String observedProperty, Field field) {
		setFieldSelection(observedProperty, field, false);
	}
	
	/**
	 * Sets the selection status of the given field
	 *  
	 * @param observedProperty 
	 * @param field
	 * @param selected
	 */
	public boolean setFieldSelection(String observedProperty, Field field, boolean selected) {
		Collection<Field> fs = fields.get(observedProperty);
		if (fs != null) {
			for (Field f : fs) {
				if (f.equals(field)) {
					f.setSelected(selected);
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns true if the field is selected, false otherwise 
	 * 
	 * @param observedProperty
	 * @param field
	 * @return
	 */
	public boolean isFieldSelected(String observedProperty, Field field) {
		Collection<Field> fs = fields.get(observedProperty);
		if (fs != null) {
			for (Field f : fs) {
				if (f.equals(field)) {
					return f.isSelected();
				}
			}
		}
		// default
		return false;
	}
}
