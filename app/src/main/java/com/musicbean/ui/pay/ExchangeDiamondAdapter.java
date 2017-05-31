package com.musicbean.ui.pay;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.musicbean.R;
import com.musicbean.http.bean.ExchangeInfo;
import com.musicbean.ui.MyBaseAdapter;

/**
 * Created by boyo on 16/7/8.
 */
public class ExchangeDiamondAdapter extends MyBaseAdapter<ExchangeInfo> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exchange, parent, false);
            vh = new ViewHolder();
            vh.diamonds = (TextView) convertView.findViewById(R.id.diamonds);
            vh.beans = (TextView) convertView.findViewById(R.id.beans);

            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        ExchangeInfo info = getItem(position);

        vh.diamonds.setText(info.diamonds + "");
        vh.beans.setText(info.beans + "音悦豆");

        return convertView;
    }

    class ViewHolder {
        TextView diamonds;
        TextView beans;
    }

}
