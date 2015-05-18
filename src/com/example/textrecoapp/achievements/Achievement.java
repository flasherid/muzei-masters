/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.achievements;

import java.io.Serializable;

import com.example.textrecoapp.gamification.AchievementCriteria;
import com.example.textrecoapp.models.Mission;

public class Achievement implements Serializable {

	private static final long serialVersionUID = 5948239323797798106L;

	private String name;
	private String description;
	private AchievementCriteria criteria;
	private boolean unlocked;

	private boolean continuous;
	private int times;

	public Achievement(String name, String description,
			AchievementCriteria criteria, boolean continuous) {
		this.name = name;
		this.description = description;
		this.criteria = criteria;
		this.continuous = continuous;

		times = 0;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void checkAchievement(Mission mission) {
		if (continuous) {
			if (criteria.checkCriteria(mission)) {
				times++;
			}
			return;
		}

		if (unlocked) {
			return;
		}

		unlocked = criteria.checkCriteria(mission);
	}

	public boolean isUnlocked() {
		return unlocked;
	}

	public boolean isContinuous() {
		return continuous;
	}

	public int getTimes() {
		return times;
	}

	public void lockAchievement() {
		times = 0;
		unlocked = false;
	}

	public AchievementCriteria getCriteria() {
		return criteria;
	}
}
