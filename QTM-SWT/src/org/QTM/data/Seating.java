/*
 * Created on 29.12.2004
 *
 */
package org.QTM.data;

import org.QTM.app.Result;

/**
 * @author WAHL_O
 * 
 */
public class Seating implements Comparable {
	Player player1;
	Player player2;

	int p1PreviousMatchPoints;
	int p2PreviousMatchPoints;

	Result result = null;
	
	int table = 1;

	public Seating(int t, Player p1, Player p2) {
		player1 = p1;
		player2 = p2;
		
		if(player1 != null)
			p1PreviousMatchPoints = player1.getMatchPoints();
		if(player2 != null)
			p2PreviousMatchPoints = player2.getMatchPoints();

		table = t;
	}

	public boolean isFinished() {

		return result != null;
	}

	public void unfinished() {
		// can't remove what's not there
		if(!isFinished())
			return;

		if(player1 != null)
		{
			if (result.p1 > result.p2) {
				player1.removeMatchWin(player2, result.gamesPlayed(), result.p1);
				if(player2 != null)
					player2.removeMatchLoss(player1, result.gamesPlayed(), result.p2);
			} else if (result.p2 > result.p1) {
				player2.removeMatchWin(player1, result.gamesPlayed(), result.p2);
				if(player2 != null)
					player1.removeMatchLoss(player2, result.gamesPlayed(), result.p1);
			} else {
				player1.removeMatchDraw(player2, result.gamesPlayed(), result.p1);
				if(player2 != null)
					player2.removeMatchDraw(player1, result.gamesPlayed(), result.p2);
			}
		}

		result = null;
	}

	public void finished(Result r) {		
		// don't finish twice
		if(isFinished())
			return;
		
		result = r;
		
		if(player1 != null)
		{
			if (result.p1 > result.p2) {
				player1.updateMatchWin(player2, result.gamesPlayed(), result.p1);
				if(player2 != null)
					player2.updateMatchLoss(player1, result.gamesPlayed(), result.p2);
			} else if (result.p2 > result.p1) {
				player2.updateMatchWin(player1, result.gamesPlayed(), result.p2);
				if(player2 != null)
					player1.updateMatchLoss(player2, result.gamesPlayed(), result.p1);
			} else {
				player1.updateMatchDraw(player2, result.gamesPlayed(), result.p1);
				if(player2 != null)
					player2.updateMatchDraw(player1, result.gamesPlayed(), result.p2);
			}
		}
	}

	public int getTable() {
		return table;
	}

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public Result getResult() {
		return result;
	}

	public int getPlayer1PreviousMatchPoints() {
		return p1PreviousMatchPoints;
	}

	public int getPlayer2PreviousMatchPoints() {
		return p2PreviousMatchPoints;
	}

	public boolean contains(Player p) {
		return player1 == p || player2 == p;
	}

	public int compareTo(Object o) {
		Seating s2 = (Seating) o;

		return getPlayer1().getName().compareToIgnoreCase(s2.getPlayer1().getName());
	}
}