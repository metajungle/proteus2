package com.iai.proteus.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.iai.proteus.queryset.model.v1.SosBoundingBox;

/**
 * Unit tests 
 * 
 * @author b0kaj
 *
 */
public class UtilTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void boundingBoxFromSectorTest() {
		double[] pts = new double[] {
			14.1, 67.2, 55.1, 30.3	
		};
		
		Sector sector1 =
				new Sector(Angle.fromDegrees(pts[0]), 
					Angle.fromDegrees(pts[1]), 
					Angle.fromDegrees(pts[2]), 
					Angle.fromDegrees(pts[3]));

		SosBoundingBox bb = Util.sosBoundingBoxFromSector(sector1);
		
		Sector sector2 = Util.sectorFromSosBoundingBox(bb);
		
		assertThat("Should be same", sector1, is(sector2));
	}
}
