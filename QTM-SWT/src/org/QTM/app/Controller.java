/*
 * Created on 16.12.2004
 *
 */
package org.QTM.app;

import java.util.List;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.jasperreports.engine.JasperPrint;

import org.QTM.control.JasperViewDialog;
import org.QTM.control.OpenDialog;
import org.QTM.data.Result;
import org.QTM.data.Tournament;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * @author WAHL_O
 *  
 */
public class Controller {

	Tournament tournament;

	Timer timer = null;

	ItemFactory factory;

	public Controller() {
		factory = new ItemFactory();

		String lastTournament = PreferenceLoader.getPreferenceStore().getString("lastTournament");
		
		if(lastTournament.equals(""))
			tournament = factory.createTournament();
		else
		{
			tournament = factory.getTournament(lastTournament);
		}	

		// Prevent against improper shut-down...
		PreferenceLoader.getPreferenceStore().setValue("lastTournament", "");
	}

	public void dispose() {
		// going down - remember last tournament
		if(tournament.getName() != null)
			PreferenceLoader.getPreferenceStore().setValue("lastTournament", tournament.getName());

		factory.dispose();
	}

	public void newTournament(Shell shell, Observer o) {
		factory.giveBack(tournament);
		
		tournament = factory.createTournament();
		
		tournament.addObserver(o);
		o.update(tournament, tournament);
	}


	public void openTournament(Shell shell, Observer o) {
		List names = factory.queryTournament();

		OpenDialog dlg = new OpenDialog(shell, names);

		if (dlg.open() == IDialogConstants.CANCEL_ID)
			return;

		String selected = dlg.getSelection();

		factory.giveBack(tournament);
		tournament = factory.getTournament(selected);
		tournament.addObserver(o);
		o.update(tournament, tournament);
	}

	// TODO cache final standings per round and offer to print them
	// TODO autogenerate after round has been finalized
	// TOOD remove from cache after round reset to non finalized!
	public void printCurrentStandings(Shell shell) {
		ReportGenerator plp = new ReportGenerator("Standings", PreferenceLoader.getPreferenceStore()
				.getString("standingsReport"));

		JasperPrint jasperPrint = plp.print(tournament, tournament
				.getPlayersRanked());

		if (jasperPrint != null) {
			JasperViewDialog jvd = new JasperViewDialog(shell, jasperPrint);
			jvd.open();
		}
	}

	public void printResultSlips(Shell shell) {
		ReportGenerator plp = new ReportGenerator("ResultSlips", PreferenceLoader.getPreferenceStore()
				.getString("resultSlipsReport"));

		JasperPrint jasperPrint = plp.print(tournament, tournament
				.getCurrentRound().getCompleteSeatings());

		if (jasperPrint != null) {
			JasperViewDialog jvd = new JasperViewDialog(shell, jasperPrint);
			jvd.open();
		}
	}

	public void printSeatings(Shell shell) {
		ReportGenerator plp = new ReportGenerator("Seatings", PreferenceLoader.getPreferenceStore()
				.getString("seatingsReport"));

		JasperPrint jasperPrint = plp.print(tournament, tournament
				.getCurrentRound().getSortedSeatings());

		if (jasperPrint != null) {
			JasperViewDialog jvd = new JasperViewDialog(shell, jasperPrint);
			jvd.open();
		}
	}

	public void nextRound(Shell shell) {
		if (tournament.nextRound() == null) {
			MessageBox messageBox = new MessageBox(shell, SWT.OK
					| SWT.ICON_WARNING);
			messageBox.setMessage("Seating is not possible.");
			messageBox.open();
		}
	}

	class RoundTimerTask extends TimerTask {
		private Display display;

		private long startTime;

		private Observer obs;

		public RoundTimerTask(Display d, Observer o) {
			super();
			display = d;
			obs = o;
			startTime = System.currentTimeMillis();
		}

		public void run() {
			if (!display.isDisposed()) {
				display.asyncExec(new Runnable() {
					public void run() {
						obs.update(tournament, new HintElapsedRoundTime(System
								.currentTimeMillis()
								- startTime));
					}
				});
			}
		}
	}

	public void startRound(Shell shell, Observer o) {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new RoundTimerTask(shell.getDisplay(), o), 1000, // initial
																			// delay
					1000); // subsequent rate
		}
	}

	public void stopRound(Shell shell, Observer o) {

		if (timer != null) {
			timer.cancel();
			timer = null;

			o.update(tournament, new HintElapsedRoundTime(0));
		}
	}

	public boolean isRoundClockRunning() {
		return timer != null;
	}

	static Result[] best_of_three = {
	// P1 wins
			new Result(1, 0, 0), new Result(2, 0, 0), new Result(2, 1, 0),

			// P2 wins
			new Result(0, 1, 0), new Result(0, 2, 0), new Result(1, 2, 0),

			// draws
			new Result(0, 0, 0), new Result(1, 1, 0),

			// P1 wins after drawn game
			new Result(1, 0, 1), new Result(1, 0, 2), new Result(2, 0, 1),

			// P2 wins after drawn game
			new Result(0, 1, 1), new Result(0, 1, 2), new Result(0, 2, 1),

			// draws after drawn game
			new Result(0, 0, 1), new Result(0, 0, 2), new Result(0, 0, 3),
			new Result(1, 1, 1), };

	public String[] getPrintableTournamentResults() {
		// generate Best-of-3 results
		String[] results = new String[best_of_three.length];

		for (int i = 0; i < best_of_three.length; i++) {
			results[i] = best_of_three[i].toString();
		}
		return results;
	}

	public Result getResult(int index) {
		return best_of_three[index];
	}

	public Tournament getCurrentTournament() {
		return tournament;
	}

}