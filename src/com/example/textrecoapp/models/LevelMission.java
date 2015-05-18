package com.example.textrecoapp.models;

public class LevelMission {

	private String name;
	private int difficulty;
	
	public LevelMission(String name, int difficulty) {
		this.name = name;
		this.difficulty = difficulty;
	}

	public String getName() {
		return name;
	}

	public int getDifficulty() {
		return difficulty;
	}
}
