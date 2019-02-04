package com.losers.argraphlibrary.UI;

import static com.losers.argraphlibrary.SupportingClasses.Constants.divideFactor;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.gson.Gson;
import com.losers.argraphlibrary.Base.ResponseBaseView;
import com.losers.argraphlibrary.BarNode;
import com.losers.argraphlibrary.Modal.GraphConfig;
import com.losers.argraphlibrary.SupportingClasses.ARGraphHelperClass;
import com.losers.argraphlibrary.SupportingClasses.Constants;
import com.losers.argraphlibrary.SupportingClasses.LogClass;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.Iterator;

@RequiresApi(api = VERSION_CODES.N)
public class ARGraphPresenter implements ARGraphInterface {

  private ResponseBaseView mResponseBaseView;
  private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
  private Gson mGson = new Gson();

  public ARGraphPresenter(ResponseBaseView responseBaseView) {
    mResponseBaseView = responseBaseView;
  }

  @Override
  public void onGetBundleData(Bundle mBundle) {
    mCompositeDisposable.add(Single.just(mBundle)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .filter(bundle -> bundle.containsKey(Constants.INTENT_CONFIG))
        .map(bundle -> {

          ARGraphHelperClass mARGraphHelperClass = new ARGraphHelperClass();
          mARGraphHelperClass.setGraphConfig(
              mGson.fromJson(bundle.getString(Constants.INTENT_CONFIG), GraphConfig.class));
          mARGraphHelperClass.setMaximumSpeed(mARGraphHelperClass.getGraphConfig().getGraphList());
          mARGraphHelperClass.setCubeHeightFactor(getCubeHeightFactor(mARGraphHelperClass));
          verifyData(mARGraphHelperClass);
          return mARGraphHelperClass;
        })
        .subscribeWith(new DisposableMaybeObserver<ARGraphHelperClass>() {
          @Override
          public void onSuccess(ARGraphHelperClass arGraphHelperClass) {

            if (mResponseBaseView == null) {
              throw new IllegalStateException("ResponseBaseview is empty");
            }

            mResponseBaseView.onSuccess(arGraphHelperClass, null);

          }

          @Override
          public void onError(Throwable e) {
            mResponseBaseView.onSuccess(e, null);

          }

          @Override
          public void onComplete() {

          }
        }));
  }

  private Double getCubeHeightFactor(ARGraphHelperClass mARGraphHelperClass) {
    return Constants.barHeightFactor / mARGraphHelperClass.getMaximumSpeed();
  }

  private void verifyData(ARGraphHelperClass mARGraphHelperClass) {
    if (mARGraphHelperClass.getGraphConfig().getGraphList().isEmpty()) {
      throw new NullPointerException("Graph List is Empty :(");
    }

    if (Constants.barHeightFactor < 0
        || Constants.barHeightFactor > 4) {
      throw new RuntimeException("Bar height must be in between 0.0 to 1.0");
    }

    if (Constants.graphScaleFactor < 0
        || Constants.graphScaleFactor > 1) {
      throw new RuntimeException("Bar scale factor must be in between 0.0 to 1.0");
    }

    if (mARGraphHelperClass.getGraphConfig().isLoggingEnable()) {
      mARGraphHelperClass.setIsLogEnabled(true);
    } else {
      mARGraphHelperClass.setIsLogEnabled(false);
    }


  }

  @Override
  public void onPlotGraph(Context context, HitResult mHitResult,
      ARGraphHelperClass mARGraphHelperClass,
      ArFragment mARFragment) {
    mCompositeDisposable.add(Single.just(mHitResult)
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .map(hitResult -> {
          mARGraphHelperClass.setIsGraphLoaded(true);

          // Create the Anchor.
          Anchor anchor = hitResult.createAnchor();
          AnchorNode anchorNode = new AnchorNode(anchor);
          anchorNode.setParent(mARFragment.getArSceneView().getScene());

          TransformableNode mParentNode = new TransformableNode(
              mARFragment.getTransformationSystem());

          anchorNode.addChild(mParentNode);

          Node graphNode = createGraph(context, mARGraphHelperClass);
          graphNode.setLocalScale(
              new Vector3(Constants.graphScaleFactor, Constants.graphScaleFactor,
                  Constants.graphScaleFactor));

          float mXShiftPosition = -(mARGraphHelperClass.getXPositionShift() / divideFactor);

          graphNode.setLocalPosition(new Vector3(mXShiftPosition, 0.03f, 0f));

          if (mARGraphHelperClass.getGraphConfig().isEnableClassicPlatform()) {
            Node mLoadPlatformNode = loadPlatform(mARGraphHelperClass);
            mLoadPlatformNode.addChild(graphNode);

            mParentNode.addChild(mLoadPlatformNode);
          } else {

            mParentNode.addChild(graphNode);
          }

          return true;
        })
        .subscribeWith(new DisposableSingleObserver<Boolean>() {
          @Override
          public void onSuccess(Boolean mBoolean) {
            LogClass
                .getInstance()
                .infoLog(mARGraphHelperClass.getIsLogEnabled(),
                    "Bingo Graph plotted successfully!");

          }

          @Override
          public void onError(Throwable e) {
            LogClass
                .getInstance()
                .errorLog(mARGraphHelperClass.getIsLogEnabled(),
                    "Error while plotting the graph", e);
          }
        }));
  }

  private Node createGraph(Context context, ARGraphHelperClass mARGraphHelperClass) {

    Node parentNode = new Node();
    float barWidth = getBarWidth(mARGraphHelperClass);
    float xShiftPosition = 0f;
    boolean isLastItem = false;
    Iterator<Double> iterator = mARGraphHelperClass.getGraphConfig().getGraphList().iterator();

    while (iterator.hasNext()) {
      Double value = iterator.next();

      if (!iterator.hasNext()) {
        isLastItem = true;
      }

      Node mNode = createNode(context, "" + value,
          getBarHeight(value, mARGraphHelperClass),
          xShiftPosition, isMaxValue(value, mARGraphHelperClass), barWidth, mARGraphHelperClass,
          isLastItem);

      mNode.setParent(parentNode);
      xShiftPosition = xShiftPosition + (barWidth / 2) + barWidth;
      mARGraphHelperClass.setXPositionShift(xShiftPosition);

    }

    return parentNode;
  }

  private Node createNode(
      Context context,
      String maxValue,
      Double mHeight,
      Float mPreviousXPosition,
      Boolean isMaxValue,
      Float barWidth,
      ARGraphHelperClass mARGraphHelperClass,
      boolean isLastItem
  ) {

    return new BarNode(context, maxValue, mHeight, mPreviousXPosition,
        mARGraphHelperClass, barWidth, isMaxValue,isLastItem);
  }


  private Double getBarHeight(Double value, ARGraphHelperClass mARGraphHelperClass) {
    return value * mARGraphHelperClass.getCubeHeightFactor();
  }

  private Float getBarWidth(ARGraphHelperClass mARGraphHelperClass) {

    if (mARGraphHelperClass.getGraphConfig().getGraphList().size() <= 10) {
      return (1.0f / (mARGraphHelperClass
          .getGraphConfig().getGraphList().size()));
    }
    return (Constants.graphTotalLength / (mARGraphHelperClass
        .getGraphConfig().getGraphList().size()));
  }

  private Boolean isMaxValue(Double mSpeed, ARGraphHelperClass mARGraphHelperClass) {

    if (mARGraphHelperClass.getIsMaximumSpeedAlreadyPlotted().get()) {
      return false;
    }
    if (mSpeed.equals(mARGraphHelperClass.getMaximumSpeed())) {
      mARGraphHelperClass.setIsMaximumSpeedAlreadyPlotted(true);
      return true;
    }
    return false;
  }

  private Node loadPlatform(ARGraphHelperClass mARGraphHelperClass) {
    Node platformNode = new Node();
    platformNode.setRenderable(mARGraphHelperClass.getPlatformRenderable());
    return platformNode;
  }

  public void clear() {
    if (!mCompositeDisposable.isDisposed()) {
      mCompositeDisposable.clear();
    }
  }
}
