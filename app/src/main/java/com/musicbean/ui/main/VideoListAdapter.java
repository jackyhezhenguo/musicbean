package com.musicbean.ui.main;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicbean.R;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.http.bean.VideoListItem;
import com.musicbean.ui.MyBaseAdapter;
import com.musicbean.ui.user.UserPageActivity;
import com.musicbean.util.DateUtils;

import net.tsz.afinal.FinalBitmap;

/**
 * Created by boyo on 16/11/26.
 */
public class VideoListAdapter extends MyBaseAdapter<VideoListItem> implements View.OnClickListener {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_list, parent, false);
            vh = new ViewHolder();
            vh.name = (TextView) convertView.findViewById(R.id.name);
            vh.headImg = (ImageView) convertView.findViewById(R.id.head_img);
            vh.headImg.setOnClickListener(this);
            vh.coverImg = (ImageView) convertView.findViewById(R.id.cover_img);
            vh.title = (TextView) convertView.findViewById(R.id.title);
            vh.duration = (TextView) convertView.findViewById(R.id.duration);
            vh.btnPlay = (ImageView) convertView.findViewById(R.id.btn_play);
            vh.btnPlay.setOnClickListener(this);
            vh.playNum = (TextView) convertView.findViewById(R.id.play_num);
            vh.videoContainer = (ViewGroup) convertView.findViewById(R.id.video_container);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        VideoListItem info = getItem(position);

        vh.name.setText(info.userinfo.nickname);
        vh.title.setText(info.title);
        vh.headImg.setTag(info.userinfo);

        if (TextUtils.isEmpty(info.playnum)) info.playnum = "0";
        SpannableString ss = new SpannableString(parent.getResources().getString(R.string.play_num, info.playnum));
        int len = info.playnum.length();
        ForegroundColorSpan fcs = new ForegroundColorSpan(parent.getResources().getColor(R.color.custom_green));
        ss.setSpan(fcs, 0, len, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(17, true);
        ss.setSpan(ass, 0, len, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        vh.playNum.setText(ss);

        FinalBitmap.getInstance().display(vh.headImg, info.userinfo.image);
        FinalBitmap.getInstance().display(vh.coverImg, info.cover);

        vh.btnPlay.setTag(R.id.tag_first, vh.videoContainer);
        vh.btnPlay.setTag(R.id.tag_second, position);

        vh.duration.setText(DateUtils.formatDuration(info.duration * 1000));

        return convertView;
    }

    class ViewHolder {
        TextView playNum;
        TextView name;
        ImageView headImg;
        ImageView coverImg;
        TextView title;
        TextView duration;
        ImageView btnPlay;
        ViewGroup videoContainer;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                ViewGroup vg = (ViewGroup) v.getTag(R.id.tag_first);
                int pos = (Integer) v.getTag(R.id.tag_second);
                if (mListener != null) {
                    mListener.playInsideView(vg,
                            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, vg.getHeight()),
                            pos);
                }

                break;
            case R.id.head_img:
                UserInfoBean info = (UserInfoBean) v.getTag();
                Intent intent = new Intent(v.getContext(), UserPageActivity.class);
                intent.putExtra("uinfo", info);
                v.getContext().startActivity(intent);
                break;
        }
    }

    public interface OnPlayListener {
        void playInsideView(ViewGroup vg, ViewGroup.LayoutParams lp, int pos);
    }

    private OnPlayListener mListener;

    public void setOnPlayListener(OnPlayListener l) {
        mListener = l;
    }

    public static int getItemViewPosition(View view) {
        if (isItemView(view)) {
            ViewHolder holder = (ViewHolder) view.getTag();
            return (Integer) holder.btnPlay.getTag(R.id.tag_second);
        }
        return -1;
    }

    public static boolean isItemView(View v) {
        return v != null && v.getTag() != null && v.getTag() instanceof ViewHolder;
    }
}
