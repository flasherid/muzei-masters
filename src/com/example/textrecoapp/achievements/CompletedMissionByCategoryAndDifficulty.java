/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.achievements;

import com.example.textrecoapp.gamification.AchievementCriteria;
import com.example.textrecoapp.models.Artifact;
import com.example.textrecoapp.models.Mission;

public class CompletedMissionByCategoryAndDifficulty extends
		AchievementCriteria {

	private static final long serialVersionUID = 6830171716753456650L;

	private String category;
	private int difficulty;

	public CompletedMissionByCategoryAndDifficulty(String category,
			int difficulty) {
		this.category = category;
		this.difficulty = difficulty;
	}

	@Override
	public boolean checkCriteria(Mission mission) {
		Artifact artifact = mission.getSteps().get(0).getArtifact();
		return artifact.getCategory().equals(category)
				&& artifact.getDifficulty() == difficulty;
	}

	public int getDifficulty() {
		return difficulty;
	}

}
