/*
 * Created on 07.12.2004
 *
 */
package org.QTM.app;

import org.QTM.control.IconCache;
import org.eclipse.swt.widgets.Display;

/**
 * @author WAHL_O
 * 
 */

// TODO add online links to 
// TODO export to DCI reporter
// TODO DCI sanctioning
// TODO report to DCI directly

public class Startup {

	public static void main(String[] args) {
		/*
		 * Before this is run, be sure to set up the following in the launch
		 * configuration (Arguments->VM Arguments) for the correct SWT library
		 * path. The following is a windows example:
		 * -Djava.library.path="installation_directory\plugins\org.eclipse.swt.win32_3.0.0\os\win32\x86"
		 */

		// TODO start object server
		// TODO write seatings client connect
		
		
		final Display display = new Display();
		
		// TODO load preferences from file
		
		Preferences preferences = new Preferences(); 
		Controller controller = new Controller(preferences);
		Application application = new Application(display, controller);
		application.createSShell();
		application.run();

		// TODO save position to preferences 
		
		IconCache.dispose();
		application.dispose();
		controller.dispose();
		display.dispose();
	}

}