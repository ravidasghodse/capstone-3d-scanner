package Capstone.Scanner;

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

public class Preview extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder holder = null;
	private Camera mCamera = null;

	private int mFrameWidth;
	private int mFrameHeight;

	private int num;

	public Preview(Context context) {
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

	public Camera getCamera() {
		return mCamera;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i("TAG", "surfaceChanged");
		// TODO Auto-generated method stub
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setFocusMode(Parameters.FOCUS_MODE_MACRO);
		parameters.setExposureCompensation(parameters
				.getMaxExposureCompensation());
		parameters.setPictureFormat(PixelFormat.JPEG);

		// Log.i("format", parameters.getSupportedPictureFormats().toString());
		// Camera.Size picSize = parameters.getSupportedPictureSizes().get(0);
		// parameters.setPictureSize(picSize.width, picSize.height);

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
					Log.d("Size", "width = " + Integer.toString(mFrameWidth)
							+ "height = " + Integer.toString(mFrameHeight));
					minDiff = Math.abs(size.height - height);
				}
			}
		}

		parameters.setPreviewSize(getFrameWidth(), getFrameHeight());
		mCamera.setParameters(parameters);

		mCamera.startPreview();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("TAG", "surfaceCreated");
		// TODO Auto-generated method stub
		if (mCamera == null) {
			mCamera = Camera.open(2);
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				mCamera.release();
				mCamera = null;
			}
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i("TAG", "surfaceDestroyed");
		// TODO Auto-generated method stub
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
			// num = 0;
		}
	}
}
