package com.losers.argraphlibrary;

import android.util.Log;

public class LogClass {

  public static final int ASSERT = 7;
  public static final int DEBUG = 3;
  public static final int ERROR = 6;
  public static final int INFO = 4;
  public static final int VERBOSE = 2;
  public static final int WARN = 5;
  private static final LogClass ourInstance = new LogClass();
  private final String TAG = "AR Graph Library";

  private LogClass() {
  }

  public static LogClass getInstance() {
    return ourInstance;
  }

  public void debugLog(boolean isLogEnable, String message) {
    if (isLogEnable) {
      Log.d(TAG, message);
    }
  }

  public void verboseLog(boolean isLogEnable, String message) {
    if (isLogEnable) {
      Log.v(TAG, message);
    }
  }

  public void errorLog(boolean isLogEnable, String message) {
    if (isLogEnable) {
      Log.e(TAG, message);
    }
  }

  public void infoLog(boolean isLogEnable, String message) {
    if (isLogEnable) {
      Log.i(TAG, message);
    }
  }

  public void warnLog(boolean isLogEnable, String message) {
    if (isLogEnable) {
      Log.i(TAG, message);
    }
  }
}
