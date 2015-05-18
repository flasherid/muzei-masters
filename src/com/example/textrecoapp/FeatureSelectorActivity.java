/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.example.textrecoapp.achievements.AchievementsActivity;
import com.example.textrecoapp.characters.BrowseArtifacts;
import com.example.textrecoapp.gamification.IPlayerNameChanged;
import com.example.textrecoapp.gamification.RankHandler;
import com.example.textrecoapp.models.Artifact;
import com.example.textrecoapp.persistence.PersistedActivity;
import com.example.textrecoapp.settings.SettingsActivity;
import com.example.textrecoapp.wizards.AbstractWizardActivity;

@SuppressLint("NewApi")
public class FeatureSelectorActivity extends PersistedActivity implements
		IPlayerNameChanged {

	public static final int INTENT_BROWSER_CODE = 7971;
	public static final String EXTRA_BROWSE_ARTIFACT = "browse_artifact";

	private static final String FEATURE_ACHIEVEMENTS = "achievements";
	private static final String FEATURE_GAME = "game";
	private static final String FEATURE_SETTINGS = "settings";

	private View characterBackground;
	private LinearLayout characterContainer;
	private ImageView rankImage;
	private TextView playerName;
	private View browseArtifacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.character_layout);

		initUI();
		loadCharacters();
	}

	private View.OnClickListener characterListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			Intent intent = null;
			if (FEATURE_GAME.equals(v.getTag())) {
				intent = new Intent(FeatureSelectorActivity.this,
						CharacterSelectorActivity.class);
			} else if (FEATURE_ACHIEVEMENTS.equals(v.getTag())) {
				intent = new Intent(FeatureSelectorActivity.this,
						AchievementsActivity.class);
			} else if (FEATURE_SETTINGS.equals(v.getTag())) {
				intent = new Intent(FeatureSelectorActivity.this,
						SettingsActivity.class);
			}
			startActivity(intent);
		}
	};

	private void initUI() {
		characterBackground = findViewById(R.id.master_background);
		characterBackground
				.setBackgroundResource(R.drawable.muzei_background_main);
		characterContainer = (LinearLayout) findViewById(R.id.character_container);

		final RankHandler rh = App.getInstance().getRankHandler();

		rankImage = (ImageView) findViewById(R.id.rank_image);
		rankImage.setImageResource(rh.getImage());
		rankImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UiUtils.showNinjaDialog(FeatureSelectorActivity.this).show();
			}
		});

		playerName = (TextView) findViewById(R.id.player_name);
		playerName.setText(rh.getPlayer());
		playerName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UiUtils.showPlayerNameDialog(FeatureSelectorActivity.this,
						FeatureSelectorActivity.this).show();
			}
		});
		browseArtifacts = findViewById(R.id.browse_artifact);
		browseArtifacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FeatureSelectorActivity.this,
						OCRActivity.class);
				FeatureSelectorActivity.this.startActivityForResult(intent,
						INTENT_BROWSER_CODE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String scannedText = data.getExtras().getString(
					AbstractWizardActivity.EXTRAS_MISSION_STATUS);

			Artifact artifact = App.getInstance().getCartographer()
					.findArtifact(scannedText);
			if (artifact == null) {
				Toast.makeText(
						FeatureSelectorActivity.this,
						FeatureSelectorActivity.this
								.getString(R.string.no_such_artifact),
						Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = new Intent(FeatureSelectorActivity.this,
						BrowseArtifacts.class);
				intent.putExtra(EXTRA_BROWSE_ARTIFACT, artifact);
				startActivity(intent);
			}
		}
	}

	private void loadCharacters() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		ImageView achievements = new ImageView(this);
		achievements.setTag(FEATURE_ACHIEVEMENTS);
		achievements.setImageDrawable(UiUtils.getStateDrawableForId(this,
				"achievements", true));
		achievements.setLayoutParams(params);
		achievements.setOnClickListener(characterListener);
		characterContainer.addView(achievements);

		ImageView muzei = new ImageView(this);
		muzei.setTag(FEATURE_GAME);
		muzei.setImageDrawable(UiUtils.getStateDrawableForId(this, "middle",
				true));
		muzei.setLayoutParams(params);
		muzei.setOnClickListener(characterListener);
		characterContainer.addView(muzei);

		ImageView statue = new ImageView(this);
		statue.setTag(FEATURE_SETTINGS);
		statue.setImageDrawable(UiUtils.getStateDrawableForId(this, "statue",
				true));
		statue.setLayoutParams(params);
		statue.setOnClickListener(characterListener);
		characterContainer.addView(statue);
	}

	@Override
	public void setNewName(String name) {
		App.getInstance().getRankHandler().setPlayer(name);
		playerName.setText(name);
	}

}
