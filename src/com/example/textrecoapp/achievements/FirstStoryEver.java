package com.example.textrecoapp.achievements;

import com.example.textrecoapp.gamification.AchievementCriteria;
import com.example.textrecoapp.models.Mission;

public class FirstStoryEver extends AchievementCriteria {

	private static final long serialVersionUID = -2124012322625110968L;

	@Override
	public boolean checkCriteria(Mission mission) {
		return mission.getId() != 0;
	}

}
