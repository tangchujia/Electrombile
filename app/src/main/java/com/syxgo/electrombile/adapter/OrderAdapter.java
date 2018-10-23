package com.syxgo.electrombile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.model.OrderData;

import java.util.List;

/**
 * Created by tangchujia on 2017/10/18.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context mContext;
    private List<OrderData> mData;

    public OrderAdapter(Context mContext, List<OrderData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order, parent, false);
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
            OrderData order = mData.get(position);
            holder.orderId.setText(order.getOrder_no());
            holder.orderTime.setText(order.getCreatedTime());
            holder.userId.setText(order.getUser_id() + "");
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
        TextView orderId;
        TextView orderTime;
        TextView userId;
        TextView arrow;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            orderId = (TextView) itemView.findViewById(R.id.order_id);
            orderTime = (TextView) itemView.findViewById(R.id.order_time);
            userId = (TextView) itemView.findViewById(R.id.user_id);
            arrow = (TextView) itemView.findViewById(R.id.arrow_tv);
        }
    }

    public void setDateChanged(List<OrderData> orders) {
        this.mData = orders;
        this.notifyDataSetChanged();
    }
}
