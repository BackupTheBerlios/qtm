/*
 * Created on 09.01.2005
 *
 */
package org.QTM.app;

/**
 * @author WAHL_O
 */

public class Result {
	
	public int p1;
	public int p2;
	public int draws;

	public Result(int p1, int p2, int d) {
		this.p1 = p1;
		this.p2 = p2;
		this.draws = d;
	}
	
	public String toString()
	{
		StringBuffer r = new StringBuffer(5);
		
		r.append( Integer.toString(p1) );
		r.append( '-' );
		r.append( Integer.toString(p2) );
		
		if(draws>0 )
		{
			r.append( '-' );
			r.append( Integer.toString(draws) );
		}
		
		return r.toString();
	}

	public int gamesPlayed()
	{
		return p1 + p2 + draws;
	}
}
