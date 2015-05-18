/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.gameplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.textrecoapp.App;
import com.example.textrecoapp.R;
import com.example.textrecoapp.characters.Character;

public class CharacterMissionHandler {

	private Context context;
	private LayoutInflater layoutInflater;

	// views
	private TextView characterTitle;
	private TextView characterDescription;
	private ExpandableListView listView;

	public CharacterMissionHandler(Context context) {
		this.context = context;
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void handleMissionForCharacter(String characterName,
			ViewGroup leftPanel, ViewGroup rightPanel) {
		App.getInstance().getCharacterManager().changeCharacter(characterName);

		// inflate layouts
		inflateLeftPanel(leftPanel);
		inflateRightPanel(rightPanel);

		updatePanels();
	}

	public void updatePanels() {
		Character character = App.getInstance().getCharacterManager()
				.getCharacter();

		// populate always known data
		characterTitle.setText(character.getName());
		String templateString = context.getResources().getString(
				R.string.character_description_template);
		String charDescription = String.format(templateString,
				character.getCategory());
		characterDescription.setText(charDescription);
		
		MissionSelectorHandler listInitializer = new MissionSelectorHandler(context);
		listView.setAdapter(listInitializer.getAdapter());
		listView.setOnChildClickListener(listInitializer.getChildClickListener());
	}

	private void inflateLeftPanel(ViewGroup leftPanel) {
		if (characterTitle == null) {
			View leftPanelContent = layoutInflater.inflate(
					R.layout.left_panel_layout, leftPanel);

			characterTitle = (TextView) leftPanelContent
					.findViewById(R.id.character_title);
			characterDescription = (TextView) leftPanelContent
					.findViewById(R.id.character_description);
		}
	}

	private void inflateRightPanel(ViewGroup rightPanel) {
		if (listView == null) {
			View rightPanelContent = layoutInflater.inflate(
					R.layout.right_panel_layout, rightPanel);
			listView = (ExpandableListView) rightPanelContent
					.findViewById(R.id.exp_list);
		}
	}

}
