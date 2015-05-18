package com.example.textrecoapp;

import java.util.List;
import java.util.Map;

import com.example.textrecoapp.models.LevelMission;
import com.example.textrecoapp.models.Mission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<String> headers;
	private Map<String, List<? extends Object>> data;

	public ExpandableAdapter(Context context, List<String> headers,
			Map<String, List<? extends Object>> data) {
		super();
		this.context = context;
		this.headers = headers;
		this.data = data;
	}

	@Override
	public int getGroupCount() {
		return headers.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return data.get(headers.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return headers.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return data.get(headers.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.expand_list_group, null);
		}
		TextView tv = (TextView) view.findViewById(R.id.expand_group);
		tv.setText((String) getGroup(groupPosition));
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			// inflate
			view = LayoutInflater.from(context).inflate(
					R.layout.expand_list_item, null);
		}
		TextView tv = (TextView) view.findViewById(R.id.expand_tv);
		
		Object obj = getChild(groupPosition, childPosition);
		if (obj instanceof LevelMission) {
			LevelMission lm = (LevelMission) obj;
			tv.setText(lm.getName());
		} else if (obj instanceof Mission) {
			Mission sm = (Mission) obj;
			tv.setText(sm.getTitle());
		}
		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
