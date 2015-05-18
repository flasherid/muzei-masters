/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.map;

/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Custom view that handles the zooming and panning gestures of an image view that is bigger than
 * the screen size.
 */
public class ScrollableZoomableImageView extends ImageView {

  /**
   * Matrix that saves the current state of the image. It contains the latest transformations
   * (scaling, translation).
   */
  protected Matrix mapMatrix;

  // We can be in one of these 4 states
  private static final int NONE = 0;
  private static final int DRAG = 1;
  private static final int ZOOM = 2;
  private static final int CLICK = 5;
  private int mode = NONE;

  // Remember some things for zooming
  private PointF last = new PointF();
  private PointF start = new PointF();
  private float minScale = 1f;
  private float maxScale = 5f;
  private float[] m;

  private int viewWidth;
  private int viewHeight;
  private float saveScale = 1f;
  protected float origWidth;
  protected float origHeight;
  
  int oldMeasuredWidth;
  int oldMeasuredHeight;

  private ScaleGestureDetector mScaleDetector;

  /**
   * Constructor for programmatic instantiation.
   * 
   * @param context the context
   */
  public ScrollableZoomableImageView(Context context) {
    super(context);
    sharedConstructing(context);
  }

  /**
   * Constructor for instantiation using XML layout file.
   * 
   * @param context the context
   * @param attrs the set of attributes
   */
  public ScrollableZoomableImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    sharedConstructing(context);
  }

  @SuppressLint("ClickableViewAccessibility")
  private void sharedConstructing(Context context) {
    super.setClickable(true);
    mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    mapMatrix = new Matrix();
    m = new float[9];
    setImageMatrix(mapMatrix);
    setScaleType(ScaleType.MATRIX);

    setOnTouchListener(new OnTouchListener() {

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        PointF curr = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            last.set(curr);
            start.set(last);
            mode = DRAG;
            break;

          case MotionEvent.ACTION_MOVE:
            if (mode == DRAG) {
              float deltaX = curr.x - last.x;
              float deltaY = curr.y - last.y;
              float fixTransX = getFixDragTrans(deltaX, viewWidth, origWidth * saveScale);
              float fixTransY = getFixDragTrans(deltaY, viewHeight, origHeight * saveScale);
              mapMatrix.postTranslate(fixTransX, fixTransY);
              fixTrans();
              last.set(curr.x, curr.y);
            }
            break;

          case MotionEvent.ACTION_UP:
            mode = NONE;
            int xDiff = (int) Math.abs(curr.x - start.x);
            int yDiff = (int) Math.abs(curr.y - start.y);
            if (xDiff < CLICK && yDiff < CLICK) {
              performClick();
              handleClickEvent((int) event.getX(), (int) event.getY());
            }
            break;

          case MotionEvent.ACTION_POINTER_UP:
            mode = NONE;
            break;
        }

        setImageMatrix(mapMatrix);
        invalidate();
        return true; // indicate event was handled
      }

    });
    prepareDrawables();
  }

  public void setMaxZoom(float x) {
    maxScale = x;
  }

  private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
      mode = ZOOM;
      return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      float mScaleFactor = detector.getScaleFactor();
      float origScale = saveScale;
      saveScale *= mScaleFactor;
      if (saveScale > maxScale) {
        saveScale = maxScale;
        mScaleFactor = maxScale / origScale;
      } else if (saveScale < minScale) {
        saveScale = minScale;
        mScaleFactor = minScale / origScale;
      }

      if (origWidth * saveScale <= viewWidth || origHeight * saveScale <= viewHeight) {
        mapMatrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2, viewHeight / 2);
      } else {
        mapMatrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());
      }

      fixTrans();
      return true;
    }
  }

  protected void fixTrans() {
    mapMatrix.getValues(m);
    float transX = m[Matrix.MTRANS_X];
    float transY = m[Matrix.MTRANS_Y];

    float fixTransX = getFixTrans(transX, viewWidth, origWidth * saveScale);
    float fixTransY = getFixTrans(transY, viewHeight, origHeight * saveScale);

    if (fixTransX != 0 || fixTransY != 0) {
      mapMatrix.postTranslate(fixTransX, fixTransY);
    }
  }

  private float getFixTrans(float trans, float viewSize, float contentSize) {
    float minTrans;
    float maxTrans;

    if (contentSize <= viewSize) {
      minTrans = 0;
      maxTrans = viewSize - contentSize;
    } else {
      minTrans = viewSize - contentSize;
      maxTrans = 0;
    }

    if (trans < minTrans) {
      return -trans + minTrans;
    }
    if (trans > maxTrans) {
      return -trans + maxTrans;
    }
    return 0;
  }

  private float getFixDragTrans(float delta, float viewSize, float contentSize) {
    if (contentSize <= viewSize) {
      return 0;
    }
    return delta;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    viewWidth = MeasureSpec.getSize(widthMeasureSpec);
    viewHeight = MeasureSpec.getSize(heightMeasureSpec);

    // Rescaled image on rotation
    if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight || viewWidth == 0 || viewHeight == 0) {
      return;
    }
    oldMeasuredHeight = viewHeight;
    oldMeasuredWidth = viewWidth;

    if (saveScale == 1) {
      // Fit to screen.
      float scale;

      Drawable drawable = getDrawable();
      if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
        return;
      }
      int bmWidth = drawable.getIntrinsicWidth();
      int bmHeight = drawable.getIntrinsicHeight();

      Log.d("bmSize", "bmWidth: " + bmWidth + " bmHeight : " + bmHeight);

      float scaleX = (float) viewWidth / (float) bmWidth;
      float scaleY = (float) viewHeight / (float) bmHeight;

      WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
      int rotation = windowManager.getDefaultDisplay().getRotation();

      if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
        // landscape
        scale = Math.max(scaleX, scaleY);
      } else {
        scale = Math.min(scaleX, scaleY);
      }
      // scale = 1f;
      mapMatrix.setScale(scale, scale);

      // Center the image
      float redundantYSpace = (float) viewHeight - (scale * (float) bmHeight);
      float redundantXSpace = (float) viewWidth - (scale * (float) bmWidth);
      redundantYSpace /= (float) 2;
      redundantXSpace /= (float) 2;

      mapMatrix.postTranslate(redundantXSpace, redundantYSpace);

      origWidth = viewWidth - 2 * redundantXSpace;
      origHeight = viewHeight - 2 * redundantYSpace;
      setImageMatrix(mapMatrix);
    }
    fixTrans();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    drawPins(canvas);
  }

  /**
   * This method should be used in case it is needed some additional bitmaps (maps, pins) or paints
   * to be initialized for later use in the method {@link #drawPins(Canvas)}.
   * 
   */
  protected void prepareDrawables() {
    // empty on purpose
  }

  /**
   * This method should be overridden and used to handle the drawing and positioning of pins
   * depending of their coordinates and the view's current scroll and zoom values.
   * 
   * @param canvas the view's canvas object
   */
  protected void drawPins(Canvas canvas) {
    // empty on purpose
  }

  /**
   * This method should be overridden and used to process the clicks over the pins that are shown on
   * the map. The coordinates are dependent of the screen size and not the image/map shown by the
   * view.
   * 
   * @param x the x-coordinate of the click event
   * @param y the y-coordinate of the click event
   */
  protected void handleClickEvent(int x, int y) {
    // empty on purpose
  }

  public Matrix getMapMatrix() {
    return mapMatrix;
  }
  
  public void setMapMatrix(Matrix mapMatrix) {
    this.mapMatrix = mapMatrix;
  }
  
  public float getSaveScale() {
    return saveScale;
  }
  
  public void setSaveScale(float saveScale) {
    this.saveScale = saveScale;
  }
  
  public int getViewWidth() {
    return viewWidth;
  }
  
  public void setViewWidth(int viewWidth) {
    this.viewWidth = viewWidth;
  }
  
  public int getViewHeight() {
    return viewHeight;
  }

  public void setViewHeight(int viewHeight) {
    this.viewHeight = viewHeight;
  }
}
