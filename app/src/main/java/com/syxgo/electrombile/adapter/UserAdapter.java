package com.syxgo.electrombile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.model.UserData;

import java.util.List;

/**
 * Created by tangchujia on 2017/10/19.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<UserData> mData;

    public UserAdapter(List<UserData> mData) {
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user, parent, false);
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
            UserData user = mData.get(position);
            holder.userId.setText(user.getUser_id() + "");
            holder.userBike.setText(user.getBike_id() + "");
            holder.userName.setText(user.getReal_name());
            holder.userPhone.setText(user.getPhone());
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
        TextView userName;
        TextView userBike;
        TextView userPhone;
        TextView arrow;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            userBike = (TextView) itemView.findViewById(R.id.user_bike);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            userId = (TextView) itemView.findViewById(R.id.user_id);
            userPhone = (TextView) itemView.findViewById(R.id.user_phone);
            arrow = (TextView) itemView.findViewById(R.id.arrow_tv);
        }
    }

    public void setDateChanged(List<UserData> users) {
        this.mData = users;
        this.notifyDataSetChanged();
    }
}
