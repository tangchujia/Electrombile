package com.syxgo.electrombile.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.syxgo.electrombile.R;
import com.syxgo.electrombile.http.okhttp.utils.DateUtil;
import com.syxgo.electrombile.model.Bike;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangchujia on 2017/9/18.
 */

public class BikeSearchAdapter extends RecyclerView.Adapter<BikeSearchAdapter.ViewHolder> {
    private List<Bike> mBikes = new ArrayList<>();
    private Context mContext;

    public BikeSearchAdapter(Context mContext, List<Bike> mBikes) {
        this.mContext = mContext;
        this.mBikes = mBikes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.item_bike, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bike bike = mBikes.get(position);
        holder.bikeIdTv.setText(String.format("编号 %06d", bike.getId()));
        /**
         * 电量
         * 10%~20% 黄色
         * <10% 红色
         */
        holder.bikeProgress.setProgress(bike.getBattery_level());
        holder.bikeProgress.setFinishedStrokeColor(ContextCompat.getColor(mContext, R.color.color_Orange));

        holder.bikeDateTv.setText("上次骑行：" + bike.getLast_ride_time());

        holder.bikeDistanceTv.setText("");
    }

    @Override
    public int getItemCount() {
        if (mBikes == null) {
            return 0;
        }
        return mBikes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView bikeIdTv;
        TextView bikeDistanceTv;
        TextView bikeDateTv;
        ArcProgress bikeProgress;

        public ViewHolder(View itemView) {
            super(itemView);
            bikeIdTv = (TextView) itemView.findViewById(R.id.bike_id_tv);
            bikeDistanceTv = (TextView) itemView.findViewById(R.id.bike_distance_tv);
            bikeDateTv = (TextView) itemView.findViewById(R.id.bike_date_tv);
            bikeProgress = (ArcProgress) itemView.findViewById(R.id.item_progress);
        }
    }
    public void setDateChanged(List<Bike> bikes) {
        this.mBikes = bikes;
        this.notifyDataSetChanged();
    }
}
