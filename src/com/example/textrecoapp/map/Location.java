/*
 * Copyright (C) 2014 by Netcetera AG.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of Netcetera AG, Switzerland.
 * The program(s) may be used and/or copied only with the written permission of Netcetera AG or
 * in accordance with the terms and conditions stipulated in the agreement/contract under which 
 * the program(s) have been supplied.
 */
package com.example.textrecoapp.map;

import java.io.Serializable;


public class Location implements Serializable {

  private static final long serialVersionUID = 6848020802129922522L;
  
  private float xPercentage;
  private float yPercentage;
  private String floorId;
  
  public Location(float xPercentage, float yPercentage, String floorId) {
    this.xPercentage = xPercentage;
    this.yPercentage = yPercentage;
    this.floorId = floorId;
  }

  public float getxPercentage() {
    return xPercentage;
  }
  
  public float getyPercentage() {
    return yPercentage;
  }
  
  public String getFloorId() {
    return floorId;
  }
  
  
}
