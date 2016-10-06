package com.wlib.q;

import com.wlib.base.model.RequestParams;
import com.wlib.q.baseview.BaseDialog;
import com.wlib.q.camera.CameraActivity;
import com.wlib.q.exception.CrashHandler.CurActivityHandler;
import com.wlib.q.file.FileUtils;
import com.wlib.q.file.SpUtils;
import com.wlib.q.phone.Device;
import com.wlib.q.phone.GPS;
import com.wlib.q.utils.AppInfoUtils;
import com.wlib.q.utils.BitmapUtils;
import com.wlib.q.utils.StringUtils;
import com.wlib.q.viewutils.DialogUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener,CurActivityHandler{
    private ImageView imageView;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WlibApplication.setmCurActivityHandler(this);
        setContentView(R.layout.activity_main);
        findViewById(R.id.open_gps).setOnClickListener(this);
        findViewById(R.id.open_gps_setting).setOnClickListener(this);
        imageView = (ImageView) findViewById(R.id.circle_view);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.network);
        imageView.setImageBitmap(BitmapUtils.clipToOval(bitmap));
	}

    @Override
    public void onClick(View v) {
    	if (v.getId() == R.id.open_gps) {
    		GPS.openGps(this);
		}else if (v.getId() == R.id.open_gps_setting) {
			GPS.openGpsGraceful(this);
		}else if (v.getId() == R.id.open_camera) {
			startActivity(new Intent(MainActivity.this, CameraActivity.class));
		}
    }

    @Override
    protected void onResume() {
    	super.onResume();

    	Device device = Device.getInstance(this);
    	Log.i("Device", device.getInfoStr(this));

    	Log.i("GPS", GPS.isGpsOpen(this)+"");
    	Log.i("AGPS", GPS.isAgpsOpen(this)+"");
//    	new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				String ip= Network.IP_Me(false);
//				String dns = Network.DNS(1, false);
//				Looper.prepare();
//				Toast.makeText(MainActivity.this, "ip="+ip+"  dns="+dns, Toast.LENGTH_LONG).show();
//				Looper.loop();
//				Log.i("Network", "ip="+ip+"  dns="+dns);
//			}
//		}).start();

    	String path = getCacheDir().getAbsolutePath();
    	Log.i("FileUtils","isExistsFile? "+path+"  "+FileUtils.isExistsFile(path));
    	Log.i("getChannelName", ""+AppInfoUtils.getChannelName(this,"channelId"));

    	Log.i("numberCheck", StringUtils.numberCheck("已指派工作人员 黄伟良-15989365832;14717526123"));

//    	NetAccess netAccess = NetAccess.getInstance();
//    	String url = "http://220.197.207.238:24931/appservice4/coupons_v2/select_coupons_list.do";
//    	Map<String, String> params = new HashMap<String, String>();
//    	params.put("uid", "12794");
//    	params.put("requestPage", "1");
//    	RequestParams requestParams = new RequestParams();
//    	requestParams.setUid("12794");
//    	requestParams.setRequestPage(1);
//    	requestParams.setTime(System.currentTimeMillis());
//    	requestParams.setMoney(7.5d);
//    	Map<String, Object> params = requestParams.toMap();
//    	netAccess.request(RequsetType.GET, url, params, new IHttpCallback() {
//
//			@Override
//			public void onSuccess(String data) {
//				L.i("onSuccess", "data = "+data);
//                showDialog(data.substring(1,19));
//			}
//
//			@Override
//			public void onError(HttpData httpData) {
//				L.i("onError", "httpData = "+httpData.toString());
//				showDialog(httpData.getInfo());
//			}
//
//			@Override
//			public void onFailed(int flag, String message) {
//				L.i("onFailed", "flag = "+flag+" |  message = "+message);
//                showDialog(message);
//			}
//		});

//    	testMypreference();

//    	try {
//			String result = PingUtils.pingIp(3, "119.29.82.26");
//			System.out.println("result = "+result);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//    	createShortCut(this, R.drawable.logo, R.string.app_name);

    }

    //替换快捷方式
    public static void createShortCut(Activity act, int iconResId,
            int appnameResId) {

        // com.android.launcher.permission.INSTALL_SHORTCUT  

        Intent shortcutintent = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建  
        shortcutintent.putExtra("duplicate", false);
        // 需要现实的名称  
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                act.getString(appnameResId));
        // 快捷图片  
        Parcelable icon = Intent.ShortcutIconResource.fromContext(
                act.getApplicationContext(), iconResId);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        // 点击快捷图片，运行的程序主入口  
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
                new Intent(act.getApplicationContext(), act.getClass()));
        // 发送广播  
        act.sendBroadcast(shortcutintent);
    }


    public void showDialog(final String content){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtil dialogUtil = new DialogUtil(MainActivity.this);
                BaseDialog.DialogModel dialogModel = dialogUtil.new DialogModel("测试",content);
                dialogUtil.showDialog(dialogModel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this,"测试完毕",Toast.LENGTH_LONG).show();
                    }
                },true);
            }
        });

        testMypreference();
    }

    private void testMypreference() {
    	SpUtils spUtils = SpUtils.getInstance();
    	spUtils.setValue("String", "String");
    	spUtils.setValue("Integer", 15);
    	spUtils.setValue("Double", 14.5d);
    	spUtils.setValue("Float", 333.222f);
    	spUtils.setValue("Long", System.currentTimeMillis());
    	spUtils.setValue("Boolean", false);

    	String s = (String) spUtils.getValue("String", "");
    	Integer i = (Integer) spUtils.getValue("Integer", -1);
    	Double d = (Double) spUtils.getValue("Double", -1.1d);
    	Float f = (Float) spUtils.getValue("Float", -1.22f);
    	Long l = (Long) spUtils.getValue("Long", -32l);
    	Object b = spUtils.getValue("RequestParams", new RequestParams());

    	L.d("testMypreference", "s = "+s +" i = "+i+" d = "+d+" f = "+f+" l = "+l+" b = "+b);
	}

	@Override
	public void onHandler() {
		Toast.makeText(this, "发生异常", Toast.LENGTH_LONG).show();
	}
}
