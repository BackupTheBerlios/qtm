/*
 * Copyright 2003
 * G2Gui Team
 * 
 * 
 * This file is part of G2Gui.
 *
 * G2Gui is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * G2Gui is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with G2Gui; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */
package org.QTM.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

/**
 * LiquidToolTip
 *
 * @version $Id: LiquidToolTip.java,v 1.1 2005/01/23 17:20:10 cowahl Exp $ 
 *
 */
public class LiquidToolTip extends ToolTip {
	private Label details;
	/**
	 * @param aParent
	 */
	public LiquidToolTip( Composite aParent ) {
		super( aParent );
	}

	/* (non-Javadoc)
	 * @see net.mldonkey.g2gui.view.search.ToolTip#create()
	 */
	public void create( Event e ) {
		TableItem aResult = getResult( e );
		if ( aResult == null ) return;
		
		shell = new Shell(parent.getShell(), SWT.ON_TOP);
		
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.marginWidth = 2;
        gridLayout.marginHeight = 2;
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.makeColumnsEqualWidth = false;
        
		shell.setLayout(gridLayout);        
		shell.setBackground(backGround);
		
		this.title = createCLabel( shell );
		
		Image titleImage = (Image) aResult.getData(ToolTip.TOOLTIP_IMAGE);
		if (titleImage != null) {

			this.title.setImage(titleImage);
		}
		
		String titleText = (String) aResult.getData(ToolTip.TOOLTIP_TITLE);
		if (titleText != null && !"".equals(titleText)) {
			this.title.setText(titleText);
			createSeparator(shell);
		}

		String detailsText = (String) aResult.getData(ToolTip.TOOLTIP_TEXT);
		if (detailsText != null && !"".equals(detailsText)) {
			this.details = createLabel( shell );
			this.details.setFont( new Font(Display.getDefault(), "Lucida Console", 8, SWT.NORMAL) );
			this.details.setText( detailsText );
		}
		createSeparator( shell );

		Label aLabel = createLabel( shell );
		aLabel.setAlignment(SWT.RIGHT);
		aLabel.setFont( new Font(Display.getDefault(), "Verdana", 7, SWT.NORMAL) );
		aLabel.setForeground(lightForeGround);
		aLabel.setText( "Press F2 for focus" );
		
		setupShell( e );
		
		// we need focus on the parent to listen for "F2"
		parent.forceFocus();
	}

	/**
	 * 
	 * @param aComposite
	 * @return
	 */
	private Label createLabel( Composite aComposite ) {
		Label aLabel = new Label(aComposite, SWT.NONE);
		aLabel.setForeground(foreGround);
		aLabel.setBackground(backGround);
		aLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL |
				GridData.VERTICAL_ALIGN_CENTER));
		return aLabel;
	}
}
