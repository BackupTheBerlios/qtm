/*
 * Created on 09.01.2005
 *
 */
package org.QTM.control;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


/**
 * @author WAHL_O
 * 
 */

public class OpenDialog extends Dialog {

	private String result;
	
	private List list;
	Table table;
	
	public OpenDialog(Shell parent, List l) {
		super(parent);
	
		list = l;
		
		this.setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL );
	}


    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("QTM open tournament...");
        newShell.setImage(IconCache.getImage(newShell.getDisplay(), "Q Bubble 16x16.gif"));
        // TODO        newShell.setImage(new Image(null, JasperViewer.class.getResourceAsStream("images/jricon.GIF"))); //$NON-NLS-1$
	    }
	
    protected Control createDialogArea(Composite container) {
		Composite parent= (Composite) super.createDialogArea(container);

		table = new Table(parent, getTableStyle());

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final TableColumn colTable = new TableColumn(table, SWT.CENTER);
		colTable.setText("Tournament");

		Iterator it = list.iterator();
		
		while (it.hasNext()) {
			TableItem ti = new TableItem(table, SWT.NONE);
			ti.setText(0, (String) it.next() );
		}
		GridData gd= new GridData(GridData.FILL_BOTH);
		gd.heightHint= convertHeightInCharsToPixels(15);
		gd.widthHint= convertWidthInCharsToPixels(55);
		table.setLayoutData(gd);

		applyDialogFont(parent);		

		table.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = table.getClientArea();
				Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				int width = area.width - 2 * table.getBorderWidth();
				if (preferredSize.y > area.height) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = table.getVerticalBar().getSize();
					width -= vBarSize.x;
				}

				colTable.setWidth(width);
			}
		});
		
		return parent;
	}

    protected void okPressed()
    {
    	if(table.getSelectionCount()>0)
    		result = table.getSelection()[0].getText(0);
    	
    	super.okPressed();
    }
    
	protected int getTableStyle() {
		return SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
	}


	public String getSelection() {
		return result;
	}
}