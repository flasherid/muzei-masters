package com.example.textrecoapp.achievements;

import java.util.Map;
import java.util.Map.Entry;

import com.example.textrecoapp.characters.Character;
import com.example.textrecoapp.App;
import com.example.textrecoapp.gamification.AchievementCriteria;
import com.example.textrecoapp.models.Mission;

public class CompletedStoryByCategory extends AchievementCriteria {

	private static final long serialVersionUID = -6847162361349223175L;

	private String category;

	public CompletedStoryByCategory(String category) {
		this.category = category;
	}

	@Override
	public boolean checkCriteria(Mission mission) {
		Map<String, Character> allCharacters = App.getInstance()
				.getCharacterManager().getMapCharacters();

		if (category == null) {
			return checkAllCharacterStoryMissions(allCharacters);
		}

		// get only the map for the given category
		Map<Mission, Boolean> categoryStories = getStorySetForCategory(allCharacters);
		for (Entry<Mission, Boolean> entry : categoryStories.entrySet()) {
			if (!entry.getValue()) {
				return false;
			}
		}

		return true;
	}

	private boolean checkAllCharacterStoryMissions(
			Map<String, Character> allCharacters) {
		for (Entry<String, Character> entry : allCharacters.entrySet()) {

			for (Entry<Mission, Boolean> e : entry.getValue()
					.getStoryMissions().entrySet()) {
				if (!e.getValue()) {
					return false;
				}
			}

		}
		return true;
	}

	private Map<Mission, Boolean> getStorySetForCategory(
			Map<String, Character> allCharacters) {
		for (Entry<String, Character> c : allCharacters.entrySet()) {
			if (c.getValue().getCategory().equals(category)) {
				return c.getValue().getStoryMissions();
			}
		}
		return null;
	}
}
