/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.example.textrecoapp.characters.Character;
import com.example.textrecoapp.gameplay.CharacterMissionHandler;
import com.example.textrecoapp.map.Cartographer;
import com.example.textrecoapp.map.MapActivity;

public class CharacterSelectorActivity extends Activity {

	private ViewGroup leftPanel;
	private ViewGroup rightPanel;
	private ImageView cartographerImageView;
	private View characterBackground;
	private LinearLayout characterContainer;

	private int screenCenterX;
	private View selectedView;
	private int selectedViewLeft;
	
	private View rankedLayout;

	private CharacterMissionHandler missionHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.panel_layout);

		initUI();
		loadCharacters();
		loadHandlers();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (selectedView != null) {
			if (selectedView.getTag().equals(Cartographer.CARTOGRAPHER)) {
				goBackToNormal();
			} else {
				missionHandler.updatePanels();
			}
		}
	}

	private void loadHandlers() {
		missionHandler = new CharacterMissionHandler(this);
	}

	private void initUI() {
		rankedLayout = findViewById(R.id.ranking_layout);
		rankedLayout.setVisibility(View.GONE);
		// views
		characterBackground = findViewById(R.id.character_background);
		characterBackground
				.setBackgroundResource(R.drawable.character_background);
		characterContainer = (LinearLayout) findViewById(R.id.character_container);

		leftPanel = (ViewGroup) findViewById(R.id.left_info_panel);
		rightPanel = (ViewGroup) findViewById(R.id.right_info_panel);

		// values
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		screenCenterX = metrics.widthPixels / 2;
	}

	private View.OnClickListener characterClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			// stop further selecting of the same character
			if (selectedView != null) {
				return;
			}

			// fade out other views
			for (int i = 0; i < characterContainer.getChildCount(); i++) {
				final View childView = characterContainer.getChildAt(i);
				if (!v.equals(childView)) {
					AnimationUtils.fadeOutView(childView);
				}
			}

			selectedView = v;
			int left = v.getLeft() + v.getWidth() / 2;
			selectedViewLeft = left;

			if (v.getTag().equals(Cartographer.CARTOGRAPHER)) {
				Intent intent = new Intent(CharacterSelectorActivity.this,
						MapActivity.class);
				startActivity(intent);
			} else {

				v.animate().translationXBy(screenCenterX - left);

				AnimationUtils.fadeInView(leftPanel);
				AnimationUtils.fadeInView(rightPanel);

				String characterName = String.valueOf(v.getTag());
				missionHandler.handleMissionForCharacter(characterName,
						leftPanel, rightPanel);
			}
		}

	};

	private void loadCharacters() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		cartographerImageView = new ImageView(this);
		cartographerImageView.setLayoutParams(params);
		cartographerImageView.setTag(Cartographer.CARTOGRAPHER);
		cartographerImageView.setImageDrawable(UiUtils.getStateDrawableForId(
				this, Cartographer.CARTOGRAPHER, false));
		cartographerImageView.setOnClickListener(characterClickListener);
		characterContainer.addView(cartographerImageView);

		for (Character character : App.getInstance().getCharacterManager()
				.getListCharacters()) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(params);
			iv.setTag(character.getName());
			iv.setImageDrawable(UiUtils.getStateDrawableForId(this,
					character.getPictureFilename(), false));
			iv.setOnClickListener(characterClickListener);
			characterContainer.addView(iv);
		}
	}

	private void goBackToNormal() {
		for (int i = 0; i < characterContainer.getChildCount(); i++) {
			final View childView = characterContainer.getChildAt(i);
			if (!childView.equals(selectedView)) {
				AnimationUtils.fadeInView(childView);
			}
		}

		if (!selectedView.getTag().equals(Cartographer.CARTOGRAPHER)) {
			selectedView.animate().translationXBy(
					selectedViewLeft - screenCenterX);

			AnimationUtils.fadeOutView(leftPanel);
			AnimationUtils.fadeOutView(rightPanel);
		}

		selectedView = null;
	}

	@Override
	public void onBackPressed() {
		if (selectedView == null) {
			super.onBackPressed();
		} else {
			goBackToNormal();
		}
	}

}
