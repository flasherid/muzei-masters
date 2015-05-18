package com.example.textrecoapp.story;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.textrecoapp.App;
import com.example.textrecoapp.R;
import com.example.textrecoapp.map.MapActivity;
import com.example.textrecoapp.models.Artifact;
import com.example.textrecoapp.models.MissionStep;
import com.example.textrecoapp.wizards.AbstractWizardActivity;

public class StoryStepView implements PageView {

	private AbstractWizardActivity context;
	private MissionStep step;
	private View view;

	private TextView tv;
	private TextView description;

	private TextView solvedMessage;
	private View scanContainer;
	private Button scanButton;
	private Button mapButton;
	private Button artifactButton;
	
	private Artifact foundArtifact;

	public StoryStepView(AbstractWizardActivity context, MissionStep step) {
		this.context = context;
		this.step = step;
	}

	private View prepareView() {
		View v = LayoutInflater.from(context)
				.inflate(R.layout.story_step, null);
		tv = (TextView) v.findViewById(R.id.step_name);
		description = (TextView) v.findViewById(R.id.step_description);

		solvedMessage = (TextView) v.findViewById(R.id.solved_message);
		scanButton = (Button) v.findViewById(R.id.step_scan);
		scanContainer = v.findViewById(R.id.step_scan_conatainer);
		mapButton = (Button) v.findViewById(R.id.step_map);
		artifactButton = (Button) v.findViewById(R.id.step_artifact);

		populateViews();
		return v;
	}

	private void populateViews() {

		if (step.isUnlocked()) {
			tv.setText(step.getArtifact().getName());
			solvedMessage.setVisibility(View.VISIBLE);
			scanContainer.setVisibility(View.GONE);
		} else {
			tv.setText("???");
			solvedMessage.setVisibility(View.GONE);
			scanContainer.setVisibility(View.VISIBLE);
		}
		//TODO: remove getArtifact().getName() when real data is given
		description.setText(step.getDescription() + " / " + getArtifact().getName());
		scanButton.setOnClickListener(scanListener);
		mapButton.setOnClickListener(mapListener);
		artifactButton.setOnClickListener(artifactListener);
	}

	private View.OnClickListener scanListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			context.goToScanningScreen();
		}
	};

	private View.OnClickListener mapListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, MapActivity.class);
			intent.putExtra(MapActivity.REQUEST_STATUS, MapActivity.REQUEST_WRONG_ARTIFACT);
			intent.putExtra(MapActivity.ARTIFACT_FOUND, foundArtifact);
			intent.putExtra(MapActivity.ARTIFACT_TARGET, getArtifact());
			context.startActivity(intent);
		}
	};
	
	private View.OnClickListener artifactListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, MapActivity.class);
			intent.putExtra(MapActivity.REQUEST_STATUS, MapActivity.REQUEST_UNLOCKED_ARTIFACT);
			intent.putExtra(MapActivity.ARTIFACT_UNLOCKED, getArtifact());
			context.startActivity(intent);
		}
	};

	@Override
	public View getView() {
		if (view == null) {
			view = prepareView();
		}
		return view;
	}

	@Override
	public void handleResult(String result) {
		if (result.equals(step.getArtifact().getName())) {
			// 1. result is the correct one
			// set tv, set to unlocked (solved), go to
			tv.setText(result);
			solvedMessage.setVisibility(View.VISIBLE);
			scanContainer.setVisibility(View.GONE);
			step.setUnlocked(true);
			step.getArtifact().unlockArtefact(); // this may not work
			artifactButton.setVisibility(View.VISIBLE);
			
		} else {
			// 2. result is not correct but artifact exists (show map help)
			foundArtifact = App.getInstance().getCartographer()
					.findArtifact(result);
			if (foundArtifact != null) {
				mapButton.setVisibility(View.VISIBLE);
			} 
		}
	}

	@Override
	public Artifact getArtifact() {
		return step.getArtifact();
	}

}
