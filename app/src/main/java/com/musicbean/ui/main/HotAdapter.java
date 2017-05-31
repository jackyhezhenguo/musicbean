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
import com.musicbean.http.bean.HotItemInfo;
import com.musicbean.ui.MyBaseAdapter;
import com.musicbean.ui.user.UserPageActivity;

import net.tsz.afinal.FinalBitmap;

/**
 * Created by boyo on 16/6/18.
 */
public class HotAdapter extends MyBaseAdapter<HotItemInfo> implements View.OnClickListener {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hot, parent, false);
            vh = new ViewHolder();
            vh.name = (TextView) convertView.findViewById(R.id.name);
            vh.subname = (TextView) convertView.findViewById(R.id.subname);
            vh.onlineNum = (TextView) convertView.findViewById(R.id.online_num);
            vh.headImg = (ImageView) convertView.findViewById(R.id.head_img);
            vh.headImg.setOnClickListener(this);
            vh.coverImg = (ImageView) convertView.findViewById(R.id.cover_img);
            vh.status = (TextView) convertView.findViewById(R.id.status);
            vh.liveTitle = (TextView) convertView.findViewById(R.id.live_title);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        HotItemInfo info = getItem(position);

        vh.name.setText(info.anchorinfo.nickname);
        vh.subname.setText(info.anchorinfo.studio);
        vh.liveTitle.setText(info.getTitle());
        vh.headImg.setTag(info);

        if (TextUtils.isEmpty(info.online_users)) info.online_users = info.playnum;
        int strid = info.isLive() ? R.string.online_num : R.string.income_num;
        SpannableString ss = new SpannableString(parent.getResources().getString(strid, info.online_users));
        int len = info.online_users.length();
        ForegroundColorSpan fcs = new ForegroundColorSpan(parent.getResources().getColor(R.color.custom_green));
        ss.setSpan(fcs, 0, len, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(17, true);
        ss.setSpan(ass, 0, len, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        vh.onlineNum.setText(ss);

        FinalBitmap.getInstance().display(vh.headImg, info.anchorinfo.image);
        FinalBitmap.getInstance().display(vh.coverImg, info.isLive() ? info.image : info.live_image);

        vh.status.setText(info.isLive() ? "直播" : "回放");

        return convertView;
    }

    class ViewHolder {
        TextView name;
        TextView subname;
        TextView onlineNum;
        ImageView headImg;
        ImageView coverImg;
        TextView liveTitle;
        TextView status;
    }

    @Override
    public void onClick(View v) {
        HotItemInfo info = (HotItemInfo) v.getTag();
        Intent intent = new Intent(v.getContext(), UserPageActivity.class);
        intent.putExtra("uinfo", info.anchorinfo);
        v.getContext().startActivity(intent);
    }
}
