package com.iai.proteus.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.iai.proteus.model.services.Service;
import com.iai.proteus.model.services.ServiceManager;
import com.iai.proteus.model.services.ServiceType;

/**
 * Model for service UI viewers 
 * 
 * @author b0kaj
 *
 */
public class ServiceModel implements ServiceManager {
	
	private Collection<Service> services;
	
	/**
	 * Constructor 
	 * 
	 */
	public ServiceModel() {
		services = new HashSet<>();
	}
	
	/**
	 * Adds a service to this query set
	 * 
	 * Implements {@link ServiceManager}
	 * 
	 * @param service
	 */
	@Override
	public boolean addService(Service service) {
		return services.add(service);
	}

	/**
	 * Removes a service from this query set
	 * 
	 * Implements {@link ServiceManager}
	 * 
	 * @param service
	 */
	@Override
	public boolean removeService(Service service) {
		return services.remove(service);
	}
	
	/**
	 * Clears all services 
	 * 
	 * Implements {@link ServiceManager}
	 * 
	 */
	@Override
	public void clearAll() {
		services.clear();
	}

	/**
	 * Returns the services for this query set
	 * 
	 * Implements {@link ServiceManager}
	 * 
	 * @return
	 */
	@Override
	public Collection<Service> getServices() {
		return services;
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

}
