package com.example.textrecoapp.story;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.textrecoapp.App;
import com.example.textrecoapp.R;
import com.example.textrecoapp.UiUtils;
import com.example.textrecoapp.achievements.Achievement;
import com.example.textrecoapp.achievements.AchievementsActivity;
import com.example.textrecoapp.models.Artifact;
import com.example.textrecoapp.models.Mission;
import com.example.textrecoapp.wizards.AbstractWizardActivity;
import com.example.textrecoapp.wizards.AchievementListAdapter;

public class FinalStepView implements PageView {

	private AbstractWizardActivity wizard;
	private View view;
	private Button playAgain;
	private TextView storyNote;

	private ListView listView;

	public FinalStepView(AbstractWizardActivity context) {
		this.wizard = context;
	}

	private View.OnClickListener btnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Toast.makeText(wizard, "All story content is erased",
					Toast.LENGTH_SHORT).show();

			//TODO: uncomment or reimplement
			// if(context instanceof StoryModeActivity) {
			// StoryModeActivity missionContainer = (StoryModeActivity) context;
			// //get the mission and set all steps to locked again
			// }

		}
	};

	private View prepareView() {
		View v = LayoutInflater.from(wizard).inflate(R.layout.final_step, null);
		playAgain = (Button) v.findViewById(R.id.final_button);
		playAgain.setOnClickListener(btnListener);
		storyNote = (TextView) v.findViewById(R.id.story_note);
		listView = (ListView) v.findViewById(R.id.achievement_list);
		return v;
	}

	private void unlockMission() {
		Mission m = wizard.getMission();
		if (m.getId() == 0) {
			// ranked: set mission to null
			App.getInstance().getCharacterManager().getCharacter()
					.setRankedMission(null);
			// handle latest unlocked level of difficulty
			int difficulty = m.getSteps().get(0).getArtifact().getDifficulty();
			if (App.getInstance().getCharacterManager().getCharacter()
					.getLatestUnlockedLevel() == difficulty) {
				App.getInstance().getCharacterManager().getCharacter()
						.unlockNewLevel();
			}

		} else {
			// story: set boolean to true
			Map<Mission, Boolean> oldStories = App.getInstance()
					.getCharacterManager().getCharacter().getStoryMissions();
			Map<Mission, Boolean> newStories = new HashMap<Mission, Boolean>();
			for (Entry<Mission, Boolean> entry : oldStories.entrySet()) {
				if (entry.getKey().getId() == m.getId()) {
					newStories.put(m, true);
				} else {
					newStories.put(entry.getKey(), entry.getValue());
				}
			}
			App.getInstance().getCharacterManager().getCharacter()
					.setStoryMissions(newStories);
		}
	}

	private void populateViews() {
		// check if story
		Mission mission = (Mission) wizard.getMission();
		if (mission.getId() == 0) {
			storyNote.setVisibility(View.GONE);
		}

		// play again button text
		String buttonText = "Play Again\n" + mission.getTitle();
		playAgain.setText(buttonText);

		// populate list
		List<Achievement> newAchievements = App.getInstance()
				.getAchievementChecker().checkAchievements(mission);
		AchievementListAdapter adapter = new AchievementListAdapter(wizard, 0,
				newAchievements);
		listView.setAdapter(adapter);
		listView.addFooterView(prepareFooterView());
		
		if(App.getInstance().getRankHandler().refreshRank()) {
			UiUtils.showNinjaDialog(wizard);
		}
	}

	private View prepareFooterView() {
		View view = LayoutInflater.from(wizard).inflate(R.layout.footer, null);
		Button btn = (Button) view.findViewById(R.id.footer_button);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(wizard, AchievementsActivity.class);
				wizard.startActivity(intent);
			}
		});
		return view;
	}

	@Override
	public View getView() {
		if (view == null) {
			view = prepareView();
		}
		return view;
	}

	@Override
	public void handleResult(String result) {
		populateViews();
		unlockMission();
	}

	@Override
	public Artifact getArtifact() {
		return null;
	}

}
