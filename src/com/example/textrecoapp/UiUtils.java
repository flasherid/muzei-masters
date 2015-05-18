/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp;

import com.example.textrecoapp.gamification.IPlayerNameChanged;
import com.example.textrecoapp.gamification.RankHandler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public final class UiUtils {

	private static final String RES_PREFIX = "muzei_";
	private static final String RES_HIGHLIGHTED = "_highlighted";
	private static final String RES_TYPE_DRAWABLE = "drawable";

	private static final String PHOTO_KOMITA = "girl";
	private static final String PHOTO_PARTIZAN = "boy";
	private static final String PHOTO_FALANGA = "falanga_man";

	private static CustomDialogClickListener negativeCustomListener;
	private static DialogInterface.OnClickListener negativeListener;

	public static Dialog createDialogWithView(Context context, String title,
			String posBtnText, String negBtnText, View customView,
			CustomDialogClickListener posListener,
			CustomDialogClickListener negListener) {
		return new CustomDialog(context, customView, title, null, posBtnText,
				negBtnText, posListener, negListener);
	}

	public static Dialog createSimpleDialog(Context context, String title,
			String message, String posBtnText, String negBtnText,
			CustomDialogClickListener posListener,
			CustomDialogClickListener negListener) {
		Dialog dialog = new CustomDialog(context, null, title, message,
				posBtnText, negBtnText, posListener, negListener);
		return dialog;
	}

	public static Dialog createSimpleCancelDialog(Context context,
			String title, String message) {
		return new CustomDialog(context, title, message);
	}

	public static StateListDrawable getStateDrawableForId(Context context,
			String id, boolean hasPrefix) {

		String drawableId = null;

		// TODO remove this fix with proper image handling
		if (id.startsWith("images/")) {
			if (id.equals("images/komita1.jpg")) {
				drawableId = PHOTO_KOMITA;
			} else if (id.equals("images/Image-ready-CS-2-icon.png")) {
				drawableId = PHOTO_FALANGA;
			} else if (id.equals("images/images.jpg")) {
				drawableId = PHOTO_PARTIZAN;
			}
		} else {
			drawableId = id;
		}

		String str_id_regular = (hasPrefix ? RES_PREFIX : "") + drawableId;
		String str_id_pressed = (hasPrefix ? RES_PREFIX : "") + drawableId
				+ RES_HIGHLIGHTED;
		int id_normal = context.getResources().getIdentifier(str_id_regular,
				RES_TYPE_DRAWABLE, context.getPackageName());
		int id_pressed = context.getResources().getIdentifier(str_id_pressed,
				RES_TYPE_DRAWABLE, context.getPackageName());

		StateListDrawable stateList = new StateListDrawable();
		stateList.addState(new int[] { android.R.attr.state_pressed }, context
				.getResources().getDrawable(id_pressed));
		stateList.addState(new int[] { android.R.attr.state_enabled }, context
				.getResources().getDrawable(id_normal));
		return stateList;
	}

	public static int getImageDrawableId(Context context, String id) {
		int drawableId = context.getResources().getIdentifier(id,
				RES_TYPE_DRAWABLE, context.getPackageName());
		return drawableId;
	}

	public static void drawHighlightedRectangle(Canvas canvas, Point point) {
		Paint circlePaint = new Paint();
		circlePaint.setStyle(Paint.Style.FILL);
		circlePaint.setColor(Color.BLUE);
		circlePaint.setAlpha(128);

		canvas.drawCircle(point.x, point.y, 50, circlePaint);
	}

	public static void drawCircle(Canvas canvas, Point center, int radius) {
		Paint circlePaint = new Paint();
		circlePaint.setStyle(Paint.Style.FILL);
		circlePaint.setColor(Color.RED);
		circlePaint.setAlpha(128);

		canvas.drawCircle(center.x, center.y, radius, circlePaint);
	}

	public static AlertDialog showNinjaDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(prepareView(context, App.getInstance().getRankHandler()));
		builder.setCancelable(true);
		return builder.create();
	}

	private static View prepareView(Context context, RankHandler rankHandler) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.rank_dialog, null);
		
		ImageView image = (ImageView) view.findViewById(R.id.ninja_picture);
		image.setImageResource(rankHandler.getImageBig());
		
		TextView belt = (TextView) view.findViewById(R.id.belt_info);
		belt.setText(rankHandler.getDescription());
		belt.setTextColor(context.getResources().getColor(rankHandler.getColor()));
		
		return view;
	}

	public static AlertDialog showPlayerNameDialog(Context context, final IPlayerNameChanged iplc) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.change_player_name);
		final EditText edit = new EditText(context);
		builder.setView(edit);

		builder.setPositiveButton(R.string.submit,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						iplc.setNewName(String.valueOf(edit.getText()));
					}
				}).setNegativeButton(R.string.cancel, getNegativeListener());
		return builder.create();
	}

	public static CustomDialogClickListener getNegativeCustomListener() {
		if (negativeCustomListener == null) {
			negativeCustomListener = new CustomDialogClickListener() {

				@Override
				public void onClick() {
					// do nothing on purpose
				}
			};
		}
		return negativeCustomListener;
	}

	public static DialogInterface.OnClickListener getNegativeListener() {
		if (negativeListener == null) {
			negativeListener = new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			};
		}
		return negativeListener;
	}
}
