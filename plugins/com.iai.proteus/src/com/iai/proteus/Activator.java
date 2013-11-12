package com.iai.proteus;

import java.io.File;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class that controls the plug-in life cycle
 *  
 * @author jhenriksson
 *
 */
public class Activator implements BundleActivator {
	
	// The plug-in ID
	public static final String PLUGIN_ID = "com.iai.proteus"; //$NON-NLS-1$

	private static BundleContext context;
	
	// Shared state location
	private static File stateBaseDir;

	/**
	 * Constructor
	 * 
	 */
	public Activator() {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		// get the base directory for the bundles storage area 
		stateBaseDir = getContext().getDataFile("");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	
	static BundleContext getContext() {
		return context;
	}

	public static File getStateBaseDir() {
		return stateBaseDir;
	}

}
