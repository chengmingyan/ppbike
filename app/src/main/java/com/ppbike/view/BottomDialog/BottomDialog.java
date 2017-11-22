package com.ppbike.view.BottomDialog;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.ppbike.R;

import java.util.ArrayList;

public class BottomDialog {
	public static Dialog showBottomDialog(FragmentActivity context, ArrayList<String> buttonList, OnDialogItemClickListener onItemClickListener) {
		
		final Dialog dialog = new Dialog(context, R.style.BottomDialog);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom, null);
		ListView listView = (ListView) view.findViewById(R.id.listView);
		listView.setAdapter(new DialogAdapter(context, buttonList,dialog,onItemClickListener));
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.BottomDialog_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = context.getWindowManager().getDefaultDisplay().getHeight();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = LayoutParams.MATCH_PARENT;
		wl.height = LayoutParams.WRAP_CONTENT;

		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		return dialog;
	}

}
