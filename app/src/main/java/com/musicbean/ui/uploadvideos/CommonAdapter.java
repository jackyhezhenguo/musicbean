package com.musicbean.ui.uploadvideos;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

/**
 * 万能适配器
 * 
 * @author Administrator
 *
 * @param <T>
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

	private List<T> list;
	private Context context;
	private int resID;

	public CommonAdapter(Context context, List<T> list, int resID) {
		this.context = context;
		this.list = (List<T>) list;
		this.resID = resID;
	}
	
	public void setList(List<T> list){
		this.list = list;
	}
	
	public List<T> getList(){
		return this.list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = ViewHolder.getViewHolder(context, convertView, parent, position, resID);
		setItemView(vh, position, list);
		return vh.getConverView();
	}

	public abstract void setItemView(ViewHolder vh, int position, List<T> list);

}
