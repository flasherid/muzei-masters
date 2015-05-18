/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;

import com.example.textrecoapp.ar.ScanningResult;
import com.googlecode.tesseract.android.TessBaseAPI;

public class ImageOcrProcessing extends AsyncTask<Bitmap, Void, String> {

  private View progressBar;
  private TessBaseAPI api;
  private ScanningResult scanResult;

  public ImageOcrProcessing(View progressBar, TessBaseAPI api, ScanningResult scanResult) {
    this.progressBar = progressBar;
    this.api = api;
    this.scanResult = scanResult;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override
  protected String doInBackground(Bitmap... params) {
    Bitmap bmp = (Bitmap) params[0];
    api.setImage(bmp);
    return api.getUTF8Text();
  }

  @Override
  protected void onPostExecute(String result) {
    super.onPostExecute(result);
    progressBar.setVisibility(View.GONE);
    scanResult.onScanningFinished(result);
  }

}
