package com.wlib.q.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author weiliang
 * 2015年11月2日
 * @说明:
 */
public abstract class StringAdapter<T> extends AbsAdapter<T> {
	protected int mItemRes, mTextResId;
	protected int mDropDownItemRes, mDropDownTextId;

	public StringAdapter(Context context, List<T> data, int itemRes, int textResId) {
		super(context, data);
		mItemRes = itemRes;
		mTextResId = textResId;
	}

	public void setDropDownViewResource(int resource, int textId) {  // spinner 

		mDropDownItemRes = resource;
		mDropDownTextId = textId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, mItemRes, mTextResId);
	}

	private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource, int textId) {
		View view;
		TextView text;
		if (convertView == null) {
			view = getInflater().inflate(resource, parent, false);
		} else {
			view = convertView;
		}
		if (textId <= 0) {
			text = (TextView) view;
		} else {
			text = (TextView) view.findViewById(textId);
		}
		text.setText(getString(getItem(position)));
		return view;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent,
				mDropDownItemRes, mDropDownTextId);
	}

	protected abstract String getString(T item);
}
