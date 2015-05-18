/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.map;

import java.util.ArrayList;
import java.util.List;

import com.example.textrecoapp.models.Artifact;

public class Cartographer {

	public static final String CARTOGRAPHER = "cartographer";

	private BuildingNavigator navigator;
	private List<Artifact> artifacts;
	private List<Floor> floors;

	public Cartographer(List<Floor> floors, List<Artifact> artifacts) {
		this.artifacts = artifacts;
		this.floors = floors;
		navigator = new BuildingNavigator(this.floors, this.artifacts);
	}

	public List<Artifact> getArtifacts() {
		return artifacts;
	}

	public Artifact findArtifact(String artifactName) {
		Artifact desiredArtifact = null;
		for (Artifact a : artifacts) {
			if (a.getName().equals(artifactName)) {
				desiredArtifact = a;
				break;
			}
		}
		return desiredArtifact;
	}

	public void unlockArtifact(Artifact artifact) {
		for (Artifact a : artifacts) {
			if (a.getName().equals(artifact.getName())) {
				a.unlockArtefact();
				return;
			}
		}
	}

	public BuildingNavigator getNavigator() {
		return navigator;
	}

	public int getTotalLevelsForCategory(String categoryName) {
		List<Integer> diffLvls = new ArrayList<Integer>();
		for (Artifact a : artifacts) {
			if (a.getCategory().equals(categoryName)) {

				if (!diffLvls.contains(Integer.valueOf(a.getDifficulty()))) {
					diffLvls.add(Integer.valueOf(a.getDifficulty()));
				}

			}
		}
		return diffLvls.size();
	}

	public int getTotalArtifactsForCategoryAndDifficulty(String category,
			int difficulty) {
		int counter = 0;
		for (Artifact a : artifacts) {
			if (a.getCategory().equals(category)
					&& difficulty == a.getDifficulty()) {
				counter++;
			}
		}
		return counter;
	}

	public List<Floor> getAllFloors() {
		return floors;
	}
	
	public void setArtifacts(List<Artifact> newArtifacts) {
		artifacts = newArtifacts;
		navigator = new BuildingNavigator(floors, artifacts);
	}

}
