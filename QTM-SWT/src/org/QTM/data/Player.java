/*
 * Created on 21.12.2004
 *
 */
package org.QTM.data;

import java.util.ArrayList;
import java.util.Iterator;

import org.QTM.control.Utils;

/**
 * @author WAHL_O
 *  
 */
public class Player implements Comparable {

	private String name;

	private int DCI;

	private int matchesByes = 0;

	private int matchesPlayed = 0;

	private int matchesWon = 0;

	private int matchesDrawn = 0;

	private int gamesPlayed = 0;

	private int gamesDrawn = 0;

	private int gamesWon = 0;

	private int opponentScoreSum = 0;

	private int opponentGameWinPercentageSum = 0;

	ArrayList opponents = new ArrayList();

	private boolean dropped = false;

	public String getName() {
		return name;
	}

	public int getDCI() {
		return DCI;
	}

	public void setName(String s) {
		name = s;
	}

	public void setDCI(int i) {
		DCI = i;
	}

	public int getMatchPoints() {
		return matchesWon * 3 + matchesDrawn + matchesByes * 3;
	}

	public int getGamePoints() {
		return gamesWon * 3 + gamesDrawn;
	}

	private int getMatchPercentage() {
		if (matchesPlayed == 0)
			return 0;

		return Math.max( 3333, (getMatchPoints() * 100 * 100) / (matchesPlayed * 3));
	}

	private int getGameWinPercentage() {
		if (gamesPlayed == 0)
			return 0;

		return (getGamePoints() * 100 * 100) / (gamesPlayed * 3);
	}

	private int getOpponentScore() {
		return opponents.size() == 0 ? 0 : opponentScoreSum / opponents.size();
	}

	private int getOpponentGameWinPercentage() {
		return opponents.size() == 0 ? 0 : opponentGameWinPercentageSum / opponents.size();
	}

	private double printableOpponentGameWinPercentageSum() {
		return Utils.normalize( opponentGameWinPercentageSum );
	}

	private double printableOpponentScoreSum() {
		return Utils.normalize( opponentScoreSum );
	}

	public double printableOpponentGameWinPercentage() {
		return Utils.normalize( getOpponentGameWinPercentage() );
	}

	public double printableOpponentScore() {
		return Utils.normalize( getOpponentScore() );
	}

	public double printableGameWinPercentage() {
		return Utils.normalize( getGameWinPercentage() );
	}

	public int getGamesPlayed() {
		return gamesPlayed;
	}

	public int getMatchesPlayed() {
		return matchesPlayed;
	}

	public int getMatchesWon() {
		return matchesWon;
	}

	public int getMatchesLost() {
		return matchesPlayed - matchesWon - matchesDrawn;
	}

	public int getMatchesBye() {
		return matchesByes;
	}

	public int getMatchesDrawn() {
		return matchesDrawn;
	}

	public int compareTo(Object arg0) {
		Player p2 = (Player) arg0;

		if (getMatchPoints() != p2.getMatchPoints())
			return p2.getMatchPoints() - getMatchPoints();

		// 1. tiebreaker
		if (getOpponentScore() != p2.getOpponentScore())
			return p2.getOpponentScore() - getOpponentScore();

		// 2. tiebreaker
		if (getGameWinPercentage() != p2.getGameWinPercentage())
			return p2.getGameWinPercentage() - getGameWinPercentage();

		// 3. tiebreaker or equal
		if (getOpponentGameWinPercentage() != p2.getOpponentGameWinPercentage())
			return p2.getOpponentGameWinPercentage() - getOpponentGameWinPercentage();
		
		return name.compareToIgnoreCase(p2.name);
	}

	private void removeOpponent(Player opponent) {
		if (opponent != null) {
			if (!opponents.contains(opponent)) {
				opponents.remove(opponent);

				opponent.removeOpponent(this);
			}
		}
	}

	private void addOpponent(Player opponent) {
		if (opponent != null) {
			if (!opponents.contains(opponent)) {
				opponents.add(opponent);

				opponent.addOpponent(this);
			}
		}
	}

	private void removeMatch(Player opponent, int games_played, int games_won) {
		gamesPlayed -= games_played;
		gamesWon -= games_won;

		if (opponent != null) {
			matchesPlayed--;
			removeOpponent(opponent);
		} else {
			matchesByes--;
		}

		Iterator it = opponents.iterator();
		while (it.hasNext()) {
			Player p = (Player) it.next();

			p.updateOpponentScore();
		}
	}

	private void updateMatch(Player opponent, int games_played, int games_won) {
		gamesPlayed += games_played;
		gamesWon += games_won;

		if (opponent != null) {
			matchesPlayed++;
			addOpponent(opponent);
		} else {
			matchesByes++;
		}

		Iterator it = opponents.iterator();
		while (it.hasNext()) {
			Player p = (Player) it.next();

			p.updateOpponentScore();
		}
	}

	private void updateOpponentScore() {
		opponentScoreSum = 0;
		opponentGameWinPercentageSum = 0;

		if (opponents.isEmpty())
			return;

		Iterator it = opponents.iterator();
		while (it.hasNext()) {
			Player p = (Player) it.next();

			opponentScoreSum += p.getMatchPercentage();
			opponentGameWinPercentageSum += p.getGameWinPercentage();
		}
	}

	public void updateMatchWin(Player opponent, int games_played, int games_won) {
		if (opponent != null)
			matchesWon++;
		updateMatch(opponent, games_played, games_won);
	}

	public void removeMatchWin(Player opponent, int games_played, int games_won) {
		if (opponent != null)
			matchesWon--;
		removeMatch(opponent, games_played, games_won);
	}

	public void updateMatchLoss(Player opponent, int games_played, int games_won) {
		updateMatch(opponent, games_played, games_won);
	}


	public void removeMatchLoss(Player opponent, int games_played, int games_won) {
		removeMatch(opponent, games_played, games_won);
	}

	public void updateMatchDraw(Player opponent, int games_played, int games_won) {
		matchesDrawn++;

		updateMatch(opponent, games_played, games_won);
	}

	public void removeMatchDraw(Player opponent, int games_played, int games_won) {
		matchesDrawn--;

		removeMatch(opponent, games_played, games_won);
	}

	public boolean hasPlayed(Player player2) {
		return opponents.contains(player2);
	}

	public boolean hasBye() {
		return matchesByes > 0;
	}

	public String printableStanding() {
		StringBuffer sb = new StringBuffer();

		sb.append("Player && Opponent              Matches  Match   Match  Games   Game      Game\n");
		sb.append("                                P/W/D   Points  Win%   played  Points    Win%\n");

		sb.append(printablePlayerStats());

		sb.append("\n");

		Iterator it = opponents.iterator();

		while (it.hasNext()) {
			Player p = (Player) it.next();
			sb.append(p.printablePlayerStats());
		}

		sb.append( printablePlayerSums() );
		sb.append( printablePlayerAverage() );

		sb.append("\n");
		sb.append("                                  Tie1    Tie2    Tie3\n");
		sb.append( printablePlayerTiebreakers() );

		return sb.toString();
	}

	private StringBuffer printablePlayerTiebreakers() {
		StringBuffer temp;
		StringBuffer line = new StringBuffer(80);

		line.append("Tiebreakers");
		Utils.leftAlign(line, 30);

		temp = new StringBuffer();
		temp.append( printableOpponentScore() );
		Utils.rightAlign(temp, 8);
		line.append(temp);

		temp = new StringBuffer();
		temp.append( printableGameWinPercentage() );
		Utils.rightAlign(temp, 8);
		line.append(temp);

		temp = new StringBuffer();
		temp.append( printableOpponentGameWinPercentage() );
		Utils.rightAlign(temp, 8);
		line.append(temp);
		return line.append("\n");
	}

	private StringBuffer printablePlayerSums() {
		StringBuffer temp;
		StringBuffer line = new StringBuffer(80);

		line.append("Totals");
		Utils.leftAlign(line, 30);

		temp = new StringBuffer();
		temp.append(printableOpponentScoreSum());
		Utils.rightAlign(temp, 23);
		line.append(temp);

		temp = new StringBuffer();
		temp.append(printableOpponentGameWinPercentageSum());
		Utils.rightAlign(temp, 24);
		line.append(temp);
		return line.append("\n");
	}

	private StringBuffer printablePlayerAverage() {
		StringBuffer temp;
		StringBuffer line = new StringBuffer(80);

		line.append("Average of ");
		line.append(opponents.size());
		line.append(" opponents");
		Utils.leftAlign(line, 30);

		temp = new StringBuffer();
		temp.append( printableOpponentScore() );
		Utils.rightAlign(temp, 23);
		line.append(temp);

		temp = new StringBuffer();
		temp.append( printableOpponentGameWinPercentage() );
		Utils.rightAlign(temp, 24);
		line.append(temp);
		return line.append("\n");
	}

	private StringBuffer printablePlayerStats() {
		StringBuffer temp;
		StringBuffer line = new StringBuffer();

		line.append(name);
		
		if(dropped)
			line.append(" (dropped)");

		Utils.leftAlign(line, 30);

		temp = new StringBuffer();
		temp.append(matchesPlayed);
		temp.append('/');
		temp.append(matchesWon);
		temp.append('/');
		temp.append(matchesDrawn);
		Utils.rightAlign(temp, 8);
		line.append(temp);

		temp = new StringBuffer();
		temp.append(getMatchPoints());
		Utils.rightAlign(temp, 8);
		line.append(temp);

		temp = new StringBuffer();
		temp.append((double) getMatchPercentage() / 100.0);
		Utils.rightAlign(temp, 7);
		line.append(temp);

		temp = new StringBuffer();
		temp.append(gamesPlayed);
		Utils.rightAlign(temp, 8);
		line.append(temp);

		temp = new StringBuffer();
		temp.append(getGamePoints());
		Utils.rightAlign(temp, 8);
		line.append(temp);

		temp = new StringBuffer();
		temp.append((double) getGameWinPercentage() / 100.0);
		Utils.rightAlign(temp, 8);
		line.append(temp);
		return line.append("\n");
	}

	public boolean hasDropped() {
		return dropped;
	}

	public void drop(boolean b) {
		dropped = b;
	}
}