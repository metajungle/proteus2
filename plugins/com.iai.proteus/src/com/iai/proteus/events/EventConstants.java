package com.iai.proteus.events;

/**
 * Constants for event broker communication 
 * 
 * @author jhenriksson
 *
 */
public class EventConstants {
	
	// sent by map to query set when a bounding box is created 
	public static final String EVENT_GEO_BBOX_UPDATED = "geobrowser/region/bbox/updated";
	// sent by map to query set when bounding box selection is cleared
	public static final String EVENT_GEO_BBOX_CLEARED = "geobrowser/region/bbox/cleared";
	// sent by map to query set for selected offerings
	public static final String EVENT_GEO_SELECTION_NEW = "geobrowser/selection/new";
	// sent by query set to map for selected offerings
	public static final String EVENT_GEO_SELECTION_UPDATED = "geobrowser/selection/updated";
	// sent by query set to map for focusing on offering
	public static final String EVENT_GEO_SELECTION_FOCUS = "geobrowser/selection/focus";
	
	// sent by query set to map when sensor offerings are changed/updated 
	public static final String EVENT_GEO_OFFERINGS_UPDATE = "geobrowser/offerings/update";

}
