/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.queryset.model.v1;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import com.iai.proteus.model.services.Service;
import com.iai.proteus.model.services.ServiceManager;
import com.iai.proteus.model.services.ServiceType;

/**
 * Represents all stored details of a Query Set
 * 
 * @author Jakob Henriksson
 *
 */
public class QuerySet implements ServiceManager {
	
	// version of the serialization 
	String version;
	// the UUID of the query set
	String uuid;
	// the name/title of the query set
	String title;
	// when the query set was created 
	Date dateCreated;
	
	SosSection sosSection;
	WmsSection wmsSection;

	// the file where this query set is stored
	File file;

	/**
	 * Constructor
	 */
	public QuerySet() {
		sosSection = new SosSection();
		wmsSection = new WmsSection();
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid
	 *            the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * 
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * 
	 * @param file
	 *            the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return the sectionSos
	 */
	public SosSection getSosSection() {
		return sosSection;
	}

	/**
	 * @return the sectionWms
	 */
	public WmsSection getWmsSection() {
		return wmsSection;
	}

	/**
	 * SOS Section
	 * 
	 * @author Jakob Henriksson
	 */
	public class SosSection {

		Collection<Service> sosServices = new ArrayList<>();
		Collection<SosObservedProperty> observedProperties = new HashSet<>();
		SosBoundingBox boundingBox = new SosBoundingBox();

		/*
		 * Clears services
		 */
		public void clearSosServices() {
			sosServices.clear();
		}

		/**
		 * Adds a service
		 * 
		 * @param service
		 */
		public void addSosService(Service service) {
			if (service.getServiceType().equals(ServiceType.SOS)) {
				sosSection.sosServices.add(service);
			}
		}

		/**
		 * @return the sosServices
		 */
		public Collection<Service> getSosServices() {
			return sosServices;
		}

		/**
		 * Clears the observed properties
		 * 
		 */
		public void clearObservedProperties() {
			observedProperties.clear();
		}

		/**
		 * @return the observedProperties
		 */
		public Collection<SosObservedProperty> getObservedProperties() {
			return observedProperties;
		}

		/**
		 * Add an observed property
		 * 
		 * @param observedProperty
		 */
		public void addObservedProperty(String observedProperty) {
			SosObservedProperty property = new SosObservedProperty();
			property.observedProperty = observedProperty;
			observedProperties.add(property);
		}

		/**
		 * Removes an observed property
		 * 
		 * @param observedProperty
		 */
		public void removeObservedProperty(String observedProperty) {
			SosObservedProperty property = new SosObservedProperty();
			property.observedProperty = observedProperty;
			observedProperties.remove(property);
		}

		/**
		 * Returns true if the given observed property is included  
		 * 
		 * @param observedProperty
		 * @return
		 */
		public boolean containsObservedProperty(String observedProperty) {
			SosObservedProperty property = new SosObservedProperty();
			property.observedProperty = observedProperty;
			return getObservedProperties().contains(property);
		}
		
		/**
		 * Returns the bounding box 
		 * 
		 * @return the boundingBox
		 */
		public SosBoundingBox getBoundingBox() {
			return boundingBox;
		}
		
		/**
		 * Sets the bounding box
		 * 
		 * @param boundingBox
		 */
		public void setBoundingBox(SosBoundingBox boundingBox) {
			this.boundingBox = boundingBox;
		}
	}

	public class SosObservedProperty {

		String observedProperty;
		boolean checked = true;

		/**
		 * @return the observedProperty
		 */
		public String getObservedProperty() {
			return observedProperty;
		}

		public boolean isChecked() {
			return checked;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime
					* result
					+ ((observedProperty == null) ? 0 : observedProperty
							.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SosObservedProperty other = (SosObservedProperty) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (observedProperty == null) {
				if (other.observedProperty != null)
					return false;
			} else if (!observedProperty.equals(other.observedProperty))
				return false;
			return true;
		}

		private QuerySet getOuterType() {
			return QuerySet.this;
		}
	}

	/**
	 * WMS Section
	 * 
	 * @author Jakob Henriksson
	 */
	public class WmsSection {

		Collection<WmsSavedMap> maps = new ArrayList<WmsSavedMap>();

		/**
		 * @return the maps
		 */
		public Collection<WmsSavedMap> getMaps() {
			return maps;
		}
	}

	public class WmsSavedMap {
		String endpoint;
		String name;
		String title;
		String notes;
		boolean active;

		/**
		 * @return the endpoint
		 */
		public String getEndpoint() {
			return endpoint;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * @return the notes
		 */
		public String getNotes() {
			return notes;
		}

		/**
		 * @return the active
		 */
		public boolean isActive() {
			return active;
		}

	}

	/**
	 * Adds a service to this query set
	 * 
	 * Implements @{link ServiceManager}
	 * 
	 * @param service
	 */
	@Override
	public boolean addService(Service service) {
		return sosSection.sosServices.add(service);
	}

	/**
	 * Removes a service from this query set
	 * 
	 * Implements @{link ServiceManager}
	 * 
	 * @param service
	 */
	@Override
	public boolean removeService(Service service) {
		return sosSection.sosServices.remove(service);
	}

	/**
	 * Clears all services 
	 * 
	 * Implements {@link ServiceManager}
	 * 
	 */
	@Override
	public void clearAll() {
		sosSection.sosServices.clear();
	}
	
	/**
	 * Returns the services for this query set
	 * 
	 * Implements @{link ServiceManager}
	 * 
	 * @return
	 */
	@Override
	public Collection<Service> getServices() {
		return sosSection.sosServices;
	}

	/**
	 * Returns the services matching the given type
	 * 
	 * Implements @{link ServiceManager}
	 * 
	 * @param type
	 */
	@Override
	public Collection<Service> getServices(ServiceType type) {
		Collection<Service> res = new ArrayList<Service>();
		for (Service service : getServices()) {
			if (service.getServiceType().equals(type))
				res.add(service);
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		QuerySet other = (QuerySet) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

}
