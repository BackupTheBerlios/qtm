/*
 * Created on 29.12.2004
 *
 */
package org.QTM.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


/**
 * @author WAHL_O
 *
 */
public class Round {

	List seatings = new ArrayList();
	private int round;
	private boolean seatingPossible = false;
	
	public Round(int r, List nonSeatedPlayers) {

		round = r;
		
		if(nonSeatedPlayers == null)
			return;
		
		// first round: random pairing
		if(round == 1)
			seatPlayersByRandom(nonSeatedPlayers);
		// seating round 2 onwards
		else
			seatPlayersByMatchPoints(nonSeatedPlayers);	
		
		seatingPossible = (nonSeatedPlayers.size() == 0);
	}

	private void seatPlayersByMatchPoints(List nonSeatedPlayers) {
		Random ran = new Random(System.currentTimeMillis());
		int table = 1;
		int currentMaxPoints = ((Player)nonSeatedPlayers.get(0)).getMatchPoints();

		while(nonSeatedPlayers.size() > 1 )
		{
			ArrayList topList = extractTopPlayers(nonSeatedPlayers, currentMaxPoints);
			
			while( topList.size() > 1 )
			{
				// pick two players that havn't played each other
				Player player1 = getCriticalPlayer(topList);
				
				if(player1 == null)
					break;
				
				// if this player has only one seating left, need to pick it
				Player player2 = getCriticalOpponent(player1, topList);

				if(player2 == null)
				{
					player2 = getRandomOpponent(player1, topList);
				}
				
				if(player2 == null)
				{
					// throw everything back
					topList.add(player1);
					break;
				}
				// got two players that havn't played each other
				Seating seating = new Seating(table, player1,player2);
				
				seatings.add(seating);
				
				table++;
			}
			
			if(nonSeatedPlayers.size() > 0)
				currentMaxPoints = ((Player)nonSeatedPlayers.get(0)).getMatchPoints();
			// all remaining nonSeatedPlayers werde included in search, quit right now
			else if(topList.size() > 0)
			{
				nonSeatedPlayers.addAll(topList);
				break;
			}
				
			if( topList.size() > 0)
			{
				// throw all remaining back
				nonSeatedPlayers.addAll(topList);
			}
		}
		
		if(nonSeatedPlayers.size() == 1)
		{
			Seating seating = new Seating( table, (Player)nonSeatedPlayers.remove(0), null);
			seatings.add(seating);
			
			seating.finished( new Result(2,0,0) );
		}
	}

	private Player getCriticalOpponent(Player player1, ArrayList topList) {
		int[] possibleSeatingsCount = new int[topList.size()];
		
		calcCriticalPlayers(player1, topList, possibleSeatingsCount);

		for(int i = possibleSeatingsCount.length-1; i >= 0 ; i--)
		{
			// is this the critical partner?
			if(possibleSeatingsCount[i] == 1)
				return (Player)topList.remove(i);
		}
		
		return null;
	}

	private Player getRandomOpponent(Player player1, ArrayList players) {
		Player p = null;
		Random ran = new Random(System.currentTimeMillis());
		int idx = ran.nextInt(players.size());
		int startIdx = idx;
		
		while( p == null )
		{
			Player player2 = (Player)players.get(idx);
			
			if( player1 != player2 && !player1.hasPlayed(player2))
			{
				players.remove(player2);
				
				return player2;
			}
			idx = (idx+1) % players.size();
			
			if(idx == startIdx)
				break;
		}	
		return p;
	}

	private Player getCriticalPlayer(List topList) {
		int[] possibleSeatingsCount = new int[topList.size()];

		for(int p1Idx = 0;p1Idx < topList.size()-1;p1Idx++)
		{
			Player p1 = (Player) topList.get(p1Idx);

			calcCriticalPlayers(p1, topList, possibleSeatingsCount);

			if(possibleSeatingsCount[p1Idx] > 1 && p1.hasBye())
			{
				possibleSeatingsCount[p1Idx] = 2;
			}
		}
		
		int minIdx = 0, minValue= Integer.MAX_VALUE;
		for(int i = 0; i < possibleSeatingsCount.length; i++)
		{
			// there is one player that can't be seated in this lot
			if(possibleSeatingsCount[i] == 0)
			{
				return null;
			}
			
			if(possibleSeatingsCount[i] < minValue)
			{
				minValue = possibleSeatingsCount[i];
				minIdx = i;
			}
		}
		return (Player)topList.remove(minIdx);
	}

	private void calcCriticalPlayers(Player p1, List topList, int[] possibleSeatingsCount) {
		int p1Idx = topList.indexOf(p1);
		for(int p2Idx = p1Idx + 1;p2Idx < topList.size();p2Idx++)
		{
			Player p2 = (Player)topList.get(p2Idx);
			
			if(p1 != p2 && !p1.hasPlayed(p2) )
			{
				if(p1Idx >= 0)
					possibleSeatingsCount[p1Idx]++;
				possibleSeatingsCount[p2Idx]++;
			}
		}
	}

	private ArrayList extractTopPlayers(List nonSeatedPlayers, int maxPoints ) {
		
		ArrayList topList = new ArrayList();
		
		// pick all players with maxPoints
		while(nonSeatedPlayers.size() > 0)
		{
			Player p = (Player) nonSeatedPlayers.remove(0);
			if (p.getMatchPoints() >= maxPoints) {
				topList.add(p);				
			} else {
				// push back
				nonSeatedPlayers.add(p);
				break;
			}
		}
		
		return topList;
	}

	private void seatPlayersByRandom(List nonSeatedPlayers) {
		Random ran = new Random(System.currentTimeMillis());
		int table = 1;
		while(nonSeatedPlayers.size() > 1 )
		{
			Player player1 = (Player)nonSeatedPlayers.remove(ran.nextInt(nonSeatedPlayers.size()));
			Player player2 = (Player)nonSeatedPlayers.remove(ran.nextInt(nonSeatedPlayers.size()));
			
			Seating seating = new Seating(table, player1,player2);
			
			seatings.add(seating);
			
			table++;
		}
		
		if(nonSeatedPlayers.size() > 0)
		{
			Seating seating = new Seating( table, (Player)nonSeatedPlayers.remove(0), null);
			seatings.add(seating);
			
			seating.finished( new Result(2,0,0) );
		}
	}

	public boolean isFinished() {
		Iterator iter = seatings.iterator();
		
		while(iter.hasNext()) {
			Seating s = (Seating) iter.next();
			
			if(!s.isFinished())
				return false;
		}
		return true;
	}

	public List getSortedSeatings() {
		SortedArrayList sorted = new SortedArrayList( seatings );
		sorted.sort();

		// create copy without BYES
		Iterator it = seatings.iterator();
		while (it.hasNext()) {
			Seating s = (Seating) it.next();
			
			if(s.getPlayer2() != null)
			{
				Seating switched = new Seating(s.getTable(), s.getPlayer2(), s.getPlayer1());

				sorted.add(switched);
			}
		}
		
		return sorted;
	}

	public List getCompleteSeatings() {
		List copy = new ArrayList( );

		// create copy without BYES
		Iterator it = seatings.iterator();
		while (it.hasNext()) {
			Seating s = (Seating) it.next();
			
			if(s.getPlayer2() != null)
				copy.add(s);
		}
		
		return copy;
	}

	public List getSeatings() {
		return seatings;
	}

	public int getRound() {
		return round;
	}
	
	public boolean isSeatingPossible() {
		return seatingPossible;
	}
}
