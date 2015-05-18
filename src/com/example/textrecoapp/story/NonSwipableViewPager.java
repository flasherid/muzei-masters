package com.example.textrecoapp.story;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

@SuppressLint("ClickableViewAccessibility")
public class NonSwipableViewPager extends ViewPager {

	public NonSwipableViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NonSwipableViewPager(Context context) {
		super(context);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return false;
	}
	
}
