package org.QTM.control;

import java.io.File;
import java.io.IOException;

/**
 * This class contains frequently-used static utility methods.
 *  
 */
public final class Utils {
	/**
	 * Private constructor to prevent instantiation.
	 */
	private Utils() {
	}

	private static final String SPACES = "                                 ";

	private static final int SPACES_LEN = SPACES.length();

	/**
	 * Right aligns some text in a StringBuffer N.B. modifies the input buffer
	 * 
	 * @param in
	 *            StringBuffer containing some text
	 * @param len
	 *            output length desired
	 * @return input StringBuffer, with leading spaces
	 */
	public static StringBuffer rightAlign(StringBuffer in, int len) {
		int pfx = len - in.length();
		while (pfx > 0) {
			pfx = Math.min(pfx, SPACES_LEN);
			in.insert(0, SPACES.substring(0, pfx));

			pfx = len - in.length();
		}
		return in;
	}

	/**
	 * Left aligns some text in a StringBuffer N.B. modifies the input buffer
	 * 
	 * @param in
	 *            StringBuffer containing some text
	 * @param len
	 *            output length desired
	 * @return input StringBuffer, with trailing spaces
	 */
	public static StringBuffer leftAlign(StringBuffer in, int len) {
		int sfx = len - in.length();
		while (sfx > 0) {
			sfx = Math.min(sfx, SPACES_LEN);
			in.append(SPACES.substring(0, sfx));

			sfx = len - in.length();
		}
		return in;
	}

	public static double normalize(double d) {
		return d / 100.0;
	}

	public static File tempFile(String prefix, String ext) throws IOException {
		File file = null;

		file = File.createTempFile(prefix, ext);
		file.deleteOnExit();
		return file;
    }
}