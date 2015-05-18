package com.example.textrecoapp.models;

import java.io.Serializable;
import java.util.List;

public class Mission implements Serializable {

	private static final long serialVersionUID = 1058784172547470011L;
	
	private int id;
	private String name;
	private String intro;
	private List<MissionStep> steps;
	
	public Mission(int id, String name, String intro, List<MissionStep> steps) {
		this.id = id;
		this.intro = intro;
		this.name = name;
		this.steps = steps;
	}
	public String getIntro() {
		return intro;
	}
	
	public List<MissionStep> getSteps() {
		return steps;
	}
	
	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		Mission m1 = (Mission) o;
		return id == m1.getId();
	}
}
