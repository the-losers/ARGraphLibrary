package com.losers.argraph;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.losers.argraphlibrary.Modal.GraphConfig;
import com.losers.argraphlibrary.Modal.PlotGraph;
import com.losers.argraphlibrary.SupportingClasses.VideoRecorder.VIDEO_QUALITY;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    GraphConfig mGraphConfig = GraphConfig.newBuilder()
        .setGraphList(getOneSpeedList())
        .setEnableClassicPlatform(true)
        .setEnableLogging(true)
        .setEnableVideo(true)
        .setVideoQuality(VIDEO_QUALITY.QUALITY_720P)
        .build();
    PlotGraph.get(getApplicationContext()).loadGraph(mGraphConfig);

  }

  private List<Double> getOneSpeedList() {
    List<Double> mFloatsList = new ArrayList<>();

    mFloatsList.add(50d);
    mFloatsList.add(10d);
    mFloatsList.add(80d);
//    mFloatsList.add(50d);
//    mFloatsList.add(10d);
//    mFloatsList.add(80d);
//    mFloatsList.add(50d);
//    mFloatsList.add(10d);
//    mFloatsList.add(80d);

    return mFloatsList;
  }

  private List<Double> getSpeedList() {
    List<Double> mFloatsList = new ArrayList<>();
    mFloatsList.add(1d);

    mFloatsList.add(50d);

    mFloatsList.add(100d);
    mFloatsList.add(83d);
    mFloatsList.add(1d);
    mFloatsList.add(100d);
    mFloatsList.add(14d);
    mFloatsList.add(50d);

    mFloatsList.add(61d);
    mFloatsList.add(55d);
    mFloatsList.add(83d);
    mFloatsList.add(1d);
    mFloatsList.add(100d);
    mFloatsList.add(14d);
    mFloatsList.add(50d);

    mFloatsList.add(61d);
    mFloatsList.add(55d);
    mFloatsList.add(83d);
    mFloatsList.add(1d);
    mFloatsList.add(100d);
    mFloatsList.add(14d);
    mFloatsList.add(50d);

    mFloatsList.add(61d);
    mFloatsList.add(55d);
    mFloatsList.add(83d);
    mFloatsList.add(1d);
    mFloatsList.add(100d);
    mFloatsList.add(14d);
    mFloatsList.add(50d);

    mFloatsList.add(61d);
    mFloatsList.add(55d);
    mFloatsList.add(83d);
    mFloatsList.add(1d);
    mFloatsList.add(100d);
    mFloatsList.add(14d);
    mFloatsList.add(50d);

    mFloatsList.add(61d);
    mFloatsList.add(55d);
    mFloatsList.add(83d);
    mFloatsList.add(1d);
    mFloatsList.add(100d);
    mFloatsList.add(14d);
    mFloatsList.add(50d);

    mFloatsList.add(61d);
    mFloatsList.add(55d);
    mFloatsList.add(83d);
    mFloatsList.add(1d);
    mFloatsList.add(100d);
    mFloatsList.add(14d);
    mFloatsList.add(50d);

    mFloatsList.add(61d);
    mFloatsList.add(55d);
    mFloatsList.add(83d);
    mFloatsList.add(1d);
    mFloatsList.add(100d);
    mFloatsList.add(14d);
    mFloatsList.add(50d);

    mFloatsList.add(61d);
    mFloatsList.add(55d);
    mFloatsList.add(83d);
    mFloatsList.add(1d);
    mFloatsList.add(100d);
    mFloatsList.add(14d);
    mFloatsList.add(50d);

    mFloatsList.add(14d);
    mFloatsList.add(50d);

    mFloatsList.add(61d);
    mFloatsList.add(55d);
    mFloatsList.add(83d);
    mFloatsList.add(1d);
    mFloatsList.add(100d);
    mFloatsList.add(14d);
    mFloatsList.add(50d);

    mFloatsList.add(61d);
    mFloatsList.add(55d);
    mFloatsList.add(83d);
    mFloatsList.add(1d);
    mFloatsList.add(100d);
    mFloatsList.add(14d);
    mFloatsList.add(50d);

    mFloatsList.add(61d);
    mFloatsList.add(55d);
    mFloatsList.add(83d);
    mFloatsList.add(1d);
    mFloatsList.add(100d);
    mFloatsList.add(14d);
    mFloatsList.add(50d);

    mFloatsList.add(61d);
    mFloatsList.add(55d);
    mFloatsList.add(83d);
    mFloatsList.add(1d);
    mFloatsList.add(100d);
    mFloatsList.add(14d);
    mFloatsList.add(50d);

    return mFloatsList;
  }

}
