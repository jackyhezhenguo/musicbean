package com.musicbean.ui.settings;

import android.content.Context;
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
import com.musicbean.ui.user.UserListAdapter;

import net.tsz.afinal.FinalBitmap;

/**
 * Created by boyo on 16/7/8.
 */
public class NotificationListAdapter extends UserListAdapter {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
            vh = new ViewHolder();
            vh.image = (ImageView) convertView.findViewById(R.id.head_image);
            vh.name = (TextView) convertView.findViewById(R.id.name);
            vh.button = (CheckBox) convertView.findViewById(R.id.switcher);
            vh.button.setOnCheckedChangeListener(this);

            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        UserInfoBean info = getItem(position);

        FinalBitmap.getInstance().display(vh.image, info.image);
        vh.name.setText(LevelManager.getInstance().getNameSexLevelSS(info));

        vh.button.setChecked(info.remind_one == 1);

        vh.button.setTag(info);

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

        if (isChecked && info.remind_one == 0) {
            remind(buttonView.getContext(), 1, info);
        } else if (!isChecked && info.remind_one == 1) {
            remind(buttonView.getContext(), 0, info);
        }
    }

    private void remind(Context context, final int remind, final UserInfoBean info) {
        UserApi.remind(context, info.id, remind, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                info.remind_one = remind;
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

}
