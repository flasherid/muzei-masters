/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.characters;

import java.io.Serializable;
import java.util.Map;

import com.example.textrecoapp.App;
import com.example.textrecoapp.models.Mission;

/**
 * AKA Category.
 */
public class Character implements Serializable {

	private static final long serialVersionUID = 2100891000937263976L;

	public static final int KNOWLEDGE_BASIC = 1;
	public static final int KNOWLEDGE_INTERMEDIATE = 2;
	public static final int KNOWLEDGE_SUPERIOR = 3;
	public static final int KNOWLEDGE_EXPERT = 4;

	private Mission rankedMission;
	private Map<Mission, Boolean> storyMissions;
	private String name;
	private String category;
	private String pictureFilename;
	private int latestUnlockedLevel;
	private int id;
	
	public Character(int id, String name, String category, String pictureFilename, Map<Mission, Boolean> storyMissions) {
		this(id, name, category, pictureFilename, storyMissions, null);
	}
	
	public Character(int id, String name, String category, String pictureFilename, Map<Mission, Boolean> storyMissions, Mission rankedMisison) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.pictureFilename = pictureFilename;
		this.rankedMission = rankedMisison;
		this.storyMissions = storyMissions;
		latestUnlockedLevel = 1;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public String getPictureFilename() {
		return pictureFilename;
	}

	public int getLatestUnlockedLevel() {
		return latestUnlockedLevel;
	}

	public void unlockNewLevel() {
		int total = App.getInstance().getCartographer()
				.getTotalLevelsForCategory(category);
		if (total > latestUnlockedLevel) {
			latestUnlockedLevel++;
		}
	}
	
	public Mission getRankedMission() {
		return rankedMission;
	}

	public void setRankedMission(Mission rankedMission) {
		this.rankedMission = rankedMission;
	}
	
	public void setStoryMissions(Map<Mission, Boolean> storyMissions) {
		this.storyMissions = storyMissions;
	}
	
	public Map<Mission, Boolean> getStoryMissions() {
		return storyMissions;
	}
	
	public void setLatestUnlockedLevel(int latestUnlockedLevel) {
		this.latestUnlockedLevel = latestUnlockedLevel;
	}

}
