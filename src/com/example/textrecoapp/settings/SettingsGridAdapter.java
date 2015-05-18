package com.example.textrecoapp.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.textrecoapp.App;
import com.example.textrecoapp.R;
import com.example.textrecoapp.models.Mission;
import com.example.textrecoapp.models.MissionStep;

public class SettingsGridAdapter extends ArrayAdapter<Pair<String, String>> {

	public static final String LABEL = "label";
	public static final String BUTTON_RANKED = "button_ranked";
	public static final String BUTTON_STORY = "button_story";

	public SettingsGridAdapter(Context context, int resource,
			List<Pair<String, String>> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Pair<String, String> item = getItem(position);
		String type = item.first;
		if (type.equals(LABEL)) {
			TextView tv = new TextView(getContext());
			tv.setTextColor(Color.WHITE);
			tv.setGravity(Gravity.CENTER_VERTICAL);
			tv.setTextSize(20);
			tv.setPadding(10, 0, 0, 0);
			tv.setText(item.second);
			return tv;
		} else if (type.equals(BUTTON_RANKED)) {
			Button button = new Button(getContext());
			button.setText(getContext().getString(R.string.clear_ranked));
			button.setOnClickListener(resetRankedListener);
			button.setTextColor(getContext().getResources().getColor(
					android.R.color.holo_orange_light));
			button.setTag(item.second);
			return button;
		} else if (type.equals(BUTTON_STORY)) {
			Button button = new Button(getContext());
			button.setText(getContext().getString(R.string.clear_story));
			button.setOnClickListener(resetStoriesListener);
			button.setTextColor(getContext().getResources().getColor(
					android.R.color.holo_orange_light));
			button.setTag(item.second);
			return button;
		}
		return null;
	}

	private View.OnClickListener resetStoriesListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String name = String.valueOf(v.getTag());
			App.getInstance().getCharacterManager().changeCharacter(name);
			Map<Mission, Boolean> oldStoryMissions = App.getInstance()
					.getCharacterManager().getCharacter().getStoryMissions();
			Map<Mission, Boolean> newStoryMissions = new HashMap<Mission, Boolean>();

			for (Entry<Mission, Boolean> entry : oldStoryMissions.entrySet()) {
				Mission m = entry.getKey();
				for (MissionStep step : m.getSteps()) {
					step.setUnlocked(false);
				}
				newStoryMissions.put(m, false);
			}

			App.getInstance().getCharacterManager().getCharacter()
					.setStoryMissions(newStoryMissions);

			Toast.makeText(getContext(),
					getContext().getString(R.string.reset_ranked),
					Toast.LENGTH_SHORT).show();
		}
	};

	private View.OnClickListener resetRankedListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String name = String.valueOf(v.getTag());

			App.getInstance().getCharacterManager().changeCharacter(name);
			App.getInstance().getCharacterManager().getCharacter()
					.setRankedMission(null);

			Toast.makeText(getContext(),
					getContext().getString(R.string.reset_ranked),
					Toast.LENGTH_SHORT).show();
		}
	};

}
