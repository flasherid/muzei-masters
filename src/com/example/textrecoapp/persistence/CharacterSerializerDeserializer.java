package com.example.textrecoapp.persistence;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.example.textrecoapp.characters.Character;
import com.example.textrecoapp.map.Location;
import com.example.textrecoapp.models.Artifact;
import com.example.textrecoapp.models.Mission;
import com.example.textrecoapp.models.MissionStep;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CharacterSerializerDeserializer implements
		JsonSerializer<Character>, JsonDeserializer<Character> {

	@Override
	public JsonElement serialize(Character src, Type typeOf,
			JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add("category", new JsonPrimitive(src.getCategory()));
		result.add("name", new JsonPrimitive(src.getName()));
		result.add("pictureFilename", new JsonPrimitive(src.getPictureFilename()));
		result.add("id", new JsonPrimitive(src.getId()));
		result.add("latestUnlockedLevel", new JsonPrimitive(src.getLatestUnlockedLevel()));

		JsonArray storyMissions = new JsonArray();

		for (Entry<Mission, Boolean> entry : src.getStoryMissions().entrySet()) {
			Mission m = entry.getKey();
			JsonObject storyMissionObj = new JsonObject();
			storyMissionObj.add("id", new JsonPrimitive(m.getId()));
			storyMissionObj.add("name", new JsonPrimitive(m.getTitle()));
			storyMissionObj.add("intro", new JsonPrimitive(m.getIntro()));

			JsonArray jsonSteps = new JsonArray();
			for (MissionStep step : m.getSteps()) {
				jsonSteps.add(serializeStoryStep(step));
			}
			storyMissionObj.add("steps", jsonSteps);

			JsonObject mapStoryEntry = new JsonObject();
			mapStoryEntry.add("key", storyMissionObj);
			mapStoryEntry.add("value", new JsonPrimitive(entry.getValue()));

			storyMissions.add(mapStoryEntry);
		}

		result.add("storyMissions", storyMissions);

		return result;
	}

	private JsonElement serializeStoryStep(MissionStep step) {
		JsonObject jsonStep = new JsonObject();
		jsonStep.addProperty("description", step.getDescription());
		jsonStep.add("artifact", serializeArtifact(step.getArtifact()));
		return jsonStep;
	}

	private JsonObject serializeArtifact(Artifact artifact) {
		JsonObject jsonArtifact = new JsonObject();
		jsonArtifact.add("category", new JsonPrimitive(artifact.getCategory()));
		jsonArtifact.add("description", new JsonPrimitive(artifact.getDescription()));
		jsonArtifact.add("name", new JsonPrimitive(artifact.getName()));
		jsonArtifact.add("pictureFilename", new JsonPrimitive(artifact.getPictureFilename()));
		jsonArtifact.add("difficulty", new JsonPrimitive(artifact.getDifficulty()));
		jsonArtifact.add("unlockedArtifact", new JsonPrimitive(artifact.isArtefactUnlocked()));
		jsonArtifact.add("location", serializeLocation(artifact.getLocation()));
		return jsonArtifact;
	}

	private JsonObject serializeLocation(Location location) {
		JsonObject jsonLocation = new JsonObject();
		jsonLocation.add("floorId", new JsonPrimitive(location.getFloorId()));
		jsonLocation.add("xPercentage",  new JsonPrimitive(location.getxPercentage()));
		jsonLocation.add("yPercentage", new JsonPrimitive(location.getyPercentage()));
		return jsonLocation;
	}

	@Override
	public Character deserialize(JsonElement json, Type typeOf,
			JsonDeserializationContext context) throws JsonParseException {

		JsonObject characterJson = json.getAsJsonObject();
		String category = characterJson.get("category").getAsString();
		String name = characterJson.get("name").getAsString();
		String pictureFilename = characterJson.get("pictureFilename").getAsString();
		int id = characterJson.get("id").getAsInt();
		int latestUnlockedLevel = characterJson.get("latestUnlockedLevel").getAsInt();

		Character character = new Character(id, name, category,
				pictureFilename,
				deserializeStoryMissions(characterJson.get("storyMissions")));
		character.setLatestUnlockedLevel(latestUnlockedLevel);
		return character;
	}

	private Map<Mission, Boolean> deserializeStoryMissions(
			JsonElement jsonElement) {
		JsonArray storiesArray = jsonElement.getAsJsonArray();
		Map<Mission, Boolean> map = new HashMap<Mission, Boolean>();
		
		for (int i = 0; i < storiesArray.size(); i++) {
			JsonObject storyJson = storiesArray.get(i).getAsJsonObject();
			Mission m = deserializeMission(storyJson.get("key"));
			map.put(m, storyJson.get("value").getAsBoolean());
		}
		
		return map;
	}

	private Mission deserializeMission(JsonElement jsonElement) {
		JsonObject missionJson = jsonElement.getAsJsonObject();
		int id = missionJson.get("id").getAsInt();
		String name = missionJson.get("name").getAsString();
		String intro = missionJson.get("intro").getAsString();
		Mission mission = new Mission(id, name, intro,
				deserializeMissionSteps(missionJson.get("steps")));
		return mission;
	}

	private List<MissionStep> deserializeMissionSteps(JsonElement jsonElement) {
		JsonArray stepsArray = jsonElement.getAsJsonArray();
		List<MissionStep> steps = new ArrayList<MissionStep>();
		for (int i = 0; i < stepsArray.size(); i++) {
			JsonObject stepJson = stepsArray.get(i).getAsJsonObject();
			String description = stepJson.get("description").getAsString();
			Artifact artifact = deserializeArtifact(stepJson.get("artifact"));
			MissionStep step = new MissionStep(artifact, description);
			steps.add(step);
		}
		return steps;
	}

	private Artifact deserializeArtifact(JsonElement jsonElement) {
		JsonObject artifactJson = jsonElement.getAsJsonObject();
		String name = artifactJson.get("name").getAsString();
		String description = artifactJson.get("description").getAsString();
		String category = artifactJson.get("category").getAsString();
		String pictureFilename = artifactJson.get("pictureFilename").getAsString();
		int difficulty = artifactJson.get("difficulty").getAsInt();
		Location location = deserializeLocation(artifactJson.get("location"));
		boolean unlockedArtifact = artifactJson.get("unlockedArtifact").getAsBoolean();
				
		Artifact artifact = new Artifact(name, description, category, pictureFilename, location, difficulty);
		if(unlockedArtifact) {
			artifact.unlockArtefact();
		}
		return artifact;
	}

	private Location deserializeLocation(JsonElement jsonElement) {
		JsonObject locationJson = jsonElement.getAsJsonObject();
		String floorId = locationJson.get("floorId").getAsString();
		float xPercentage = locationJson.get("xPercentage").getAsFloat();
		float yPercentage = locationJson.get("yPercentage").getAsFloat();
		Location location = new Location(xPercentage, yPercentage, floorId);
		return location;
	}
}
