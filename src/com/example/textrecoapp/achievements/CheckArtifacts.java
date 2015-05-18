/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.achievements;

import com.example.textrecoapp.App;
import com.example.textrecoapp.gamification.AchievementCriteria;
import com.example.textrecoapp.models.Artifact;
import com.example.textrecoapp.models.Mission;

public class CheckArtifacts extends AchievementCriteria {

	private static final long serialVersionUID = -611583510219673907L;

	private int difficulty;
	private String category;
	private String status;

	public CheckArtifacts(int difficulty, String category) {
		this.difficulty = difficulty;
		this.category = category;
	}

	@Override
	public boolean checkCriteria(Mission mission) {

		if (category == null) {
			return checkArtifactsByRank();
		}

		int totalRequestedArtifacts = App
				.getInstance()
				.getCartographer()
				.getTotalArtifactsForCategoryAndDifficulty(category, difficulty);

		int counter = 0;
		for (Artifact a : App.getInstance().getCartographer().getArtifacts()) {
			if (a.getCategory().equals(category)
					&& a.getDifficulty() == difficulty
					&& a.isArtefactUnlocked()) {
				counter++;
			}
		}
		status = counter + "/" + totalRequestedArtifacts;
		return counter == totalRequestedArtifacts;
	}

	public String getStatus() {
		return status;
	}

	private boolean checkArtifactsByRank() {
		int totalArtifacts = App.getInstance().getCartographer().getArtifacts()
				.size();

		int counter = 0;
		for (Artifact a : App.getInstance().getCartographer().getArtifacts()) {
			if (a.getDifficulty() == difficulty) {
				counter++;
			}
		}
		status = counter + "/" + totalArtifacts;
		return counter == totalArtifacts;
	}

	public int getDifficulty() {
		return difficulty;
	}

}
