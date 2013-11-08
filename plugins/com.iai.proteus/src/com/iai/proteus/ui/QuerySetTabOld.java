package com.iai.proteus.ui;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.swt.widgets.Composite;

public class QuerySetTabOld {
	
	@Inject
	private MDirtyable dirty;
	
	private Composite parent;


	
	/**
	 * Constructor 
	 * 
	 * @param parent
	 * @param style
	 */
	public QuerySetTabOld(Composite parent) {
		this.parent = parent;
	}
	
	
	@Persist
	public void save() {
		dirty.setDirty(false);
	}	
}