package com.wlib.q.baseview;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * @author weiliang
 * 2015年11月24日
 * @说明:
 * 此类是UI 中 Dialog 基础类
 */
public class BaseDialog {
	protected static Dialog dialog;
	protected Activity activity;
	private OnClickListener cancelOnClick;

	protected BaseDialog(Activity activity) {
		this.activity =activity;
		if (dialog==null) {
			dialog = new Dialog(activity);
		}
	}
	
	protected DialogModel getDialogModel(String title,String content,String confirm,String cancel) {
		return new DialogModel(title, content, confirm, cancel);
	}

	protected void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	protected void showDialog() {
		if (dialog != null && !dialog.isShowing()) {
			dialog.show();
		}
	}

	public Dialog getDialog(DialogModel dialogModel,OnClickListener dOnClickListener) {
		return showDialog(dialogModel,dOnClickListener,false);
	}
	
	// 提示窗口
	public Dialog showDialog(DialogModel dialogModel,OnClickListener dOnClickListener,boolean isShow) {
		if (!BaseJudge.isActivityRunning(activity)) {
			return null;
		}
		AlertDialog.Builder builer = new Builder(activity);
		builer.setTitle(dialogModel.title);
		builer.setMessage(dialogModel.content);
		builer.setNegativeButton(dialogModel.confirm, dOnClickListener);
		builer.setPositiveButton(dialogModel.cancel, cancelOnClick==null?new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}:cancelOnClick);

		dialog = builer.create();
		dialog.setCanceledOnTouchOutside(false);
		if (isShow) {
			showDialog();
		}
		return dialog;
	}
	
	public OnClickListener getCancelOnClick() {
		return cancelOnClick;
	}

	public void setCancelOnClick(OnClickListener cancelOnClick) {
		this.cancelOnClick = cancelOnClick;
	}

	public class DialogModel{

		public DialogModel(String title,String content){
			this.title = title;
			this.content =content;
			this.confirm = "确定";
			this.cancel = "取消";
		};
		public DialogModel(String title,String content,String confirm,String cancel) {
			this.title = title;
			this.content =content;
			this.confirm = confirm;
			this.cancel = cancel;
		}
		
		public String title;
		public String content;
		public String confirm = "确定";
		public String cancel = "取消";
	}
}
