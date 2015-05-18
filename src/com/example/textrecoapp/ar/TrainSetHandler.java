package com.example.textrecoapp.ar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;


public class TrainSetHandler {

  public static final String LOG_TAG = "TrainSetHandler";
  public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString()
      + "/TextRecoApp";

  private String lang;
  private Context context;

  public TrainSetHandler(Context context, String lang) {
    this.context = context;
    this.lang = lang;
  }

  public void initDirectory() {
    String[] paths = new String[]{DATA_PATH, DATA_PATH + "/tessdata/"};
    for (String path : paths) {
      File dir = new File(path);
      if (!dir.exists()) {
        if (!dir.mkdirs()) {
          Log.e(LOG_TAG, "ERROR: Creation of directory " + path + " on SD Card failed");
          return;
        } else {
          Log.v(LOG_TAG, "Created directory " + path + " on SD Card");
        }
      }
    }

    checkAndCreateTrainedData();
  }

  private void checkAndCreateTrainedData() {
    if (!(new File(DATA_PATH + "/tessdata/" + lang + ".traineddata")).exists()) {
      try {
        AssetManager assetsManager = context.getAssets();
        InputStream is = assetsManager.open(lang + ".traineddata");
        OutputStream os = new FileOutputStream(DATA_PATH + "/tessdata/" + lang + ".traineddata");

        byte[] buffer = new byte[1024];
        int lenght;

        while ((lenght = is.read(buffer)) > 0) {
          os.write(buffer, 0, lenght);
        }

        is.close();
        os.close();
        
        Log.v(LOG_TAG, "Copied " + lang + ".traineddata");
      } catch (Exception e) {
        Log.e(LOG_TAG, "Was unable to copy " + lang + ".traineddata");
      }
    }
  }

}
