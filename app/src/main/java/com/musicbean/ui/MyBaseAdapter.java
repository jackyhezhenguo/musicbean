package com.musicbean.ui;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {

    protected ArrayList<T> mData = null;

    protected MyBaseAdapter() {
        mData = new ArrayList<T>();
    }

    public void addData(T object) {
        if ((mData != null) && (object != null)) {
            mData.add(object);
            notifyDataSetChanged();
        }
    }

    public void addData(T object, int index) {
        if ((mData != null) && (object != null)) {
            mData.add(index, object);
            notifyDataSetChanged();
        }
    }

    public void setData(T object) {
        if ((mData != null) && (object != null)) {
            mData.clear();
            mData.add(object);
            notifyDataSetChanged();
        }
    }


    public void addDatas(List<T> datas) {
        if ((mData != null) && (datas != null)) {
            mData.addAll(datas);
            notifyDataSetChanged();
        }
    }


    public void setDatas(List<T> datas) {
        if ((mData != null) && (datas != null)) {
            mData.clear();
            mData.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public void reset() {
        if (mData != null) {
            mData.clear();
        }
    }


    @Override
    public int getCount() {
        return (mData != null) ? mData.size() : 0;
    }

    @Override
    public T getItem(int position) {
        if ((position < getCount()) && (position >= 0)) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
