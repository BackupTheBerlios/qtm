/*
 * Created on 13.01.2005
 *
 */
package org.QTM.app;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.QTM.data.Player;
import org.QTM.data.Round;
import org.QTM.data.Seating;
import org.QTM.data.Tournament;

/**
 * @author WAHL_O
 *
 */
public class ReportGenerator {
	
	private String jobName;
	private String reportFile;

	public ReportGenerator(String j, String r) {
		jobName = j;
		reportFile = r;
	}
	
	public JasperPrint print(Tournament t, List l) {
		JasperPrint jasperPrint = null;
		try {
			// First, load JasperDesign from XML and compile it into JasperReport
			InputStream stream = ReportGenerator.class.getResourceAsStream(reportFile);
			if(stream == null)
				return null;
			
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
			if(jasperReport == null)
				return null;

			// Second, create a map of parameters to pass to the report.
			Map parameters = new HashMap();
			parameters.put("JobName", jobName );
			parameters.put("TournamentName", t.getName());
			parameters.put("TournamentLocation", t.getLocation());
			parameters.put("TournamentDate", t.getDate());
			
			Round round = t.getCurrentRound();
			parameters.put("Final", new Boolean(round.isFinished()));
			
			if(round != null)
				parameters.put("CurrentRound", new Integer(round.getRound()));
			else
				parameters.put("CurrentRound", new Integer(0));
			
			// Fourth, create JasperPrint using fillReport() method
			jasperPrint = JasperFillManager.fillReport( jasperReport, parameters, new ListDataSource(l) );

			jasperPrint.setName(jobName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jasperPrint;
	}

	  class ListDataSource implements JRDataSource {

		Iterator iterator;
		Player player = null;
		Seating seating = null;

		public ListDataSource(List l) {
			iterator = l.iterator();
		}

		public boolean next() throws JRException {

			if( iterator.hasNext() )
			{
				Object o = iterator.next();

				player = null;
				seating = null;

				if(o instanceof Player)
					player = (Player)o;
				if(o instanceof Seating)
					seating = (Seating)o;
				return true;
			}
			
			return false;
		}

		public Object getFieldValue(JRField jrField) throws JRException {
			String field = jrField.getName();

			if(player != null)
			{
				if( "Name".equals(field)) {
					return player.getName();
				} else if( "MatchPoints".equals(field)) {
					return Integer.toString(player.getMatchPoints());
				} else if( "OpponentScore".equals(field)) {
					return Double.toString(player.printableOpponentScore())+"%";
				} else if( "GameWinPercentage".equals(field)) {
					return Double.toString(player.printableGameWinPercentage())+"%";
				} else if( "OpponentGameWinPercentage".equals(field)) {
					return Double.toString(player.printableOpponentGameWinPercentage())+"%";
				} else if( "MatchesPlayed".equals(field)) {
					return Integer.toString(player.getMatchesPlayed());
				} else if( "MatchesWon".equals(field)) {
					return Integer.toString(player.getMatchesWon());
				} else if( "MatchesLost".equals(field)) {
					return Integer.toString(player.getMatchesLost());
				} else if( "MatchesDrawn".equals(field)) {
					return Integer.toString(player.getMatchesDrawn());
				} else if( "MatchesBye".equals(field)) {
					return Integer.toString(player.getMatchesBye());
				} else if( "Dropped".equals(field)) {
					return new Boolean( player.hasDropped() );
				}
			} else if (seating != null) {
				Player player1 = seating.getPlayer1();
				Player player2 = seating.getPlayer2();
				
				if( "Table".equals(field)) {
					return new Integer(seating.getTable());
				} else if( "Player1Name".equals(field) && player1 != null) {
					return player1.getName();
				} else if( "Player2Name".equals(field) && player2 != null) {
					return player2.getName();
				} else if( "Player1MatchPoints".equals(field) && player1 != null) {
					return new Integer(player1.getMatchPoints());
				} else if( "Player2MatchPoints".equals(field) && player2 != null) {
					return new Integer(player2.getMatchPoints());
				}
			}
			
			return null; // Not found...
		}
	}
}
