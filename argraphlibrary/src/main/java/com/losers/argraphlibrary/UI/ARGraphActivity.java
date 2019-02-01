package com.losers.argraphlibrary.UI;

import static android.view.View.GONE;

import android.content.ContentValues;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.gson.Gson;
import com.losers.argraphlibrary.CylinderNode;
import com.losers.argraphlibrary.R;
import com.losers.argraphlibrary.SupportingClasses.ARGraphHelperClass;
import com.losers.argraphlibrary.SupportingClasses.Constants;
import com.losers.argraphlibrary.SupportingClasses.GraphConfig;
import com.losers.argraphlibrary.SupportingClasses.LogClass;
import com.losers.argraphlibrary.SupportingClasses.Utils;
import com.losers.argraphlibrary.SupportingClasses.VideoRecorder;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ARGraphActivity extends AppCompatActivity {

  private FloatingActionButton recordButton;
  private final Gson mGson = new Gson();
  private ArFragment mARFragment;
  private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
  private final ARGraphHelperClass mARGraphHelperClass = new ARGraphHelperClass();
  private int mDivideFactor = 20;
  private VideoRecorder mVideoRecorder;

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
    recordButton = findViewById(R.id.record);
    recordButton.setOnClickListener(view -> toggleRecording());
    recordButton.setEnabled(true);
//    recordButton.setImageResource(R.drawable.round_videocam);

    mARFragment.setOnTapArPlaneListener(
        (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {

          if (mARGraphHelperClass.getIsGraphLoaded().get() ||
              !mARGraphHelperClass.getHasFinishedLoading().get()) {
            return;
          }

          Trackable trackable = hitResult.getTrackable();
          if (trackable instanceof Plane && ((Plane) trackable)
              .isPoseInPolygon(hitResult.getHitPose())) {

            plotGraph(hitResult);

          }


        });

  }


  private void toggleRecording() {
    if (!Utils.hasWritePermission(this)) {

      if (!Utils.shouldShowStorageRequestPermissionRationale(this)) {
        // Permission denied with checking "Do not ask again".
        Utils.launchStoragePermissionSettings(this);
      } else {
        LogClass.getInstance().errorLog(mARGraphHelperClass.getIsLogEnabled(),
            "Video recording requires the WRITE_EXTERNAL_STORAGE permission", null);

        Toast.makeText(
            this,
            "Video recording requires the WRITE_EXTERNAL_STORAGE permission",
            Toast.LENGTH_LONG)
            .show();
        return;
      }
    }
    boolean recording = mVideoRecorder.onToggleRecord();
    if (recording) {
//      recordButton.setImageResource(R.drawable.round_stop);
    } else {
//      recordButton.setImageResource(R.drawable.round_videocam);
      String videoPath = mVideoRecorder.getVideoPath().getAbsolutePath();
      Toast.makeText(this, "Video saved: " + videoPath, Toast.LENGTH_SHORT).show();

      LogClass.getInstance()
          .infoLog(mARGraphHelperClass.getIsLogEnabled(), "Video saved: " + videoPath);

      // Send  notification of updated content.
      ContentValues values = new ContentValues();
      values.put(MediaStore.Video.Media.TITLE, Utils.getFileName());
      values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
      values.put(MediaStore.Video.Media.DATA, videoPath);
      getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }
  }

  private void plotGraph(HitResult mHitResult) {
    mCompositeDisposable.add(Single.just(mHitResult)
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .map(new Function<HitResult, Boolean>() {
          @Override
          public Boolean apply(HitResult hitResult) throws Exception {
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
                new Vector3(Constants.graphScaleFactor, Constants.graphScaleFactor,
                    Constants.graphScaleFactor));

            float mXShiftPosition = -(mARGraphHelperClass.getXPositionShift() / mDivideFactor);

            graphNode.setLocalPosition(new Vector3(mXShiftPosition, 0.03f, 0f));

            Node mLoadPlatformNode = loadPlatform();
            mLoadPlatformNode.addChild(graphNode);

            mParentNode.addChild(mLoadPlatformNode);
            return true;
          }
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

  private Node loadPlatform() {
    Node platformNode = new Node();
    platformNode.setRenderable(mARGraphHelperClass.getPlatformRenderable());
    return platformNode;
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

          mVideoRecorder = new VideoRecorder();
          int orientation = getResources().getConfiguration().orientation;
          mVideoRecorder.setVideoQuality(mVideoRecorder
                  .getVideoQuality(mARGraphHelperClass.getGraphConfig().getVIDEO_quality()),
              orientation);
          mVideoRecorder.setSceneView(mARFragment.getArSceneView());
          return mARGraphHelperClass.getGraphConfig();
        })
        .subscribeWith(new DisposableMaybeObserver<GraphConfig>() {
          @Override
          public void onSuccess(GraphConfig graphConfig) {

            if (!graphConfig.isEnableVideo()) {
              recordButton.setVisibility(GONE);
            }
            loadMaterials(graphConfig.isEnableClassicPlatform());
          }

          @Override
          public void onError(Throwable e) {
            finish();
            LogClass
                .getInstance()
                .errorLog(mARGraphHelperClass.getIsLogEnabled(),
                    "Error While convert the json to GraphConfig", e);
          }

          @Override
          public void onComplete() {

          }
        }));
  }

  private void loadMaterials(boolean isIncludeClassicPlatform) {

    CompletableFuture<ModelRenderable> platformStage = ModelRenderable.builder()
        .setSource(this, R.raw.above).build();
    CompletableFuture<Material> redMaterial = MaterialFactory
        .makeOpaqueWithColor(this, new Color(android.graphics.Color.RED));
    CompletableFuture<Material> blackMaterial = MaterialFactory
        .makeOpaqueWithColor(this, new Color(android.graphics.Color.BLACK));
    CompletableFuture<Material> blueMaterial = MaterialFactory
        .makeOpaqueWithColor(this, new Color(android.graphics.Color.BLUE));
    CompletableFuture.allOf(
        redMaterial,
        blackMaterial,
        platformStage,
        blueMaterial)
        .handle(
            (notUsed, throwable) -> {
              if (throwable != null) {
                LogClass
                    .getInstance()
                    .errorLog(mARGraphHelperClass.getIsLogEnabled(), "Unable to load renderable",
                        throwable);
//                Utils.displayError(this, "Unable to load renderable", throwable);
                return null;
              }

              try {
                mARGraphHelperClass.setMaxSpeedMaterial(blueMaterial.get());
                mARGraphHelperClass.setNormalMaterial(redMaterial.get());
                // Everything finished loading successfully.
                mARGraphHelperClass.setHasFinishedLoading(true);

                if (isIncludeClassicPlatform) {
                  mARGraphHelperClass.setPlatformRenderable(platformStage.get());
                } else {
                  mARGraphHelperClass.setPlatformRenderable(
                      ShapeFactory
                          .makeCube(new Vector3(0.2f, 0.2f, 0.2f), new Vector3(0.1f, 0.0f, 0.0f),
                              blackMaterial.get()));
                }
              } catch (InterruptedException | ExecutionException ex) {
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
        Utils.launchCameraPermissionSettings(this);
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
        mARGraphHelperClass.getGraphConfig(), barWidth);
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
        mARGraphHelperClass.getGraphConfig(), barWidth);
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
    return value * Constants.cubeHeight;
  }

  private Float getBarWidth() {

    return (Constants.graphTotalLength / (mARGraphHelperClass
        .getGraphConfig().getGraphList().size()));
  }

  private void verifyCubeHeight() {
    if (mARGraphHelperClass.getGraphConfig().getGraphList().isEmpty()) {
      throw new NullPointerException("Graph List is Empty :(");
    }

    if (Constants.cubeHeight < 0
        || Constants.cubeHeight > 1) {
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
}
