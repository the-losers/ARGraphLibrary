
package com.losers.argraphlibrary;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.view.MotionEvent;
import androidx.annotation.RequiresApi;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.losers.argraphlibrary.SupportingClasses.ARGraphHelperClass;
import com.losers.argraphlibrary.SupportingClasses.Constants;

@RequiresApi(api = VERSION_CODES.N)
public class BarNode extends Node implements Node.OnTapListener {

  private final String value;
  private ARGraphHelperClass mARGraphHelperClass;
  private Node lastPositionVisual;
  private Float mXPreviousPosition;
  private Double mBarHeight;
  private Node nodeVisual;
  private final Context context;
  private Material mMaterial;
  private Float mBarWidth;
  private boolean isMax;
  private boolean isLastItem;


  public BarNode(Context context, String value,
      Double mBarHeight, Float mXPreviousPosition, ARGraphHelperClass mARGraphHelperClass,
      Float mBarWidth, boolean isMax, boolean isLastItem) {
    this.context = context;
    this.value = value;

    this.mBarHeight = mBarHeight;
    this.mXPreviousPosition = mXPreviousPosition;
    this.mARGraphHelperClass = mARGraphHelperClass;
    this.isMax = isMax;
    this.mBarWidth = mBarWidth;
    this.mMaterial = getCylinderMaterial(isMax, mARGraphHelperClass);
    this.isLastItem = isLastItem;
    setOnTapListener(this);
  }


  @Override
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  public void onActivate() {

    if (getScene() == null) {
      throw new IllegalStateException("Scene is null!");
    }

    if (isLastItem) {
    if (lastPositionVisual == null) {
      lastPositionVisual = new Node();
      lastPositionVisual.setParent(this);
      lastPositionVisual.setEnabled(true);
      lastPositionVisual
          .setRenderable(getEndPointRenderable(mARGraphHelperClass.getWhiteMaterial(),
              mBarHeight.floatValue()));
      lastPositionVisual.setLocalPosition(
          new Vector3(mXPreviousPosition, 0, 0));

//        ViewRenderable.builder()
//            .setView(context, R.layout.info_card)
//            .build()
//            .thenAccept(
//                (ViewRenderable renderable) -> {
//                  lastPositionVisual.setRenderable(renderable);
//                  TextView textView = (TextView) renderable.getView();
//                  textView.setText("23");
//                })
//            .exceptionally(
//                (throwable) -> {
//                  throw new AssertionError("Could not load plane card view.", throwable);
//                });
    }
    }

    if (nodeVisual == null) {
      nodeVisual = new Node();
      nodeVisual.setParent(this);
      nodeVisual.setRenderable(getCylinderRenderable(mMaterial, mBarHeight.floatValue()));
      nodeVisual.setLocalPosition(new Vector3(mXPreviousPosition, 0.0f, 0.0f));
//      nodeVisual.setLocalScale(new Vector3(planetScale, planetScale, planetScale));
    }
  }


  private ModelRenderable getCylinderRenderable(Material mMaterial, Float height) {

    float mYAboveGround = (height / 2);
    return ShapeFactory
        .makeCube(
            new Vector3(mBarWidth, mBarHeight.floatValue(),
                Constants.barLength),
            new Vector3(0.0f, mYAboveGround, 0.0f), mMaterial);

  }

  private ModelRenderable getEndPointRenderable(Material mMaterial, Float height) {

    float forwardFromBar = (Constants.barLength / 2);
    return ShapeFactory
        .makeCube(
            new Vector3(mBarWidth, 0.1f,
                Constants.barLength),
            new Vector3(0.0f, 0.2f , (forwardFromBar + 0.5f)), mMaterial);

  }

  @Override
  public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
    if (lastPositionVisual == null) {
      return;
    }

    lastPositionVisual.setEnabled(!lastPositionVisual.isEnabled());
  }

  private Material getCylinderMaterial(boolean isMax, ARGraphHelperClass arGraphHelperClass) {
    if (isMax) {
      return arGraphHelperClass.getMaxMaterial();
    }
    return arGraphHelperClass.getNormalMaterial();
  }

//  @Override
//  public void onUpdate(FrameTime frameTime) {
//    if (lastPositionVisual == null) {
//      return;
//    }
//
//    // Typically, getScene() will never return null because onUpdate() is only called when the node
//    // is in the scene.
//    // However, if onUpdate is called explicitly or if the node is removed from the scene on a
//    // different thread during onUpdate, then getScene may be null.
////    if (getScene() == null) {
////      return;
////    }
////    Vector3 cameraPosition = getScene().getCamera().getWorldPosition();
////    Vector3 cardPosition = lastPositionVisual.getWorldPosition();
////    Vector3 direction = Vector3.subtract(cameraPosition, cardPosition);
////    Quaternion lookRotation = Quaternion.lookRotation(direction, Vector3.up());
////    lastPositionVisual.setWorldRotation(lookRotation);
//
//  }
}
