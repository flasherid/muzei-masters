package com.example.textrecoapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomDialog extends Dialog implements View.OnClickListener {

	// views
	private View buttonContainer;
	private Button positiveButton;
	private Button negativeButton;
	private TextView message;
	private TextView title;
	private LinearLayout customViewContainer;

	// content
	private String messageText;
	private String titleText;
	private String posBtnText;
	private String negBtnText;
	private View customView;
	private CustomDialogClickListener posListener;
	private CustomDialogClickListener negListener;

	public CustomDialog(Context context, String titleText, String posBtnText,
			String negBtnText, CustomDialogClickListener posListener,
			CustomDialogClickListener negListener) {
		this(context, null, titleText, null, posBtnText, negBtnText,
				posListener, negListener);
	}

	public CustomDialog(Context context, String titleText, String messageText) {
		this(context, null, titleText, messageText, null, null, null, null);
	}

	public CustomDialog(Context context, View customView, String titleText,
			String messageText, String posBtnText, String negBtnText,
			CustomDialogClickListener posListener,
			CustomDialogClickListener negListener) {
		super(context);
		this.titleText = titleText;
		this.messageText = messageText;
		this.customView = customView;
		this.posBtnText = posBtnText;
		this.negBtnText = negBtnText;
		this.posListener = posListener;
		this.negListener = negListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setContentView(R.layout.dialog);

		initUi();
		populateUi();
	}

	private void initUi() {
		title = (TextView) findViewById(R.id.title);
		message = (TextView) findViewById(R.id.message);
		positiveButton = (Button) findViewById(R.id.positive_button);
		negativeButton = (Button) findViewById(R.id.negative_button);
		buttonContainer = findViewById(R.id.button_container);
		customViewContainer = (LinearLayout) findViewById(R.id.inflatable_view);
	}

	private void populateUi() {
		title.setText(titleText);

		if (messageText != null) {
			message.setText(messageText);
		} else {
			message.setVisibility(View.GONE);
		}

		if (posBtnText == null || negBtnText == null) {
			buttonContainer.setVisibility(View.GONE);
		} else {
			positiveButton.setText(posBtnText);
			negativeButton.setText(negBtnText);
			positiveButton.setOnClickListener(this);
			negativeButton.setOnClickListener(this);
		}
		
		if(customView != null) {
			customViewContainer.addView(customView);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.positive_button:
			posListener.onClick();
			break;
		case R.id.negative_button:
			negListener.onClick();
			break;
		}
		dismiss();
	}

}
