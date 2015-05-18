/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.gamification;

import java.util.ArrayList;
import java.util.List;

import com.example.textrecoapp.App;
import com.example.textrecoapp.achievements.Achievement;
import com.example.textrecoapp.models.Mission;

public class AchievementChecker {

  public List<Achievement> checkAchievements(Mission mission) {
    List<Achievement> freshlyUnlockedAchievements = new ArrayList<Achievement>();

    for (List<Achievement> list : App.getInstance().getAchievements().values()) {
      for (Achievement achievement : list) {

        if (achievement.isContinuous()) {
          int timesBeforeCheck = achievement.getTimes();
          achievement.checkAchievement(mission);
          if (achievement.getTimes() > timesBeforeCheck) {
            freshlyUnlockedAchievements.add(achievement);
          }
        } else {
          boolean previouslyUnlocked = achievement.isUnlocked();
          achievement.checkAchievement(mission);
          if (achievement.isUnlocked() && !previouslyUnlocked) {
            freshlyUnlockedAchievements.add(achievement);
          }
        }

      }

    }
    return freshlyUnlockedAchievements;
  }
}
