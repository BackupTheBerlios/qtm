/*
 * Created on 18.01.2005
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package org.QTM.app;

import java.text.MessageFormat;
import java.text.NumberFormat;

public class HintElapsedRoundTime {
	long millis;

	static final MessageFormat MINUTE_SECONDS = new MessageFormat("{0}:{1}");

	private static final NumberFormat fmt = NumberFormat.getInstance();

	static {
	    fmt.setMinimumIntegerDigits(2);
		MINUTE_SECONDS.setFormat(0, fmt);
		MINUTE_SECONDS.setFormat(1, fmt);
	}

	public HintElapsedRoundTime(long time) {
		this.millis = time;
	}

	public long getMinutes()
	{
		long seconds = millis / 1000;
	
		return seconds / 60;
	}
	
	public String formatElapsedTime() {
		long seconds = millis / 1000;
		long minutes = seconds / 60;
		Object[] args = { new Long(minutes), new Long(seconds % 60) };
		return MINUTE_SECONDS.format(args);
	}

}