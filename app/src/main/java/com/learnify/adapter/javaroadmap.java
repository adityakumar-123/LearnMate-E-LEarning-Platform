package com.learnify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.learnify.R;

import java.util.HashMap;
import java.util.List;

public class javaroadmap extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listGroup;
    private HashMap<String, List<String>> listItem;

    public javaroadmap(Context context, List<String> listGroup, HashMap<String, List<String>> listItem) {
        this.context = context;
        this.listGroup = listGroup;
        this.listItem = listItem;
    }

    @Override
    public int getGroupCount() {
        return listGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listItem.get(listGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listItem.get(listGroup.get(groupPosition)).get(childPosition);
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

    // Parent row (group view)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(groupTitle);
        textView.setTextSize(18f);
        textView.setPadding(72, 24, 16, 24);

        return convertView;
    }

    // Child row
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        String item = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(item);
        textView.setTextSize(16f);
        textView.setPadding(96, 16, 16, 16);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
