package com.musicbean.ui.pay;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.musicbean.R;
import com.musicbean.ui.MyBaseAdapter;

/**
 * Created by boyo on 16/7/8.
 */
public class BuyAdapter extends MyBaseAdapter<BuyInfo> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buy, parent, false);
            vh = new ViewHolder();
            vh.diamonds = (TextView) convertView.findViewById(R.id.diamonds);
            vh.money = (TextView) convertView.findViewById(R.id.money);
            vh.free = (TextView) convertView.findViewById(R.id.free);

            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        BuyInfo info = getItem(position);

        vh.diamonds.setText(info.diamond + "");
        vh.money.setText(info.money + "元");
        if (info.free > 0) {
            vh.free.setText("赠送" + info.free + "钻");
            vh.free.setVisibility(View.VISIBLE);
        } else {
            vh.free.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    class ViewHolder {
        TextView diamonds;
        TextView money;
        TextView free;
    }

}
