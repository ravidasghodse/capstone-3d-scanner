package com.Photo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * com.Aina.Android Pro_Camera
 * 
 * @author Aina.huang E-mail: 674023920@qq.com
 * @version 创建时间：2010 Jul 7, 2010 2:50:15 PM 类说明
 */
public class PreView extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder holder = null;
	private Camera mCamera = null;
	private Bitmap mBitmap = null;

    private int                 mFrameWidth;
    private int                 mFrameHeight;

    private int num;
    
	public PreView(Context context) {
		super(context);
		Log.i("TAG", "PreView()");
		// TODO Auto-generated constructor stub
		holder = this.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		num = 0;
	}

    public int getFrameWidth() {
        return mFrameWidth;
    }

    public int getFrameHeight() {
        return mFrameHeight;
    }
	
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i("TAG", "surfaceChanged");
		// TODO Auto-generated method stub
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setFocusMode(Parameters.FOCUS_MODE_MACRO);
		parameters.setPictureFormat(PixelFormat.JPEG);//设置图片格式
//		Camera.Size picSize = parameters.getSupportedPictureSizes().get(0);
//		parameters.setPictureSize(picSize.width, picSize.height);
		
		mCamera.setParameters(parameters);
		
		List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        mFrameWidth = width;
        mFrameHeight = height;

        // selecting optimal camera preview size
        {
            double minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - height) < minDiff) {
                    mFrameWidth = size.width;
                    mFrameHeight = size.height;
                    Log.d("Size", "width = " + Integer.toString(mFrameWidth) + "height = " + Integer.toString(mFrameHeight));
                    minDiff = Math.abs(size.height - height);
                }
            }
        }

        parameters.setPreviewSize(getFrameWidth(), getFrameHeight());
        mCamera.setParameters(parameters);
		
		mCamera.startPreview();//开始预览
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("TAG", "surfaceCreated");
		// TODO Auto-generated method stub
		if(mCamera == null) {
			mCamera = Camera.open(2);//启动服务
			
			try {
				mCamera.setPreviewDisplay(holder);//设置预览
			} catch (IOException e) {
				mCamera.release();//释放
				mCamera = null;
			}
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i("TAG", "surfaceDestroyed");
		// TODO Auto-generated method stub
		if(mCamera != null) {
			mCamera.stopPreview();//停止预览
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
//			num = 0;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_CAMERA){
			if(mCamera !=null){
				mCamera.takePicture(null, null,pic);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	//拍照后输出图片
	public Camera.PictureCallback pic = new Camera.PictureCallback(){

		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.d("takephoto", "lalalalalalal!");
			mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//			File datafile = new File(android.os.Environment.getExternalStorageDirectory()
//					+ "/capstone/camera" + Integer.toString(num++) + ".dat");			
			
			File imagefile = new File(android.os.Environment.getExternalStorageDirectory()
					+ "/capstone/camera" + Integer.toString(num++) + ".jpg");
			try {
				BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(imagefile));
				mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
				
				os.flush();
				os.close();
				
//				os = new BufferedOutputStream(new FileOutputStream(datafile));
//				os.write(data);
//				os.flush();
//				os.close();
				
//				Canvas canvas = holder.lockCanvas();
//				canvas.drawBitmap(mBitmap, 0, 0, null);
//				holder.unlockCanvasAndPost(canvas);
				mCamera.startPreview();
				
		        float[] dis = new float[3];
		        Parameters parameters = mCamera.getParameters();
		        parameters.getFocusDistances(dis);
		        Log.d("focal dis", Float.toString(dis[0]) + " " + Float.toString(dis[1]) + " " + Float.toString(dis[2]));
		        
		        Log.d("focal length", Float.toString(parameters.getFocalLength()));
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("takephoto", "lalalalalalal!");
		}
		
	};
}

