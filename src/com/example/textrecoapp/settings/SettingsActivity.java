package com.example.textrecoapp.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.textrecoapp.App;
import com.example.textrecoapp.R;
import com.example.textrecoapp.achievements.Achievement;
import com.example.textrecoapp.characters.Character;
import com.example.textrecoapp.models.Artifact;

public class SettingsActivity extends Activity {

	private GridView grid;
	private Button clearAchievements;
	private Button checkForUpdate;
	private Button lockArtifacts;
	private Button unlockArtifacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		initUi();
		populateGrid();
	}

	private void initUi() {
		grid = (GridView) findViewById(R.id.settings_grid);
		clearAchievements = (Button) findViewById(R.id.btn_achievements);
		checkForUpdate = (Button) findViewById(R.id.btn_check_update);
		lockArtifacts = (Button) findViewById(R.id.btn_lock_artifacts);
		unlockArtifacts = (Button) findViewById(R.id.btn_unlock_artifacts);

		clearAchievements.setOnClickListener(clearAchievementsListener);
		checkForUpdate.setOnClickListener(checkForUpdateListener);
		lockArtifacts.setOnClickListener(lockArtifactsListener);
		unlockArtifacts.setOnClickListener(unlockARtifactsListener);
	}

	private void populateGrid() {
		List<Character> characters = App.getInstance().getCharacterManager()
				.getListCharacters();
		List<Pair<String, String>> gridData = new ArrayList<Pair<String, String>>();

		for (Character c : characters) {
			gridData.add(new Pair<String, String>(SettingsGridAdapter.LABEL, c
					.getName() + " (" + c.getCategory() + ")"));
			gridData.add(new Pair<String, String>(
					SettingsGridAdapter.BUTTON_RANKED, c.getName()));
			gridData.add(new Pair<String, String>(
					SettingsGridAdapter.BUTTON_STORY, c.getName()));
		}

		SettingsGridAdapter adapter = new SettingsGridAdapter(this, 0, gridData);
		grid.setAdapter(adapter);
	}

	private View.OnClickListener clearAchievementsListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Map<String, List<Achievement>> oldAchievements = App.getInstance()
					.getAchievements();
			Map<String, List<Achievement>> newAchievements = new HashMap<String, List<Achievement>>();

			for (Entry<String, List<Achievement>> entry : oldAchievements
					.entrySet()) {
				newAchievements.put(entry.getKey(),
						new ArrayList<Achievement>());

				for (Achievement a : entry.getValue()) {
					a.lockAchievement();
					newAchievements.get(entry.getKey()).add(a);
				}
			}

			App.getInstance().setAchievements(newAchievements);

			Toast.makeText(SettingsActivity.this,
					getString(R.string.achievements_clicked),
					Toast.LENGTH_SHORT).show();
		}
	};

	private View.OnClickListener checkForUpdateListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			// TODO: implement this after offline resources have been
			// implemented

			Toast.makeText(SettingsActivity.this,
					getString(R.string.update_checking), Toast.LENGTH_SHORT)
					.show();
		}
	};

	private View.OnClickListener lockArtifactsListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			List<Artifact> oldArtifacts = App.getInstance().getCartographer()
					.getArtifacts();
			List<Artifact> newArtifacts = new ArrayList<Artifact>();

			for (Artifact old : oldArtifacts) {
				old.lockArtifact();
				newArtifacts.add(old);
			}

			App.getInstance().getCartographer().setArtifacts(newArtifacts);

			Toast.makeText(SettingsActivity.this,
					getString(R.string.artifacts_locked), Toast.LENGTH_SHORT)
					.show();
		}
	};

	private View.OnClickListener unlockARtifactsListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			List<Artifact> oldArtifacts = App.getInstance().getCartographer()
					.getArtifacts();
			List<Artifact> newArtifacts = new ArrayList<Artifact>();

			for (Artifact old : oldArtifacts) {
				old.unlockArtefact();
				newArtifacts.add(old);
			}

			App.getInstance().getCartographer().setArtifacts(newArtifacts);

			Toast.makeText(SettingsActivity.this,
					getString(R.string.artifacts_locked), Toast.LENGTH_SHORT)
					.show();
			
			Toast.makeText(SettingsActivity.this,
					getString(R.string.artifacts_unlocked), Toast.LENGTH_SHORT)
					.show();
		}
	};
}
