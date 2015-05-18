/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.textrecoapp.App;
import com.example.textrecoapp.R;
import com.example.textrecoapp.models.Artifact;

public class MapActivity extends Activity {

	public static final String REQUEST_STATUS = "request_status";
	public static final String REQUEST_WRONG_ARTIFACT = "wrong_artifact";
	public static final String REQUEST_UNLOCKED_ARTIFACT = "unlocked_artifact";

	public static final String ARTIFACT_UNLOCKED = "artifact_unlocked";
	public static final String ARTIFACT_TARGET = "artifact_target";
	public static final String ARTIFACT_FOUND = "artifact_found";

	private MapControllerView mapView;
	
	private TextView floorLevel;
	private TextView floorName;
	private ListView floorList;
	
	private List<String> floorNames;

	private BuildingNavigator navigator;

	// artifact details
	private ScrollView detailsContainer;
	private TextView detailsName;
	private TextView detailsDescription;
	private TextView detailsCategory;
	private ImageView detailsImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		initUI();
		navigator = App.getInstance().getCartographer().getNavigator();
		mapView.setNavigator(navigator);
		mapView.setActivity(this);

		populateLevelNavigation();

		if (getIntent().getExtras() != null) {
			String request = getIntent().getExtras().getString(REQUEST_STATUS);
			if (request.equals(REQUEST_UNLOCKED_ARTIFACT)) {
				Artifact unlockedArtifact = (Artifact) getIntent().getExtras()
						.get(ARTIFACT_UNLOCKED);
				handleUnlockingArtifact(unlockedArtifact);
			} else if (request.equals(REQUEST_WRONG_ARTIFACT)) {
				Artifact targetArtifact = (Artifact) getIntent().getExtras()
						.get(ARTIFACT_TARGET);
				Artifact foundArtifact = (Artifact) getIntent().getExtras()
						.get(ARTIFACT_FOUND);
				handleWrongExistingArtifactScanned(foundArtifact,
						targetArtifact);
			}
		}

	}

	private void initUI() {
		mapView = (MapControllerView) findViewById(R.id.map_view);

		floorLevel = (TextView) findViewById(R.id.map_floor_level);
		floorName = (TextView) findViewById(R.id.map_floor_name);
		floorList = (ListView) findViewById(R.id.map_floor_list);
		
		detailsContainer = (ScrollView) findViewById(R.id.artifact_details_container);
		detailsImage = (ImageView) findViewById(R.id.artifact_image);
		detailsName = (TextView) findViewById(R.id.artifact_name);
		detailsCategory = (TextView) findViewById(R.id.artifact_category);
		detailsDescription = (TextView) findViewById(R.id.artifact_description);
	}

	private void populateLevelNavigation() {
		floorNames = new ArrayList<String>();
		for (Floor floor : App.getInstance().getCartographer().getAllFloors()) {
			floorNames.add(floor.getFloorId());
		}
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, floorNames);
		floorList.setAdapter(spinnerArrayAdapter);
		floorList.setOnItemClickListener(spinnerListener);
		floorLevel.setOnClickListener(levelClickListener);
		
		floorName.setText(navigator.getCurrentFloorId());
		floorLevel.setText(getFloorLevelForFloor(navigator.getCurrentFloorId()));
	}
	
	private View.OnClickListener levelClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			toggleNavigationViews();
		}
	};
	
	private void toggleNavigationViews() {
		if(floorName.getVisibility() == View.VISIBLE) {
			floorName.setVisibility(View.GONE);
			floorList.setVisibility(View.VISIBLE);
		} else {
			floorName.setVisibility(View.VISIBLE);
			floorList.setVisibility(View.GONE);
		}
	}

	private OnItemClickListener spinnerListener = new OnItemClickListener() {


		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String floorId = floorNames.get(position);
			navigator.changeStorey(floorId);
			mapView.changeFloor(floorId);
			floorName.setText(floorId);
			floorLevel.setText(getFloorLevelForFloor(floorId));
			toggleNavigationViews();
		}

	};
	
	private CharSequence getFloorLevelForFloor(String floorId) {
		String str = null;
		for (Floor floor : App.getInstance().getCartographer().getAllFloors()) {
			if(floor.getFloorId().equals(floorId)) {
				str = "" + floor.getFloorLevel();
			}
		}
		return str;
	}

	private void handleUnlockingArtifact(Artifact artifact) {
		navigator.changeStorey(artifact.getFloorId());
		mapView.changeFloor(artifact.getFloorId());
		mapView.updatePinBitmaps();
		mapView.invalidate();

		Pin preparedPin = new Pin(artifact);
		mapView.markAndCenterPin(preparedPin);
	}

	private void handleWrongExistingArtifactScanned(Artifact foundArtifact,
			Artifact targetArtifact) {

		handleUnlockingArtifact(foundArtifact);

		if (foundArtifact.getFloorId().equals(targetArtifact.getFloorId())) {
			HelperCircle helperCircle = calculateHelperCircle(foundArtifact,
					targetArtifact);
			mapView.setHelperCircle(helperCircle);
			mapView.invalidate();
		} else {
			Toast.makeText(this, "You're here, the target is on another floor",
					Toast.LENGTH_SHORT).show();
		}

	}

	private HelperCircle calculateHelperCircle(Artifact found, Artifact target) {
		float currentX = found.getLocation().getxPercentage();
		float currentY = found.getLocation().getyPercentage();

		float targetX = target.getLocation().getxPercentage();
		float targetY = target.getLocation().getyPercentage();

		float mustRunThroughX = (currentX + targetX) / 2;
		float mustRunThroughY = (currentY + targetY) / 2;

		float centerX = 0;
		float centerY = 0;

		if (targetX >= currentX) {
			centerX = (1.0f - targetX) / 2;
		} else {
			centerX = targetX / 2;
		}

		if (targetY >= currentY) {
			centerY = (1.0f - targetY) / 2;
		} else {
			centerY = targetY / 2;
		}

		PointF cp = new PointF(centerX, centerY);
		PointF pp = new PointF(mustRunThroughX, mustRunThroughY);

		HelperCircle hc = new HelperCircle(cp, pp);
		return hc;
	}

	public void handleClickedArtifact(Artifact artifact) {
		detailsName.setText(artifact.getName());
		detailsCategory.setText(artifact.getCategory());
		detailsDescription.setText(artifact.getDescription());

		try {
			InputStream is = getAssets().open(artifact.getPictureFilename());
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			detailsImage.setImageBitmap(bitmap);
		} catch (IOException e) {
			System.out.println("Failed to load image: "
					+ artifact.getPictureFilename());
			e.printStackTrace();
		}

		// show view
		detailsContainer.setVisibility(View.VISIBLE);
	}

	public void handleDissmissingClickedArtifact() {
		detailsContainer.setVisibility(View.GONE);
	}

	@Override
	public void onBackPressed() {
		if (detailsContainer.getVisibility() == View.VISIBLE) {
			handleDissmissingClickedArtifact();
		} else {
			super.onBackPressed();
		}
	}

}
