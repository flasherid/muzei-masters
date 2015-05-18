/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StoreyState {

  private String storeyMapFilename;
  private List<Pin> pins;

  public StoreyState(String storeyMapFilename) {
    this.storeyMapFilename = storeyMapFilename;
    pins = new ArrayList<Pin>();
  }

  public void sortByVerticalCoordinate() {
    Collections.sort(pins, verticalCoordinateComparator);
  }

  private Comparator<Pin> verticalCoordinateComparator = new Comparator<Pin>() {

    @Override
    public int compare(Pin lhs, Pin rhs) {
      float lhsY = lhs.getY();
      float rhsY = rhs.getY();

      if (lhsY < rhsY) {
        return -1;
      } else if (lhsY > rhsY) {
        return 1;
      }
      return 0;
    }
  };

  public String getStoreyMapFilename() {
    return storeyMapFilename;
  }

  public List<Pin> getPins() {
    return pins;
  }

}
