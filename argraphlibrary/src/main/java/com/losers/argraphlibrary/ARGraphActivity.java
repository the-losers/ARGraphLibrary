package com.losers.argraphlibrary;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.gson.Gson;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class ARGraphActivity extends AppCompatActivity {

  private final Gson mGson = new Gson();
  private ArFragment mARFragment;
  private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
  private GraphConfig mGraphConfig;

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
  }

  private void getIntentData(Bundle mBundle) {
    mCompositeDisposable.add(Single.just(mBundle)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .filter(bundle -> bundle.containsKey(Constants.INTENT_CONFIG))
        .map(bundle -> mGson.fromJson(bundle.getString(Constants.INTENT_CONFIG), GraphConfig.class))
        .subscribeWith(new DisposableMaybeObserver<GraphConfig>() {
          @Override
          public void onSuccess(GraphConfig graphConfig) {
            mGraphConfig = graphConfig;
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
}
