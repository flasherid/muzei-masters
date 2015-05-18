package com.example.textrecoapp.gamification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.example.textrecoapp.App;
import com.example.textrecoapp.R;
import com.example.textrecoapp.achievements.Achievement;

public class RankHandler {

	private Map<String, String> rankings;
	private String currentRank;

	private static final String SP_PREFS = "sp";
	private static final String SP_PLAYER = "sp_player";
	private static final String SP_IMAGE = "sp_image";
	private static final String SP_IMAGE_BIG = "sp_image_big";
	private static final String SP_COLOR = "sp_color";
	private static final String SP_DESCRIPTION = "sp_description";

	private static final int TYPE_STRING = 1;
	private static final int TYPE_COLOR = 2;
	private static final int TYPE_DRAWABLE = 3;

	private static final String RES_STRING = "string";
	private static final String RES_COLOR = "color";
	private static final String RES_DRAWABLE = "drawable";

	private SharedPreferences prefs;

	private String player;
	private int image;
	private int imageBig;
	private int color;
	private int description;

	public RankHandler() {
		initMap();
		// handle shared preferences
		prefs = App.getInstance().getSharedPreferences(SP_PREFS,
				Context.MODE_PRIVATE);

		player = prefs.getString(SP_PLAYER, "player");
		image = prefs.getInt(SP_IMAGE, R.drawable.white_belt);
		imageBig = prefs.getInt(SP_IMAGE_BIG, R.drawable.white_belt_big);
		color = prefs.getInt(SP_COLOR, R.color.white_belt);
		description = prefs.getInt(SP_DESCRIPTION, R.string.white_belt);
	}

	private void initMap() {
		rankings = new HashMap<String, String>();
		rankings.put("0.0", "white_belt");
		rankings.put("0.1", "yellow_belt");
		rankings.put("0.2", "orange_belt");
		rankings.put("0.3", "green_belt");
		rankings.put("0.4", "blue_belt");
		rankings.put("0.5", "purple_belt");
		rankings.put("0.6", "red_belt");
		rankings.put("0.7", "brown_belt");
		rankings.put("1.0", "black_belt");
	}

	public boolean refreshRank() {
		int totalAchievements = getTotalNumOfAchievements();
		int unlockedSoFar = getUnlockedAchievements();

		double result = (double) unlockedSoFar / (double) totalAchievements;
		String res = String.valueOf(result);
		res = res.substring(0, 3);

		if (!currentRank.equals(rankings.get(res))) {
			currentRank = rankings.get(res);

			image = getId(currentRank, TYPE_DRAWABLE, false);
			imageBig = getId(currentRank, TYPE_DRAWABLE, true);
			color = getId(currentRank, TYPE_COLOR, false);
			description = getId(currentRank, TYPE_STRING, false);

			Editor editor = prefs.edit();
			editor.putInt(SP_IMAGE, image);
			editor.putInt(SP_IMAGE_BIG, imageBig);
			editor.putInt(SP_COLOR, color);
			editor.putInt(SP_DESCRIPTION, description);
			editor.commit();

			return true;
		}

		return false;
	}

	private int getId(String id, int type, boolean isBigPicture) {
		Context context = App.getInstance();
		switch (type) {
		case TYPE_COLOR:
			return context.getResources().getIdentifier(id, RES_COLOR,
					context.getPackageName());

		case TYPE_STRING:
			return context.getResources().getIdentifier(id, RES_STRING,
					context.getPackageName());

		case TYPE_DRAWABLE:
			String resultId = isBigPicture ? (id + "_big") : id;
			return context.getResources().getIdentifier(resultId, RES_DRAWABLE,
					context.getPackageName());
		}
		return 0;
	}

	private int getTotalNumOfAchievements() {
		int count = 0;
		for (Map.Entry<String, List<Achievement>> list : App.getInstance()
				.getAchievements().entrySet()) {
			count += list.getValue().size();
		}
		return count;
	}

	private int getUnlockedAchievements() {
		int count = 0;
		for (Map.Entry<String, List<Achievement>> list : App.getInstance()
				.getAchievements().entrySet()) {
			for (Achievement a : list.getValue()) {
				if (a.isUnlocked()) {
					count++;
				}
			}
		}
		return count;
	}

	public String getPlayer() {
		return player;
	}

	public int getImage() {
		return image;
	}

	public int getImageBig() {
		return imageBig;
	}

	public int getDescription() {
		return description;
	}

	public int getColor() {
		return color;
	}

	public void setPlayer(String name) {
		if (TextUtils.isEmpty(name)) {
			return;
		}
		player = name;
		Editor editor = prefs.edit();
		editor.putString(SP_PLAYER, name);
		editor.commit();
	}

}
