/*******************************************************************************
 * Copyright (c) 2010 - 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <lars.Vogel@gmail.com> - Bug 419770
 *******************************************************************************/
package com.iai.proteus.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class AlertPerspectiveHandler {

	@CanExecute
	public boolean canExecute(EPartService partService) {
		return true;
	}

	@Execute
	public void execute(MApplication app, EModelService modelService,
			EPartService partService)
	{
		MPerspective thisElement = (MPerspective) modelService
				.find("com.iai.proteus.perspective.alert", app);
		if (thisElement != null) {
			partService.switchPerspective(thisElement);			
		}
	}
}