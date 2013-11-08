/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.parts;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.FlatGlobe;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwindx.examples.util.ToolTipController;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.iai.proteus.common.sos.model.SensorOffering;
import com.iai.proteus.common.sos.model.SosCapabilities;
import com.iai.proteus.common.sos.util.SosUtil;
import com.iai.proteus.events.EventConstants;
import com.iai.proteus.map.AlertLayer;
import com.iai.proteus.map.MarkerSelection;
import com.iai.proteus.map.SectorSelector;
import com.iai.proteus.map.SelectionNotifier;
import com.iai.proteus.map.SensorOfferingMarker;
import com.iai.proteus.map.WorldWindUtils;
import com.iai.proteus.map.wms.MapAVKey;
import com.iai.proteus.map.wms.WmsCache;
import com.iai.proteus.map.wms.WmsLayerInfo;
import com.iai.proteus.map.wms.WmsUtil;
import com.iai.proteus.model.MapId;
import com.iai.proteus.model.map.IMapLayer;
import com.iai.proteus.model.map.WmsMapLayer;
import com.iai.proteus.model.map.WmsSavedMap;
import com.iai.proteus.model.services.Service;
import com.iai.proteus.queryset.Facet;
import com.iai.proteus.queryset.FacetChangeToggle;
import com.iai.proteus.queryset.RearrangeMapsEventValue;
import com.iai.proteus.queryset.SosOfferingLayer;
import com.iai.proteus.ui.UIUtil;

/**
 * A World Wind View making use of the "Albireo" plug-in to embed World Wind
 *
 * @author Jakob Henriksson
 *
 */
public class WorldWindView implements SelectListener, PropertyChangeListener {

	@Inject
	private IEventBroker eventBroker;
	
	private static final Logger log = Logger.getLogger(WorldWindView.class);

	public static final String ID = "com.iai.proteus.view.WorldWindView";

//	private SwingControl swingControl = null;

	private Image imgSectorSelect;
	private Image imgSectorClear;
	
	private static WorldWindowGLCanvas world;
    private Globe roundGlobe;
    private FlatGlobe flatGlobe;

	/*
	 * Fixed Layers
	 */
	private SectorSelector selector;
	private Sector latestUserSelection;


    // the ToolTipController can be used to show a tool tip over markers and such
    protected ToolTipController toolTipController;

    private RenderableLayer tooltipLayer;
    private GlobeAnnotation tooltipAnnotation;

    private AlertLayer alertLayer; 

	/**
	 * Constructor
	 *
	 */
	public WorldWindView() {

//		Configuration.setValue(AVKey.GLOBE_CLASS_NAME, EarthFlat.class.getName());
//		Configuration.setValue(AVKey.VIEW_CLASS_NAME, FlatOrbitView.class.getName());

		// resources 
		imgSectorSelect = UIUtil.getImage("resources/icons/fugue/zone--plus.png");
		imgSectorClear = UIUtil.getImage("resources/icons/fugue/zone--minus.png");
		
		// initialize World Wind 
		world = new WorldWindowGLCanvas();
		Model model = (Model) WorldWind.createConfigurationComponent(
				AVKey.MODEL_CLASS_NAME);
		getWwd().setModel(model);
		
//		this.flatGlobe = new EarthFlat();
//        this.roundGlobe = getWwd().getModel().getGlobe();

		// initialize sector selector 
		initializeSectorSelection();

		this.toolTipController =
				new ToolTipController(this.getWwd(), AVKey.DISPLAY_NAME, null);
		getWwd().addSelectListener(toolTipController);
		tooltipAnnotation =
			new GlobeAnnotation("", Position.fromDegrees(0, 0, 0));
		tooltipAnnotation.getAttributes().setSize(new Dimension(270, 0));
		tooltipAnnotation.getAttributes().setDistanceMinScale(1);
		tooltipAnnotation.getAttributes().setDistanceMaxScale(1);
		tooltipAnnotation.getAttributes().setVisible(false);
		tooltipAnnotation.setAlwaysOnTop(true);

		tooltipLayer = new RenderableLayer();
		tooltipLayer.addRenderable(tooltipAnnotation);
		getWwd().getModel().getLayers().add(tooltipLayer);
		
		// add alert layer 
		alertLayer = new AlertLayer(getWwd());
		getWwd().getModel().getLayers().add(alertLayer);
	}

	/**
	 * Create the control 
	 * 
	 */
	@PostConstruct
	public void createPartControl(final Composite parent) {

		parent.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (imgSectorSelect != null) {
					imgSectorSelect.dispose();
				}
				if (imgSectorClear != null) {
					imgSectorClear.dispose();
				}
			}
		});
		
		parent.setLayout(new GridLayout(1, true));
		
		ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		
		final ToolItem tltmSelectRegion = new ToolItem(toolBar, SWT.NONE);
		tltmSelectRegion.setText("Select region");
		tltmSelectRegion.setImage(imgSectorSelect);
		
		final ToolItem tltmClearRegion = new ToolItem(toolBar, SWT.NONE);
		tltmClearRegion.setText("Clear region");
		tltmClearRegion.setImage(imgSectorClear);
		// default
		tltmClearRegion.setEnabled(false);

		// tool item listeners 
		tltmSelectRegion.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableRegionSelection();
				tltmSelectRegion.setEnabled(false);
				tltmClearRegion.setEnabled(true);
			}
		});
		
		tltmClearRegion.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				disableRegionSelection();
				tltmSelectRegion.setEnabled(true);
				tltmClearRegion.setEnabled(false);
			}
		});
		
		// GUI: an SWT composite on top
		Composite top = new Composite(parent, SWT.EMBEDDED);
		top.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Swing Frame and Panel
		java.awt.Frame worldFrame = SWT_AWT.new_Frame(top);
		java.awt.Panel panel = new java.awt.Panel(new java.awt.BorderLayout());
		worldFrame.add(panel);

		// Add the WWJ 3D OpenGL Canvas to the Swing Panel
		panel.add(world, BorderLayout.CENTER);
				
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		/* add select listener that responds to clicks */
		getWwd().getInputHandler().addSelectListener(this);
	}
	
	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
//		IMenuManager menuManager = getViewSite().getActionBars()
//				.getMenuManager();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Focus
	public void setFocus() {
	}

	/**
	 * Returns World Wind 
	 * 
	 * @return
	 */
	private WorldWindowGLCanvas getWwd() {
		return world;
	}

	/**
	 * Implements SelectListener
	 *
	 * @param event
	 */
	@Override
	public void selected(SelectEvent event) {

		if (event.getEventAction().equals(SelectEvent.LEFT_CLICK)) {

			// get the picked object
			PickedObject picked = event.getTopPickedObject();

			if (picked != null) {

				Object pickedObj = picked.getObject();

				/*
				 * we only handle sensor markers, if something else
				 * is clicked, return
				 */
				if (!(pickedObj instanceof SensorOfferingMarker)) {
					return;
				}

				// convert to our marker object
				SensorOfferingMarker marker =
						((SensorOfferingMarker) picked.getObject());

				// populate selection object
				final MarkerSelection selection = new MarkerSelection();
				selection.add(marker);

				// notify listeners of selection
				SelectionNotifier.getInstance().selectionChanged(selection);

			}
		}
	}

	/**
	 * Returns the layer for the given @{link IMapLayer} 
	 * 
	 * @param mapLayer
	 * @return
	 */
	private Layer getLayer(IMapLayer mapLayer) {
		return getLayer(mapLayer.getMapId());
	}
	
	/**
	 * Returns the layer with the given map ID, null if it does not exist
	 *
	 * @param mapId
	 * @return
	 */
	private Layer getLayer(MapId mapId) {
		for (Layer layer : getWwd().getModel().getLayers()) {
			Object value = layer.getValue(MapAVKey.MAP_ID);
			if (value != null && value instanceof String) {
				String mapIdStr = (String) value;
				if (mapId.toString().equals(mapIdStr)) {
					return layer;
				}
			}
		}
		log.warn("Could not find the layer with map id: " + mapId);
		return null;
	}

	/**
	 * Returns the layers corresponding to the MapLayer model object,
	 * an empty list of none was found
	 *
	 * @param mapLayers
	 * @return
	 */
	private List<Layer> getLayers(List<IMapLayer> mapLayers) {
		List<Layer> foundLayers = new ArrayList<Layer>();
		// go through all layers 
		for (Layer layer : getWwd().getModel().getLayers()) {
			// see if the layer as the required metadata 
			Object value = layer.getValue(MapAVKey.MAP_ID);
			if (value != null && value instanceof String) {
				String mapIdStr = (String) value;
				// go though the layers we are looking for
				for (IMapLayer mapLayer : mapLayers) {
					if (mapLayer.getMapId().toString().equals(mapIdStr)) {
						// only add the layer if it did not already exist
						if (!foundLayers.contains(layer))
							foundLayers.add(layer);
					}
				}
			}
		}
		return foundLayers;
	}

	/**
	 * Deletes the given map layers (by @{link MapId}), if they exist
	 *
	 * @param mapLayers
	 */
	private void deleteLayers(Collection<IMapLayer> mapLayers) {
		for (IMapLayer mapLayer : mapLayers) {
			deleteLayer(mapLayer);
		}
	}

	/**
	 * Deletes the given map layer (by @{link MapId}), if it exists
	 *
	 * @param mapLayer
	 */
	private void deleteLayer(IMapLayer mapLayer) {
		Layer layer = getLayer(mapLayer);
		if (layer != null) {
			if (layer instanceof SosOfferingLayer) {
				SosOfferingLayer offeringLayer = (SosOfferingLayer) layer;
				// prepare to delete sensor offering layer
				offeringLayer.prepareLayerDelete();
			}
			// remove layer
			getWwd().getModel().getLayers().remove(layer);
		}
	}

	
	/**
	 * Initialize a #{link SosOfferingLayer}
	 *
	 * @param mapId
	 */
	private SosOfferingLayer initializeOfferingsLayer(MapId mapId) {
		// create layer 
		SosOfferingLayer layer =
				new SosOfferingLayer(getWwd(),
						createSectorSelector(), mapId);
		// associate ID with layer 
		layer.setValue(MapAVKey.MAP_ID, mapId.toString());
		// add marker layer to World Wind
		getWwd().getModel().getLayers().add(layer);
		log.info("Initialized sensor offerings layer: " + mapId);
		return layer;
	}
	

	/**
	 * Create or update the given map layer with sensor offerings from 
	 * the given services  
	 * 
	 * @param mapId
	 * @param services
	 */
	private void createOrUpdateOfferingsLayer(final MapId mapId, 
			final List<Service> services) 
	{
		// create job for updating offerings layer
		Job job = new Job("Downloading Capabilities documents...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {

					monitor.beginTask("Downloading Capabilities documents...",
							services.size());
					
					System.out.println("Services: " + services.size());

					// collect markers from all given services 
					List<Renderable> allMarkers = new ArrayList<Renderable>();
					for (Service service : services) {

						SosCapabilities capabilities =
								SosUtil.getCapabilities(service.getEndpoint());
						List<Renderable> markers =
								WorldWindUtils.getCapabilitiesMarkers(capabilities,
										service.getColor());
						allMarkers.addAll(markers);

						monitor.worked(1);

						// TODO: properly handle cancel operation
						if (monitor.isCanceled())
							break;
					}

					// create layer with markers using the default map ID
					createOrUpdateOfferingsLayerWithMarkers(mapId, allMarkers);

				} finally {
					monitor.done();
				}

				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}

	/**
	 * Creates a layer with the given markers
	 *
	 * @param mapId
	 * @param markers
	 */
	private void createOrUpdateOfferingsLayerWithMarkers(final MapId mapId, 
			final List<Renderable> markers)
	{
		// run in a separate thread 
		new Thread(new Runnable() {
			@Override
			public void run() {

				// find and use the sensor offering layer, if it exists 
				Layer layer = getLayer(mapId);
				if (layer != null && layer instanceof SosOfferingLayer) {

					// update existing layer
					SosOfferingLayer offeringLayer =
							(SosOfferingLayer) layer;
					// reset markers 
					offeringLayer.resetRenderables(markers);
					offeringLayer.setEnabled(true);

				} else {

					if (markers.size() <= 0)
						log.warn("Creating a sensor offering layer with no markers");
					
					// create new sensor offering layer
					// the name of the layer is set automatically
					SosOfferingLayer offeringLayer = 
							initializeOfferingsLayer(mapId);
					
					offeringLayer.setRenderables(markers);
					offeringLayer.setEnabled(true);
				}
			}
		}).start();
	}

	/**
	 * Creates a layer with the given markers
	 *
	 * @param mapId
	 * @param markers
	 */
//	private void createSosLayerWithMarkers(final MapId mapId, final List<Renderable> markers)
//	{
//		/*
//		 * Create the layer if there are markers to add to it
//		 */
//		if (markers.size() > 0) {
//
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//
//					LayerList layers = getWwd().getModel().getLayers();
//
//					// the name of the layer is set automatically
//					SosOfferingLayer offeringLayer =
//							new SosOfferingLayer(getWwd(), selector, mapId);
//					// associate ID with layer 
//					offeringLayer.setValue(MapAVKey.MAP_ID, mapId.toString());
//					offeringLayer.setRenderables(markers);
//					offeringLayer.setEnabled(true);
//
//					/* add marker layer to World Wind */
//					layers.add(offeringLayer);
//				}
//			}).start();
//
//		} else {
//			log.warn("Tried to create a layer, but there were no markers.");
//		}
//	}

	/**
	 * Initializes the selection of a sector
	 *
	 */
	private void initializeSectorSelection() {

		this.selector = new SectorSelector(getWwd());
		this.selector.setInteriorColor(new Color(1f, 1f, 1f, 0.1f));
		this.selector.setBorderColor(new Color(1f, 0f, 0f, 0.5f));
		this.selector.setBorderWidth(3);
	}

	/**
	 * Creates and returns a SectorSelector object for constraining areas
	 *
	 * @return
	 */
	private SectorSelector createSectorSelector() {
		SectorSelector selector = new SectorSelector(getWwd());
		selector.setInteriorColor(new Color(1f, 1f, 1f, 0.1f));
		selector.setBorderColor(new Color(1f, 0f, 0f, 0.5f));
		selector.setBorderWidth(3);
		return selector;
	}

	/**
	 * Sets the color of the SOS offering layer  
	 * 
	 * @param mapId
	 * @param color
	 * @param serviceEndpoint
	 */
	private void setSosOfferingLayerColor(MapId mapId, Color color, 
			String serviceEndpoint) 
	{
		Layer layer = getLayer(mapId);
		if (layer != null && layer instanceof SosOfferingLayer) {
			SosOfferingLayer offeringLayer = (SosOfferingLayer) layer;
			// set the color for the offerings from the relevant service 
			offeringLayer.setOfferingColorForService(color, serviceEndpoint);
		}
	}
	
	/**
	 * Toggles the services on or off for the given map (query set)
	 *
	 * @param mapId
	 * @param serviceObjects
	 */
	private void setServices(MapId mapId, Collection<?> serviceObjects) {
		// check and convert object types 
		List<Service> services = new ArrayList<Service>();
		for (Object obj : serviceObjects) {
			if (obj instanceof Service) {
				Service service = (Service) obj;
				// only include active services 
				if (service.isActive())
					services.add(service);
			}
		}
		// create or update the sensor offering layer 
		createOrUpdateOfferingsLayer(mapId, services);
	}
	
	/**
	 * Activate the layers with the matching IDs, hide other layers
	 *
	 * @param mapLayers
	 */
	private void activateLayers(Collection<IMapLayer> mapLayers) {

		for (Layer layer : getWwd().getModel().getLayers()) {
			
			boolean found = false;
			
			// NOTE: we only deal with layers that have the right metadata 
			Object obj = layer.getValue(MapAVKey.MAP_ID);
			if (obj != null && obj instanceof String) {
				String mapIdStr = (String) obj;

				for (IMapLayer mapLayer : mapLayers) {
					// we found a matching map layer
					if (mapLayer.getMapId().toString().equals(mapIdStr)) {

						// enable layers appropriately 
						if (layer instanceof SosOfferingLayer) {
							layer.setEnabled(true);
						} else {
							layer.setEnabled(mapLayer.isActive());
						}
						
						// indicate that we found the layer we were looking for
						found = true;
						break;
					}
				}
				
				// if the layer was not found, but did have a 
				// {@link MapAVKey.MAP_ID} value, disable the layer
				// because it is not part of our context
				if (!found) {
					layer.setEnabled(false);
				}
				
				// special handling of @{link SosOfferingLayer} 
				if (layer instanceof SosOfferingLayer) {
					SosOfferingLayer offeringLayer = (SosOfferingLayer) layer;
					if (found) {
						offeringLayer.showSector();
					} else {
						offeringLayer.hideSector();
					}					
				}
			}
		}
	}	
	
	/**
	 * Re-arranges layers 
	 * 
	 * TODO: need to properly implement this function 
	 * 
	 * @param mapLayers
	 */
	private void rearrangeLayers(RearrangeMapsEventValue eventValue) {
		
		Layer targetLayer = getLayer(eventValue.getTarget());
		List<Layer> movedLayers = getLayers(eventValue.getMaps());
		
		LayerList layerList = getWwd().getModel().getLayers();
		
		// get the index of the target
		int idx = layerList.indexOf(targetLayer);
		// remove moved layers
		layerList.removeAll(movedLayers);
		// re-locate the moved layers
		layerList.addAll(idx, movedLayers);
	}

	/**
	 * Enables geographical region selection in a @{link SosOfferingLayer}.
	 *
	 * @param mapId
	 */
	private void enableRegionSelection() {
		selector.addPropertyChangeListener(this);
		selector.enable();
	}

	/**
	 * Disables geographical region selection in a @{link SosOfferingLayer}.
	 *
	 * @param mapId
	 */
	private void disableRegionSelection() {
		selector.removePropertyChangeListener(this);
		selector.disable();
		// send update 
		eventBroker.post(EventConstants.EVENT_GEO_BBOX_UPDATED, "");
	}
	
	/**
	 * Event listener for changes in the sector selector
	 *
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();

		if (oldValue instanceof Sector) {

			Sector oldSector = (Sector) oldValue;
			Sector newSector = (Sector) newValue;

			if (newSector == null) {

				if (latestUserSelection == null ||
						!latestUserSelection.equals(oldSector))	{

					// send update 
					eventBroker.post(EventConstants.EVENT_GEO_BBOX_UPDATED, 
							"" + oldSector.toString());

					log.trace("Updating contributions from propertyChange()");
				}

				latestUserSelection = oldSector;

			}
		}
	}	
	
	/**
	 * Sets the geographical selection in a @{link SosOfferingLayer}
	 * 
	 * @param mapId
	 * @param bbox
	 */
	private void setRegionSelection(MapId mapId, double[] bbox) {
		Layer layer = getLayer(mapId);
		if (layer != null) {
			if (layer instanceof SosOfferingLayer) {
				SosOfferingLayer offeringLayer = (SosOfferingLayer) layer;
				// create sector
				Sector sector = Sector.fromDegrees(bbox);
				System.out.println("SECTOR: " + sector);
				// set the sector for the offering layer 
				offeringLayer.setSector(sector);
			}
		}
	}

	/**
	 * Handles events
	 *
	 * @param event
	 */
//	@Override
//	public void update(com.iai.proteus.events.Event event) {
//
//		final Object value = event.getValue();
//		EventType type = event.getEventType();
//
//		switch (type) {
//
//		case MAP_TOGGLE_LAYER:
//
//			if (value instanceof String) {
//				String layerName = (String) value;
//				// the value can have a list of layer names
//				String[] layers = layerName.split(",");
//				for (String name : layers) {
//					// toggle layer
//					toggleMapBaseLayers(name.trim());
//				}
//			}
//
//			break;
//			
//		case MAP_TOGGLE_ALERT_LAYER:
//			if (value instanceof Boolean) {
//				// show or hide the alert layer 
//				alertLayer.setEnabled((Boolean) value);
//			}
//			
//			break;
//			
//		case MAP_TOGGLE_GLOBE_TYPE:
//			if (value instanceof String) {
//				String globeType = (String) value;
//				if (globeType.equalsIgnoreCase("flat")) {
//					// enable flat globe
//					enableFlatGlobe(false);
//				} else {
//					// disable flat globe
//					enableFlatGlobe(false);
//				}
//			}
//			
//			break;
//
//		}
//	}

	/**
	 * Listening to selection changes in the workspace view
	 *
	 */
//	@Override
//	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
//
//		String partId = part.getSite().getId();
//		
//		/*
//		 * For @{link DiscoverView}  
//		 */
//		if (partId.equals(DiscoverView.ID)) {
//
//			List<SensorOffering> sensorOfferings = new ArrayList<SensorOffering>();
//
//			if (!selection.isEmpty()) {
//				if (selection instanceof StructuredSelection) {
//					StructuredSelection structuredSelection =
//							(StructuredSelection) selection;
//
//					Iterator<?> iterator = structuredSelection.iterator();
//
//					while (iterator.hasNext()) {
//						Object element = iterator.next();
//						
//						if (element instanceof SensorOfferingItem) {
//
//							SensorOffering sensorOffering =
//									((SensorOfferingItem) element).getSensorOffering();
//							sensorOfferings.add(sensorOffering);
//
//						} 
//					}
//
//					// show the selection
//					showSelectionInOfferingLayer(sensorOfferings);
//				}
//			}
//		} 
//		/*
//		 * For @{link CommunityGroupView}
//		 */
//		else if (partId.equals(CommunityHubGroupsView.ID)) {
//			
//			if (!selection.isEmpty()) {
//				if (selection instanceof StructuredSelection) {
//					StructuredSelection structuredSelection =
//							(StructuredSelection) selection;
//					Object element = structuredSelection.getFirstElement();
//					if (element instanceof Group) {
//						Group group = (Group) element;
//
//						// get service address from preference store
//						IPreferenceStore store =
//								Activator.getDefault().getPreferenceStore();						
//
//						try {
//							
//							// collect the alerts from the Community Hub
//							Collection<Alert> alerts = 
//									ProteusUtil.getAlerts(store, group);
//							
//							// set the alerts in the alert layer 
//							alertLayer.setAlerts(alerts);
//							
//						} catch (Exception e) {
//							log.error("Exception: " + e.getMessage());
//						}
//					}
//				}
//			}
//		}
//	}

	/**
	 * Informs the active layers that the list of sensor offerings should
	 * be highlighted
	 *
	 * @param sensorOfferings
	 */
	private void showSelectionInOfferingLayer(List<SensorOffering> sensorOfferings) {
		for (Layer layer : getWwd().getModel().getLayers()) {
			if (layer instanceof SosOfferingLayer && layer.isEnabled()) {
				SosOfferingLayer offeringLayer = (SosOfferingLayer) layer;
				offeringLayer.showSelection(sensorOfferings);
			}
		}
	}
	
	/**
	 * Toggling WMS map layer 
	 * 
	 * @param mapLayer
	 */
	private void toggleWmsMapLayer(final WmsMapLayer mapLayer) {
		
		// check if the layer exists
		Layer layer = getLayer(mapLayer.getMapId());
		if (layer != null) {

			// toggle the layer  
			layer.setEnabled(mapLayer.isActive());

			// check if we should remove the 'preview' tag 
			if (mapLayer instanceof WmsSavedMap)
				layer.removeKey(MapAVKey.WMS_MAP_PREVIEW);

			// we're done
			return;
		}

		// run long-running job in separate thread 
		Job job = new Job("Contacting WMS service") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {

				monitor.beginTask("Retrieving WMS layers",
						IProgressMonitor.UNKNOWN);

				/*
				 * If it did not, try and get it
				 */
				Object wmsLayer = getWmsLayer(mapLayer);

				if (wmsLayer != null && wmsLayer instanceof Layer) {
					log.trace("A layer was returned from WMS: " + wmsLayer);
					Layer newLayer = (Layer) wmsLayer;
					newLayer.setName(mapLayer.getMapId().toString());
					newLayer.setEnabled(true);

					// set attributes
					newLayer.setValue(MapAVKey.MAP_ID, mapLayer.getMapId().toString());
					newLayer.setValue(MapAVKey.WMS_SERVICE_URL, 
							mapLayer.getServiceEndpoint());
					if (!(mapLayer instanceof WmsSavedMap))
						// indicates that the layer is a preview from a WMS
						// in contrast to a saved map 
						newLayer.setValue(MapAVKey.WMS_MAP_PREVIEW, true);

					// add layer
					getWwd().getModel().getLayers().add(newLayer);
				} else {
					log.warn("Return object was not a Layer object");
				}

				monitor.done();

				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}
	
	/**
	 * Removes all WMS map layers from a given endpoint 
	 * 
	 * @param endpoint
	 */
	private void removeWmsMapLayers(String endpoint) {
		
		/*
		 * Remove all layers that are from a WMS service 
		 */
		if (endpoint == null) {

			LayerList layerList = getWwd().getModel().getLayers();
			for (Layer layer : layerList) {
				Object valueServiceUrl = layer.getValue(MapAVKey.WMS_SERVICE_URL);
				Object valuePreview = layer.getValue(MapAVKey.WMS_MAP_PREVIEW);
				// only remove the layer if it has an associated service
				// and has the @{link MapAVKey.WMS_MAP_PREVIEW} value set
				if (valueServiceUrl != null && valueServiceUrl instanceof String
						&& valuePreview != null && (Boolean)valuePreview) {
					layerList.remove(layer);
					log.trace("Removed layer " + layer.getName());
				}
			}
		}
		/*
		 * Removes layers from a WMS service that is no longer 
		 * active. When the service is activated, new layers
		 * will be created 
		 */
		else {

			int count = 0;
			LayerList layerList = getWwd().getModel().getLayers();
			for (Layer layer : layerList) {
				Object valueServiceUrl = layer.getValue(MapAVKey.WMS_SERVICE_URL);
				Object valuePreview = layer.getValue(MapAVKey.WMS_MAP_PREVIEW);
				// only remove the layer if it has an associated service
				// and has the @{link MapAVKey.WMS_MAP_PREVIEW} value set
				if (valueServiceUrl != null && valueServiceUrl instanceof String
						&& valuePreview != null && (Boolean)valuePreview) {
					if (valueServiceUrl.equals(endpoint)) {
						layerList.remove(layer);
						log.trace("Removed layer " + layer.getName());
						count++;
					}
				}
			}
			log.trace("Removed " + count + " layers related to: " + 
					endpoint);
		} 
	}

	/**
	 * TODO: to write 
	 *
	 * @param mapLayer
	 * @return
	 */
	private Object getWmsLayer(WmsMapLayer mapLayer) {

		WmsCache cache = WmsCache.getInstance();
		
		String serviceEndpoint = mapLayer.getServiceEndpoint();
		
		Collection<WmsLayerInfo> layerInfos = new ArrayList<WmsLayerInfo>();

		// check if the cache has the layers, if so, fetch them 
		if (cache.containsLayers(serviceEndpoint)) {
			layerInfos = cache.getLayers(serviceEndpoint);
		} else {
			// if not, fetch the layers from the service
			// (they will be put in the cache) 
			layerInfos = WmsUtil.getLayers(serviceEndpoint);
		}

		// try and find the layer with the right name 
		for (WmsLayerInfo layerInfo : layerInfos) {
			if (layerInfo.getName().equals(mapLayer.getName())) {
				return WmsUtil.getWMSLayer(layerInfo);
			}
		}
			
		log.warn("Sorry, could not find the requested layer");
		return null;
	}

    /**
     * Notifies layers that the sector has been cleared
     *
     */
    private void notifyLayersOfClearedRestrictions() {
    	/*
    	 * Notify layers of the change so they can update themselves
    	 */
    	for (Layer layer : getWwd().getModel().getLayers()) {
    		if (layer instanceof SosOfferingLayer) {
    			SosOfferingLayer offeringLayer =
    					(SosOfferingLayer) layer;
    			if (offeringLayer.isEnabled()) {
    				offeringLayer.clearSector();
    				offeringLayer.clearFacets();
    			}
    		}
    	}
    }

    /**
     * Notify layer to clear the facet type
     *
     * @param mapId
     * @param facetType
     */
    private void notifyLayerOfFacetClearing(MapId mapId, Facet facetType) {

    	/*
    	 * Notify layer of the change so it can update
    	 */
    	Layer layer = getLayer(mapId);
    	if (layer != null && layer instanceof SosOfferingLayer) {
    		SosOfferingLayer offeringLayer = (SosOfferingLayer) layer;
    		offeringLayer.clearFacetConstraints(facetType);
    	}
    }

    /**
     * Notifies the appropriate layers of the change in facet selection
     *
     * @param mapId
     * @param facet
     */
    private void notifyLayerOfFacetSelection(MapId mapId,
    		FacetChangeToggle facet)
    {
    	List<FacetChangeToggle> changes = new ArrayList<FacetChangeToggle>();
    	changes.add(facet);

    	notifyLayerOfFacetChanges(mapId, changes);
    }


    /**
     * Notifies the appropriate layers of the change in facet selection
     *
     * @param mapId
     * @param facets
     */
    private void notifyLayerOfFacetSelection(MapId mapId, Collection<?> facets) {

    	/*
    	 * Make sure we are dealing with objects of the right type
    	 */
    	List<FacetChangeToggle> changes = new ArrayList<FacetChangeToggle>();
    	for (Object obj : facets) {
    		if (obj instanceof FacetChangeToggle) {
    			changes.add((FacetChangeToggle) obj);
    		}
    	}

    	notifyLayerOfFacetChanges(mapId, changes);
    }

    /**
     * Notifies the appropriate layers of the change in facet selection
     *
     * @param mapId
     * @param facets
     */
    private void notifyLayerOfFacetChanges(MapId mapId,
    		Collection<FacetChangeToggle> facets)
    {
    	/*
    	 * Notify layer of the change so it can update
    	 */
    	Layer layer = getLayer(mapId);
    	if (layer != null && layer instanceof SosOfferingLayer) {
    		SosOfferingLayer offeringLayer = (SosOfferingLayer) layer;
    		offeringLayer.addOrRemoveFacetConstraints(facets);
    	}
    }

    /**
     * Returns a collection of renderables from SosOfferingLayers that
     * are enabled
     *
     * @return
     */
    private Collection<Renderable> getSelectedOfferingMarkers() {

    	Collection<Renderable> markers =
    			new ArrayList<Renderable>();

//    	if (pickedMarkers.size() > 0) {
//    		for (SensorOfferingMarker marker : pickedMarkers) {
//    			markers.add(marker);
//    		}
//    		return markers;
//    	}

    	for (Layer layer : getWwd().getModel().getLayers()) {
    		if (layer instanceof SosOfferingLayer) {
    			SosOfferingLayer sosLayer = (SosOfferingLayer) layer;
    			if (sosLayer.isEnabled()) {
    				int count = 0;
    				for (Renderable renderable : sosLayer.getRenderables()) {
    					if (renderable instanceof SensorOfferingMarker) {
    						markers.add(renderable);
    						count++;
    					}
    				}
    				log.trace("Layer: " +
    						sosLayer.getName() + " contributed " +
    						count + " markers");
    			}
    		}
    	}
    	return markers;
    }

    /**
     * Toggle visibility of a base layer map
     *
     * @param layerName
     */
    private void toggleMapBaseLayers(String layerName) {
    	for (Layer layer : getWwd().getModel().getLayers()) {
    		if (layer.getName().equalsIgnoreCase(layerName))
    			layer.setEnabled(!layer.isEnabled());
    	}
    }
    
    /**
     * Returns true if the globe is flat
     * 
     * @return
     */
    public boolean isFlatGlobe() {
        return getWwd().getModel().getGlobe() instanceof FlatGlobe;
    }
    
    /**
     * Enables or disables a flat globe
     * 
     * (from NASA's @{link FlatWorldPanel})
     * 
     * @param flat
     */
//    private void enableFlatGlobe(boolean flat) {
//
//        if (isFlatGlobe() == flat)
//            return;
//
//        if (!flat) {
//            // Switch to round globe
//            getWwd().getModel().setGlobe(roundGlobe);
//            // Switch to orbit view and update with current position
//            FlatOrbitView flatOrbitView = (FlatOrbitView)getWwd().getView();
//            BasicOrbitView orbitView = new BasicOrbitView();
//            orbitView.setCenterPosition(flatOrbitView.getCenterPosition());
//            orbitView.setZoom(flatOrbitView.getZoom( ));
//            orbitView.setHeading(flatOrbitView.getHeading());
//            orbitView.setPitch(flatOrbitView.getPitch());
//            getWwd().setView(orbitView);
//            // Change sky layer
//            LayerList layers = getWwd().getModel().getLayers();
//            for (int i = 0; i < layers.size(); i++) {
//                if (layers.get(i) instanceof SkyColorLayer)
//                    layers.set(i, new SkyGradientLayer());
//            }
//        }
//        else
//        {
//            // Switch to flat globe
//            getWwd().getModel().setGlobe(flatGlobe);
//            flatGlobe.setProjection(FlatGlobe.PROJECTION_MERCATOR);
//            // Switch to flat view and update with current position
//            BasicOrbitView orbitView = (BasicOrbitView)getWwd().getView();
//            FlatOrbitView flatOrbitView = new FlatOrbitView();
//            flatOrbitView.setCenterPosition(orbitView.getCenterPosition());
//            flatOrbitView.setZoom(orbitView.getZoom( ));
//            flatOrbitView.setHeading(orbitView.getHeading());
//            flatOrbitView.setPitch(orbitView.getPitch());
//            getWwd().setView(flatOrbitView);
//            // Change sky layer
//            LayerList layers = getWwd().getModel().getLayers();
//            for (int i = 0; i < layers.size(); i++) {
//                if (layers.get(i) instanceof SkyGradientLayer)
//                    layers.set(i, new SkyColorLayer());
//            }
//        }
//        
//        getWwd().redraw();
//    }    

}
