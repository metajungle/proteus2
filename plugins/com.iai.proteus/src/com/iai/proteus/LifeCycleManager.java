package com.iai.proteus;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
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
	
//	@Inject
//	EModelService service;
//	
//	@Inject 
//	@Optional
//	MApplication application;

	@PostContextCreate
	private void postContextCreate(final IEventBroker eventBroker, IApplicationContext context)  {
		// load services 
		Startup.loadServices();
		// store capabilities
		Startup.loadCapabilities();
	}
	
	@ProcessAdditions
	public void processAdditions(IEclipseContext context) {
		
//		// setting the progress monitor
//		MToolControl element = 
//				(MToolControl) service.find("com.iai.proteus.toolcontrol.jobstatus",
//						application);
//		
//		if (element != null) {
//			Object widget = element.getObject();
//			final IProgressMonitor p = (IProgressMonitor) widget;
//			
//			ProgressProvider provider = new ProgressProvider() {
//				@Override
//				public IProgressMonitor createMonitor(Job job) {
//					return p;
//				}
//			};		
//			
//		    IJobManager manager = Job.getJobManager();
//		    manager.setProgressProvider(provider);
//		} else {
//			System.err.println("Tool control was null");
//		}
	}
	
	@PreSave
	private void preSave() {
		// save services
		Startup.saveServices(ServiceRoot.getInstance().getServices());
		// store capabilities
		Startup.storeCapabilities();
	}
}
