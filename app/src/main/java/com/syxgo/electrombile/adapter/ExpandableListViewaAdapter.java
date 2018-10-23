package com.syxgo.electrombile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.model.Station;
import com.syxgo.electrombile.model.StationGroup;

import java.util.List;

/**
 * @Author: Huangweicai
 * @date 2016-12-19 17:34
 * @Description:(这里用一句话描述这个类的作用)
 */

public class ExpandableListViewaAdapter extends BaseExpandableListAdapter {
    private List<StationGroup> list;
    private Context context;

    public ExpandableListViewaAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).getStations().size();
    }

    @Override
    public StationGroup getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public Station getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getStations().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder itemView = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.view_expand, null);
            itemView = new GroupViewHolder();
            itemView.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

            convertView.setTag(itemView);
        } else {
            itemView = (GroupViewHolder) convertView.getTag();
        }
        itemView.tvTitle.setText(getGroup(groupPosition).getName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        DeviceViewHolder itemView = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_device, null);
            itemView = new DeviceViewHolder();
            itemView.deviceName = (TextView) convertView.findViewById(R.id.tv_device_name);
            convertView.setTag(itemView);
        } else {
            itemView = (DeviceViewHolder) convertView.getTag();
        }

        Station station = getChild(groupPosition, childPosition);
        String distance = "";
        if (station.getDistance() == 0) {
            distance = "";
        } else if (station.getDistance() < 1000) {
            distance = String.format("%.2fm", station.getDistance());
        } else {
            distance = String.format("%.2fkm", station.getDistance() / 1000);
        }
        itemView.deviceName.setText(station.getName() + "     " + distance);


        return convertView;
    }

    private class DeviceViewHolder {
        public TextView deviceName;

    }

    private class GroupViewHolder {
        public TextView tvTitle;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setDataChanged(List<StationGroup> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
