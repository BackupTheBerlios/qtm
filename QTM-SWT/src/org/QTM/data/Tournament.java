/*
 * Created on 16.12.2004
 *
 */
package org.QTM.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

/**
 * @author WAHL_O
 * 
 */
public class Tournament extends Observable {

	String name;

	String location;

	Date date = new Date();

	List rounds = new ArrayList();

	List players = new ArrayList();
	
	public Tournament() {
		super();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
		setChanged();
		notifyObservers( this.date );
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		if(this.location == null || !this.location.equals(location))
		{
			this.location = location;
			setChanged();
			notifyObservers(new HintTournamentLocation(this.location));
	
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(this.name == null || !this.name.equals(name))
		{
			this.name = name;
			setChanged();
			
			notifyObservers( new HintTournamentName(this.name) );

		}
	}

	public List getPlayersRanked() {
		SortedArrayList playersRanked = new SortedArrayList(players);
		return playersRanked;
	}

	public List getPlayers() {
		return new ArrayList( players );
	}

	public void changePlayerName(Player p, String s) {
		p.setName(s);

		if( players.contains(p) )
		{
			setChanged();
			notifyObservers(p);
		}
	}

	public void changePlayerDCI(Player p, int d) {
		p.setDCI(d);

		if( players.contains(p) )
		{
			setChanged();
			notifyObservers(p);
		}
	}

	public void addPlayer(String s, int i) {
		Player p = new Player();
		
		p.setName(s);
		p.setDCI(i);
		
		players.add(p);
		
		setChanged();
		notifyObservers( new HintNewPlayer(p) );
	}

	public void removePlayer(Player p) {
		if( players.contains(p) )
		{
			players.remove(p);

			setChanged();
			notifyObservers( new HintRemovedPlayer(p) );
		}
	}
	
	public void dropPlayer(Player p, boolean b) {
		p.drop(b);
		
		if( players.contains(p) )
		{
			setChanged();
			notifyObservers(p);
		}
	}
	
	public boolean isRoundFinished() {
		Round r = getCurrentRound();

		return r == null ? false : r.isFinished();
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
			setChanged();
			notifyObservers(r);
			return r;
		}
		
		return null;
	}

	public int getPlayerRank(Player p) {
		if(players.contains(p))
			return getPlayersRanked().indexOf(p) + 1;
		else
			return 0;
	}

	public boolean hasStarted() {
		return (getCurrentRound() != null);
	}

	public void updateMatch(Seating s, Result r) {
		
		if(r != null)
			s.finished(r);
		else
			s.unfinished();
		
		notifyObservers( s );
		notifyObservers( s.getPlayer1() );
		notifyObservers( s.getPlayer2() );
		notifyObservers( getCurrentRound() );
	}

	public boolean isRoundStartable() {
		Round r = getCurrentRound();
		
		if( r != null )
			return r.isFinished();
		
		return players.size() >= 2;
	}
}