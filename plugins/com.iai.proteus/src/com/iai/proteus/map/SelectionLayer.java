/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.map;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.PointPlacemarkAttributes;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ShapeAttributes;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.iai.proteus.common.sos.model.SensorOffering;
import com.iai.proteus.queryset.SosSensorOffering;


/**
 * Layer highlighting selections of offerings
 *
 * @author Jakob Henriksson
 *
 */
public class SelectionLayer extends RenderableLayer {

	private static final Logger log =
		Logger.getLogger(SelectionLayer.class);

	private WorldWindow world;

	// attributes
	protected static ShapeAttributes regionAttributes;
	protected static PointPlacemarkAttributes placemarkAttributes;

	// create attributes
	static {

		Material material =	new Material(new Color(0, 255, 0));

		/* region attributes */
		regionAttributes = new BasicShapeAttributes();
		regionAttributes.setDrawOutline(false);
		regionAttributes.setDrawInterior(true);
		regionAttributes.setInteriorMaterial(material);

		/* marker attributes */
		placemarkAttributes = new PointPlacemarkAttributes();
		placemarkAttributes.setUsePointAsDefaultImage(true);
		placemarkAttributes.setScale(15d);
		placemarkAttributes.setLineMaterial(material);
	}

	/**
	 * Constructor
	 *
	 * @param worldWindow
	 */
	public SelectionLayer(WorldWindow worldWindow) {

		if (worldWindow == null) {
			String msg = "World window object was null";
			log.error(msg);
			throw new IllegalArgumentException(msg);
		}

		this.world = worldWindow;

		// general layer settings
		setName("Sensor offering selection");
		setPickEnabled(false);
	}

	/**
	 * Hides the selection
	 */
	public void clearSelection() {
		removeAllRenderables();
	}

	/**
	 * Sets the selection according to the given sensor offering
	 *
	 * @param sensorOffering
	 */
	public void setSelection(SosSensorOffering sensorOffering) {
		setSelection(Arrays.asList(new SosSensorOffering[] { sensorOffering }));
	}

	/**
	 * Sets the selection according to the given sensor offerings
	 *
	 * @param sensorOfferings
	 */
	public void setSelection(Collection<SosSensorOffering> sensorOfferings) {
		// clear old selection
		clearSelection();

		// add offerings selections
		for (SosSensorOffering offering : sensorOfferings) {

			SensorOffering sensorOffering = offering.getSensorOffering();
			
			Renderable renderable = null;

			if (WorldWindUtils.isRegion(sensorOffering)) {

				// construct the offering region
				renderable = new SensorOfferingRegion(sensorOffering,
						WorldWindUtils.getBoundingBoxSector(sensorOffering),
						regionAttributes);

			} else {

				renderable = new SensorOfferingPlacemark(sensorOffering,
						WorldWindUtils.getCentralPosition(sensorOffering),
						placemarkAttributes);
			}

			// add the new selection renderable
			if (renderable != null)
				addRenderable(renderable);
		}

		redraw();
	}

	/**
	 * Updates the map
	 */
	protected void redraw() {
		// update
		world.redrawNow();
	}

	protected WorldWindow getWwd() {
		return world;
	}
}
