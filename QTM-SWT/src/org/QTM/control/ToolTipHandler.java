package org.QTM.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


/**
 * Emulated tooltip handler
 * Notice that we could display anything in a tooltip besides text and images.
 * For instance, it might make sense to embed large tables of data or buttons linking
 * data under inspection to material elsewhere, or perform dynamic lookup for creating
 * tooltip text on the fly.
 * 
 * ToolTipHandler
 * 
 * @version $Id: ToolTipHandler.java,v 1.1 2005/01/23 17:20:10 cowahl Exp $
 */
public class ToolTipHandler implements Listener {
	private ToolTip solid, liquid;
	private Event event, lastEvent;

	/**
     * Creates a new tooltip handler
     * @param parent the parent Shell
     */
    public ToolTipHandler(Composite parent) {
    	// initialize the helpers
    	lastEvent = new Event();
    	lastEvent.x = 0;
    	lastEvent.y = 0;
    	event = lastEvent;

    	// create the two different tooltips
    	this.solid = new SolidToolTip( parent );
    	this.liquid = new LiquidToolTip( parent );
    	
    	// register for this events by the parent
    	parent.addListener(SWT.KeyDown, this);
        parent.addListener(SWT.MouseExit, this);
        parent.addListener(SWT.MouseMove, this);
        parent.addListener(SWT.MouseDown, this);
        parent.addListener(SWT.MouseHover, this);
        parent.addListener(SWT.Close, this);
    }

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	public void handleEvent(Event e) {
		// this is damn complicated because MouseHover is sent as long as the mouse is hovering
		// this results in flickering tooltip :(
		switch ( e.type ) {
			// listen for mousehover as long as solid isnt active or the last event was mousehover too
			case SWT.MouseHover:
				if ( lastEvent.type != e.type && !solid.isVisisble() ) {
					liquid.dispose();
					liquid.create( e );
					// store this event for solid
					event = e;
				}
				break;

			// close liquid on any key. open solid on F2
			case SWT.KeyDown:
				liquid.dispose();
				if ( e.keyCode == SWT.F2 ) {
					solid.dispose();
					solid.create( event );
				}
				break;

			// close liquid as well as solid
			case SWT.MouseDown:
				liquid.dispose();
				solid.dispose();
				break;
			
			// close liquid always. solid only on leaving the parent area
			case SWT.MouseExit:
				liquid.dispose();
				if ( !solid.isVisisble() && !solid.contains( e.x, e.y ) )
					solid.dispose();
				break;
				
			// close solid on close event (ESC)
			case SWT.Close:
				if ( solid.isVisisble() )
					solid.dispose();
				break;
				
			// mouse move close liquid
			default:
				liquid.dispose();
				break;
		}
		// we need to know the last event
		lastEvent = e;
	}
}
