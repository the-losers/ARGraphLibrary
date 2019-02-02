package com.losers.argraphlibrary.UI;

import android.content.Context;
import android.os.Bundle;
import com.google.ar.core.HitResult;
import com.google.ar.sceneform.ux.ArFragment;
import com.losers.argraphlibrary.SupportingClasses.ARGraphHelperClass;

public interface ARGraphInterface {

  void onGetBundleData(Bundle bundle);

  void onPlotGraph(Context context,HitResult mHitResult, ARGraphHelperClass mARGraphHelperClass,
      ArFragment mARFragment);
}
