package com.musicbean.ui.live;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.musicbean.R;
import com.musicbean.http.bean.GiftBean;
import com.musicbean.ui.MyBaseAdapter;

import net.tsz.afinal.FinalBitmap;

/**
 * Created by boyo on 16/7/3.
 */
public class GiftAdapter extends MyBaseAdapter<GiftBean> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift, parent, false);
            vh = new ViewHolder();
            vh.diamond = (TextView) convertView.findViewById(R.id.diamond);
            vh.image = (ImageView) convertView.findViewById(R.id.img);
            vh.tag = (ImageView) convertView.findViewById(R.id.tag);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        GiftBean info = getItem(position);

        vh.diamond.setText(info.diamond+"");
        //FinalBitmap.getInstance().display(vh.image, info.image);
        Glide.with(convertView.getContext()).load(info.image).into(vh.image);

        if (info.isSelected) {
            vh.tag.setImageResource(R.drawable.icon_gift_checked);
        } else {
            if (info.is_doublehit == 1) {
                vh.tag.setImageResource(R.drawable.icon_gift_lianji);
            } else {
                vh.tag.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }

        return convertView;
    }

    class ViewHolder {
        ImageView image;
        TextView diamond;
        ImageView tag;
    }
}
