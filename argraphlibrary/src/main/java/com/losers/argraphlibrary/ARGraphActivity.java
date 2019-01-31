package com.losers.argraphlibrary;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleObserver;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.gson.Gson;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ARGraphActivity extends AppCompatActivity {

  private final Gson mGson = new Gson();
  private ArFragment mARFragment;
  private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
  private final ARGraphHelperClass mARGraphHelperClass = new ARGraphHelperClass();
  private int mDivideFactor = 20;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //check if device support the arcore or not
    if (!Utils.checkIsSupportedDeviceOrFinish(this)) {
      return;
    }

    setContentView(R.layout.activity_argraph);
    mARFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

    Bundle mBundle = getIntent().getExtras();
    if (mBundle == null) {
      finish();
      return;
    }

    getIntentData(mBundle);

    mARFragment.setOnTapArPlaneListener(
        (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {

          if (mARGraphHelperClass.getIsGraphLoaded().get() ||
              !mARGraphHelperClass.getHasFinishedLoading().get()) {
            return;
          }

          Trackable trackable = hitResult.getTrackable();
          if (trackable instanceof Plane && ((Plane) trackable)
              .isPoseInPolygon(hitResult.getHitPose())) {

            mARGraphHelperClass.setIsGraphLoaded(true);

            // Create the Anchor.
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(mARFragment.getArSceneView().getScene());

            TransformableNode mParentNode = new TransformableNode(
                mARFragment.getTransformationSystem());

            anchorNode.addChild(mParentNode);

            Node graphNode = createGraph();
            graphNode.setLocalScale(
                new Vector3(mARGraphHelperClass.getGraphConfig().getGraphScaleFactor(),
                    mARGraphHelperClass.getGraphConfig().getGraphScaleFactor(),
                    mARGraphHelperClass.getGraphConfig().getGraphScaleFactor()));

            Float mXShiftPosition = -(mARGraphHelperClass.getXPositionShift() / mDivideFactor);

            graphNode.setLocalPosition(new Vector3(mXShiftPosition, 0.03f, 0f));

//            Node mLoadPlatformNode = loadPlatform();
//            mLoadPlatformNode.addChild(graphNode);

//            mParentNode.addChild(mLoadPlatformNode);

          }


        });

  }

  private void getIntentData(Bundle mBundle) {
    mCompositeDisposable.add(Single.just(mBundle)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .filter(bundle -> bundle.containsKey(Constants.INTENT_CONFIG))
        .map(bundle -> {
          mARGraphHelperClass.setGraphConfig(
              mGson.fromJson(bundle.getString(Constants.INTENT_CONFIG), GraphConfig.class));
          mARGraphHelperClass.setMaximumSpeed(mARGraphHelperClass.getGraphConfig().getGraphList());
          verifyCubeHeight();
          return mARGraphHelperClass.getGraphConfig();
        })
        .subscribeWith(new DisposableMaybeObserver<GraphConfig>() {
          @Override
          public void onSuccess(GraphConfig graphConfig) {

            loadMaterials();
          }

          @Override
          public void onError(Throwable e) {
            finish();
          }

          @Override
          public void onComplete() {

          }
        }));
  }

  private void loadMaterials() {

    CompletableFuture<Material> redMaterial = MaterialFactory
        .makeOpaqueWithColor(this, new Color(android.graphics.Color.RED));
    CompletableFuture<Material> blueMaterial = MaterialFactory
        .makeOpaqueWithColor(this, new Color(android.graphics.Color.BLUE));
    CompletableFuture.allOf(
        redMaterial,
        blueMaterial)
        .handle(
            (notUsed, throwable) -> {
              if (throwable != null) {
                LogClass
                    .getInstance()
                    .errorLog(mARGraphHelperClass.getIsLogEnabled(), "Unable to load renderable",
                        throwable);
                Utils.displayError(this, "Unable to load renderable", throwable);
                return null;
              }

              try {
                mARGraphHelperClass.setMaxSpeedMaterial(blueMaterial.get());
                mARGraphHelperClass.setNormalMaterial(redMaterial.get());
                // Everything finished loading successfully.
                mARGraphHelperClass.setHasFinishedLoading(true);

              } catch (InterruptedException | ExecutionException ex) {
                Utils.displayError(this, "Unable to load renderable", ex);
                LogClass
                    .getInstance()
                    .errorLog(mARGraphHelperClass.getIsLogEnabled(), "Unable to load renderable",
                        ex);
              }

              return null;
            });

  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
    if (!Utils.hasCameraPermission(this)) {
      if (!Utils.shouldShowRequestPermissionRationale(this)) {
        // Permission denied with checking "Do not ask again".
        Utils.launchPermissionSettings(this);
      } else {
        Toast.makeText(
            this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
            .show();
      }
      finish();
    }
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus) {
      // Standard Android full-screen functionality.
      getWindow()
          .getDecorView()
          .setSystemUiVisibility(
              View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
  }


  private Node createGraph() {

    Node parentNode = new Node();
    Float barWidth = getBarWidth();
    Float xShiftPosition = 0f;

    for (Double value : mARGraphHelperClass.getGraphConfig().getGraphList()) {

      if (isMaxRun(value)) {
        createNode("Maximum speed is : " + value, parentNode, getBarHeight(value),
            mARGraphHelperClass.getMaxSpeedMaterial(), xShiftPosition, true, barWidth);
      } else {
        createNode(parentNode, getBarHeight(value),
            mARGraphHelperClass.getNormalMaterial(),
            xShiftPosition, barWidth);
      }

      xShiftPosition = xShiftPosition + (barWidth / 2) + barWidth;
      mARGraphHelperClass.setXPositionShift(xShiftPosition);
    }
    return parentNode;
  }

  private Node createNode(
      String maxValue,
      Node parent,
      Double mHeight,
      Material material,
      Float mPreviousXPosition,
      Boolean isMaxRun,
      Float barWidth
  ) {

    CylinderNode planet = new CylinderNode(this, maxValue, material, mHeight, mPreviousXPosition,
        mARGraphHelperClass.getGraphConfig(), isMaxRun, barWidth);
    planet.setParent(parent);
    return planet;
  }

  private Node createNode(
      Node parent,
      Double mHeight,
      Material material,
      Float mPreviousXPosition,
      Float barWidth
  ) {

    CylinderNode planet = new CylinderNode(this, null, material, mHeight, mPreviousXPosition,
        mARGraphHelperClass.getGraphConfig(), false, barWidth);
    planet.setParent(parent);
    return planet;
  }

  private Boolean isMaxRun(Double mSpeed) {

    if (mARGraphHelperClass.getIsMaximumSpeedAlreadyPlotted().get()) {
      return false;
    }
    if (mSpeed.equals(mARGraphHelperClass.getMaximumSpeed())) {
      mARGraphHelperClass.setIsMaximumSpeedAlreadyPlotted(true);
      return true;
    }
    return false;
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    clear();
  }


  private void clear() {
    if (!mCompositeDisposable.isDisposed()) {
      mCompositeDisposable.clear();
    }
  }

  private Double getBarHeight(Double value) {
    return value * mARGraphHelperClass.getGraphConfig().getCubeHeight();
  }

  private Float getBarWidth() {
    return (mARGraphHelperClass.getGraphConfig().getGraphTotalLength() / (mARGraphHelperClass
        .getGraphConfig().getGraphList().size()));
  }

  private void verifyCubeHeight() {
    if (mARGraphHelperClass.getGraphConfig().getGraphList().isEmpty()) {
      throw new NullPointerException("Graph List is Empty :(");
    }

    if (mARGraphHelperClass.getGraphConfig().getCubeHeight() < 0
        || mARGraphHelperClass.getGraphConfig().getCubeHeight() > 1) {
      throw new RuntimeException("Bar height must be in between 0.0 to 1.0");
    }

    if (mARGraphHelperClass.getGraphConfig().getGraphScaleFactor() < 0
        || mARGraphHelperClass.getGraphConfig().getGraphScaleFactor() > 1) {
      throw new RuntimeException("Bar scale factor must be in between 0.0 to 1.0");
    }

  }
}
