/*
 * Created on 16.12.2004
 *
 */
package org.QTM.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author WAHL_O
 * 
 */
public class Tournament {

	String name;

	String location;

	Date date = new Date();

	boolean dirty;

	List rounds = new ArrayList();

	List players = new ArrayList();
	
	public Tournament() {
		setDirty(false);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDirty(boolean d) {
		dirty = d;
	}

	public boolean isDirty() {
		return dirty;
	}

	public List getPlayersRanked() {
		SortedArrayList playersRanked = new SortedArrayList(players);
		return playersRanked;
	}

	public List getPlayers() {
		return players;
	}

	public Player addPlayer(String s, int i) {
		Player p = new Player();
		
		p.setName(s);
		p.setDCI(i);
		
		players.add(p);
		
		setDirty(true);

		return p;
	}

	public void removePlayer(Player p) {
		players.remove(p);

		setDirty(true);
	}
	
	public Round getCurrentRound() {
		
		if(rounds.isEmpty())
			return null;
		
		return (Round)rounds.get(rounds.size()-1);
	}

	public Round nextRound() {
		int count = 0;
		
		if(!rounds.isEmpty())
			count = rounds.size();
		
		Iterator it = players.iterator(); 
		SortedArrayList nonSeatedPlayers = new SortedArrayList( );
		nonSeatedPlayers.setAutoSort(true);
		
		while (it.hasNext()) {
			Player p = (Player) it.next();
			
			if( !p.hasDropped() )
			{
				nonSeatedPlayers.add(p);
			}
		}

		Round r = new Round(count+1, nonSeatedPlayers);

		if( r.isSeatingPossible() )
		{
			rounds.add(r);
			setDirty(true);
			
			return r;
		}
		
		return null;
	}

	public int getRank(Player p) {
		return getPlayersRanked().indexOf(p) + 1;
	}
}