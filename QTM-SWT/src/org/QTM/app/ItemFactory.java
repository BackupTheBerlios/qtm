/*
 * Created on 02.02.2005
 *
 */
package org.QTM.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.QTM.data.HintNewPlayer;
import org.QTM.data.HintRemovedPlayer;
import org.QTM.data.HintTournamentLocation;
import org.QTM.data.HintTournamentName;
import org.QTM.data.Player;
import org.QTM.data.Round;
import org.QTM.data.Seating;
import org.QTM.data.Tournament;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * @author WAHL_O
 *  
 */
public class ItemFactory implements Observer {
	ObjectContainer db;

	ItemFactory() {
		Db4o.configure().objectClass(Tournament.class).callConstructor(true);
		Db4o.configure().objectClass(Tournament.class).cascadeOnActivate(true);
		Db4o.configure().objectClass(Tournament.class).cascadeOnUpdate(true);
		Db4o.configure().objectClass(Observable.class).translate(new com.db4o.config.TNull());
		
		Db4o.configure().objectClass(Player.class).cascadeOnActivate(true);
		Db4o.configure().objectClass(Player.class).cascadeOnUpdate(true);
		
		Db4o.configure().objectClass(Round.class).cascadeOnActivate(true);
		Db4o.configure().objectClass(Round.class).cascadeOnUpdate(true);
	
		Db4o.configure().objectClass(Seating.class).cascadeOnActivate(true);
		Db4o.configure().objectClass(Seating.class).cascadeOnUpdate(true);

		Db4o.configure().activationDepth(1);
		Db4o.configure().exceptionsOnNotStorable(true);

		// TODO remove after debugging
		Db4o.configure().messageLevel(0);

		// TODO add if callbacks are not used
		// Db4o.configure().callbacks(false);
		// Db4o.configure().testConstructors(false);

		db = Db4o.openFile(PreferenceLoader.getPreferenceStore().getString("tournamentDB"));

	}

	public void dispose() {
		db.commit();
		db.close();
	}

	public void update(Observable o, Object arg) {

		// TODO is it enough to only save the changed object e.g. player/round,
		// etc. in arg?
		// handle auto-save if it is the our tournament
		if (arg instanceof HintTournamentLocation || arg instanceof HintTournamentName || arg instanceof HintNewPlayer || arg instanceof HintRemovedPlayer ) {
			long start = System.currentTimeMillis();

			db.set(o);

			System.out.print(System.currentTimeMillis() - start);
			System.out.println(" ms to save tournament");
		} else if (arg instanceof Player ) {
			long start = System.currentTimeMillis();

			db.set(arg);

			System.out.print(System.currentTimeMillis() - start);
			System.out.println(" ms to save player");
		} else if (arg instanceof Round ) {
			long start = System.currentTimeMillis();

			db.set(arg);

			System.out.print(System.currentTimeMillis() - start);
			System.out.println(" ms to save round");
		}
	}

	public void giveBack(Tournament tournament) {
		if(tournament != null)
		{
			tournament.deleteObservers();
			db.deactivate(tournament, Integer.MAX_VALUE);		
		}
		tournament = null;
	}

	public Tournament createTournament() {
		Tournament tournament = new Tournament();

		tournament.addObserver(this);

		return tournament;
	}
	
	public List queryTournament() {
		List names = new ArrayList();
		
		Query q = db.query();
		q.constrain(Tournament.class);
		q.orderAscending();
		
		ObjectSet set = q.execute();

		while (set.hasNext()) {
			Tournament t = (Tournament) set.next();
			names.add(t.getName());
		}
		return names;
	}

	public Tournament getTournament(String selected) {
		Query q = db.query();
		q.constrain(Tournament.class);
		q.orderAscending();
		q.descend("name").constrain(selected);

		ObjectSet set = q.execute();

		if (set.hasNext()) {
			Tournament tournament = (Tournament) set.next();

			db.activate(tournament, 1);

			tournament.addObserver(this);
			
			return tournament;
		}
		
		return null;
	}


}