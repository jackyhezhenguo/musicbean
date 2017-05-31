package com.musicbean.ui.PopularityList;

import android.content.Context;

import com.musicbean.ui.uploadvideos.CommonAdapter;
import com.musicbean.ui.uploadvideos.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public class ChildPlaceAdapter extends CommonAdapter<CompetitionBean>{

    public ChildPlaceAdapter(Context context, List<CompetitionBean> list, int resID) {
        super(context, list, resID);
    }

    @Override
    public void setItemView(ViewHolder vh, int position, List<CompetitionBean> list) {

    }
}
