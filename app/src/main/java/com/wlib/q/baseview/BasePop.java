package com.wlib.q.baseview;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;

import com.wlib.q.viewutils.PopupwindowsUtil;

/**
 * @author weiliang
 * 2015年11月24日
 * @说明:
 */
public class BasePop {
	private static String TAG = PopupwindowsUtil.class.getName();
	protected static PopupWindow popupWindow;
	protected Activity activity;

	protected BasePop(Activity activity) {
		this.activity = activity;
		TAG += activity.getLocalClassName();
		if (popupWindow == null) {
			popupWindow = new PopupWindow();
		}
	}

	/**
	 * 由于popupwindow比较复杂这个方法只是做一个参考 在这个类里面添加相关需求的popupWindow
	 * 
	 * @param anchor
	 */
	protected void showPopupwindow(final TextView anchor) {
		Log.i(TAG, " popupWindow =" + popupWindow);
		if (popupWindow != null && popupWindow.isShowing()) {
			return;
		}
		View contentView = new View(activity);// TODO 初始化对应view
		int[] pos = new int[2];
		anchor.getLocationInWindow(pos);
		// popupWindow外点击 ;
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);

		// 点击返回后 popupwindow可以dismiss
		ColorDrawable dw = new ColorDrawable(-00000);
		popupWindow.setBackgroundDrawable(dw);
		popupWindow.update();
		contentView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
		Log.i(TAG, "pos[0],pos[1] {" + pos[0] + "," + pos[1] + "}");
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				Log.i(TAG, "onDismiss()");
			}
		});
		popupWindow.showAtLocation(anchor, Gravity.CENTER, 0, 0);
	}

	/**
	 * 由于popupwindow比较复杂这个方法只是做一个参考 在这个类里面添加相关需求的popupWindow
	 * 
	 * @param anchor
	 */
	@SuppressWarnings("deprecation")
	protected PopupWindow getBasePopupwindow(final View anchor, View contentView) {
		// Log.i(TAG, " popupWindow =" + popupWindow);
		// if (popupWindow != null && popupWindow.isShowing()) {
		// return;
		// }
		popupWindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		int[] pos = new int[2];
		anchor.getLocationInWindow(pos);
		// popupWindow外点击 ;
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);

		// 点击返回后 popupwindow可以dismiss
		ColorDrawable dw = new ColorDrawable(-00000);
		popupWindow.setBackgroundDrawable(dw);
		popupWindow.update();
		contentView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
		Log.i(TAG, "pos[0],pos[1] {" + pos[0] + "," + pos[1] + "}");
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				Log.i(TAG, "onDismiss()");
			}
		});
		// popupWindow.showAtLocation(anchor, Gravity.CENTER, 0, 0);
		return popupWindow;
	}

	protected void dismiss() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}
}
