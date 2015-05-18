/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.ar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.textrecoapp.R;

@SuppressLint("ViewConstructor")
public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String LOG_TAG = "CameraPreview";

	private SurfaceHolder mHolder;
	private Camera mCamera;

	@SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();

			Camera.Parameters parameters = mCamera.getParameters();
			if( Build.MANUFACTURER.equals("samsung")) {
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			} else {
				parameters
				.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			}

			int focusRectHalfWidth = getContext().getResources()
					.getDimensionPixelOffset(R.dimen.scan_area_width);
			int focusRectHalfHeight = getContext().getResources()
					.getDimensionPixelOffset(R.dimen.scan_area_height);

			Rect focusRect = new Rect(focusRectHalfWidth * (-1),
					focusRectHalfHeight * (-1), focusRectHalfWidth,
					focusRectHalfHeight);

			Camera.Area focusArea = new Area(focusRect, 1000);
			List<Camera.Area> areas = new ArrayList<Camera.Area>();
			areas.add(focusArea);

			parameters.setFocusAreas(areas);

			mCamera.setParameters(parameters);
		} catch (IOException e) {
			Log.d(LOG_TAG, "Error setting camera preview: " + e.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (Exception e) {
			Log.d(LOG_TAG, "Error starting camera preview: " + e.getMessage());
		}
	}
}
