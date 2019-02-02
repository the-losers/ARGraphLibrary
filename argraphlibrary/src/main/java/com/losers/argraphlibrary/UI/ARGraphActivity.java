package com.losers.argraphlibrary.UI;

import static android.view.View.GONE;

import android.content.ContentValues;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.circularreveal.CircularRevealFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.losers.argraphlibrary.Base.ResponseBaseView;
import com.losers.argraphlibrary.R;
import com.losers.argraphlibrary.SupportingClasses.ARGraphHelperClass;
import com.losers.argraphlibrary.SupportingClasses.LogClass;
import com.losers.argraphlibrary.SupportingClasses.SnackbarHelper;
import com.losers.argraphlibrary.SupportingClasses.Utils;
import com.losers.argraphlibrary.SupportingClasses.VideoRecorder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ARGraphActivity extends AppCompatActivity implements ResponseBaseView {

  private FloatingActionButton recordButton;
  private ImageButton blinkingBtn;
  private ArFragment mARFragment;
  private ARGraphHelperClass mARGraphHelperClass = new ARGraphHelperClass();
  private VideoRecorder mVideoRecorder;
  private final MediaActionSound sound = new MediaActionSound();
  private ARGraphPresenter mARGraphPresenter;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //check if device support the arcore or not
    if (!Utils.checkIsSupportedDeviceOrFinish(this)) {
      return;
    }

    setContentView(R.layout.activity_argraph);
    mARFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
    mARGraphPresenter = new ARGraphPresenter(this);

    Bundle mBundle = getIntent().getExtras();
    if (mBundle == null) {
      finish();
      return;
    }

    getIntentData(mBundle);
    blinkingBtn = findViewById(R.id.blinking_btn);
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

        SnackbarHelper.getInstance()
            .showMessage(this, "Video recording requires the WRITE_EXTERNAL_STORAGE permission");

        return;
      }
    }

    boolean recording = mVideoRecorder.onToggleRecord();
    if (recording) {
      stopRecording();
    } else {
      startRecording();
    }
  }

  private void stopRecording() {
    blinkingBtn.setVisibility(View.VISIBLE);
    shutterSound(false);
    recordButton.setBackground(getResources().getDrawable(R.drawable.ic_stop_black_24dp));
    Utils.blinkAnimation(blinkingBtn, true);
  }

  private void startRecording() {
    Utils.blinkAnimation(blinkingBtn, false);
    blinkingBtn.setVisibility(View.GONE);
    shutterSound(true);
    recordButton
        .setBackground(getResources().getDrawable(R.drawable.ic_play_circle_filled_black_24dp));
    String videoPath = mVideoRecorder.getVideoPath().getAbsolutePath();

    SnackbarHelper.getInstance()
        .showMessage(this, "Video saved: " + videoPath);
    LogClass.getInstance()
        .infoLog(mARGraphHelperClass.getIsLogEnabled(), "Video saved: " + videoPath);

    // Send  notification of updated content.
    ContentValues values = new ContentValues();
    values.put(MediaStore.Video.Media.TITLE, Utils.getFileName());
    values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
    values.put(MediaStore.Video.Media.DATA, videoPath);
    getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
  }

  private void plotGraph(HitResult mHitResult) {
    mARGraphPresenter
        .onPlotGraph(getApplicationContext(), mHitResult, mARGraphHelperClass, mARFragment);
  }


  private void getIntentData(Bundle mBundle) {
    mARGraphPresenter.onGetBundleData(mBundle);
  }

  private void loadMaterials(boolean isIncludeClassicPlatform) {

    CompletableFuture<ModelRenderable> platformStage = ModelRenderable.builder()
        .setSource(this, R.raw.above).build();
    CompletableFuture<Material> redMaterial = MaterialFactory
        .makeOpaqueWithColor(this, new Color(android.graphics.Color.RED));
    CompletableFuture<Material> blueMaterial = MaterialFactory
        .makeOpaqueWithColor(this, new Color(android.graphics.Color.BLUE));
    CompletableFuture.allOf(
        redMaterial,
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

        SnackbarHelper.getInstance()
            .showMessage(this, "Camera permission is needed to run this application");
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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    sound.release();
    mARGraphPresenter.clear();
  }

  private void shutterSound(boolean toPlay) {
    AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    switch (audio.getRingerMode()) {
      case AudioManager.RINGER_MODE_NORMAL:
        if (toPlay) {
          sound.play(MediaActionSound.START_VIDEO_RECORDING);
        } else {
          sound.play(MediaActionSound.STOP_VIDEO_RECORDING);
        }
        break;

    }
  }

  @Override
  public void onError(Object object, Object object2) {
    Throwable mThrowable = (Throwable) object;
    finish();
    LogClass
        .getInstance()
        .errorLog(mARGraphHelperClass.getIsLogEnabled(),
            "Error While convert the json to GraphConfig", mThrowable);
  }

  @Override
  public void onSuccess(Object object, Object object2) {

    mARGraphHelperClass = (ARGraphHelperClass) object;
    mVideoRecorder = new VideoRecorder();
    int orientation = getResources().getConfiguration().orientation;
    mVideoRecorder.setVideoQuality(mVideoRecorder
            .getVideoQuality(mARGraphHelperClass.getGraphConfig().getVIDEO_quality()),
        orientation);
    mVideoRecorder.setSceneView(mARFragment.getArSceneView());
    if (!mARGraphHelperClass.getGraphConfig().isEnableVideo()) {
      recordButton.setVisibility(GONE);
    }
    loadMaterials(mARGraphHelperClass.getGraphConfig().isEnableClassicPlatform());
  }

  @Override
  public void onLoading(String message) {

  }


}
