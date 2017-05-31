package com.musicbean.ui.live;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.musicbean.R;
import com.musicbean.http.bean.UserInfoBean;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boyo on 16/7/2.
 */
public class LiveUsersAdapter extends RecyclerView.Adapter<LiveUsersAdapter.UserHeadRecyclerHolder> implements View.OnClickListener {

    private ArrayList<UserInfoBean> mData = new ArrayList();

    static class UserHeadRecyclerHolder extends RecyclerView.ViewHolder {
        ImageView headerImg;

        public UserHeadRecyclerHolder(View itemView) {
            super(itemView);
            headerImg = (ImageView) itemView.findViewById(R.id.user_image);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public void onBindViewHolder(UserHeadRecyclerHolder holder, int position) {
        FinalBitmap.getInstance().display(holder.headerImg, mData.get(position).image);
        holder.itemView.setTag(mData.get(position));
    }

    @Override
    public UserHeadRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_user_head, parent, false);
        UserHeadRecyclerHolder vh = new UserHeadRecyclerHolder(view);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        return vh;
    }

    public void addDatas(List<UserInfoBean> datas) {
        if ((mData != null) && (datas != null)) {
            mData.addAll(datas);
            notifyDataSetChanged();
        }
    }


    public void setDatas(List<UserInfoBean> datas) {
        if ((mData != null) && (datas != null)) {
            mData.clear();
            mData.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public void reset() {
        if (mData != null) {
            mData.clear();
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (UserInfoBean) v.getTag());
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, UserInfoBean data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

}
