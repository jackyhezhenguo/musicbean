package com.musicbean.ui.uploadvideos;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * 万能适配器的ViewHoleder
 * 
 * @author Administrator
 *
 */
public class ViewHolder {
	private Context context;
	private View convertView;
	private ViewGroup parent;
	private int position;
	private SparseArray<View> views;

	public ViewHolder(Context context, View convertView, ViewGroup parent, int position) {
		this.context = context;
		this.convertView = convertView;
		this.parent = parent;
		this.position = position;
		views = new SparseArray<View>();
		convertView.setTag(this);

	}

	// ���ViewHolderʵ�����
	public static ViewHolder getViewHolder(Context context, View convertView, ViewGroup parent, int position,
			int resID) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(resID, parent, false);
			return new ViewHolder(context, convertView, parent, position);
		} else {
			return (ViewHolder) convertView.getTag();
		}
	}

	// ʵ�ֻ�ÿؼ�
	public <T extends View> T get(int viewID) {
		T v = (T) views.get(viewID);
		if (v == null) {
			v = (T) convertView.findViewById(viewID);
			views.put(viewID, v);
		}
		return v;

	}

	// ���convertView
	public View getConverView() {
		return convertView;
	}

}
