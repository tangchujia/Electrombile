package com.syxgo.electrombile.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.model.BikeData;

import java.util.List;

/**
 * Created by tangchujia on 2017/10/19.
 */

public class BikeAdapter extends RecyclerView.Adapter<BikeAdapter.ViewHolder> {
    private List<BikeData> mData;

    public BikeAdapter(List<BikeData> mData) {
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bike, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position % 2 != 1) {
            holder.view.setBackgroundResource(R.color.color_grey);
        } else {
            holder.view.setBackgroundResource(R.color.colorWhite);
        }
        holder.arrow.setVisibility(View.VISIBLE);

        try {
            BikeData bikeData = mData.get(position);
            holder.bikeId.setText(bikeData.getBike_id() + "");
            holder.bikeBattery.setText(bikeData.getBattery_level() + "%");
            if (bikeData.is_lock()) {
                holder.bikeLock.setText("关锁");

            } else {
                holder.bikeLock.setText("开锁");

            }
            if (bikeData.is_rent()) {
                holder.bikeIsRent.setText("是");
            } else {
                holder.bikeIsRent.setText("否");
            }

            if (bikeData.getBattery_level() < 33) {
                holder.bikeId.setTextColor(Color.rgb(237,75,40));
                holder.bikeBattery.setTextColor(Color.rgb(237,75,40));
                holder.bikeLock.setTextColor(Color.rgb(237,75,40));
                holder.bikeOnline.setTextColor(Color.rgb(237,75,40));
                holder.bikeIsRent.setTextColor(Color.rgb(237,75,40));
            } else {
                holder.bikeId.setTextColor(Color.rgb(126,211,33));
                holder.bikeBattery.setTextColor(Color.rgb(126,211,33));
                holder.bikeLock.setTextColor(Color.rgb(126,211,33));
                holder.bikeOnline.setTextColor(Color.rgb(126,211,33));
                holder.bikeIsRent.setTextColor(Color.rgb(126,211,33));
            }
            if (bikeData.is_offline()) {
                holder.bikeOnline.setText("否");
                holder.bikeId.setTextColor(Color.rgb(155,155,155));
                holder.bikeBattery.setTextColor(Color.rgb(155,155,155));
                holder.bikeLock.setTextColor(Color.rgb(155,155,155));
                holder.bikeOnline.setTextColor(Color.rgb(155,155,155));
                holder.bikeIsRent.setTextColor(Color.rgb(155,155,155));
            } else {
                holder.bikeOnline.setText("是");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView bikeId;
        TextView bikeBattery;
        TextView bikeLock;
        TextView bikeOnline;
        TextView bikeIsRent;
        TextView arrow;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            bikeId = (TextView) itemView.findViewById(R.id.bike_id);
            bikeBattery = (TextView) itemView.findViewById(R.id.bike_battery);
            bikeLock = (TextView) itemView.findViewById(R.id.bike_lock);
            bikeOnline = (TextView) itemView.findViewById(R.id.bike_online);
            bikeIsRent = (TextView) itemView.findViewById(R.id.bike_isrent);
            arrow = (TextView) itemView.findViewById(R.id.arrow_tv);
        }
    }

    public void setDateChanged(List<BikeData> orders) {
        this.mData = orders;
        this.notifyDataSetChanged();
    }
}
