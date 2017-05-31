package com.musicbean.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.musicbean.R;
import com.musicbean.http.bean.HotItemInfo;
import com.musicbean.ui.MyBaseAdapter;

import net.tsz.afinal.FinalBitmap;

/**
 * Created by boyo on 16/6/24.
 */
public class NewAdapter extends MyBaseAdapter<HotItemInfo> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new, parent, false);
            vh = new ViewHolder();
            vh.coverImg = (ImageView) convertView.findViewById(R.id.cover_img);
            vh.typeImage = (ImageView) convertView.findViewById(R.id.cover_type);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        HotItemInfo info = getItem(position);

        FinalBitmap.getInstance().display(vh.coverImg, info.isLive() ? info.image : info.live_image);

        if (info.isLive()) {
            vh.typeImage.setImageResource(R.drawable.zuixin_zhibo);
        } else {
            vh.typeImage.setImageResource(R.drawable.zuixin_huifang);
        }

        return convertView;
    }

    class ViewHolder {
        ImageView coverImg;
        ImageView typeImage;
    }
}
