package com.iai.proteus.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.Composite;

public class SpaceToolControl {

	@Inject 
	UISynchronize sync;

	@PostConstruct
	public void createControls(Composite parent) {
		
	}

} 