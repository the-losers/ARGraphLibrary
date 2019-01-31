package com.losers.argraphlibrary;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class GraphConfig {


  private List<Double> GraphList;
  private Float mGraphScaleFactor;
  private Float mGraphTotalLength;
  private Float mCubeWidth;
  private Float mCubeLength;
  private Float mSeriesGap;
  private Float mCubeHeight;
  private boolean isLoggingEnable;

  private GraphConfig(Builder builder) {
    this.GraphList = builder.GraphList;
    this.mGraphScaleFactor = builder.mGraphScaleFactor;
    this.mGraphTotalLength = builder.mGraphTotalLength;
    this.mCubeWidth = builder.mCubeWidth;

    this.mCubeLength = builder.mCubeLength;
    this.mSeriesGap = builder.mSeriesGap;
    this.mCubeHeight = builder.mCubeHeight;
    this.isLoggingEnable = builder.isLoggingEnable;


  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public List<Double> getGraphList() {
    return GraphList;
  }

  public void setGraphList(List<Double> graphList) {
    GraphList = graphList;
  }

  public Float getGraphScaleFactor() {
    return mGraphScaleFactor;
  }

  public void setGraphScaleFactor(Float graphScaleFactor) {
    mGraphScaleFactor = graphScaleFactor;
  }

  public boolean isLoggingEnable() {
    return isLoggingEnable;
  }

  public Float getGraphTotalLength() {
    return mGraphTotalLength;
  }

  public void setGraphTotalLength(Float graphTotalLength) {
    mGraphTotalLength = graphTotalLength;
  }

  public Float getCubeWidth() {
    return mCubeWidth;
  }

  public void setCubeWidth(Float cubeWidth) {
    mCubeWidth = cubeWidth;
  }

  public Float getCubeLength() {
    return mCubeLength;
  }

  public void setCubeLength(Float cubeLength) {
    mCubeLength = cubeLength;
  }

  public Float getSeriesGap() {
    return mSeriesGap;
  }

  public void setSeriesGap(Float seriesGap) {
    mSeriesGap = seriesGap;
  }

  public Float getCubeHeight() {
    return mCubeHeight;
  }

  public void setCubeHeight(Float cubeHeight) {
    mCubeHeight = cubeHeight;
  }

  public static class Builder {

    private List<Double> GraphList = new ArrayList<>();
    private Float mGraphScaleFactor = Constants.mGraphScaleFactor;
    private boolean isLoggingEnable = false;
    private Float mGraphTotalLength = Constants.mGraphTotalLength;
    private Float mCubeWidth = Constants.mCubeWidth;
    private Float mCubeLength = Constants.mCubeLength;
    private Float mSeriesGap = Constants.mSeriesGap;
    private Float mCubeHeight = Constants.mCubeHeight;

    public Builder setEnableLogging(boolean mIsLoggingEnable) {
      isLoggingEnable = mIsLoggingEnable;
      return this;
    }

    public Builder setGraphScaleFactor(Float graphScaleFactor) {
      mGraphScaleFactor = graphScaleFactor;
      return this;
    }

    public Builder setGraphTotalLength(Float graphTotalLength) {
      mGraphTotalLength = graphTotalLength;
      return this;
    }

    public Builder setCubeWidth(Float cubeWidth) {
      mCubeWidth = cubeWidth;
      return this;
    }

    public Builder setCubeLength(Float cubeLength) {
      mCubeLength = cubeLength;
      return this;
    }

    public Builder setSeriesGap(Float seriesGap) {
      mSeriesGap = seriesGap;
      return this;
    }

    public Builder setCubeHeight(Float cubeHeight) {
      mCubeHeight = cubeHeight;
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
