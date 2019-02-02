
package com.losers.argraphlibrary;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.TextView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.losers.argraphlibrary.SupportingClasses.Constants;
import com.losers.argraphlibrary.Modal.GraphConfig;

public class CylinderNode extends Node implements Node.OnTapListener {

  private final String planetName;


  private GraphConfig mGraphConfig;

  private Node infoCard;
  //  private Node speedCard;
  private Float mXPreviousPosition;
  private Double mBarHeight;
  private Node nodeVisual;
  private final Context context;
  private Material mMaterial;
  //  private ArFragment arFragment
  private Float mBarWidth;
//  private ModelRenderable mArrowRenderable;

  public CylinderNode(Context context, String planetName, Material mMaterial,
      Double mBarHeight, Float mXPreviousPosition, GraphConfig mGraphConfig,
       Float mBarWidth) {
    this.context = context;
    this.planetName = planetName;
    this.mMaterial = mMaterial;
    this.mBarHeight = mBarHeight;
    this.mXPreviousPosition = mXPreviousPosition;
    this.mGraphConfig = mGraphConfig;

    this.mBarWidth = mBarWidth;
//    this.mArrowRenderable = mArrowRenderable;
    setOnTapListener(this);
  }


  @Override
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  public void onActivate() {

    if (getScene() == null) {
      throw new IllegalStateException("Scene is null!");
    }

    if (infoCard == null) {
      infoCard = new Node();
      infoCard.setParent(this);
      infoCard.setEnabled(true);
      infoCard.setLocalScale(new Vector3(0.1f, 0.1f, 0.1f));
//        infoCard.setRenderable(mArrowRenderable);
      infoCard.setLocalPosition(
          new Vector3(mXPreviousPosition, (mBarHeight.floatValue()), 0.0f));

      ViewRenderable.builder()
          .setView(context, R.layout.info_card)
          .build()
          .thenAccept(
              (renderable) -> {
                infoCard.setRenderable(renderable);
                TextView textView = (TextView) renderable.getView();
                textView.setText(planetName);
              })
          .exceptionally(
              (throwable) -> {
                throw new AssertionError("Could not load plane card view.", throwable);
              });
    }

    if (nodeVisual == null) {
      nodeVisual = new Node();
      nodeVisual.setParent(this);
      nodeVisual.setRenderable(getPlanetRenderable(mMaterial, mBarHeight.floatValue()));
      nodeVisual.setLocalPosition(new Vector3(mXPreviousPosition, 0.0f, 0.0f));
//      nodeVisual.setLocalScale(new Vector3(planetScale, planetScale, planetScale));
    }
  }

  private ModelRenderable getPlanetRenderable(Material mMaterial, Float height) {

    Float mYAboveGround = (height / 2);
//    Log.d("Distance 1", mGraphSettings.getCubeWidth() + " ");
    return ShapeFactory
        .makeCube(
            new Vector3(mBarWidth.floatValue(), mBarHeight.floatValue(),
                Constants.cubeLength),
            new Vector3(0.0f, mYAboveGround, 0.0f), mMaterial);

  }

  @Override
  public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
    if (infoCard == null) {
      return;
    }

    infoCard.setEnabled(!infoCard.isEnabled());
  }

  @Override
  public void onUpdate(FrameTime frameTime) {
    if (infoCard == null) {
      return;
    }

    // Typically, getScene() will never return null because onUpdate() is only called when the node
    // is in the scene.
    // However, if onUpdate is called explicitly or if the node is removed from the scene on a
    // different thread during onUpdate, then getScene may be null.
    if (getScene() == null) {
      return;
    }
    Vector3 cameraPosition = getScene().getCamera().getWorldPosition();
    Vector3 cardPosition = infoCard.getWorldPosition();
    Vector3 direction = Vector3.subtract(cameraPosition, cardPosition);
    Quaternion lookRotation = Quaternion.lookRotation(direction, Vector3.up());
    infoCard.setWorldRotation(lookRotation);

  }
}
