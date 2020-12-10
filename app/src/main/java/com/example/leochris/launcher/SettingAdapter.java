package com.example.leochris.launcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static java.security.AccessController.getContext;

public class SettingAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<FragmentInfo> mDataSource;
    private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();
    //it probably doesnt need to be a map
    public SettingAdapter(Context context, List<FragmentInfo> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void setNewSelection(int position, boolean value) {
        mSelection.put(position, value);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return mSelection.keySet();
    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelection = new HashMap<Integer, Boolean>();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.list_item, parent, false);
        TextView titleTextView =
                (TextView) rowView.findViewById(R.id.fragment_title);
        FragmentInfo mFragment = (FragmentInfo) getItem(position);

        titleTextView.setText(mFragment.getName());
        rowView.setBackgroundColor(mContext.getResources().getColor(android.R.color.background_light));
        if (mSelection.get(position) != null) {
            rowView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryLight));// this is a selected position so make it red
        }
        return rowView;
    }

}
