/*
 * Created on 10.12.2004
 *
 */
package org.QTM.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author WAHL_O
 *
 */
public class GfxLabel extends CLabel {

	static Font f = null;
	
	/**
	 * @param parent
	 * @param style
	 */
	public GfxLabel(Composite parent, int style, Color cl, Color cr) {
		super(parent, style);

		Display disp = getDisplay();
		
		if (f == null)
			f = new Font(disp, "Verdana", 16, SWT.BOLD);
		
		setFont(f);
		setForeground(disp.getSystemColor(SWT.COLOR_WHITE));
		setBackground(new Color[]{cl, cr}, new int[] {100});

	}

	
}
