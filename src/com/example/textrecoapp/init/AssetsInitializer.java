package com.example.textrecoapp.init;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.SparseArray;

import com.example.textrecoapp.App;
import com.example.textrecoapp.achievements.Achievement;
import com.example.textrecoapp.achievements.CheckArtifacts;
import com.example.textrecoapp.achievements.CompletedMissionByCategoryAndDifficulty;
import com.example.textrecoapp.achievements.CompletedStoryByCategory;
import com.example.textrecoapp.achievements.FirstMissionEver;
import com.example.textrecoapp.achievements.FirstStoryEver;
import com.example.textrecoapp.characters.Character;
import com.example.textrecoapp.gamification.AchievementCriteria;
import com.example.textrecoapp.map.Floor;
import com.example.textrecoapp.map.Location;
import com.example.textrecoapp.models.Artifact;
import com.example.textrecoapp.models.Mission;
import com.example.textrecoapp.models.MissionStep;

public class AssetsInitializer {

	private static final String FILE_FLOORS = "floors.json";
	private static final String FILE_ARTIFACTS = "artifacts.json";
	private static final String FILE_STORIES = "stories.json";
	private static final String FILE_CHARACTERS = "characters.json";
	private static final String FILE_ACHIEVEMENTS = "achievements.json";

	private static final int CHECK_ARTIFACTS = 1;
	private static final int COMPLETED_MISSION_BY_CATEGORY_DIFFICULTY = 2;
	private static final int FIRST_MISSION_EVER = 3;
	private static final int FIRST_STORY_MISSION = 4;
	private static final int COMPLETED_STORIES_BY_CATEGORY = 5;

	private Context context;
	private SparseArray<String> map;

	public AssetsInitializer(Context context) {
		this.context = context;
	}

	public List<Artifact> parseArtifacts() throws IOException {
		List<Artifact> artifacts = new ArrayList<Artifact>();
		String json = readJsonFile(context.getAssets().open(FILE_ARTIFACTS));
		try {
			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				String name = obj.getString("name");
				String description = obj.getString("description");
				String category = obj.getString("category");
				String picture = obj.getString("picture");
				int difficulty = obj.getInt("difficulty");

				JSONObject locationObj = obj.getJSONObject("location");
				float xPercentage = (float) locationObj.getDouble("xpercent");
				float yPercentage = (float) locationObj.getDouble("ypercent");
				String floorId = locationObj.getString("name");
				Location location = new Location(xPercentage, yPercentage,
						floorId);

				Artifact artifact = new Artifact(name, description, category,
						picture, location, difficulty);
				artifacts.add(artifact);
			}
		} catch (JSONException e) {
			logException(FILE_ARTIFACTS);
			e.printStackTrace();
		}
		return artifacts;
	}

	public List<Floor> parseFloors() throws IOException {
		List<Floor> floors = new ArrayList<Floor>();
		String json = readJsonFile(context.getAssets().open(FILE_FLOORS));
		try {
			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				String floorId = obj.getString("name");
				@SuppressWarnings("unused")
				String picture = obj.getString("picture");
				int level = obj.getInt("level");
				boolean isDefault = obj.getInt("isdefault") == 1 ? true : false;
				//TODO: remove hardcoded string for picture
				Floor floor = new Floor(floorId, "images/map.png", level, isDefault);
				floors.add(floor);
			}
		} catch (JSONException e) {
			logException(FILE_FLOORS);
			e.printStackTrace();
		}
		return floors;
	}

	public List<Character> parseCharacters() throws IOException {
		Map<String, List<Mission>> stories = parseStories();
		List<Character> characters = new ArrayList<Character>();

		String json = readJsonFile(context.getAssets().open(FILE_CHARACTERS));

		try {
			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);

				if (obj.getInt("mapper") != 0) {
					continue;
				}

				int id = obj.getInt("id");
				String name = obj.getString("name");
				String category = obj.getString("category");
				String pictureFilename = obj.getString("picture_filename");
				
				Map<Mission, Boolean> map = prepareStoriesMap(stories.get(category));

				Character character = new Character(id, name, category,
						pictureFilename, map);
				characters.add(character);
			}
		} catch (JSONException e) {
			logException(FILE_CHARACTERS);
			e.printStackTrace();
		}

		return characters;
	}

	private Map<Mission, Boolean> prepareStoriesMap(List<Mission> list) {
		Map<Mission, Boolean> map = new HashMap<Mission, Boolean>();
		for(Mission m : list) {
			map.put(new Mission(m.getId(), m.getTitle(), m.getIntro(), m.getSteps()), false);
		}
		return map;
	}

	public Map<String, List<Achievement>> parseAchievements()
			throws IOException {
		Map<String, List<Achievement>> achievements = new HashMap<String, List<Achievement>>();
		map = getCharacterCategoryMap();
		String json = readJsonFile(context.getAssets().open(FILE_ACHIEVEMENTS));
		try {
			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);

				int criteria = obj.getInt("criteria");
				int difficulty = obj.getInt("difficulty");
				int category = obj.getInt("category");

				AchievementCriteria criterium = getCriteria(criteria, category,
						difficulty);

				String name = obj.getString("name");
				String description = obj.getString("description");
				boolean continuous = obj.getInt("continuous") == 0 ? false
						: true;

				Achievement achievement = new Achievement(name, description,
						criterium, continuous);

				String resolvedCategory = map.get(category);
				if (!achievements.containsKey(resolvedCategory)) {
					achievements.put(resolvedCategory,
							new ArrayList<Achievement>());
				}
				achievements.get(resolvedCategory).add(achievement);

			}
		} catch (JSONException e) {
			logException(FILE_ACHIEVEMENTS);
			e.printStackTrace();
		}

		return achievements;
	}

	private AchievementCriteria getCriteria(int criteriaId, int category,
			int difficulty) {
		switch (criteriaId) {
		case FIRST_MISSION_EVER:
			return new FirstMissionEver();
		case CHECK_ARTIFACTS:
			return new CheckArtifacts(difficulty, category == 0 ? null
					: map.get(category));
		case FIRST_STORY_MISSION:
			return new FirstStoryEver();
		case COMPLETED_STORIES_BY_CATEGORY:
			return new CompletedStoryByCategory(category == 0 ? null
					: map.get(category));
		case COMPLETED_MISSION_BY_CATEGORY_DIFFICULTY:
			return new CompletedMissionByCategoryAndDifficulty(
					map.get(category), difficulty);
		}
		return null;
	}

	private Map<String, List<Mission>> parseStories() throws IOException {
		Map<String, List<Mission>> stories = new HashMap<String, List<Mission>>();
		String json = readJsonFile(context.getAssets().open(FILE_STORIES));
		try {
			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				String category = obj.getString("category");

				int id = obj.getInt("id");
				String name = obj.getString("name");
				String intro = obj.getString("intro");

				JSONArray stepsArray = obj.getJSONArray("steps");
				List<MissionStep> steps = new ArrayList<MissionStep>();
				for (int j = 0; j < stepsArray.length(); j++) {
					JSONObject stepObj = stepsArray.getJSONObject(j);
					String description = stepObj.getString("description");
					String artifactName = stepObj.getString("name");

					Artifact artifact = getArtifactByName(artifactName);
					MissionStep step = new MissionStep(artifact, description);
					steps.add(step);
				}

				Mission mission = new Mission(id, name, intro, steps);

				if (!stories.containsKey(category)) {
					stories.put(category, new ArrayList<Mission>());
				}
				stories.get(category).add(mission);

			}
		} catch (JSONException e) {
			logException(FILE_STORIES);
			e.printStackTrace();
		}

		return stories;
	}

	private Artifact getArtifactByName(String artifactName) {
		List<Artifact> allArtifacts = App.getInstance().getCartographer()
				.getArtifacts();
		for (Artifact a : allArtifacts) {
			if (a.getName().equals(artifactName)) {
				return a;
			}
		}
		return null;
	}

	private String readJsonFile(InputStream is) {
		BufferedReader reader = null;
		StringBuilder stringBuilder = new StringBuilder();
		reader = new BufferedReader(new InputStreamReader(is));
		String line;
		try {
			line = reader.readLine();
			while (line != null) {
				stringBuilder.append(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return stringBuilder.toString();
	}

	private SparseArray<String> getCharacterCategoryMap() {
		SparseArray<String> map = new SparseArray<String>();
		for (Character character : App.getInstance().getCharacterManager()
				.getListCharacters()) {
			map.put(character.getId(), character.getCategory());
		}
		map.put(0, "general");
		return map;
	}

	private void logException(String file) {
		System.out.println("Error while parsing file: " + file);
	}
}
