package com.syxgo.electrombile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.syxgo.electrombile.R;
import com.syxgo.electrombile.model.Item;

import java.util.List;

/**
 * Created by ruichengrui on 2017/5/29.
 */

public class MainGridAdapter extends BaseAdapter {
    private List<Item> data;
    private LayoutInflater mInflater;
    private Context context;

    public MainGridAdapter(Context context, List<Item> data) {
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_main_grid_list, null);
            holder.item_main_name_tv = (TextView) convertView.findViewById(R.id.item_main_name_tv);
            holder.item_main_logo_iv = (ImageView) convertView.findViewById(R.id.item_main_logo_iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Item item = data.get(position);
        holder.item_main_name_tv.setText(item.getName());
        try {
            holder.item_main_logo_iv.setImageDrawable(context.getResources().getDrawable(item.getImgurl()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        TextView item_main_name_tv;
        ImageView item_main_logo_iv;
    }
}

