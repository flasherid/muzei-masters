package com.example.textrecoapp.wizards;

import android.os.Bundle;
import android.view.View;

import com.example.textrecoapp.R;
import com.example.textrecoapp.models.MissionStep;
import com.example.textrecoapp.story.FinalStepView;
import com.example.textrecoapp.story.PageView;
import com.example.textrecoapp.story.StoryStepView;

public class RankedMissionActivity extends AbstractWizardActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getInfoButton().setVisibility(View.GONE);
	}

	@Override
	public PageView getFinalStepView() {
		return new FinalStepView(this);
	}

	@Override
	public PageView getStepView(MissionStep step) {
		return new StoryStepView(this, step);
	}

	@Override
	public int getBackgroundResource() {
		return R.drawable.ranked_background;
	}

}
