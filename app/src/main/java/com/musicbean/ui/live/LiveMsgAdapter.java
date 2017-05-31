package com.musicbean.ui.live;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.musicbean.R;
import com.musicbean.http.bean.MessageInfo;
import com.musicbean.manager.LevelManager;
import com.musicbean.ui.MyBaseAdapter;

/**
 * Created by boyo on 16/7/2.
 */
public class LiveMsgAdapter extends MyBaseAdapter<MessageInfo> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg, parent, false);
        }

        TextView tv = (TextView) convertView;
        MessageInfo info = getItem(position);

        if (!TextUtils.isEmpty(info.level)) {
            SpannableString ss = new SpannableString(". " + info.name + ": " + info.msg);
            ImageSpan is = LevelManager.getInstance().getLevelSpan(Integer.parseInt(info.level));
            ss.setSpan(is, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan fcs = new ForegroundColorSpan(Color.parseColor("#9dc82e"));
            ss.setSpan(fcs, 1, info.name.length() + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tv.setText(ss);
        } else {
            SpannableString ss = new SpannableString(info.name + ": " + info.msg);
            ForegroundColorSpan fcs = new ForegroundColorSpan(Color.parseColor("#9dc82e"));
            ss.setSpan(fcs, 0, info.name.length() + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tv.setText(ss);
        }

        return convertView;
    }

    @Override
    public void addData(MessageInfo object) {

        super.addData(object);
    }
}
