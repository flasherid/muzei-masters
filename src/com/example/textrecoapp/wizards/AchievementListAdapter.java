package com.example.textrecoapp.wizards;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.textrecoapp.R;
import com.example.textrecoapp.achievements.Achievement;

public class AchievementListAdapter extends ArrayAdapter<Achievement> {

	public AchievementListAdapter(Context context, int resource,
			List<Achievement> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(getContext()).inflate(
					R.layout.list_item, null);
		}

		TextView tv = (TextView) view.findViewById(R.id.list_item_content);
		tv.setText(getItem(position).getName());

		return view;
	}

}
