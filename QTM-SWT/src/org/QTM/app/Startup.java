/*
 * Created on 07.12.2004
 *
 */
package org.QTM.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.QTM.control.IconCache;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

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
        boolean running = false;

        runSingleInstance(display);
        
		initializeResources();

		Controller controller = new Controller(PreferenceLoader.getPreferenceStore());
		Application application = new Application(display, controller);
		application.createSShell();
		application.run();

		IconCache.dispose();
		controller.dispose();
		
		exit(display, 0);
	}

	private static void exit(Display display, int errorCode) {
		if (display != null && !display.isDisposed())
			display.dispose();

		System.exit(errorCode);
	}
	
	private static void runSingleInstance(Display display) {
        File ourLockFile = new File(VersionInfo.getHomeDirectory() + ".lock");
        boolean running = ourLockFile.exists();

        if (running) {
            MessageBox alreadyRunning = new MessageBox(new Shell(display), SWT.YES | SWT.ICON_ERROR);
            alreadyRunning.setText(VersionInfo.getName());
            alreadyRunning.setMessage(VersionInfo.getName() + " " + VersionInfo.getVersion() + " is already runnning.");

            alreadyRunning.open();
            exit(display, 1);
          }

        if (!running) {
          FileOutputStream out;

          try {
            out = new FileOutputStream(ourLockFile);
            PrintStream p = new PrintStream(out);

            p.close();
            out.close();
            ourLockFile.deleteOnExit();

          } catch (FileNotFoundException fnf) {
          } catch (IOException io) {
          }
        }
	}

	private static void initializeResources() {
	    try {
	      PreferenceLoader.initialize();
	    } catch (IOException e) {
	      e.printStackTrace();
	      System.exit(2);
	    }

	    IconCache.initialize();
	    PreferenceLoader.initialize2();
	  }
}