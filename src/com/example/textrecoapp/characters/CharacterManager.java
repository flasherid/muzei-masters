/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.characters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterManager {

	private Map<String, Character> characters;
	private String characterName;
	private List<Character> listCharacters;

	public CharacterManager(List<Character> chars) {
		listCharacters = chars;
		characters = new HashMap<String, Character>();
		for (Character c : chars) {
			characters.put(c.getName(), c);
		}
	}

	public void changeCharacter(String name) {
		this.characterName = name;
	}

	public Character getCharacter() {
		return characters.get(characterName);
	}

	public Map<String, Character> getMapCharacters() {
		return characters;
	}

	public List<Character> getListCharacters() {
		return listCharacters;
	}

}
