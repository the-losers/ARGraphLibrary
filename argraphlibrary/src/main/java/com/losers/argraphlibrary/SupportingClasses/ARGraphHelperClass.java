package com.losers.argraphlibrary.SupportingClasses;

import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ARGraphHelperClass {

  private AtomicBoolean isLogEnabled = new AtomicBoolean(false);
  private AtomicBoolean hasFinishedLoading = new AtomicBoolean(false);
  private AtomicBoolean isMaximumSpeedAlreadyPlotted = new AtomicBoolean(false);
  private AtomicBoolean mIsGraphLoaded = new AtomicBoolean(false);
  private GraphConfig mGraphConfig;
  private Double mMaximumSpeed;
  private Float mXPositionShift = 0.0f;

  private Material mNormalMaterial, mMaxSpeedMaterial;
  private ModelRenderable mPlatformRenderable, mBlackPlatformRenderable;



  public Material getNormalMaterial() {
    return mNormalMaterial;
  }

  public void setNormalMaterial(Material normalMaterial) {
    mNormalMaterial = normalMaterial;
  }

  public Material getMaxSpeedMaterial() {
    return mMaxSpeedMaterial;
  }

  public void setMaxSpeedMaterial(Material maxSpeedMaterial) {
    mMaxSpeedMaterial = maxSpeedMaterial;
  }

  public ModelRenderable getPlatformRenderable() {
    return mPlatformRenderable;
  }

  public void setPlatformRenderable(ModelRenderable platformRenderable) {
    mPlatformRenderable = platformRenderable;
  }

  public ModelRenderable getArrowRenderable() {
    return mBlackPlatformRenderable;
  }

  public void setArrowRenderable(ModelRenderable arrowRenderable) {
    mBlackPlatformRenderable = arrowRenderable;
  }

  public Double getMaximumSpeed() {
    return mMaximumSpeed;
  }

  public void setMaximumSpeed(List<Double> maximumSpeedList) {
    if (!maximumSpeedList.isEmpty()) {
      mMaximumSpeed = Collections.max(maximumSpeedList);
    } else {
      mMaximumSpeed = 0.0;
    }
  }

  public GraphConfig getGraphConfig() {
    return mGraphConfig;
  }

  public void setGraphConfig(GraphConfig graphConfig) {
    mGraphConfig = graphConfig;
  }

  public AtomicBoolean getIsLogEnabled() {
    return isLogEnabled;
  }

  public void setIsLogEnabled(boolean isLogEnabled) {
    this.isLogEnabled.set(isLogEnabled);
  }

  public AtomicBoolean getHasFinishedLoading() {
    return hasFinishedLoading;
  }

  public void setHasFinishedLoading(boolean hasFinishedLoading) {
    this.hasFinishedLoading.set(hasFinishedLoading);
  }

  public AtomicBoolean getIsMaximumSpeedAlreadyPlotted() {
    return isMaximumSpeedAlreadyPlotted;
  }

  public void setIsMaximumSpeedAlreadyPlotted(
      boolean isMaximumSpeedAlreadyPlotted) {
    this.isMaximumSpeedAlreadyPlotted.set(isMaximumSpeedAlreadyPlotted);
  }

  public AtomicBoolean getIsGraphLoaded() {
    return mIsGraphLoaded;
  }

  public void setIsGraphLoaded(boolean isGraphLoaded) {
    mIsGraphLoaded.set(isGraphLoaded);
  }

  public Float getXPositionShift() {
    return mXPositionShift;
  }

  public void setXPositionShift(Float XPositionShift) {
    mXPositionShift = XPositionShift;
  }
}
