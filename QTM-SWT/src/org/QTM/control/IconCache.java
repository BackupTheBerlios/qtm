/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.QTM.control;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 * Manages icons for the application.
 * This is necessary as we could easily end up creating thousands of icons
 * bearing the same image.
 */
public class IconCache {
	static HashMap images = new HashMap(16);
	
	static String location = "/images/";

	private IconCache() {
	}

	static public void dispose() {

		Iterator it = images.values().iterator();
		while (it.hasNext()) {
			Image image = (Image) it.next();
			image.dispose();
		}
	}
	
	/**
	 * Creates a stock image
	 * 
	 * @param display
	 *            the display
	 * @param path
	 *            the relative path to the icon
	 */
	static public Image getImage(Display display, String path) {
		Image image = (Image)images.get(path);

		if(image != null)
			return image;

		try {
			InputStream stream = IconCache.class.getResourceAsStream(location + path);
			if (stream != null) {
				ImageData imageData = new ImageData(stream);
				if (imageData != null) {
					ImageData mask = imageData.getTransparencyMask();
					return new Image(display, imageData, mask);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
