package com.wlib.q.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraHandler {

    private Activity activity;
    private SurfaceView surfaceView;
    private Camera camera;
	private Camera.Parameters parameters = null;
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头

    private static final String CAMERA_PARAM_ORIENTATION = "orientation";
    private static final String CAMERA_PARAM_LANDSCAPE = "landscape";
    private static final String CAMERA_PARAM_PORTRAIT = "portrait";

	public CameraHandler(Activity activity, SurfaceView surfaceView) {
		this.activity = activity;
		this.surfaceView = surfaceView;
        initCameraHandler();
	}

    private  CameraHandler(){

    }

	private void initCameraHandler() {
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//		surfaceView.getHolder().setFixedSize(176, 144); // 设置Surface分辨率
		surfaceView.getHolder().setKeepScreenOn(true);// 屏幕常亮
		surfaceView.getHolder().addCallback(new SurfaceCallback());// 为SurfaceView的句柄添加一个回调函数
	}
	
	private void openCamera(){
		 try {  
             camera = Camera.open(); // 打开摄像头
             camera.setPreviewDisplay(surfaceView.getHolder()); // 设置用于显示拍照影像的SurfaceHolder对象  
             camera.setDisplayOrientation(getCameraDisplayOrientation(0,camera));
             camera.startPreview(); // 开始预览
         } catch (Exception e) {
             e.printStackTrace();  
         }
	}

    public void takePicture(){
        //快门
        camera.autoFocus(new Camera.AutoFocusCallback() {//自动对焦
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if(success) {
                    //设置参数，并拍照
                    Camera.Parameters params = camera.getParameters();
                    params.setPictureFormat(PixelFormat.JPEG);//图片格式
                    Camera.Size size = params.getSupportedPreviewSizes().get(3);
                    params.setPreviewSize(size.width, size.height);//图片大小
                    Camera.Size size2 = params.getSupportedPictureSizes().get(3);
                    params.setPictureSize(size2.width, size2.height);//图片大小
                    camera.setParameters(params);//将参数设置到我的camera
                    camera.takePicture(null, null, jpeg);//将拍摄到的照片给自定义的对象
                }
            }
        });
    }

    //创建jpeg图片回调数据对象
    Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            try {
                Bitmap bitmap0 = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix m = new Matrix();
                m.setRotate(90);
                Bitmap bitmap = Bitmap.createBitmap(bitmap0, 0, 0, bitmap0.getWidth(), bitmap0.getHeight(), m, true);
                //自定义文件保存路径  以拍摄时间区分命名
                File picFileDir = new File(Environment.getExternalStorageDirectory().toString()+
                        File.separator +"MyPictures");//仅创建路径的File对象
                if(!picFileDir.exists()){
                    picFileDir.mkdir();//如果路径不存在就先创建路径
                }
                File picFile = new File(picFileDir, new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".jpeg");//然后再创建路径和文件的File对象
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(picFile));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩的流里面
                bos.flush();// 刷新此缓冲区的输出流
                bos.close();// 关闭此输出流并释放与此流有关的所有系统资源
                camera.stopPreview();//关闭预览 处理数据
                camera.startPreview();//数据处理完后继续开始预览
                bitmap.recycle();//回收bitmap空间
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    public String changeSpotLight(boolean isFirst){
        if (camera!=null){
            parameters = camera.getParameters();
            String flashMode = parameters.getFlashMode();
            if (!isFirst) {
                if (flashMode.equals(Camera.Parameters.FLASH_MODE_AUTO)) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                } else if (flashMode.equals(Camera.Parameters.FLASH_MODE_ON)) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                } else if (flashMode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                } else {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                }
                camera.setParameters(parameters);
            }
            return parameters.getFlashMode();
        }

        return null;
    };

    public void changeCamera(){
        SurfaceHolder holder = surfaceView.getHolder();
        //切换前后摄像头
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数

        for(int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if(cameraPosition == 1) {
                //现在是后置，变更为前置
                if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    camera.stopPreview();//停掉原来摄像头的预览
                    camera.release();//释放资源
                    camera = null;//取消原来摄像头
                    camera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    camera.setDisplayOrientation(getCameraDisplayOrientation(i,camera));
                    camera.startPreview();//开始预览
                    cameraPosition = 0;
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    camera.stopPreview();//停掉原来摄像头的预览
                    camera.release();//释放资源
                    camera = null;//取消原来摄像头
                    camera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    camera.setDisplayOrientation(getCameraDisplayOrientation(i,camera));
                    camera.startPreview();//开始预览
                    cameraPosition = 1;
                    break;
                }
            }

        }
    }

    private void addCameraParams() {
        addCameraParams(0,0);
    }
	
	private void addCameraParams(int width,
            int height) {
		 parameters = camera.getParameters(); // 获取各项参数
         parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式  
         parameters.setPreviewSize(width, height); // 设置预览大小  
         parameters.setPreviewFrameRate(5);  //设置每秒显示4帧
        if ( width != 0&& height != 0)
         parameters.setPictureSize(width, height); // 设置保存的图片尺寸  
         parameters.setJpegQuality(100); // 设置照片质量
	}
	
	class SurfaceCallback implements Callback{

		@Override
		public void surfaceCreated(SurfaceHolder holder) {

            openCamera();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			addCameraParams(width, height);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null) {  
                camera.release(); // 释放照相机  
                camera = null;  
            }  
		}
	}

    public int getCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {

        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();

        android.hardware.Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return  result;
    }
	
	 // 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度  
    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向  
        int rotation = activity.getWindowManager().getDefaultDisplay()  
                .getRotation();  
        int degree = 0;  
        // 根据手机的方向计算相机预览画面应该选择的角度  
        switch (rotation) {  
        case Surface.ROTATION_0:  
            degree = 90;
            break;  
        case Surface.ROTATION_90:  
            degree = 0;
            break;  
        case Surface.ROTATION_180:  
            degree = 270;
            break;  
        case Surface.ROTATION_270:  
            degree = 180;
            break;  
        }  
        return degree;  
    }


    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm
     *            需要旋转的图片
     * @param degree
     *            旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }


    /**
     * 读取图片的旋转的角度
     *
     * @param path
     *            图片绝对路径
     * @return 图片的旋转角度
     */
    private int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case 0:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
}
