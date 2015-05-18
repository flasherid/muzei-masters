/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.persistence;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.example.textrecoapp.App;
import com.example.textrecoapp.characters.Character;

public class PersistedActivity extends Activity {

  @Override
  protected void onStop() {
    super.onStop();

    new Runnable() {
		
		@Override
		public void run() {
			App.getInstance().getPersister().storeAchievements(App.getInstance().getAchievements());
			App.getInstance().getPersister().storeArtifacts(App.getInstance().getCartographer().getArtifacts());
			storeCharacters();
		}
	}.run();
  }

  private void storeCharacters() {
    List<Character> characters = new ArrayList<Character>();
    
    for (Character c : App.getInstance().getCharacterManager().getListCharacters()) {
      App.getInstance().getCharacterManager().changeCharacter(c.getName());
      characters.add(App.getInstance().getCharacterManager().getCharacter());
    }
    
    App.getInstance().getPersister().storeCharacters(characters);
  }

}
