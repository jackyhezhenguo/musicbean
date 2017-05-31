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
public class RecordListAdapter extends MyBaseAdapter<RecordListAdapter.RecordAdapterBean> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
            vh = new ViewHolder();
            vh.title = (TextView) convertView.findViewById(R.id.title);
            vh.time = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        RecordAdapterBean bean = getItem(position);
        vh.time.setText(bean.time);

        vh.title.setText(bean.title);

        return convertView;
    }

    class ViewHolder {
        TextView title;
        TextView time;
    }

    public static class RecordAdapterBean {
        public String title;
        public String time;

        public RecordAdapterBean(String title, String time) {
            this.time = time;
            this.title = title;
        }
    }

}
