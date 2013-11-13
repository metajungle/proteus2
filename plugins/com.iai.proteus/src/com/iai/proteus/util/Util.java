package com.iai.proteus.util;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;

import com.iai.proteus.queryset.model.v1.SosBoundingBox;

/**
 * Utility methods 
 * 
 * @author b0kaj
 *
 */
public class Util {

	/**
	 * Convert a sector to a Sos Bounding Box 
	 * @param sector
	 * @return
	 */
	public static SosBoundingBox sosBoundingBoxFromSector(Sector sector) {
		return new SosBoundingBox(sector.asDegreesArray());
	}
	
	/**
	 * Convert an Sos Bounding Box object to a Sector
	 * 
	 * @param bb
	 * @return
	 */
	public static Sector sectorFromSosBoundingBox(SosBoundingBox bb) {
		double[] pts = bb.getAsArray();
		return new Sector(Angle.fromDegrees(pts[0]), 
				Angle.fromDegrees(pts[1]), 
				Angle.fromDegrees(pts[2]), 
				Angle.fromDegrees(pts[3]));
	}
}
