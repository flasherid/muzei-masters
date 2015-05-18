package com.example.textrecoapp.characters;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.textrecoapp.FeatureSelectorActivity;
import com.example.textrecoapp.R;
import com.example.textrecoapp.map.MapActivity;
import com.example.textrecoapp.models.Artifact;

public class BrowseArtifacts extends Activity {

	private static final String LOG_TAG = "BrowseArtifacts";
	private ImageView image;
	private TextView difficulty;
	private TextView name;
	private TextView category;
	private TextView description;
	private TextView status;
	private Button location;

	private Artifact artifact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_artifact);
		initUi();
		artifact = (Artifact) getIntent().getExtras().get(
				FeatureSelectorActivity.EXTRA_BROWSE_ARTIFACT);
		populateData();
	}

	private void populateData() {
		try {
			InputStream is = getAssets().open(artifact.getPictureFilename());
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			image.setImageBitmap(bitmap);
		} catch (IOException e) {
			Log.w(LOG_TAG, "Failed loading artifact image");
		}

		name.setText(artifact.getName());
		description.setText(artifact.getDescription());
		difficulty.setText(artifact.getDifficulty() + "");
		status.setText(artifact.isArtefactUnlocked() ? "UNLOCKED" : "LOCKED");
		category.setText(artifact.getCategory());
		location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BrowseArtifacts.this, MapActivity.class);
				intent.putExtra(MapActivity.REQUEST_STATUS, MapActivity.REQUEST_UNLOCKED_ARTIFACT);
				intent.putExtra(MapActivity.ARTIFACT_UNLOCKED, artifact);
				startActivity(intent);
			}
		});
	}

	private void initUi() {
		image = (ImageView) findViewById(R.id.artifact_image);
		difficulty = (TextView) findViewById(R.id.artifact_difficulty);
		name = (TextView) findViewById(R.id.artifact_name);
		description = (TextView) findViewById(R.id.artifact_description);
		status = (TextView) findViewById(R.id.artifact_status);
		location = (Button) findViewById(R.id.artifact_location);
		category = (TextView) findViewById(R.id.artifact_category);
	}
}
