package Capstone.Scanner;

//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.List;
//
//import org.opencv.core.Size;
//import org.opencv.highgui.VideoCapture;
//import org.opencv.highgui.Highgui;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.hardware.Camera;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//
//public abstract class CvViewBase extends SurfaceView implements SurfaceHolder.Callback, Runnable {
//    private static final String TAG = "Sample::SurfaceView";
//
//    private SurfaceHolder       mHolder;
//    private VideoCapture        mCamera;
//    
//    private int picNo;
//    
//    public CvViewBase(Context context) {
//        super(context);
//        mHolder = getHolder();
//        mHolder.addCallback(this);
//        
//        picNo = 0;
//        
//        Log.i(TAG, "Instantiated new " + this.getClass());
//    }
//
//    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) {
//        Log.i(TAG, "surfaceCreated");
//        synchronized (this) {
//            if (mCamera != null && mCamera.isOpened()) {
//                Log.i(TAG, "before mCamera.getSupportedPreviewSizes()");
//                List<Size> sizes = mCamera.getSupportedPreviewSizes();
//                Log.i(TAG, "after mCamera.getSupportedPreviewSizes()");
//                int mFrameWidth = width;
//                
//                int mFrameHeight = height;
//
//                //selecting optimal camera preview size
//                {
//                    double minDiff = Double.MAX_VALUE;
//                    for (Size size : sizes) {
//                        if (Math.abs(size.height - height) < minDiff) {
//                            mFrameWidth = (int) size.width;
//                            mFrameHeight = (int) size.height;
//                            Log.i(TAG, "width = "+ Integer.toString(mFrameWidth));
//                            Log.i(TAG, "height = "+ Integer.toString(mFrameHeight));
//                            minDiff = Math.abs(size.height - height);
//                        }
//                    }
//                }
//                
//                //System.out.println(mCamera.getSupportedPreviewSizes().toString());
//                Log.i(TAG, "Supported preview: " + mCamera.getSupportedPreviewSizes().toString());
//                
//                //mFrameWidth = 1280;
//                //mFrameHeight = 720;
//
//                mCamera.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, mFrameWidth);
//                mCamera.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, mFrameHeight);
//            }
//        }
//    }
//
//    public void surfaceCreated(SurfaceHolder holder) {
//        Log.i(TAG, "surfaceCreated");
//        mCamera = new VideoCapture(Highgui.CV_CAP_ANDROID+2);
//        if (mCamera.isOpened()) {
//            (new Thread(this)).start();
//        } else {
//            mCamera.release();
//            mCamera = null;
//            Log.e(TAG, "Failed to open native camera");
//        }
//    }
//
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        Log.i(TAG, "surfaceDestroyed");
//        if (mCamera != null) {
//            synchronized (this) {
//                mCamera.release();
//                mCamera = null;
//            }
//        }
//    }
//
//    protected abstract Bitmap processFrame(VideoCapture capture);
//
//    public void run() {
//        Log.i(TAG, "Starting processing thread");
//        while (true) {
//            Bitmap bmp = null;
//
//            synchronized (this) {
//                if (mCamera == null)
//                    break;
//
//                if (!mCamera.grab()) {
//                    Log.e(TAG, "mCamera.grab() failed");
//                    break;
//                }
//
//                bmp = processFrame(mCamera);
//            }
//
//            if (bmp != null) {
//            	// save page
////            	Log.i(TAG, android.os.Environment.getExternalStorageState());
////            	File f = new File(android.os.Environment.getExternalStorageDirectory()
////            			+ "/camera" + Integer.toString(picNo++) + ".jpg");
////				try {
////            	BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(f));
////				bmp.compress(Bitmap.CompressFormat.JPEG, 80, os);
////				os.flush();
////				os.close();
////				} catch (FileNotFoundException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				} catch (IOException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
//				
//                Canvas canvas = mHolder.lockCanvas();
//                if (canvas != null) {
//                    canvas.drawBitmap(bmp, (canvas.getWidth() - bmp.getWidth()) / 2, (canvas.getHeight() - bmp.getHeight()) / 2, null);
//                    mHolder.unlockCanvasAndPost(canvas);
//                }
//                bmp.recycle();
//            }
//        }
//
//        Log.i(TAG, "Finishing processing thread");
//    }
//}

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
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class CvViewBase extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = "Sample::SurfaceView";

    private Camera              mCamera;
    private SurfaceHolder       mHolder;
    private int                 mFrameWidth;
    private int                 mFrameHeight;
    private byte[]              mFrame;
    private boolean             mThreadRun;

    public CvViewBase(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    public int getFrameWidth() {
        return mFrameWidth;
    }

    public int getFrameHeight() {
        return mFrameHeight;
    }

    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) {
        Log.i(TAG, "surfaceCreated");
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            
            // show supported parameters of camera
            Camera.CameraInfo info = new Camera.CameraInfo();
            for(int i = 0; i < Camera.getNumberOfCameras(); i++) {
	            Camera.getCameraInfo(i, info);
	            Log.d(String.format("param:camera%d", i), String.format("facing: %d, orientation: %d", info.facing, info.orientation));
            }
            Log.d("param:focus", params.getSupportedFocusModes().toString());
//            Log.d("param:preview fps", params.getSupportedPreviewFrameRates().toString());
//            Log.d("param:preview framerate", params.getSupportedPreviewFrameRates().toString());
            // end of parameters

            List<Camera.Size> picSizes = params.getSupportedPictureSizes();
            for(Camera.Size size : picSizes)
            	Log.d("param:pic size", String.format("width = %d height = %d", size.width, size.height));
            
            
            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            mFrameWidth = width;
            mFrameHeight = height;

            // selecting optimal camera preview size
            {
                double minDiff = Double.MAX_VALUE;
                for (Camera.Size size : sizes) {
                	Log.d("param:preview size", String.format("width = %d height = %d", size.width, size.height));
                    if (Math.abs(size.height - height) < minDiff) {
                        mFrameWidth = size.width;
                        mFrameHeight = size.height;
                        minDiff = Math.abs(size.height - height);
                    }
                }
            }

            params.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
           
            params.setPreviewSize(getFrameWidth(), getFrameHeight());
            mCamera.setParameters(params);
            try {
				mCamera.setPreviewDisplay(null);
			} catch (IOException e) {
				Log.e(TAG, "mCamera.setPreviewDisplay fails: " + e);
			}
            mCamera.startPreview();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        mCamera = Camera.open(0);
        try {
        	mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
        	mCamera.release();
        	mCamera = null;
        }
//        mCamera.setPreviewCallback(new PreviewCallback() {
//            public void onPreviewFrame(byte[] data, Camera camera) {
//                synchronized (CvViewBase.this) {
//                    mFrame = data;
//                    CvViewBase.this.notify();
//                }
//            }
//        });
        (new Thread(this)).start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        mThreadRun = false;
        if (mCamera != null) {
            synchronized (this) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.release();
                mCamera = null;
            }
        }
    }

    protected abstract Bitmap processFrame(byte[] data);

    public void run() {
        mThreadRun = true;
        Log.i(TAG, "Starting processing thread");
        while (mThreadRun) {
            Bitmap bmp = null;

            synchronized (this) {
                try {
                    this.wait();
        			if(mCamera !=null){
        				mCamera.takePicture(null, null,pic);
        			}
                    bmp = processFrame(mFrame);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (bmp != null) {
                Canvas canvas = mHolder.lockCanvas();
                if (canvas != null) {
                	canvas.drawRect(r, new Paint());
                    canvas.drawBitmap(bmp, (canvas.getWidth() - getFrameWidth()) / 2, (canvas.getHeight() - getFrameHeight()) / 2, null);
                    mHolder.unlockCanvasAndPost(canvas);
                }
                bmp.recycle();
            }
        }
    }
    
    public Camera.PictureCallback pic = new Camera.PictureCallback(){

		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.d("takephoto", "lalalalalalal!");
			synchronized (CvViewBase.this) {
				mFrame = data;
				CvViewBase.this.notify();
			}
//			Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//			
//			File imagefile = new File(android.os.Environment.getExternalStorageDirectory()
//					+ "/capstone/camera" + Integer.toString(num++) + ".jpg");
//			try {
//				BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(imagefile));
//				mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
//				
//				os.flush();
//				os.close();
//				
////				Canvas canvas = holder.lockCanvas();
////				canvas.drawBitmap(mBitmap, 0, 0, null);
////				holder.unlockCanvasAndPost(canvas);
//				mCamera.startPreview();
//				
////		        float[] dis = new float[3];
////		        Parameters parameters = mCamera.getParameters();
////		        parameters.getFocusDistances(dis);
////		        Log.d("focal dis", Float.toString(dis[0]) + " " + Float.toString(dis[1]) + " " + Float.toString(dis[2]));
////		        
////		        Log.d("focal length", Float.toString(parameters.getFocalLength()));
//				
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			Log.d("takephoto", "lalalalalalal!");
		}
		
	};
}