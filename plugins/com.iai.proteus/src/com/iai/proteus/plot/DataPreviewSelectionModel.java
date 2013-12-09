package com.iai.proteus.plot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.iai.proteus.common.sos.data.Field;

public class DataPreviewSelectionModel {
	
	// maps sensor offering id -> object 
	private Map<String, DataPreviewSensorOfferingSelectionModel> sensorOfferings;
	
	/**
	 * Constructor 
	 * 
	 */
	public DataPreviewSelectionModel() {
		sensorOfferings = new HashMap<>();
	}
	
	/**
	 * Sets the selected observed property for the given sensor offering 
	 * 
	 * @param sensorOfferingId
	 * @param observedProperty
	 */
	public void setSelectedObservedProperty(String sensorOfferingId, 
			String observedProperty) 
	{
		DataPreviewSensorOfferingSelectionModel m = 
				sensorOfferings.get(sensorOfferingId);
		if (m == null) {
			// create object 
			m = new DataPreviewSensorOfferingSelectionModel();
			sensorOfferings.put(sensorOfferingId, m);
		}
		// update 
		m.setSelectedObservedProperty(observedProperty);
	}
	
	/**
	 * Returns the selected observed property for the given sensor offering, 
	 * null if it is not set 
	 * 
	 * @param sensorOfferingId
	 * @return
	 */
	public String getSelectedObservedProperty(String sensorOfferingId) {
		DataPreviewSensorOfferingSelectionModel m = 
				sensorOfferings.get(sensorOfferingId);
		if (m != null) {
			return m.getSelectedObservedProperty();
		}
		// default
		return null;
	}
	
	/**
	 * Adds a collection of fields for the given offering and observed property  
	 * 
	 * @param sensorOfferingId
	 * @param observedProperty
	 * @param fields
	 */
	public void addFields(String sensorOfferingId, String observedProperty, 
			Collection<Field> fields) 
	{
		DataPreviewSensorOfferingSelectionModel m = 
				sensorOfferings.get(sensorOfferingId);
		if (m == null) {
			// create object 
			m = new DataPreviewSensorOfferingSelectionModel();
			sensorOfferings.put(sensorOfferingId, m);
		}
		m.addFields(observedProperty, fields);
	}
	
	/**
	 * Returns the fields for the given sensor offering and observed property
	 * 
	 * @param sensorOfferingId
	 * @param observedProperty
	 * @return
	 */
	public Collection<Field> getFields(String sensorOfferingId, String observedProperty) {
		DataPreviewSensorOfferingSelectionModel m = 
				sensorOfferings.get(sensorOfferingId);
		if (m != null) {
			return m.getFields(observedProperty);
		}
		// default
		return new ArrayList<>();
	}
	
	/**
	 * Marks the given field as selected for the sensor offering and 
	 * observed property 
	 * 
	 * @param sensorOfferingId
	 * @param observedProperty
	 * @param field
	 */
	public void fieldSelected(String sensorOfferingId, String observedProperty, 
			Field field) 
	{
		DataPreviewSensorOfferingSelectionModel m = 
				sensorOfferings.get(sensorOfferingId);
		m.selectField(observedProperty, field);
	}
	
	/**
	 * Marks the given field as not selected for the sensor offering and 
	 * observed property 
	 * 
	 * @param sensorOfferingId
	 * @param observedProperty
	 * @param field
	 */
	public void fieldDeselected(String sensorOfferingId, String observedProperty, 
			Field field) 
	{
		DataPreviewSensorOfferingSelectionModel m = 
				sensorOfferings.get(sensorOfferingId);
		m.deselectVariable(observedProperty, field);
	}	
	
	/**
	 * Utility method
	 * 
	 * @param fields
	 * @return
	 */
	public static String[] fieldsToStringArray(Collection<Field> fields) {
		Collection<String> strs = new ArrayList<>();
		for (Field f : fields) {
			strs.add(f.toString());
		}
		return strs.toArray(new String[0]);
	}

}
