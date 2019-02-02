package com.losers.argraphlibrary.Modal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import com.google.gson.Gson;
import com.losers.argraphlibrary.SupportingClasses.Constants;
import com.losers.argraphlibrary.UI.ARGraphActivity;

public class PlotGraph {

  @SuppressLint("StaticFieldLeak")
  private static PlotGraph singleton;
  @SuppressLint("StaticFieldLeak")
  private static Context context;
  private final Gson mGson = new Gson();

  private PlotGraph() {
  }


  public static PlotGraph get(Context mContext) {

    context = mContext;
    if (singleton == null) {
      synchronized (PlotGraph.class) {
        if (singleton == null) {
          if (context == null) {
            throw new IllegalStateException("-> context == null");
          }
          singleton = new PlotGraph(context);
        }
      }
    }
    return singleton;
  }


  public PlotGraph(Context context) {

  }


  public PlotGraph loadGraph(GraphConfig graphConfig) {
    Intent intent = getIntentFromUrl(graphConfig);
    context.startActivity(intent);
    return singleton;
  }


  private Intent getIntentFromUrl(GraphConfig graphConfig) {

    Intent intent = new Intent(context, ARGraphActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra(Constants.INTENT_CONFIG, getJson(graphConfig));

    return intent;
  }

  private String getJson(Object object) {

    return mGson.toJson(object);
  }
}
