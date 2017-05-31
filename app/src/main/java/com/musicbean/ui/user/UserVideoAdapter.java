package com.musicbean.ui.user;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicbean.R;
import com.musicbean.http.bean.HotItemInfo;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.http.bean.VideoInfo;
import com.musicbean.ui.live.PlaybackActivity;

import net.tsz.afinal.FinalBitmap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserVideoAdapter extends RecyclerView.Adapter<UserVideoAdapter.VideoViewHolder> implements View.OnClickListener {
    private List<VideoInfo> dataList;
    private Context context;
    private UserInfoBean userInfo;

    public UserVideoAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<VideoInfo> dataList, UserInfoBean userInfo) {
        this.dataList = dataList;
        this.userInfo = userInfo;
    }

    public void addData(List<VideoInfo> dataList) {
        this.dataList.addAll(dataList);
    }

    @Override
    public UserVideoAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        v.setOnClickListener(this);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserVideoAdapter.VideoViewHolder holder, int position) {
        VideoInfo videoInfo = dataList.get(position);
        FinalBitmap.getInstance().display(holder.img, videoInfo.getImage());
        String playCount = context.getString(R.string.play_count, String.valueOf(videoInfo.getPlaynum()));
        holder.count.setText(playCount);
        holder.name.setText(videoInfo.getTitle());
        String date = getDateDesc(videoInfo.getPubdate());
        holder.date.setText(date);
        holder.itemView.setTag(videoInfo);
    }

    private String getDateDesc(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        long day = 0;
        try {
            Date begin = format.parse(date);
            day = (System.currentTimeMillis() - begin.getTime()) / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (day <= 0) {
            return "当天";
        } else {
            return day + "天前";
        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, PlaybackActivity.class);
        intent.putExtra(PlaybackActivity.EXTRA_ANCHOR_DATA, userInfo);
        VideoInfo videoInfo = (VideoInfo) v.getTag();
        intent.putExtra(PlaybackActivity.EXTRA_PLAY_URL, videoInfo.getPlay_addr());
        intent.putExtra(PlaybackActivity.EXTRA_VIDEO_ID, videoInfo.getId());
        intent.putExtra(PlaybackActivity.EXTRA_TYPE, HotItemInfo.video);
        context.startActivity(intent);
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView count;
        TextView name;
        TextView date;

        VideoViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.video_item_image);
            count = (TextView) itemView.findViewById(R.id.video_item_count);
            name = (TextView) itemView.findViewById(R.id.video_item_name);
            date = (TextView) itemView.findViewById(R.id.video_item_time);
        }
    }
}
