package com.example.textrecoapp.story;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class StoryPagerAdapter extends PagerAdapter {

	private List<PageView> steps;

	public StoryPagerAdapter(List<PageView> steps) {
		this.steps = steps;
	}

	@Override
	public int getCount() {
		return steps.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = steps.get(position).getView();
		((ViewPager) container).addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

}
