package com.wlib.q.camera;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.wlib.q.R;

public class CameraActivity extends Activity implements View.OnClickListener{
	SurfaceView mSurfaceView;
    ToggleButton toggleCamBtn;
    CameraHandler cameraHandler;
    Button spotlightMode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_camera_view);
		initView();
        cameraHandler = new CameraHandler(this,mSurfaceView);
//        setFlashText(true);
	}

	private  void initView(){
        toggleCamBtn = (ToggleButton) findViewById(R.id.change_cam);
        toggleCamBtn.setOnClickListener(this);
		mSurfaceView = (SurfaceView)findViewById(R.id.m_surface);
        spotlightMode = (Button)findViewById(R.id.spotlight_mode);
	}

    private void setFlashText(boolean isFirst){
        String flashModeStr = cameraHandler.changeSpotLight(isFirst);
        if (flashModeStr.equals("auto"))
            flashModeStr = "自动";
        else if(flashModeStr.equals("on"))
            flashModeStr = "开启";
        else if (flashModeStr.equals("off"))
            flashModeStr = "关闭";

        spotlightMode.setText("闪光灯模式\n（"+flashModeStr +"）");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.change_cam){
            cameraHandler.changeCamera();
        }else if(v.getId() == R.id.spotlight_mode){
            setFlashText(false);
        }else if(v.getId() == R.id.take_pic_btn){
            cameraHandler.takePicture();
        }
    }
}
