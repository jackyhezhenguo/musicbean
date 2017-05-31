package com.musicbean.ui.user;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.manager.LevelManager;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.MyBaseAdapter;

import net.tsz.afinal.FinalBitmap;

/**
 * Created by boyo on 16/7/8.
 */
public class UserListAdapter extends MyBaseAdapter<UserInfoBean> implements CompoundButton.OnCheckedChangeListener {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_video, parent, false);
            vh = new ViewHolder();
            vh.image = (ImageView) convertView.findViewById(R.id.head_image);
            vh.name = (TextView) convertView.findViewById(R.id.name);
            vh.button = (CheckBox) convertView.findViewById(R.id.btn_follow);
            vh.button.setOnCheckedChangeListener(this);

            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        UserInfoBean info = getItem(position);

        vh.button.setTag(info);
        FinalBitmap.getInstance().display(vh.image, info.image);
        vh.name.setText(LevelManager.getInstance().getNameSexLevelSS(info));

        if(info.id.equals(LoginManager.getInstance().getUserCookie().userinfo.id)){
            vh.button.setVisibility(View.GONE);
        } else {
            vh.button.setVisibility(View.VISIBLE);
            if (info.relation == 1) {
                vh.button.setChecked(true);
                vh.button.setText("已关注");
            } else {
                vh.button.setChecked(false);
                vh.button.setText("关注");
            }
        }


        return convertView;
    }

    class ViewHolder {
        ImageView image;
        TextView name;
        CheckBox button;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        UserInfoBean info = (UserInfoBean) buttonView.getTag();
        if (info == null) {
            return;
        }

        if (isChecked && info.relation == 0) {
            follow(buttonView.getContext(), info);
        } else if (!isChecked && info.relation == 1) {
            disfollow(buttonView.getContext(), info);
        }
    }

    private void follow(Context context, final UserInfoBean info) {
        UserApi.follow(context, info.id, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                info.relation = 1;
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    private void disfollow(Context context, final UserInfoBean info) {
        UserApi.disFollow(context, info.id, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                info.relation = 0;
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

}
