/*
 * Created on 16.12.2004
 *
 */
package org.QTM.app;

import org.QTM.data.Tournament;

/**
 * @author WAHL_O
 * 
 */
public interface ITournamentListener {

	/**
	 *  
	 */
	void update(Tournament t, Object hint);

	void updatePlayers(Tournament t, Object hint);

	void updateStandings(Tournament tournament, Object hint);

	void updateSeatings(Tournament tournament, Object hint);

}