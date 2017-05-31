package com.musicbean.ui.user;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicbean.R;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.manager.LevelManager;
import com.musicbean.ui.MyBaseAdapter;

import net.tsz.afinal.FinalBitmap;

/**
 * Created by boyo on 16/7/7.
 */
public class FansRankAdapter extends MyBaseAdapter<UserInfoBean> {

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (position > 3) {
            position = 3;
        }
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            if (type == 0) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fans_first, parent, false);
            } else if (type == 1) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fans_second, parent, false);
            } else if (type == 2) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fans_third, parent, false);
            } else {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fans_normal, parent, false);
                vh.rank = (TextView) convertView.findViewById(R.id.rank);
            }

            vh.name = (TextView) convertView.findViewById(R.id.name);
            vh.contribution = (TextView) convertView.findViewById(R.id.contribution);
            vh.img = (ImageView) convertView.findViewById(R.id.head_image);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        UserInfoBean info = getItem(position);

        FinalBitmap.getInstance().display(vh.img, info.image);
        vh.name.setText(LevelManager.getInstance().getNameSexLevelSS(info));

        SpannableString ss = new SpannableString("贡献" + info.contribution + "豆");
        ForegroundColorSpan fcs = new ForegroundColorSpan(parent.getResources().getColor(R.color.text_green));
        ss.setSpan(fcs, 2, 2 + info.contribution.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        vh.contribution.setText(ss);

        if (type == 3) {
            vh.rank.setText("NO." + (position + 1));
        }

        return convertView;
    }


    class ViewHolder {
        TextView rank;
        ImageView img;
        TextView name;
        TextView contribution;
    }

}
