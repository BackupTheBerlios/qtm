package org.QTM.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class PreferenceLoader {
  private static List colorArray = new ArrayList();
  private static Map colorMap = new Hashtable();
  private static List fontArray = new ArrayList();
  private static Map fontMap = new Hashtable();
  private static PreferenceStore preferenceStore = null;
  public static boolean customPrefFile; // winreg
  public static boolean customHomeDir; // winreg
  private static String prefFileName;
  private static String homeDirectory;
  private static String ENTRY_SEPARATOR = ";";

  private PreferenceLoader() {
  }

  public static void cleanUp() {
    for (int i = 0; i < fontArray.size(); i++) {
      Font font = (Font) fontArray.get(i);
      if (font != null && !font.isDisposed())
        font.dispose();
    }
    for (int i = 0; i < colorArray.size(); i++) {
      Color color = (Color) colorArray.get(i);
      if (color != null && !color.isDisposed())
        color.dispose();
    }
  }

  public static boolean contains(String preferenceString) {
    return preferenceStore.contains(preferenceString);
  }

  public static PreferenceStore getPreferenceStore() {
    return preferenceStore;
  }

  public static String getPrefFile() {
    return prefFileName;
  }

  public static void initialize() throws IOException {

    if (preferenceStore == null) {
      prefFileName = VersionInfo.getName() + ".pref";

      if (new File(prefFileName).exists())
        preferenceStore = new PreferenceStore(prefFileName);
      else {
        prefFileName = VersionInfo.getHomeDirectory() + prefFileName;

        File prefFile = new File(prefFileName);
        if (!prefFile.exists()) {
          File parentDir = new File(prefFile.getParent());
          parentDir.mkdirs();
        }
        preferenceStore = new PreferenceStore(prefFileName);
      }
    }

    try {
      preferenceStore.load();

    } catch (IOException e) {
      preferenceStore.save();
    }

    // preferenceStore = (PreferenceStore) setDefaults(preferenceStore);
  }

  public static void initialize2() {
    preferenceStore = (PreferenceStore) setDefaults(preferenceStore);
  }

  public static boolean loadBoolean(String preferenceString) {
    if (preferenceStore.contains(preferenceString))
      return preferenceStore.getBoolean(preferenceString);

    return false;
  }

  public static Color loadColor(String preferenceString) {
    if (preferenceStore.contains(preferenceString)) {
      Color newColor = new Color(null, PreferenceConverter.getColor(preferenceStore, preferenceString));
      if (colorMap.containsKey(preferenceString) && !((Color) colorMap.get(preferenceString)).isDisposed()) {
        if (newColor.getRGB().equals(((Color) colorMap.get(preferenceString)).getRGB()))
          newColor.dispose();
        else {
          colorArray.add(newColor);
          colorMap.put(preferenceString, newColor);
        }
      } else {
        colorArray.add(newColor);
        colorMap.put(preferenceString, newColor);
      }
      return (Color) colorMap.get(preferenceString);
    }
    return null;
  }

  public static Font loadFont(String preferenceString) {
    if (preferenceStore.contains(preferenceString)) {
      Font newFont = new Font(null, PreferenceConverter.getFontDataArray(preferenceStore, preferenceString));
      if (fontMap.containsKey(preferenceString) && !((Font) fontMap.get(preferenceString)).isDisposed()) {
        if (newFont.getFontData()[0].equals(((Font) fontMap.get(preferenceString)).getFontData()[0]))
          newFont.dispose();
        else {
          fontArray.add(newFont);
          fontMap.put(preferenceString, newFont);
        }
      } else {
        fontArray.add(newFont);
        fontMap.put(preferenceString, newFont);
      }
      return (Font) fontMap.get(preferenceString);
    }
    return null;
  }

  public static int loadInt(String preferenceString) {
    if (preferenceStore.contains(preferenceString))
      return preferenceStore.getInt(preferenceString);
    return 0;
  }

  public static int loadOrientation(String preferenceString) {
    if (preferenceStore.contains(preferenceString)) {
      int orientation = preferenceStore.getInt(preferenceString);
      if ((orientation == SWT.VERTICAL) || (orientation == SWT.HORIZONTAL))
        return orientation;
    }
    return SWT.HORIZONTAL;
  }

  public static Rectangle loadRectangle(String preferenceString) {
    if (preferenceStore.contains(preferenceString))
      return PreferenceConverter.getRectangle(preferenceStore, preferenceString);
    return null;
  }

  public static String loadString(String preferenceString) {
    if (preferenceStore.contains(preferenceString))
      return preferenceStore.getString(preferenceString).intern();
    return "";
  }

  public static String[] loadStringArray(String preferenceString) {
    StringTokenizer tokenizer = new StringTokenizer(loadString(preferenceString), ENTRY_SEPARATOR);
    int numTokens = tokenizer.countTokens();
    String[] stringArray = new String[numTokens];
    for (int i = 0; i < numTokens; i++) {
      stringArray[i] = tokenizer.nextToken();
    }
    return stringArray;
  }

  public static void setValue(String preferenceString, String[] stringArray) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < stringArray.length; i++) {
      buffer.append(stringArray[i]);
      buffer.append(ENTRY_SEPARATOR);
    }

    preferenceStore.setValue(preferenceString, buffer.toString().intern());
  }

  public static void setValue(String preferenceString, String[] stringArray, int maxLength) {
    if (stringArray.length > maxLength) {
      String[] newArray = new String[maxLength];
      for (int i = 0; i < maxLength; i++)
        newArray[i] = stringArray[i];
      stringArray = newArray;
    }
    setValue(preferenceString, stringArray);
  }

  public static void saveStore() {
    try {
      preferenceStore.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static void setDColor(IPreferenceStore pS, Display d, String string, int colorInt) {
    PreferenceConverter.setDefault(pS, string, d.getSystemColor(colorInt).getRGB());
  }

  static IPreferenceStore setDefaults(IPreferenceStore pS) {
    Display d = Display.getDefault();
    FontData[] dFontData = JFaceResources.getDefaultFont().getFontData();

    pS.setDefault("initialized", false);
    pS.setDefault("windowMaximized", false);
    
    pS.setDefault("splashScreen", true);

	pS.setDefault( "tournamentDB", VersionInfo.getHomeDirectory() + "Tournament.db");
	pS.setDefault( "localPlayersDB", VersionInfo.getHomeDirectory() + "LocalPlayers.db");
	pS.setDefault( "lastTournament", "");

	pS.setDefault( "standingsReport", "/reports/Standings.jasper");
	pS.setDefault( "seatingsReport", "/reports/Seatings.jasper");
	pS.setDefault( "resultSlipsReport", "/reports/ResultSlips.jasper");

    PreferenceConverter.setDefault(pS, "tableFontData", dFontData);
    PreferenceConverter.setDefault(pS, "headerFontData", dFontData);

    return pS;
  }

  public static String getHomeDirectory() {
    return homeDirectory;
  }

  public static void setHomeDirectory(String string) {
    customHomeDir = true;
    homeDirectory = string;
    String sep = System.getProperty("file.separator");
    if (!string.endsWith(sep))
      homeDirectory += sep;
  }
}