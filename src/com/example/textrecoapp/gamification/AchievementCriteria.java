/*
 * Copyright (C) 2014 by Netcetera AG.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of Netcetera AG, Switzerland.
 * The program(s) may be used and/or copied only with the written permission of Netcetera AG or
 * in accordance with the terms and conditions stipulated in the agreement/contract under which 
 * the program(s) have been supplied.
 */
package com.example.textrecoapp.gamification;

import java.io.Serializable;

import com.example.textrecoapp.models.Mission;


public abstract class AchievementCriteria implements Serializable {
  
  
  public AchievementCriteria() {
  }
  
  private static final long serialVersionUID = -5419844084770354108L;

  public boolean checkCriteria(Mission mission) {
    return false;
  };
}
