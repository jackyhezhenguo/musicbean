package com.musicbean.ui.user;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.musicbean.R;

import java.util.List;

/**
 * Created by Administrator on 2017/5/17 0017.
 */

public class ChildZoneAdapter extends RecyclerView.Adapter<ChildZoneAdapter.ChildViewHolder> {
private List<String> mDatas;
private Context  context;

    public ChildZoneAdapter(List<String> mDatas,Context context){
        this.mDatas = mDatas;
        this.context = context;
    }

    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChildViewHolder holder = new ChildViewHolder(LayoutInflater.from(context).inflate(R.layout.child_item,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ChildViewHolder holder, int position) {
        holder.tv.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder{
        TextView tv ;
        public ChildViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_letter);
        }
    }
}

