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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

/**
 * SolidToolTip
 * 
 * @version $Id: SolidToolTip.java,v 1.1 2005/01/23 17:20:10 cowahl Exp $
 *  
 */
public class SolidToolTip extends ToolTip {
	private StyledText details;

	/**
	 * @param aParent
	 */
	public SolidToolTip(Composite aParent) {
		super(aParent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.mldonkey.g2gui.view.search.ToolTip#create(org.eclipse.swt.widgets.Event)
	 */
	public void create(Event e) {
		TableItem aResult = getResult(e);
		if (aResult == null)
			return;

		shell = new Shell(parent.getShell(), SWT.RESIZE | SWT.CLOSE);
		shell.setText("result details");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.makeColumnsEqualWidth = false;

		shell.setLayout(gridLayout);
		shell.setBackground(backGround);

		// title field
		this.title = createCLabel(shell);

		Image titleImage = (Image) aResult.getData(ToolTip.TOOLTIP_IMAGE);
		if (titleImage != null) {

			this.title.setImage(titleImage);
		}

		String titleText = (String) aResult.getData(ToolTip.TOOLTIP_TITLE);
		if (titleText != null && !"".equals(titleText)) {
			this.title.setText(titleText);
			createSeparator(shell);
		}
		
		// detail field
		String detailsText = (String) aResult.getData(ToolTip.TOOLTIP_TEXT);
		if (detailsText != null && !"".equals(detailsText)) {
			this.details = createSyledText(shell);
			this.details.setFont( new Font(Display.getDefault(), "Lucida Console", 8, SWT.NORMAL) );

			this.details.setText(detailsText);
			Menu popupMenu = new Menu(this.details);
			MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
			menuItem.setText("Copy");
			//		menuItem.setImage( G2GuiResources.getImage( "copy" ) );
			menuItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					details.copy();
				}
			});

			menuItem = new MenuItem(popupMenu, SWT.PUSH);
			menuItem.setText("Select all");
			//		menuItem.setImage( G2GuiResources.getImage( "plus" ) );
			menuItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					details.selectAll();
				}
			});
			this.details.setMenu(popupMenu);
		}

		setupShell(e);
		this.shell.forceFocus();
	}

	/**
	 * class MyList
	 */
	private class MyList extends List {
		private List self;

		private GridData gridData;

		public MyList(Composite parent, int style) {
			super(parent, style);
			self = this;
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.heightHint = 23;
			this.setLayoutData(gridData);
			this.setForeground(foreGround);
			this.setBackground(backGround);

			Menu popupMenu = new Menu(this);
			MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
			menuItem.setText("Copy");
			//			menuItem.setImage( G2GuiResources.getImage( "copy" ) );
			menuItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					int i = self.getSelectionIndex();
					if (i != -1) {
						Clipboard clipboard = new Clipboard(self.getDisplay());
						clipboard.setContents(new Object[] { self.getItem(i) },
								new Transfer[] { TextTransfer.getInstance() });
						clipboard.dispose();
					}
				}
			});
			this.setMenu(popupMenu);
		}

		public void add(String[] strings) {
			for (int i = 0; i < strings.length; i++) {
				this.add(strings[i]);
			}
		}

		public void dispose() {
			if (!this.isDisposed())
				super.dispose();
		}

		protected void checkSubclass() {
		}
	}
}