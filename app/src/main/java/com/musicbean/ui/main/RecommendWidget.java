package com.musicbean.ui.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.ui.MyBaseAdapter;
import com.musicbean.ui.user.UserPageActivity;

import net.tsz.afinal.FinalBitmap;

import java.util.List;


public class RecommendWidget extends FrameLayout {

    private DataAdapter dataAdapter;

    public RecommendWidget(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.empty_follow_recommend, this);
        ListView listView = (ListView) findViewById(R.id.re_hot_list);
        dataAdapter = new DataAdapter();
        listView.setAdapter(dataAdapter);
    }

    public void setData(List<UserInfoBean> userInfoList) {
        dataAdapter.addDatas(userInfoList);
    }

    class DataAdapter extends MyBaseAdapter<UserInfoBean> {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_hot_recommed, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.img = (ImageView) convertView.findViewById(R.id.re_item_img);
                viewHolder.name = (TextView) convertView.findViewById(R.id.re_item_name);
                viewHolder.count = (TextView) convertView.findViewById(R.id.re_item_count);
                viewHolder.follow = (TextView) convertView.findViewById(R.id.re_item_follow);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final UserInfoBean userInfo = getItem(position);
            FinalBitmap.getInstance().display(viewHolder.img, userInfo.image);
            viewHolder.img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), UserPageActivity.class);
                    intent.putExtra("uinfo", userInfo);
                    v.getContext().startActivity(intent);
                }
            });
            viewHolder.name.setText(userInfo.nickname);
            viewHolder.count.setText(String.valueOf(userInfo.bean));
            if (userInfo.relation == 1) {
                viewHolder.follow.setText("已关注");
            } else {
                viewHolder.follow.setText("关注");
            }
            viewHolder.follow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userInfo.relation == 1) {
                        disfollow(getContext(), userInfo, v);
                    } else {
                        follow(getContext(), userInfo, v);
                    }
                }
            });
            return convertView;
        }
    }

    class ViewHolder {
        ImageView img;
        TextView name;
        TextView count;
        TextView follow;
    }

    private void follow(Context context, final UserInfoBean info, final View view) {
        UserApi.follow(context, info.id, new ResponseHandler() {
            @Override
            public void onStart() {
                view.setEnabled(false);
            }

            @Override
            public void onSuccess(String data) {
                info.relation = 1;
                dataAdapter.notifyDataSetChanged();
                ViewParent parent = getParent();
                if (parent instanceof AttendsWidget) {
                    ((AttendsWidget) parent).refresh();
                }
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }

            @Override
            public void onFinish() {
                view.setEnabled(true);
            }
        });
    }

    private void disfollow(Context context, final UserInfoBean info, final View view) {
        UserApi.disFollow(context, info.id, new ResponseHandler() {
            @Override
            public void onStart() {
                view.setEnabled(false);
            }

            @Override
            public void onSuccess(String data) {
                info.relation = 0;
                dataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }

            @Override
            public void onFinish() {
                view.setEnabled(true);
            }
        });
    }
}
