/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import android.os.Environment;
import android.util.Log;

import com.example.textrecoapp.achievements.Achievement;
import com.example.textrecoapp.characters.Character;
import com.example.textrecoapp.gamification.AchievementCriteria;
import com.example.textrecoapp.models.Artifact;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class GSONPersister {

  public static final String ACHIEVEMENTS = "achievements.json";
  public static final String CHARACTER_MISSIONS = "character_missions.json";
  public static final String CARTOGRAPHER_ARTIFACTS = "cartographer_artifacts.json";

  private static final String FOLDER = "/store";
  private static final String LOG_TAG = "GSONPersister";

  private Gson gson;

  public GSONPersister() {
    GsonBuilder gsonBilder = new GsonBuilder();
    gsonBilder.registerTypeAdapter(AchievementCriteria.class, new CriteriaSerializerDeserializer());
    gsonBilder.registerTypeAdapter(Character.class, new CharacterSerializerDeserializer());
    gson = gsonBilder.create();

    File folder = new File(Environment.getExternalStorageDirectory() + FOLDER);
    if (!folder.exists()) {
      folder.mkdir();
    }
  }

  public List<Character> getStoredCharacters() {
    File file = new File(Environment.getExternalStorageDirectory() + FOLDER + "/" + CHARACTER_MISSIONS);
    if (file.exists()) {
      String jsonCharacters = readFile(file);
      if (jsonCharacters == null) {
        return null;
      }
      Type type = new TypeToken<List<Character>>() {}.getType();
      List<Character> characters = gson.fromJson(jsonCharacters, type);
      return characters;
    } else {
      return null;
    }
  }

  public void storeCharacters(List<Character> characters) {
    String jsonCharacters = gson.toJson(characters);
    File file = new File(Environment.getExternalStorageDirectory() + FOLDER + "/" + CHARACTER_MISSIONS);
    writeFile(jsonCharacters, file);
  }

  public Map<String, List<Achievement>> getStoredAchievements() {
    File file = new File(Environment.getExternalStorageDirectory() + FOLDER + "/" + ACHIEVEMENTS);
    if (file.exists()) {
      String jsonAchievements = readFile(file);
      if (jsonAchievements == null) {
        return null;
      }
      Type type = new TypeToken<Map<String, List<Achievement>>>() {}.getType();
      Map<String, List<Achievement>> achievements = gson.fromJson(jsonAchievements, type);
      return achievements;
    } else {
      return null;
    }
  }

  public void storeAchievements(Map<String, List<Achievement>> achievements) {
    String jsonAchievements = gson.toJson(achievements);
    File file = new File(Environment.getExternalStorageDirectory() + FOLDER + "/" + ACHIEVEMENTS);
    writeFile(jsonAchievements, file);
  }

  public List<Artifact> getStoredArtifacts() {
    File file = new File(Environment.getExternalStorageDirectory() + FOLDER + "/" + CARTOGRAPHER_ARTIFACTS);
    if (file.exists()) {
      String jsonArtifacts = readFile(file);
      if (jsonArtifacts == null) {
        return null;
      }
      Type type = new TypeToken<List<Artifact>>() {}.getType();
      List<Artifact> artifacts = gson.fromJson(jsonArtifacts, type);
      return artifacts;
    } else {
      return null;
    }
  }

  public void storeArtifacts(List<Artifact> artifacts) {
    String jsonArtifacts = gson.toJson(artifacts);
    File file = new File(Environment.getExternalStorageDirectory() + FOLDER + "/" + CARTOGRAPHER_ARTIFACTS);
    writeFile(jsonArtifacts, file);
  }

  private String readFile(File file) {
    BufferedReader br = null;
    String response = null;
    try {
      StringBuffer output = new StringBuffer();
      br = new BufferedReader(new FileReader(file));
      String line = "";
      while ((line = br.readLine()) != null) {
        output.append(line + "\n");
      }
      response = output.toString();
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(LOG_TAG, "Failed to read file: " + file.getName());
      return null;
    }
    return response;
  }

  private void writeFile(String json, File file) {
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        Log.e(LOG_TAG, "Failed to create file: " + file.getName());
        return;
      }
    }
    try {
      BufferedWriter output = new BufferedWriter(new FileWriter(file));
      output.write(json);
      output.close();
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(LOG_TAG, "Failed to write to file: " + file.getName());
    }
  }

}
