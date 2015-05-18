package com.example.textrecoapp.story;

import com.example.textrecoapp.models.Artifact;

import android.view.View;

public interface PageView {
	public View getView();
	public void handleResult(String result);
	public Artifact getArtifact();
}
