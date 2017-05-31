package com.musicbean.ui.PopularityList;

import android.content.Context;

import com.musicbean.ui.uploadvideos.CommonAdapter;
import com.musicbean.ui.uploadvideos.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public class ChildContributionAdapter extends CommonAdapter<ContributionBean>{

    public ChildContributionAdapter(Context context, List<ContributionBean> list, int resID) {
        super(context, list, resID);
    }

    @Override
    public void setItemView(ViewHolder vh, int position, List<ContributionBean> list) {

    }
}
