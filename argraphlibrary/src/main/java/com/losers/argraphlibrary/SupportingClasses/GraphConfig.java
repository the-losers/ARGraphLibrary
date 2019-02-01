package com.losers.argraphlibrary.SupportingClasses;

import android.media.CamcorderProfile;
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
    private boolean isEnableClassicPlatform = true;
    private boolean isEnableVideo = false;
    private VIDEO_QUALITY mVIDEO_quality = VIDEO_QUALITY.QUALITY_720P;

    public Builder setEnableLogging(@NonNull boolean enableLogging) {
      isLoggingEnable = enableLogging;
      return this;
    }

    public Builder setVideoQuality(@NonNull VIDEO_QUALITY videoQuality) {
      mVIDEO_quality = videoQuality;
      return this;
    }

    public Builder setEnableVideo(@NonNull boolean enableVideo) {
      isEnableVideo = enableVideo;
      return this;
    }

    public Builder setEnableClassicPlatform(@NonNull boolean enableClassicPlatform) {
      isEnableClassicPlatform = enableClassicPlatform;
      return this;
    }

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


