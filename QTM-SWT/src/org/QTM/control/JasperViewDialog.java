/*
 * Created on 17.01.2005
 *
 */
package org.QTM.control;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.jasperassistant.designer.viewer.ViewerComposite;

/**
 * @author WAHL_O
 *
 */
public class JasperViewDialog extends Dialog {

	JasperPrint jasperPrint;
	
	public JasperViewDialog(Shell parent, JasperPrint j) {
		super(parent);
		
		jasperPrint = j;
		
		this.setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL );
	}


    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("QTM Report viewer...");
        newShell.setImage(new Image(null, JasperViewer.class.getResourceAsStream("images/jricon.GIF"))); //$NON-NLS-1$
		newShell.setLayout(new FillLayout());

    }

    protected void createButtonsForButtonBar(Composite parent) {
    	// leave out CANCEL button from super-call
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}
    
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite)super.createDialogArea(parent);

		ViewerComposite viewer = new ViewerComposite(composite, SWT.NONE);
		viewer.getReportViewer().setDocument(jasperPrint);
        
		applyDialogFont(composite);
		
        return composite;
    }
}
