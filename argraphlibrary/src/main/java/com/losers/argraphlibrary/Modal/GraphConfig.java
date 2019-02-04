package com.losers.argraphlibrary.Modal;

import androidx.annotation.NonNull;
import com.losers.argraphlibrary.SupportingClasses.VideoRecorder.VIDEO_QUALITY;
import java.util.ArrayList;
import java.util.List;


public class GraphConfig {


  private List<Double> GraphList;
  private boolean isEnableClassicPlatform;
  private boolean isLoggingEnable;
  private boolean isEnableVideo;
  private VIDEO_QUALITY mVIDEO_quality;


  private GraphConfig(Builder builder) {
    this.GraphList = builder.GraphList;
    this.isEnableClassicPlatform = builder.isEnableClassicPlatform;
    this.isLoggingEnable = builder.isLoggingEnable;
    this.isEnableVideo = builder.isEnableVideo;
    this.mVIDEO_quality = builder.mVIDEO_quality;


  }

  public VIDEO_QUALITY getVIDEO_quality() {
    return mVIDEO_quality;
  }

  public void setVIDEO_quality(
      VIDEO_QUALITY VIDEO_quality) {
    mVIDEO_quality = VIDEO_quality;
  }

  public boolean isEnableVideo() {
    return isEnableVideo;
  }

  public void setEnableVideo(boolean enableVideo) {
    isEnableVideo = enableVideo;
  }

  public List<Double> getGraphList() {
    return GraphList;
  }

  public void setGraphList(List<Double> graphList) {
    GraphList = graphList;
  }

  public boolean isEnableClassicPlatform() {
    return isEnableClassicPlatform;
  }

  public void setEnableClassicPlatform(boolean enableClassicPlatform) {
    isEnableClassicPlatform = enableClassicPlatform;
  }

  public boolean isLoggingEnable() {
    return isLoggingEnable;
  }

  public void setLoggingEnable(boolean loggingEnable) {
    isLoggingEnable = loggingEnable;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private List<Double> GraphList = new ArrayList<>();
    private boolean isLoggingEnable = false;
    private boolean isEnableClassicPlatform = false;
    private boolean isEnableVideo = false;
    private VIDEO_QUALITY mVIDEO_quality = VIDEO_QUALITY.QUALITY_720P;

    /**
     * @param enableLogging true to enable logs, false to disable
     */
    public Builder setEnableLogging(@NonNull boolean enableLogging) {
      isLoggingEnable = enableLogging;
      return this;
    }

    /**
     * set video quality of video recording
     *
     * @param videoQuality QUALITY_HIGH, QUALITY_2160P, QUALITY_1080P, QUALITY_720P, QUALITY_480P
     */

    public Builder setVideoQuality(@NonNull VIDEO_QUALITY videoQuality) {
      mVIDEO_quality = videoQuality;
      return this;
    }

    /**
     * if you want to allow user to take video recording of graph
     *
     * @param enableVideo true to enable , bydefault it is off
     */
    public Builder setEnableVideo(@NonNull boolean enableVideo) {
      isEnableVideo = enableVideo;
      return this;
    }


    /**
     * if you want to show graph on platform
     *
     * @param enableClassicPlatform true to enable , bydefault it is off
     */
    public Builder setEnableClassicPlatform(@NonNull boolean enableClassicPlatform) {
      isEnableClassicPlatform = enableClassicPlatform;
      return this;
    }

    /**
     * List of point you want to show on a graph
     */
    @NonNull
    public Builder setGraphList(@NonNull List<Double> graphList) {
      this.GraphList = graphList;
      return this;
    }


    public GraphConfig build() {
      return new GraphConfig(this);
    }
  }
}


