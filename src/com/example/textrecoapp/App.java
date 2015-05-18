/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.app.Application;

import com.example.textrecoapp.achievements.Achievement;
import com.example.textrecoapp.ar.TrainSetHandler;
import com.example.textrecoapp.characters.Character;
import com.example.textrecoapp.characters.CharacterManager;
import com.example.textrecoapp.gamification.AchievementChecker;
import com.example.textrecoapp.gamification.RankHandler;
import com.example.textrecoapp.init.AssetsInitializer;
import com.example.textrecoapp.map.Cartographer;
import com.example.textrecoapp.map.Floor;
import com.example.textrecoapp.models.Artifact;
import com.example.textrecoapp.persistence.GSONPersister;
import com.googlecode.tesseract.android.TessBaseAPI;

public class App extends Application {

	private static App instance;
	private TessBaseAPI ocrAPI;
	private CharacterManager characterManager;
	private Cartographer cartographer;
	private GSONPersister persister;
	private AchievementChecker achievementChecker;
	private RankHandler rankHandler;
	
	private Map<String, List<Achievement>> achievements;
	
	public static final String LANG = "mkd";

	public static App getInstance() {
		return instance;
	}

	public App() {
		instance = this;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		TrainSetHandler tsh = new TrainSetHandler(getApplicationContext(), LANG);
		tsh.initDirectory();

		new Runnable() {

			@Override
			public void run() {
				ocrAPI = new TessBaseAPI();
				ocrAPI.init(TrainSetHandler.DATA_PATH, LANG);
			}
		}.run();

		persister = new GSONPersister();

		try {
			initApp();
		} catch (IOException e) {
			System.out.println("Failed to read prebundled resources");
			e.printStackTrace();
		}
	}

	private void initApp() throws IOException {
		// artifacts
		AssetsInitializer initializer = new AssetsInitializer(instance);
		
		List<Floor> floors = initializer.parseFloors(); 
		List<Artifact> artifacts = persister.getStoredArtifacts();
		if (artifacts == null) {
			artifacts = initializer.parseArtifacts();
		}
		cartographer = new Cartographer(floors, artifacts);
		
		// characters
		List<Character> characters = persister.getStoredCharacters();
		if (characters == null) {
			characters = initializer.parseCharacters();
		}
		characterManager = new CharacterManager(characters);

		achievements = persister.getStoredAchievements();
		if (achievements == null) {
			achievements = initializer.parseAchievements();
		}

		achievementChecker = new AchievementChecker();
		
		// gamification
		rankHandler = new RankHandler();
	}

	public TessBaseAPI getOCR_API() {
		return ocrAPI;
	}

	public CharacterManager getCharacterManager() {
		return characterManager;
	}

	public Cartographer getCartographer() {
		return cartographer;
	}

	public GSONPersister getPersister() {
		return persister;
	}

	public AchievementChecker getAchievementChecker() {
		return achievementChecker;
	}
	
	public void setAchievements(Map<String, List<Achievement>> achievements) {
		this.achievements = achievements;
	}

	public Map<String, List<Achievement>> getAchievements() {
		return achievements;
	}
	
	public RankHandler getRankHandler() {
		return rankHandler;
	}
}
