package com.iai.proteus.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SpacerToolControl {

	@Inject 
	UISynchronize sync;

	@PostConstruct
	public void createControls(Composite parent) {
		new Label(parent, SWT.NONE).setText(" ");
	}

} 