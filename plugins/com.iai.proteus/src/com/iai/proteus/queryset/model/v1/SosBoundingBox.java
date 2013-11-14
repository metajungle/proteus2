package com.iai.proteus.queryset.model.v1;

public class SosBoundingBox {
	
	Double latL;
	Double latU;
	Double lonL;
	Double lonU;

	/**
	 * Constructor
	 */
	public SosBoundingBox() {
		clearBoundingBox();
	}

	/**
	 * Constructor 
	 * 
	 * @param corners
	 */
	public SosBoundingBox(double[] corners) {
		// TODO: verify that these values are right 
		if (corners.length == 4) {
			latL = corners[0];
			latU = corners[1];
			lonL = corners[2];
			lonU = corners[3];
		} else {
			throw new IllegalArgumentException("A bounding box needs four ponints");
		}
	}
	
	public void clearBoundingBox() {
		latL = -1.0;
		latU = -1.0;
		lonL = -1.0;
		lonU = -1.0;
	}

	/**
	 * Returns false if there is no active bounding box (see definition),
	 * true otherwise
	 * 
	 * @return
	 */
	public boolean hasBoundingBox() {
		if (latL == -1.0 && latU == -1.0 && lonL == -1.0 && lonU == -1.0) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the bounding box as an array
	 * 
	 * @return
	 */
	public double[] getAsArray() {
		if (hasBoundingBox()) {
			return new double[] { latL, latU, lonL, lonU };
		}
		return null;
	}
}