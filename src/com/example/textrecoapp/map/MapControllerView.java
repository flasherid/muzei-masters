/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.map;

import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.textrecoapp.R;
import com.example.textrecoapp.UiUtils;
import com.example.textrecoapp.models.Artifact;

public class MapControllerView extends ScrollableZoomableImageView {

	private BuildingNavigator navigator;
	private MapActivity activity;

	private Bitmap pinLocked;
	private Bitmap pinUnlocked;

	private float[] matrixValues = new float[9];

	private int bitmapWidth;
	private int bitmapHeight;

	private Pin preparedPin;
	private HelperCircle helperCircle;

	// TODO: floor changes, special Utility icons (toilets, stairs, elevators)
	public MapControllerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MapControllerView(Context context) {
		super(context);
		init();
	}

	private void init() {
		pinLocked = BitmapFactory.decodeResource(getResources(),
				R.drawable.pin_locked);
		pinUnlocked = BitmapFactory.decodeResource(getResources(),
				R.drawable.pin_unlocked);
	}

	public void setNavigator(BuildingNavigator navigator) {
		this.navigator = navigator;
		setLatestBlueprint();
		bitmapWidth = getDrawable().getIntrinsicWidth();
		bitmapHeight = getDrawable().getIntrinsicHeight();
		updatePinBitmaps();
		invalidate();
	}

	private void setLatestBlueprint() {
		try {
			InputStream is = getContext().getAssets().open(
					navigator.getStoreyState().getStoreyMapFilename());
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			setImageBitmap(bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updatePinBitmaps() {
		for (Pin p : navigator.getStoreyState().getPins()) {
			p.setBitmap(getBitmapForArtifact(p.getArtifact()));
		}
	}

	private Bitmap getBitmapForArtifact(Artifact artifact) {
		Bitmap bmp = artifact.isArtefactUnlocked() ? pinUnlocked : pinLocked;
		return bmp;
	}

	public void changeFloor(String newFloorId) {
		navigator.changeStorey(newFloorId);
		setLatestBlueprint();
		updatePinBitmaps();
		invalidate();
	}

	@Override
	protected void drawPins(Canvas canvas) {
		super.drawPins(canvas);
		calculateNewestMatrixChanges();

		if (preparedPin != null) {
			Point p = transformMapToScreenPoint(preparedPin.getX(),
					preparedPin.getY());
			UiUtils.drawHighlightedRectangle(canvas, p);
		}

		if (helperCircle != null) {
			Point centerPoint = transformMapToScreenPoint(
					helperCircle.getCenter().x, helperCircle.getCenter().y);
			Point periferalPoint = transformMapToScreenPoint(
					helperCircle.getPeriferial().x, helperCircle.getCenter().y);

			float dx = centerPoint.x - periferalPoint.x;
			float dy = centerPoint.y - periferalPoint.y;

			int radius = (int) Math.sqrt(dx * dx + dy * dy);
			UiUtils.drawCircle(canvas, centerPoint, radius);
		}

		for (Pin p : navigator.getStoreyState().getPins()) {
			drawPin(canvas, p);
		}
	}

	private void calculateNewestMatrixChanges() {
		getMapMatrix().getValues(matrixValues);
	}

	@Override
	protected void handleClickEvent(int x, int y) {
		super.handleClickEvent(x, y);

		for (Pin pin : navigator.getStoreyState().getPins()) {
			int bitmapWidth = pin.getBitmap().getWidth();
			int bitmapHeight = pin.getBitmap().getHeight();

			Point point = transformMapToScreenPoint(pin.getX(), pin.getY());

			Rect rect = new Rect(point.x - bitmapWidth, point.y - bitmapHeight,
					point.x + bitmapWidth, point.y);

			if (rect.contains(x, y)) {

				if(pin.getArtifact().isArtefactUnlocked()) {
					activity.handleClickedArtifact(pin.getArtifact());
				} else {
					Toast.makeText(getContext(), "Artifact is locked", bitmapHeight).show();
				}
				return;
			}
		}
		activity.handleDissmissingClickedArtifact();
	}

	private void centerPinInsideScreen(float nodeXPercentage,
			float nodeYPercentage) {
		Point pointOnScreen = transformMapToScreenPoint(nodeXPercentage,
				nodeYPercentage);
		float deltaX = (getViewWidth() / 2) - pointOnScreen.x;
		float deltaY = (getViewHeight() / 2) - pointOnScreen.y;

		calculateNewestMatrixChanges();
		getMapMatrix().postTranslate(deltaX, deltaY);
		setMapMatrix(getMapMatrix());
		setImageMatrix(getMapMatrix());
		fixTrans();
	}

	protected Point transformMapToScreenPoint(float x, float y) {
		int correctedX = (int) (bitmapWidth * x);
		int correctedY = (int) (bitmapHeight * y);
		int pinX = (int) (correctedX * matrixValues[Matrix.MSCALE_X] + matrixValues[Matrix.MTRANS_X]);
		int pinY = (int) (correctedY * matrixValues[Matrix.MSCALE_Y] + matrixValues[Matrix.MTRANS_Y]);
		return new Point(pinX, pinY);
	}

	private void drawPin(Canvas canvas, Pin pin) {
		Bitmap bitmap = pin.getBitmap();
		int offsetX = bitmap.getWidth() / 2;
		int offsetY = bitmap.getHeight() / 2;
		Point p = transformMapToScreenPoint(pin.getX(), pin.getY());
		canvas.drawBitmap(pin.getBitmap(), p.x - offsetX, p.y - offsetY, null);
	}

	public void markAndCenterPin(Pin preparedPin) {
		this.preparedPin = preparedPin;

		if (preparedPin != null) {
			centerPinInsideScreen(preparedPin.getX(), preparedPin.getY());

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					dispatchFakeTouchInTheMiddleOfTheScreen();
				}
			}, 300);
		}
	}

	/**
	 * Dispatches touch event to the map view which corrects bad drawings caused
	 * by matrix transitions.
	 */
	@SuppressLint("Recycle")
	public void dispatchFakeTouchInTheMiddleOfTheScreen() {
		dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
				getWidth() / 2f, getHeight() / 2f, 0));
		performClick();
		fixTrans();
		invalidate();
	}

	public void setHelperCircle(HelperCircle circle) {
		helperCircle = circle;
	}
	
	public void setActivity(MapActivity activity) {
		this.activity = activity;
	}
}
