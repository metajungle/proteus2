package com.iai.proteus;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.equinox.app.IApplicationContext;

import com.iai.proteus.model.services.ServiceRoot;

/**
 * Application life cycle manager 
 * 
 * @author jhenriksson
 *
 */
@SuppressWarnings("restriction")
public class LifeCycleManager {

	@PostContextCreate
	private void postContextCreate(final IEventBroker eventBroker, IApplicationContext context)  {
		// load services 
		Startup.loadServices();
	}
	
	@PreSave
	private void preSave() {
		// save services
		Startup.saveServices(ServiceRoot.getInstance().getServices());
		// store capabilities
		Startup.storeCapabilities();
	}
}
