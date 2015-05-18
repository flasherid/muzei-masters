/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.map;

import java.io.Serializable;

import android.graphics.Bitmap;

import com.example.textrecoapp.models.Artifact;

public class Pin implements Serializable {

  private static final long serialVersionUID = 6487672438654082634L;
  
  private Artifact artifact;
  private Bitmap bitmap;
  
  public Pin(Artifact artifact) {
    this.artifact = artifact;
  }

  public Artifact getArtifact() {
    return artifact;
  }

  public void setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
  }

  public Bitmap getBitmap() {
    return bitmap;
  }

  public float getX() {
    return artifact.getLocation().getxPercentage();
  }

  public float getY() {
    return artifact.getLocation().getyPercentage();
  }
}
