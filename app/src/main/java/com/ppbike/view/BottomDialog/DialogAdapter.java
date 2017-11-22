package com.ppbike.view.BottomDialog;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.ppbike.R;

import java.util.ArrayList;

public class DialogAdapter extends BaseAdapter{
	private final FragmentActivity context ;
	private final ArrayList<String> dataList;
	private final Dialog dialog;
	private final OnDialogItemClickListener onItemClickListener;
	public DialogAdapter(FragmentActivity context, ArrayList<String> dataList, Dialog dialog, OnDialogItemClickListener onItemClickListener) {
		this.context = context;
		this.dataList = dataList;
		this.dialog = dialog;
		this.onItemClickListener = onItemClickListener;
	}
	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public String getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ItemView view = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_dialog_bottom,null);
			view = new ItemView();
			view.button = (Button) convertView.findViewById(R.id.btn_dialog);
			convertView.setTag(view);
		}
		view = (ItemView) convertView.getTag();
		view.button.setText(getItem(position));
		if (position == 0) {
			view.button.setBackgroundResource(R.drawable.dialog_item_top_selector);
		}
		view.button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				onItemClickListener.onDialogItemClick(position, v, dialog);
			}
		});
		return convertView;
	}
	class ItemView {
		public Button button;
	}
}
