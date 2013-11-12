/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.queryset.model.v1;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iai.proteus.Activator;
import com.iai.proteus.model.services.Service;
import com.iai.proteus.model.services.ServiceType;
import com.iai.proteus.ui.UIUtil;

/**
 * Support for reading and writing query sets to/from disk 
 * 
 * @author Jakob Henriksson
 *
 */
public class QuerySetPersist {

	private static final Logger log = Logger.getLogger(QuerySetPersist.class);

	// name of folder where query sets are stored 
	public static String folderQuerySets = "querysets";

	// extension for serialized query set files  
	public static String querySetExtension = "json";
	
	// format for storing time stamps
	public static SimpleDateFormat format = 
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
	
	// version of serialization 
	public static String version = "1.0";
	
	/**
	 * Returns the folder where the query sets are stored 
	 * 
	 * @return
	 */
	public static File getStorageLocation() {
		
		// load stored query sets 
		File parent = Activator.getStateBaseDir();
		File folder = new File(parent, folderQuerySets);
		
		// ensure the folder exists
		if (!folder.exists())
			folder.mkdir();
		
		return folder;
	}
	
	/**
	 * Writes a query set
	 * 
	 * @param querySet
	 */
	public static String write(QuerySet querySet) {

		try {
			
			JSONObject root = new JSONObject();
			
			// metadata
			root.put(Keys.META_VERSION.toString(), version);
			root.put(Keys.META_UUID.toString(), querySet.getUuid());
			root.put(Keys.META_TITLE.toString(), querySet.getTitle());
			root.put(Keys.META_DATE_CREATED.toString(), format.format(new Date()));
			
			// SOS
			JSONObject sos = new JSONObject();

			// SOS - services 
			JSONArray sosServices = new JSONArray();
			for (Service service : querySet.getSectionSos().getSosServices()) {
				JSONObject sosService = new JSONObject();
				sosService.put(Keys.SERVICE_ENDPOINT.toString(), service.getEndpoint());
				sosService.put(Keys.SERVICE_TITLE.toString(), service.getName());
				sosService.put(Keys.SERVICE_ACTIVE.toString(), service.isActive());
				String color = UIUtil.colorToHexString(service.getColor());
				color = "#" + color.substring(2, color.length());
				sosService.put(Keys.SERVICE_COLOR.toString(), color);
				sosServices.put(sosService);
			}
			sos.put(Keys.SOS_SERVICES.toString(), sosServices);
			
			// SOS - bounding box 
			JSONArray boundingBox = new JSONArray();
			if (querySet.getSectionSos().getBoundingBox().hasBoundingBox()) {
				for (double d : querySet.getSectionSos().getBoundingBox().getAsArray()) {
					boundingBox.put(d);
				}
			}
			sos.put(Keys.SOS_BBOX.toString(), boundingBox);
			
			// SOS - observed properties
			JSONArray observedProperties = new JSONArray();
			for (QuerySet.SosObservedProperty property : querySet.getSectionSos().getObservedProperties()) {
				if (property.isChecked()) {
					observedProperties.put(property.getObservedProperty());
				}
			}
			sos.put("observedProperties", observedProperties);
			
			root.put(Keys.SECTION_SOS.toString(), sos);
			
			// WMS
			
			// WMS - maps
			JSONArray wmsMaps = new JSONArray();
			for (QuerySet.WmsSavedMap savedMap : querySet.getSectionWms().getMaps()) {
				JSONObject wmsMap = new JSONObject();
				wmsMap.put(Keys.SERVICE_ENDPOINT.toString(), savedMap.getEndpoint());
				wmsMap.put(Keys.SERVICE_ACTIVE.toString(), savedMap.isActive());
				wmsMap.put(Keys.MAP_NAME.toString(), savedMap.getName());
				wmsMap.put(Keys.MAP_TITLE.toString(), savedMap.getTitle());
				wmsMap.put(Keys.MAP_NOTES.toString(), savedMap.getNotes());
				wmsMaps.put(wmsMap);
			}
			
			root.put(Keys.SECTION_WMS.toString(), wmsMaps);
			
			// generate JSON, pretty format with tab-size of 2
			return root.toString(2);
				
		} catch (JSONException e) {
			log.error("JSON Exception: " + e.getMessage());
		}

		// error 
		return null;
	}
	
	/**
	 * Reads a query set
	 * 
	 * @param querySet
	 */
	public static QuerySet read(File file) {

		QuerySet qs = new QuerySet();
		
		try {

			JSONObject jsonRoot = 
					new JSONObject(FileUtils.readFileToString(file));
			
			// check version 
			String strVersion = jsonRoot.getString(Keys.META_VERSION.toString());
			if (!(strVersion.equals(version))) {
				log.error("The version did not match, " + 
						"expected version " + version);
				return null;
			}
			
			// set version
			qs.version = version;
			// set UUID
			qs.uuid = jsonRoot.getString(Keys.META_UUID.toString());
			// set title
			qs.title = jsonRoot.getString(Keys.META_TITLE.toString());
			// set dateCreated 
			String strDateCreated = jsonRoot.getString(Keys.META_DATE_CREATED.toString());
			qs.dateCreated = format.parse(strDateCreated);
			
			// SOS
			JSONObject jsonSos = jsonRoot.getJSONObject(Keys.SECTION_SOS.toString());
			
			// services
			JSONArray sosServices = 
					jsonSos.getJSONArray(Keys.SOS_SERVICES.toString());
			for (int i = 0; i < sosServices.length(); i++) {
				JSONObject objSosService = sosServices.getJSONObject(i);
				
				// create and populate the object
				Service sosService = new Service(ServiceType.SOS);
				sosService.setEndpoint(objSosService.getString(Keys.SERVICE_ENDPOINT.toString()));
				sosService.setName(objSosService.getString(Keys.SERVICE_TITLE.toString()));
				sosService.setActive(objSosService.getBoolean(Keys.SERVICE_ACTIVE.toString()));
				// color is optional 
				try {
					Color color = 
							UIUtil.colorFromHexString(objSosService.getString(Keys.SERVICE_COLOR.toString()));
					sosService.setColor(color);
				} catch (JSONException e) {
					
				} catch (IllegalArgumentException e) {
					System.err.println("Illegal argument: " + e.getMessage());
				}
				
				// add to services
				qs.sosSection.sosServices.add(sosService);
			}
			
			// bounding box
			JSONArray arrBbox = jsonSos.getJSONArray(Keys.SOS_BBOX.toString());
			if (arrBbox.length() == 4) {
				qs.sosSection.boundingBox.latL = arrBbox.getDouble(0);
				qs.sosSection.boundingBox.latU = arrBbox.getDouble(1);
				qs.sosSection.boundingBox.lonL = arrBbox.getDouble(2);
				qs.sosSection.boundingBox.lonU = arrBbox.getDouble(3);
				System.out.println("Yes...");
			}
			
			// observed properties 
			JSONArray arrOp = 
					jsonSos.getJSONArray(Keys.SOS_OBSERVED_PROPERTIES.toString());
			for (int i = 0; i < arrOp.length(); i++) {
				// create observed property object 
				QuerySet.SosObservedProperty op = qs.new SosObservedProperty();
				op.observedProperty = arrOp.getString(i);
				
				// add to observed properties 
				qs.sosSection.observedProperties.add(op);
			}
			
			// WMS
			JSONArray jsonWms = jsonRoot.getJSONArray(Keys.SECTION_WMS.toString());
			for (int i = 0; i < jsonWms.length(); i++) {
				JSONObject obj = (JSONObject) jsonWms.get(i);
				
				// create and populate the map object
				QuerySet.WmsSavedMap map = qs.new WmsSavedMap(); 
				map.endpoint = obj.getString(Keys.SERVICE_ENDPOINT.toString());
				map.active = obj.getBoolean(Keys.SERVICE_ACTIVE.toString());
				map.name = obj.getString(Keys.MAP_NAME.toString());
				map.title = obj.getString(Keys.MAP_TITLE.toString());
				map.notes = obj.getString(Keys.MAP_NOTES.toString());
				
				// add the map
				qs.wmsSection.maps.add(map);
			}
			
			return qs;

		} catch (IOException e) {
			log.error("Error reading query set: " + e.getMessage());
		} catch (JSONException e) {
			log.error("JSON Exception: " + e.getMessage());
		} catch (ParseException e) {
			log.error("Error parsing creation date: " + e.getMessage());
		}
		
		// error
		return null;
	}	
	
	/**
	 * Keys for writing/reading JSON
	 * 
	 * @author Jakob Henriksson
	 *
	 */
	enum Keys {
		
		META_VERSION("version"),
		META_UUID("uuid"),
		META_TITLE("title"),
		META_DATE_CREATED("dateCreated"),
		
		SERVICE_ENDPOINT("endpoint"),
		SERVICE_TITLE("title"),
		SERVICE_ACTIVE("active"),
		SERVICE_COLOR("color"), 
		
		MAP_NAME("name"),
		MAP_TITLE("title"),
		MAP_NOTES("notes"), 
		
		SECTION_SOS("sos"),
		SECTION_WMS("wms"),
		
		SOS_SERVICES("services"),
		SOS_BBOX("bbox"),
		SOS_OBSERVED_PROPERTIES("observedProperties");
		
		String tag;
		
		/**
		 * Constructor 
		 * 
		 * @param tag
		 */
		private Keys(String tag) {
			this.tag = tag;
		}
		
		@Override
		public String toString() {
			return tag;
		}
	}
}
