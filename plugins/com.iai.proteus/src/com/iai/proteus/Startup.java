/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iai.proteus.common.model.sos_v1.SosCapabilities;
import com.iai.proteus.common.sos.GetCapabilities;
import com.iai.proteus.common.sos.SosCapabilitiesCache;
import com.iai.proteus.model.map.MapLayer;
import com.iai.proteus.model.services.Service;
import com.iai.proteus.model.services.ServiceRoot;
import com.iai.proteus.model.services.ServiceType;
import com.iai.proteus.ui.UIUtil;

public class Startup {

	private static final Logger log = Logger.getLogger(Startup.class);

	/*
	 * Folder names
	 *
	 */
	public static String folderCapabilities = "capabilities";

	/*
	 * File names
	 */
	public static String servicesFilename = "services.json";

    /**
     * Loads the layer configuration from a configuration file
     *
     */
    public static void loadSetup() {

    }

    /**
     * Returns true if the services file exists, false otherwise
     *
     * @return
     */
    public static boolean servicesFileExists() {
		File parent = Activator.getStateBaseDir();
		File services = new File(parent, servicesFilename);
		return services.exists();
    }

    /**
     * Persists the services to disk
     *
     */
    public static void saveServices(Collection<Service> services) {
    	
    	File parent = Activator.getStateBaseDir();
    	File file = new File(parent, servicesFilename);
    	
    	log.trace("Persisting services to disk (file: " + file + ".");

    	try {
    		
    		JSONArray array = servicesToJSON(services);
    		String json = array.toString(2);
    		FileUtils.writeStringToFile(file, json);
    		
    		log.trace("Persisted services to disk");
    		
    	} catch (JSONException e) {
    		log.error("JSONException: " + e.getMessage());
    	} catch (IOException e) {
    		log.error("IOException: " + e.getMessage());
		}
    }

    /**
     * Loads the workspace from persisted state
     *
     */
    public static void loadServices() {

    	File parent = Activator.getStateBaseDir();
    	File file = new File(parent, servicesFilename);
    	
    	if (file.exists()) {
	    	log.trace("Restoring services from disk (file: " + file + ".");
	    	try {
	    		
				JSONArray json = 
						new JSONArray(FileUtils.readFileToString(file));
				
				Collection<Service> services = servicesFromJSON(json);
				
				for (Service service : services) {
					ServiceRoot.getInstance().addService(service);
				}
				
	    	} catch (JSONException e) {
	    		log.error("JSONException: " + e.getMessage());
	    	} catch (IOException e) {
	    		log.error("IOException: " + e.getMessage());
	    	}
    	} else {
    		log.warn("Services file " + file + " did not exist");
    	}
    }
    
    /**
     * Enum class for JSON keys for service serialization  
     * 
     * @author b0kaj
     *
     */
    public enum ServiceJSONKeys {
    	
    	ENDPOINT("endpoint"), 
    	TYPE("type"), 
    	NAME("name"), 
    	COLOR("color");
    	
    	private String key;
    	
    	/**
    	 * Constructor
    	 * 
    	 * @param key
    	 */
    	private ServiceJSONKeys(String key) {
    		this.key = key;
    	}
    	
    	@Override
    	public String toString() {
    		return key;
    	}
    }
    
    /**
     * Serializes a collection of services to JSON 
     * 
     * @param services
     * @return
     * @throws JSONException
     */
    private static JSONArray servicesToJSON(Collection<Service> services) throws JSONException {
    	
    	JSONArray array = new JSONArray();
    	for (Service service : services) {
    		JSONObject object = new JSONObject();
    		object.put(ServiceJSONKeys.ENDPOINT.toString(), service.getEndpoint());
    		object.put(ServiceJSONKeys.TYPE.toString(), service.getServiceType());
    		object.put(ServiceJSONKeys.NAME.toString(), service.getName());
    		object.put(ServiceJSONKeys.COLOR.toString(), UIUtil.colorToHexString(service.getColor()));
    		array.put(object);
    	}
    	
    	return array;
    }
    
    /**
     * Serializes a collection of services to JSON 
     * 
     * @param services
     * @return
     * @throws JSONException
     */
    private static Collection<Service> servicesFromJSON(JSONArray json) throws JSONException {

    	Collection<Service> services = new ArrayList<>();
    	for (int i = 0; i < json.length(); i++) {
    		Object object = json.get(i);
    		if (object instanceof JSONObject) {
    			JSONObject jsonObj = (JSONObject) object;
    			Service service = new Service();
    			try {
    				try {
        				String type = jsonObj.getString(ServiceJSONKeys.TYPE.toString());
    					ServiceType serviceType = ServiceType.parse(type);
    					service.setServiceType(serviceType);
    				} catch (IllegalArgumentException e) {
    					System.err.println("Did not understand service type: " + e.getMessage());
    					continue;
    				}
    				String endpoint = jsonObj.getString(ServiceJSONKeys.ENDPOINT.toString());
    				service.setEndpoint(endpoint);
    				String name = jsonObj.getString(ServiceJSONKeys.NAME.toString());
    				service.setName(name);
    				String color = jsonObj.getString(ServiceJSONKeys.COLOR.toString());
    				service.setColor(UIUtil.colorFromHexString(color));
    				
    				services.add(service);
    				
    			} catch (JSONException e) {
    				log.error("Key not found: " + e.getMessage());
    			}
    		}
    	}
    	
    	return services;
    }    

    /**
     * Store capabilities documents to disk 
     * 
     */
	public static void storeCapabilities() {

		File parent = Activator.getStateBaseDir();
		File folder = new File(parent, folderCapabilities);
		if (!folder.exists())
			folder.mkdir();

		// TODO: can we optimize this so that we only delete and write
		//       Capabilities documents that were modified?

		/*
		 * Delete old cache files
		 */
		String[] ext = new String[] { "xml" };
		Iterator<File> it =
			FileUtils.iterateFiles(folder, ext, false);
		while (it.hasNext()) {
			it.next().delete();
		}

		/*
		 * Write new cache files
		 */
		SosCapabilitiesCache cache = SosCapabilitiesCache.getInstance();
		Iterator<String> urls = cache.iterator();
		int counter = 0;
		while (urls.hasNext()) {
			String url = urls.next();
			String contents = cache.getDocument(url);
			if (contents != null) {
				File file = new File(folder,
						"capabilities" + counter++ + ".xml");
				try {
					FileUtils.writeStringToFile(file, contents);
				} catch (IOException e) {
					log.warn("IOException: " + e.getMessage());
				}
			}
		}

	}

	/**
	 * Loads capabilities documents from cache
	 *
	 */
	public static void loadCapabilities() {

		File parent = Activator.getStateBaseDir();
		final File folder = new File(parent, folderCapabilities);

		if (!folder.exists()) {

			/*
			 * Initialize the Capabilities cache
			 */
//			Collection<String> documents = Discovery.getCapabilitiesDocuments();
//
//			CapabilitiesCache cache = CapabilitiesCache.getInstance();
//
//			for (String document : documents) {
//				SosCapabilities capabilities =
//					GetCapabilities.parseCapabilitiesDocument(document);
//
//				if (capabilities != null) {
//					cache.commit(document, capabilities);
//				}
//			}

			log.info("There were no cached capabilities to load.");
			return;
		}

		Job job = new Job("Retrieving Capabilities document") {
			protected IStatus run(IProgressMonitor monitor) {

				log.trace("Starting to load cached Capabilities documents");

				File[] files = folder.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File arg0, String filename) {
						return filename.endsWith(".xml");
					}
				});

				try {

					monitor.beginTask("Loading cached Capabilities documents",
							files.length);

					// delete old files
					for (File file : files) {

						try {

							String contents = FileUtils.readFileToString(file);

							SosCapabilities capabilities =
								GetCapabilities.parseCapabilitiesDocument(contents);

							SosCapabilitiesCache.getInstance().commit(contents,
									capabilities);

							monitor.worked(1);

						} catch (IOException e) {
							log.warn("IOException reading cached capabilities " +
									"document: " + e.getMessage());
						}
					}

					log.trace("Done loading cached Capabilities documents");

				} finally {
					monitor.done();
				}

				return Status.OK_STATUS;
			}
		};
		job.schedule();

	}

	/**
	 * Detailed bean serialization specifications
	 *
	 */
	private static void controlBeanSerialization() {

    	// make the 'active' field in MapLayer transient
    	try {
    		BeanInfo info = Introspector.getBeanInfo(MapLayer.class);
    		PropertyDescriptor[] propertyDescriptors =
    				info.getPropertyDescriptors();
    		for (int i = 0; i < propertyDescriptors.length; i++) {
    			PropertyDescriptor pd = propertyDescriptors[i];
    			if (pd.getName().equals("active")) {
    				pd.setValue("transient", Boolean.TRUE);
    			}
    		}
    	} catch (IntrospectionException e) {

    	}
	}

}
