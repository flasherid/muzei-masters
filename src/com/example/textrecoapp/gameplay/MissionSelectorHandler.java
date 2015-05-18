package com.example.textrecoapp.gameplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.example.textrecoapp.App;
import com.example.textrecoapp.CustomDialogClickListener;
import com.example.textrecoapp.ExpandableAdapter;
import com.example.textrecoapp.R;
import com.example.textrecoapp.UiUtils;
import com.example.textrecoapp.characters.Character;
import com.example.textrecoapp.models.LevelMission;
import com.example.textrecoapp.models.Mission;
import com.example.textrecoapp.story.StoryModeActivity;
import com.example.textrecoapp.wizards.AbstractWizardActivity;
import com.example.textrecoapp.wizards.RankedMissionActivity;

/**
 * ranked missions will be called (Level 1, Level 2, ...) story missions will
 * have custom names (The Story of Marko Krale, The Macedonian Bloody Wedding
 * story, ...)
 * 
 * @author ivilievs
 */
public class MissionSelectorHandler {

	private Context context;
	private Character character;
	private ExpandableAdapter adapter;
	private Map<String, List<? extends Object>> data;
	private List<String> headers;

	public MissionSelectorHandler(Context context) {
		this.context = context;
	}

	private void prepareListData() {
		character = App.getInstance().getCharacterManager().getCharacter();

		headers = new ArrayList<String>();
		headers.add(0, context.getString(R.string.ranked_missions));
		headers.add(1, context.getString(R.string.story_missions));

		data = new HashMap<String, List<? extends Object>>();
		data.put(headers.get(0), getRankedMissions());
		data.put(headers.get(1), getStoryMissions());
	}

	private List<? extends Object> getStoryMissions() {
		List<Object> stories = new ArrayList<Object>();
		for (Map.Entry<Mission, Boolean> entry : character.getStoryMissions()
				.entrySet()) {
			stories.add(entry.getKey());
		}
		return stories;
	}

	private List<LevelMission> getRankedMissions() {
		List<LevelMission> levels = new ArrayList<LevelMission>();

		for (int i = 1; i <= character.getLatestUnlockedLevel(); i++) {
			levels.add(new LevelMission("Level " + i, i));
		}

		return levels;
	}

	public ExpandableAdapter getAdapter() {
		if (adapter == null) {
			prepareListData();
			adapter = new ExpandableAdapter(context, headers, data);
		}
		return adapter;
	}

	private OnChildClickListener listener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {

			Object obj = data.get(headers.get(groupPosition))
					.get(childPosition);
			if (obj instanceof LevelMission) {
				LevelMission lm = (LevelMission) obj;
				// check if is the same with the one stored in the character
				resolvePreviousMission(lm);
			} else if (obj instanceof Mission) {
				Mission sm = (Mission) obj;
				navigateToWizard(true, sm);
			}
			return false;
		}
	};

	public OnChildClickListener getChildClickListener() {
		return listener;
	}

	private void navigateToWizard(boolean isStory, Mission mission) {
		Class<? extends AbstractWizardActivity> cls = isStory ? StoryModeActivity.class
				: RankedMissionActivity.class;
		Intent intent = new Intent(context, cls);
		intent.putExtra(AbstractWizardActivity.EXTRAS_MISSION, mission);
		context.startActivity(intent);
	}

	private Mission getRankedMissionForDifficulty(int difficulty) {
		Mission mission = MissionGenerator.getInstance()
				.generateMissionForCharacter(context, character.getCategory(),
						difficulty);
		character.setRankedMission(mission);
		return mission;
	}

	private void resolvePreviousMission(LevelMission lm) {
		Mission mission = null;
		if (character.getRankedMission() == null) {
			mission = getRankedMissionForDifficulty(lm.getDifficulty());
			App.getInstance().getCharacterManager().getCharacter()
					.setRankedMission(mission);
			navigateToWizard(false, mission);
		} else {
			// same difficulty as previous
			if (character.getRankedMission().getSteps().get(0).getArtifact()
					.getDifficulty() == lm.getDifficulty()) {
				// return the old mission
				mission = character.getRankedMission();
				navigateToWizard(false, mission);
			} else {
				//alert user for this condition
				NewMissionAnyway positiveListener = new NewMissionAnyway(
						lm.getDifficulty());
				UiUtils.createSimpleDialog(context,
						context.getString(R.string.dialog_mission_title),
						context.getString(R.string.dialog_mission_message),
						context.getString(R.string.start_anyway),
						context.getString(R.string.cancel), positiveListener,
						UiUtils.getNegativeCustomListener()).show();
			}
		}

	}

	private class NewMissionAnyway implements CustomDialogClickListener {
		private int difficulty;
		
		public NewMissionAnyway(int difficulty) {
			this.difficulty = difficulty;
		}
		
		@Override
		public void onClick() {
			Mission m = getRankedMissionForDifficulty(difficulty);
			App.getInstance().getCharacterManager().getCharacter()
					.setRankedMission(m);
			navigateToWizard(false, m);			
		}
	}
}
