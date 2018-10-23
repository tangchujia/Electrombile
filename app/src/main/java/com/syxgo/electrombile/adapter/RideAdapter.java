package com.syxgo.electrombile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.model.RideData;

import java.util.List;

/**
 * Created by tangchujia on 2017/10/19.
 */

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.ViewHolder> {
    private List<RideData> mData;

    public RideAdapter(List<RideData> mData) {
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ride, parent, false);
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
            RideData ride = mData.get(position);
            holder.userId.setText(ride.getUser_id() + "");
            holder.bikeId.setText(ride.getBike_id() + "");
            holder.rideId.setText(ride.getRideId()+"");
            holder.rideTime.setText(ride.getCreatedTime());
            if (ride.getStatus() == 1) {
                holder.rideStatus.setText("骑行中");

            } else if (ride.getStatus() == 2) {
                holder.rideStatus.setText("已结束");

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
        TextView userId;
        TextView bikeId;
        TextView rideId;
        TextView rideTime;
        TextView rideStatus;
        TextView arrow;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            bikeId = (TextView) itemView.findViewById(R.id.bike_id);
            rideId = (TextView) itemView.findViewById(R.id.ride_id);
            userId = (TextView) itemView.findViewById(R.id.user_id);
            rideTime = (TextView) itemView.findViewById(R.id.ride_time);
            rideStatus = (TextView) itemView.findViewById(R.id.ride_status);
            arrow = (TextView) itemView.findViewById(R.id.arrow_tv);
        }
    }

    public void setDateChanged(List<RideData> rides) {
        this.mData = rides;
        this.notifyDataSetChanged();
    }
}
