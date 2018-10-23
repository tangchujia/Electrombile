package com.syxgo.electrombile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.syxgo.electrombile.R;
import com.syxgo.electrombile.model.ECU;

import java.util.List;

/**
 * Created by tangchujia on 2017/8/29.
 */

public class EcuAdapter extends RecyclerView.Adapter<EcuAdapter.ViewHolder> {
    private Context context;
    private List<ECU> mData;

    public EcuAdapter(List<ECU> mData) {
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_ecu, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EcuAdapter.ViewHolder holder, int position) {
        ECU ecu = mData.get(position);
        holder.id.setText("id："+ecu.getId());
        holder.time.setText(ecu.getCreated());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            id= (TextView) itemView.findViewById(R.id.id_tv);
            time= (TextView) itemView.findViewById(R.id.time_tv);
        }
    }
}
