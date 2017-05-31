package com.musicbean.ui.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.manager.LevelManager;

import net.tsz.afinal.FinalBitmap;

/**
 * Created by boyo on 16/7/8.
 */
public class ManagerListAdapter extends UserListAdapter implements View.OnClickListener {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            vh = new ViewHolder();
            vh.image = (ImageView) convertView.findViewById(R.id.head_image);
            vh.name = (TextView) convertView.findViewById(R.id.name);
            vh.button = (CheckBox) convertView.findViewById(R.id.btn_follow);
            //vh.button.setOnCheckedChangeListener(this);
            vh.button.setChecked(true);
            vh.button.setText("删除");
            vh.button.setOnClickListener(this);
            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        UserInfoBean info = getItem(position);

        FinalBitmap.getInstance().display(vh.image, info.image);
        vh.name.setText(LevelManager.getInstance().getNameSexLevelSS(info));
        vh.button.setChecked(true);
        vh.button.setTag(info);

        return convertView;
    }

//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        UserInfoBean info = (UserInfoBean) buttonView.getTag();
//        if (info == null) {
//            return;
//        }
//
//        if (isChecked) {
//            delManager(buttonView.getContext(), info);
//            mData.remove(info);
//            notifyDataSetChanged();
//        }
//    }

    @Override
    public void onClick(View v) {
        UserInfoBean info = (UserInfoBean) v.getTag();
        if (info == null) {
            return;
        }

        delManager(v.getContext(), info);
        mData.remove(info);
        notifyDataSetChanged();
    }

    private void delManager(Context context, final UserInfoBean info) {
        UserApi.delManager(context, info.id, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {

            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

}
