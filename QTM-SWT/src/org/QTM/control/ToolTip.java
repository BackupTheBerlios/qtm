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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


/**
 * ToolTip
 *
 * @version $Id: ToolTip.java,v 1.1 2005/01/23 17:20:10 cowahl Exp $ 
 *
 */
public abstract class ToolTip {
	public static String TOOLTIP_TITLE = "TOOLTIP_TITLE";
	public static String TOOLTIP_IMAGE = "TOOLTIP_IMAGE";
	public static String TOOLTIP_TEXT = "TOOLTIP_TEXT";
	
	protected Shell shell;
	protected CLabel title;
	protected Control parent;
	
	protected static Color foreGround;
	protected static Color backGround;
	protected static Color lightForeGround;
	
	/**
	 * 
	 * @param aParent
	 */
	public ToolTip( Composite aParent ) {
		parent = aParent;
		backGround = parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
		foreGround = parent.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND);

		lightForeGround = parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW);
	}

	/**
	 * Creates the tooltip on demand
	 * @param e
	 */
	public abstract void create( Event e );
	
	/**
	 * Dispose the tooltip on demand
	 */
	public void dispose() {
		// dispose the shell and all overlaying widgets
		if ( this.shell != null && !this.shell.isDisposed() ) {
			this.shell.dispose();
		}
	}
	
	/**
	 * Returns <code>true</code> if the receiver is visible and all
	 * of the receiver's ancestors are visible and <code>false</code>
	 * otherwise.
	 *
	 * @return the receiver's visibility state
	 */
	public boolean isVisisble() {
		if ( shell != null && !shell.isDisposed() )
			return this.shell.isVisible();
		else
			return false;
	}

	
	/**
	 * Returns <code>true</code> if the given point is inside the
	 * area specified by the receiver, and <code>false</code>
	 * otherwise.
	 *
	 * @param pt the point to test for containment
	 * @return <code>true</code> if the rectangle contains the point and <code>false</code> otherwise
	 */
	public boolean contains( int x, int y ) {
		if ( shell != null && !shell.isDisposed() )
			return this.shell.getBounds().contains( parent.toDisplay( x, y ) );
		else
			return false;
	}
	
	/**
	 * 
	 * @param e
	 * @return
	 */
	protected TableItem getResult( Event e ) {
		if ( e.widget == null ) return null;
		Table table = (Table) e.widget;
		if ( e.x == 0 && e.y == 0 ) return null;
		TableItem anItem = table.getItem( new Point( e.x, e.y ) );
		if ( anItem == null ) return null;
		
		return anItem;
	}
	
	/**
	 * 
	 * @param aComposite
	 * @return
	 */
	protected CLabel createCLabel( Composite aComposite ) {
		CLabel aCLabel = new CLabel(aComposite, SWT.NONE);
		FontData[] fontDataArray = aCLabel.getFont().getFontData();
		for (int i = 0; i < fontDataArray.length; i++)
			fontDataArray[ i ].setStyle(SWT.BOLD);
		Font boldFont = new Font(null, fontDataArray);
		aCLabel.setFont(boldFont);
		aCLabel.setAlignment(SWT.LEFT);
		aCLabel.setForeground(foreGround);
		aCLabel.setBackground(backGround);
		aCLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL |
				GridData.HORIZONTAL_ALIGN_BEGINNING));
		return aCLabel;
	}

	protected StyledText createSyledText(Composite aComposite) {
		StyledText details = new StyledText(shell, SWT.NONE);
		details.setForeground(foreGround);
		details.setBackground(backGround);
		details.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER));
		details.setEditable(false);
		
		return details;
	}
	
	/**
	 * 
	 * @param aComposite
	 * @return
	 */
	protected Control createSeparator( Composite aComposite ) {
		Label separator = new Label(aComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return separator;
	}

	/**
	 * Sets the shells size and location
	 * @param e
	 */
	protected void setupShell( Event e ) {
		shell.pack();

		// calculate the location of the shell
		Point pt = parent.toDisplay( e.x, e.y );
		Rectangle displayBounds = shell.getDisplay().getBounds();
		Rectangle shellBounds = shell.getBounds();
		int x = Math.max( Math.min( pt.x, displayBounds.width - shellBounds.width ), 0 );
		int y = Math.max( Math.min( pt.y, displayBounds.height - shellBounds.height ), 0 );
		shell.setBounds( new Rectangle( x + 4, y + 4, shellBounds.width, shellBounds.height ) );
		shell.open();
	}

}
