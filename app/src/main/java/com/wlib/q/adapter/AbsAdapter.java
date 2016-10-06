package com.wlib.q.adapter;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

/**
 * @author weiliang
 * 2015年11月2日
 * @说明:
 */
public abstract class AbsAdapter<T> extends BaseAdapter {
	public final List<T> EMPTY = Collections.emptyList();
	private List<T> mData;
	private LayoutInflater mInflater;

	public AbsAdapter(Context context) {
		this(context, null);
	}

	public AbsAdapter(Context context, List<T> data) {
		mInflater = LayoutInflater.from(context);
		mData = data == null ? EMPTY : data;
	}

	public void setDataSource(List<T> data) {
		mData = data == null ? EMPTY : data;
		notifyDataSetChanged();
	}

	public List<T> getData() {
		return mData;
	}

	protected LayoutInflater getInflater() {
		return mInflater;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public T getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
