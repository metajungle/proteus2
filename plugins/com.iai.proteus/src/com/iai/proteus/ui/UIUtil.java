package com.iai.proteus.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.iai.proteus.Activator;

/**
 * User interface utilities 
 * 
 * @author jhenriksson
 *
 */
public class UIUtil {
	
	public static List<Color> colors = new ArrayList<Color>() {
		private static final long serialVersionUID = 1L;
		{
			add(Color.decode("#6A4B36"));
			add(Color.decode("#BC967C"));
			add(Color.decode("#3C703C"));
			add(Color.decode("#9EE7A8"));
			add(Color.decode("#7F3F6C"));
			add(Color.decode("#CCAA04"));
			add(Color.decode("#FCE46E"));
			add(Color.decode("#B5B5B5"));
			add(Color.decode("#BC967C"));
			add(Color.decode("#0699CC"));
			add(Color.decode("#012937"));
			add(Color.decode("#384985"));
			add(Color.decode("#F9645B"));
			add(Color.decode("#3275E4"));
			add(Color.decode("#B9D0F5"));
			add(Color.decode("#8B8032"));
			add(Color.decode("#9AFF7F"));
			add(Color.decode("#8CA5AC"));
		}
	};
	
	/**
	 * Returns an image from the specified location
	 *
	 * @param location e.g. "icons/source.png"
	 * @return
	 */
	public static Image getImage(String location) {
		return getImageDescriptor(location).createImage();
	}	

	/**
	 * Returns an image descriptor from the specified location
	 *
	 * @param location e.g. "icons/source.png"
	 * @return
	 */
	public static ImageDescriptor getImageDescriptor(String location) {
		// uses the main bundle as default
		return getImageDescriptor(Activator.PLUGIN_ID, location);
	}
	
	/**
	 * Returns an image descriptor from the specified location
	 *
	 * @param bundleId
	 * @param location e.g. "icons/source.png"
	 * @return
	 */
	public static ImageDescriptor getImageDescriptor(String bundleId, String location) {
		return ImageDescriptor.createFromURL(
				FileLocator.find(Platform.getBundle(bundleId),
						new Path(location), null));
	}
	
}
