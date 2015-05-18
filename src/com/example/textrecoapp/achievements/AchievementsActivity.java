/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.achievements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.example.textrecoapp.App;
import com.example.textrecoapp.R;
import com.example.textrecoapp.gamification.AchievementGridAdapter;

public class AchievementsActivity extends Activity {

	private Map<String, List<Achievement>> achievements;
	private GridView gridView;
	private LinearLayout headersContainer;
	private AchievementGridAdapter adapter;
	
	private LinearLayout details;
	private ImageView achImage;
	private TextView achName;
	private TextView achDescription;
	private TextView achStatus;
	private TextView achProgress;
	private Button achClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.achievements_grid);

		headersContainer = (LinearLayout) findViewById(R.id.headers_container);
		gridView = (GridView) findViewById(R.id.grid_view);
		details = (LinearLayout) findViewById(R.id.achievement_details);
		inflateDetails();

		achievements = App.getInstance().getAchievements();
		int numColumns = achievements.size();
		loadHeaders(achievements.keySet());
		gridView.setNumColumns(numColumns * 2);
		loadAchievements(achievements.values());
		adapter.setNumColumnsAndCalculateHeight(numColumns * 2);

		gridView.setOnItemClickListener(gridListener);
	}
	
	private void inflateDetails() {
		achImage = (ImageView) details.findViewById(R.id.ach_img);
		achName = (TextView) details.findViewById(R.id.ach_name);
		achDescription = (TextView) details.findViewById(R.id.ach_description);
		achStatus = (TextView) details.findViewById(R.id.ach_status);
		achProgress = (TextView) details.findViewById(R.id.ach_progress);		
		achClose = (Button) details.findViewById(R.id.ach_close);
		achClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				details.setVisibility(View.GONE);
			}
		});
	}

	private OnItemClickListener gridListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Object obj = parent.getItemAtPosition(position);
			if (obj instanceof Achievement) {
				Achievement a = (Achievement) obj;
				prepareAchievementDetails(a);
				details.setVisibility(View.VISIBLE);
			}
		}

	};

	@SuppressLint("InflateParams")
	private void loadHeaders(Set<String> headers) {
		int headersNum = headers.size();
		int margin = getResources().getDimensionPixelOffset(
				R.dimen.grid_margin_horizontal);
		int rightMargin = getResources().getDimensionPixelOffset(
				R.dimen.column_spacing);
		DisplayMetrics metrics = getResources().getDisplayMetrics();

		int headerWidth = ((metrics.widthPixels - 2 * margin) - (headersNum - 1)
				* rightMargin)
				/ headersNum;

		int counter = 0;

		headersContainer.removeAllViews();

		for (String title : headers) {

			View gridHeader = getLayoutInflater().inflate(
					R.layout.grid_header_layout, null);
			TextView tv = (TextView) gridHeader.findViewById(R.id.grid_title);
			tv.setBackgroundResource(getBakcgroundForTitle(title));

			LayoutParams params = new LayoutParams(headerWidth,
					LayoutParams.WRAP_CONTENT);
			if (counter != headersNum - 1) {
				params.rightMargin = rightMargin;
			}
			gridHeader.setLayoutParams(params);

			headersContainer.addView(gridHeader);

			counter++;
		}
	}

	private int getBakcgroundForTitle(String title) {
		if(title.equals("komiti")) {
			return R.drawable.komits;
		} else if(title.equals("spartanci")) {
			return R.drawable.ancients;
		} else if(title.equals("partizani")) {
			return R.drawable.partizans;
		}
		return R.drawable.general;
	}

	private void loadAchievements(Collection<List<Achievement>> collection) {
		List<Object> objects = new ArrayList<Object>();
		int lonegstListSize = getLongestListSize(collection);

		for (int i = 0; i < lonegstListSize; i+=2) {
			for (List<Achievement> list : collection) {
				try {
					objects.add(list.get(i));
					objects.add(list.get(i + 1));
				} catch (IndexOutOfBoundsException e) {
					objects.add(new Object());
				}
			}
		}

		adapter = new AchievementGridAdapter(this, 0, objects);
		gridView.setAdapter(adapter);
	}

	private int getLongestListSize(Collection<List<Achievement>> collection) {
		int longest = 0;
		for (List<Achievement> list : collection) {
			longest = list.size() > longest ? list.size() : longest;
		}
		return longest;
	}
	
	private void prepareAchievementDetails(Achievement a) {
		//populate details views
		achImage.setImageResource(adapter.assignIcon(a.getCriteria()));
		achName.setText(a.getName());
		achDescription.setText(a.getDescription());
	
		String statusText = a.isUnlocked() ? "UNLOCKED" : "LOCKED";
		if(a.isContinuous() && a.getTimes() > 0) {
			statusText += " x" + a.getTimes();
		}
		achStatus.setText(statusText);
		
		if(a.getCriteria() instanceof CheckArtifacts && a.isUnlocked()) {
			CheckArtifacts ca = (CheckArtifacts) a.getCriteria();
			ca.checkCriteria(null);
			achProgress.setText(ca.getStatus() + " found");
			achProgress.setVisibility(View.VISIBLE);
		} else {
			achProgress.setVisibility(View.GONE);
		}
	}
}
