package org.QTM.app;

import org.eclipse.swt.SWT;

public class VersionInfo {
  
  private static final String VERSION = "0.1";

  public static String getBugPage() {
    return "http://qtm.berlios.de";
  }

  public static String getUserHomeDirectory() {
    String homeDirectory;
    if ((homeDirectory = PreferenceLoader.getHomeDirectory()) != null)
      return homeDirectory;

    return System.getProperty("user.home");

  }

  public static String getHomeDirectory() {
    return getUserHomeDirectory() + System.getProperty("file.separator") + "." + getName()
        + System.getProperty("file.separator");
  }

  public static String getHomePage() {
    return "http://qtm.berlios.de";
  }

  public static String getName() {
    return "QTM";
  }

  public static String getOSPlatform() {
    return System.getProperty("os.name");
  }

  public static String getSWTPlatform() {
    String platform;

    if ((platform = SWT.getPlatform()).equals("fox")) {
      if ((System.getProperty("os.name").length() > 7) && System.getProperty("os.name").startsWith("Windows"))
        return "win32-fox";
      else
        return "fox";
    } else
      return platform;
  }

  public static String getVersion() {
    return VERSION;
  }

}