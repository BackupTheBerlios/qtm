/*
 * Created on 16.12.2004
 *
 */
package org.QTM.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.jasperreports.engine.JasperPrint;

import org.QTM.control.JasperViewDialog;
import org.QTM.control.OpenDialog;
import org.QTM.data.Player;
import org.QTM.data.Result;
import org.QTM.data.Round;
import org.QTM.data.Seating;
import org.QTM.data.Tournament;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * @author WAHL_O
 * 
 */
public class Controller {
	List listeners = new ArrayList();

	private Tournament tournament;

	ObjectContainer db;

	private Preferences preferences;

	private Timer timer = null;

	public Controller(Preferences p) {
		preferences = p;

		Db4o.configure().objectClass(Tournament.class).cascadeOnUpdate(true);
		Db4o.configure().objectClass(Tournament.class).cascadeOnActivate(true);
		Db4o.configure().objectClass(Tournament.class).updateDepth(2);
		
		Db4o.configure().objectClass(Player.class).cascadeOnActivate(true);
		Db4o.configure().objectClass(Player.class).cascadeOnUpdate(true);

		Db4o.configure().objectClass(Round.class).cascadeOnActivate(true);
		Db4o.configure().objectClass(Round.class).cascadeOnUpdate(true);

		Db4o.configure().objectClass(Seating.class).cascadeOnActivate(true);
		Db4o.configure().objectClass(Seating.class).cascadeOnUpdate(true);

		Db4o.configure().activationDepth(1);
		Db4o.configure().exceptionsOnNotStorable(true);
		
		tournament = new Tournament();

		// TODO remove after debugging
		Db4o.configure().messageLevel(5);
		db = Db4o.openFile(preferences.TOURNAMENT_DB_PFILENAME);

	}

	public void dispose() {
		db.close();
	}

	private void update(Object hint) {
		Iterator it = listeners.iterator();

		while (it.hasNext()) {
			ITournamentListener tl = (ITournamentListener) it.next();

			tl.update(tournament, hint);

			tl.updatePlayers(tournament, hint);

			tl.updateStandings(tournament, hint);

			tl.updateSeatings(tournament, hint);
		}
	}

	public void addListener(ITournamentListener t) {
		listeners.add(t);
	}

	public void removeListener(ITournamentListener t) {
		listeners.remove(t);
	}

	public void newTournament(Shell shell) {
		db.deactivate(tournament, Integer.MAX_VALUE);
		
		tournament = new Tournament();

		update(tournament);
	}

	public void openTournament(Shell shell) {
		List names = new ArrayList();

		Query q = db.query();
		q.constrain(Tournament.class);
		q.orderAscending();
		
		//		q.descend("name").constrain("one");
		ObjectSet set = q.execute();

		while (set.hasNext()) {
			Tournament t = (Tournament) set.next();
			names.add(t.getName());
		}

		OpenDialog dlg = new OpenDialog(shell, names);
		
		if( dlg.open() == IDialogConstants.CANCEL_ID) 
			return;
		
		String selected = dlg.getSelection();
		
		q.descend("name").constrain(selected);
		set = q.execute();
		
		if(set.hasNext())
		{
			db.deactivate(tournament, Integer.MAX_VALUE);
			
			tournament = (Tournament) set.next();
			
			db.activate(tournament, Integer.MAX_VALUE);
			
			update(tournament);
			
			Round r = tournament.getCurrentRound();
			db.activate(r, Integer.MAX_VALUE);
			if (r != null)
				update(r);
		}
	}

	public void saveTournament(Shell shell) {
		if (tournament.isDirty() && !"".equals(tournament.getName()))
		{
			tournament.setDirty(false);
			db.set(tournament);

			update(new HintDirtyFlag(tournament.isDirty()));
		}
	}

	public void changeTournamentName(String s) {

		if (!s.equals(tournament.getName())) {
			tournament.setName(s);

			tournament.setDirty(true);

			update(tournament);
		}
	}

	public void changeTournamentLocation(String s) {
		if (!s.equals(tournament.getLocation())) {
			tournament.setLocation(s);

			tournament.setDirty(true);

			update(tournament);
		}
	}

	public void changePlayerName(Player p, String s) {
		p.setName(s);

		tournament.setDirty(true);
		update(p);
		update(new HintDirtyFlag(tournament.isDirty()));
	}

	public void change_PlayerDCI(Player p, int d) {
		p.setDCI(d);

		tournament.setDirty(true);
		update(p);
		update(new HintDirtyFlag(tournament.isDirty()));
	}

	public void newPlayer() {
		Player p = tournament.addPlayer("NewKid", 1);

		tournament.setDirty(true);
		update(p);
		update(new HintDirtyFlag(tournament.isDirty()));
	}

	public void removePlayer(Player p) {
		tournament.removePlayer(p);

		tournament.setDirty(true);
		update(new HintDirtyFlag(tournament.isDirty()));
	}

	public boolean hasTournamentStarted() {
		Round r = tournament.getCurrentRound();
	
		return (r != null);
	}

	public void printCurrentStandings(Shell shell) {
		String curDir = System.getProperty("user.dir");
		
		ReportGenerator plp = new ReportGenerator("Standings", curDir + preferences.STANDING_REPORT_PFILENAME );
				
		JasperPrint jasperPrint = plp.print(tournament, tournament.getPlayersRanked());
		
		if(jasperPrint != null)
		{
			JasperViewDialog jvd = new JasperViewDialog(shell, jasperPrint);
			jvd.open();
		}
	}

	public void printResultSlips(Shell shell) {
		String curDir = System.getProperty("user.dir");
		
		ReportGenerator plp = new ReportGenerator("ResultSlips", curDir + preferences.RESULTSLIPS_REPORT_PFILENAME );
				
		JasperPrint jasperPrint = plp.print(tournament, tournament.getCurrentRound().getCompleteSeatings() );

		if(jasperPrint != null)
		{
			JasperViewDialog jvd = new JasperViewDialog(shell, jasperPrint);
			jvd.open();
		}
	}

	public void printSeatings(Shell shell) {
		String curDir = System.getProperty("user.dir");
		
		ReportGenerator plp = new ReportGenerator("Seatings", curDir + preferences.SEATING_REPORT_PFILENAME );
				
		JasperPrint jasperPrint = plp.print(tournament, tournament.getCurrentRound().getSortedSeatings());
		
		if(jasperPrint != null)
		{
			JasperViewDialog jvd = new JasperViewDialog(shell, jasperPrint);
			jvd.open();
		}
	}

	public boolean isRoundFinished() {
		Round r = tournament.getCurrentRound();

		if (r == null)
			return tournament.getPlayers().size() >= 2;

		return r.isFinished();
	}

	public void drop(Player player, boolean b) {
		player.drop(b);
		
		tournament.setDirty(true);
		update(new HintDirtyFlag(tournament.isDirty()));
		update(player);
	}
	
	public void nextRound(Shell shell) {
		if (isRoundFinished()) {
			Round r = tournament.nextRound();

			if( r != null)
			{
				tournament.setDirty(true);
				update(new HintDirtyFlag(tournament.isDirty()));
				update(r);
			}
			else
			{
				MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_WARNING);
				messageBox.setMessage("Seating is not possible.");
				messageBox.open();
			}
		}
	}

	class RoundTimerTask extends TimerTask {
		private Display display;
		
		private long startTime;
		
		public RoundTimerTask(Display d) {
			super();
			display = d;
			startTime = System.currentTimeMillis();
		}

		public void run() {
			if (!display.isDisposed()) {
				display.asyncExec(new Runnable() {
					public void run() {
						update(new HintElapsedRoundTime(System.currentTimeMillis() - startTime));
					}
				});
			}
		}
	}
	
	public void startRound(Shell shell) {
		if(timer == null)
		{
	        timer = new Timer();
	        timer.schedule(new RoundTimerTask( shell.getDisplay() ),
		               1000,   // initial delay
		               1000);  // subsequent rate
		}
	}

	public void stopRound(Shell shell) {
		
		if(timer != null)
		{
	        timer.cancel();
	        timer = null;
	        
	        update( new HintElapsedRoundTime(0) );
		}
	}
	
	public boolean isRoundClockRunning() {
		return timer != null;
	}

	public void updateMatch(Shell shell, Seating s, Result r) {
		
		if(r != null)
			s.finished(r);
		else
			s.unfinished();
		
		if(isRoundFinished() && isRoundClockRunning())
			stopRound(shell);
		
		tournament.setDirty(true);
		update(new HintDirtyFlag(tournament.isDirty()));
		update(s);
		update( tournament.getCurrentRound() );
	}

	public int getPlayerRank(Player p) {
		return tournament.getRank(p);
	}

	static Result[] best_of_three =  {
			// P1 wins
			new Result(1,0,0),
			new Result(2,0,0),
			new Result(2,1,0),
			
			// P2 wins
			new Result(0,1,0),
			new Result(0,2,0),
			new Result(1,2,0),
			
			// draws
			new Result(0,0,0),
			new Result(1,1,0),

			// P1 wins after drawn game
			new Result(1,0,1),
			new Result(1,0,2),
			new Result(2,0,1),

			// P2 wins after drawn game
			new Result(0,1,1),
			new Result(0,1,2),
			new Result(0,2,1),
			
			// draws after drawn game
			new Result(0,0,1),
			new Result(0,0,2),
			new Result(0,0,3),
			new Result(1,1,1),
	};
	
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
}