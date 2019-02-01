package com.losers.argraphlibrary.SupportingClasses;

import android.util.Log;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogClass {


  private static final LogClass ourInstance = new LogClass();
  private final String TAG = "AR Graph Library";

  private LogClass() {
  }

  public static LogClass getInstance() {
    return ourInstance;
  }

  public void debugLog(AtomicBoolean isLogEnable, String message) {
    if (isLogEnable.get()) {
      Log.d(TAG, message);
    }
  }

  public void verboseLog(AtomicBoolean isLogEnable, String message) {
    if (isLogEnable.get()) {
      Log.v(TAG, message);
    }
  }

  public void errorLog(AtomicBoolean isLogEnable, String message,Throwable throwable) {
    if (isLogEnable.get()) {
      if(throwable!=null){
        Log.e(TAG, message,throwable);
      }else {
        Log.e(TAG, message);
      }

    }
  }

  public void infoLog(AtomicBoolean isLogEnable, String message) {
    if (isLogEnable.get()) {
      Log.i(TAG, message);
    }
  }

  public void warnLog(AtomicBoolean isLogEnable, String message) {
    if (isLogEnable.get()) {
      Log.i(TAG, message);
    }
  }
}
