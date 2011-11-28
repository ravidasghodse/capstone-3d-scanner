package Capstone.Scanner;

//import org.opencv.android.Utils;
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.Point;
//import org.opencv.core.Scalar;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.highgui.Highgui;
//import org.opencv.highgui.VideoCapture;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.util.Log;
//import android.view.SurfaceHolder;
//
//class View extends CvViewBase {
//    private Mat mRgba;
//    private Mat mGray;
//    private Mat mIntermediateMat;
//
//    
//    private Mat findPoint(){
//    	Mat res = new Mat();
//    	res = Mat.zeros(mRgba.rows(), mRgba.cols(), mRgba.type());
////    	for(int i = 0; i < res.rows(); i++) {
////    		for(int j = 0; j < res.cols(); j++) {
////    			//System.out.print(String.format("%.2f ", res.get(i, j)));
////    			//Log.d("findPoint", Double.toString((res.get(i, j)[0])));
////    			if(Math.abs(mRgba.get(i, j)[0] - 245) < 5 &&
////    					mRgba.get(i, j)[2] == 255) {
////    				res.put(i, j, mRgba.get(i, j));
////    			}
////    		}
////    	}
//    	return res; 
//    }
//    
//    
//    public View(Context context) {
//        super(context);
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) {
//        super.surfaceChanged(_holder, format, width, height);
//
//        synchronized (this) {
//            // initialize Mats before usage
//            mGray = new Mat();
//            mRgba = new Mat();
//            mIntermediateMat = new Mat();
//        }
//    }
//
//    @Override
//    protected Bitmap processFrame(VideoCapture capture) {
//        switch (ScannerActivity.viewMode) {
//        case ScannerActivity.VIEW_MODE_GRAY:
//            capture.retrieve(mGray, Highgui.CV_CAP_ANDROID_GREY_FRAME);
//            Imgproc.cvtColor(mGray, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
//            break;
//        case ScannerActivity.VIEW_MODE_RGBA:
//            capture.retrieve(mRgba, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
//            Core.putText(mRgba, "OpenCV + Android", new Point(10, 100), 3/* CV_FONT_HERSHEY_COMPLEX */, 2, new Scalar(255, 0, 0, 255), 3);
//            break;
//        case ScannerActivity.VIEW_MODE_CANNY:
//            capture.retrieve(mGray, Highgui.CV_CAP_ANDROID_GREY_FRAME);
//            Imgproc.Canny(mGray, mIntermediateMat, 80, 100);
//            Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);
//            break;
//        }
//    	
////        String r = "";
////    	for(int i = 0; i < mRgba.rows(); i+=10) {
////    		for(int j = 0; j < mRgba.cols(); j+=10) {
////    			r += String.format("%f ", mRgba.get(i, j)[0]);
////    		}
////    		Log.d("findPoint", r);
////    	}
//        
//        //Mat line = findPoint();
//        //Bitmap bmp = Bitmap.createBitmap(line.cols(), line.rows(), Bitmap.Config.ARGB_8888);
//        
//        Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
//        
//        if (Utils.matToBitmap(mRgba, bmp))
//            return bmp;
//
//        bmp.recycle();
//        return null;
//    }
//
//    @Override
//    public void run() {
//        super.run();
//
//        synchronized (this) {
//            // Explicitly deallocate Mats
//            if (mRgba != null)
//                mRgba.release();
//            if (mGray != null)
//                mGray.release();
//            if (mIntermediateMat != null)
//                mIntermediateMat.release();
//
//            mRgba = null;
//            mGray = null;
//            mIntermediateMat = null;
//        }
//    }
//}

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.SurfaceHolder;
import android.opengl.*;

class View extends CvViewBase {
    private Mat mYuv;
    private Mat mRgba;
    private Mat mGraySubmat;
    private Mat mIntermediateMat;

    private ArrayList<Point> leftSpots;
    private ArrayList<Point> rightSpots;
    private ArrayList<Point> coordinations;
    private int num;
    
    public View(Context context) {
        super(context);
        num = 0;
    }

    @Override
    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) {
        super.surfaceChanged(_holder, format, width, height);

        synchronized (this) {
            // initialize Mats before usage
            mYuv = new Mat(getFrameHeight() + getFrameHeight() / 2, getFrameWidth(), CvType.CV_8UC1);
            mGraySubmat = mYuv.submat(0, getFrameHeight(), 0, getFrameWidth());

            mRgba = new Mat();
            mIntermediateMat = new Mat();
        }
    }

    @Override
    protected Bitmap processFrame(byte[] data) {
        mYuv.put(0, 0, data);

        Log.d("mYuv", String.format("col: %d  row: %d\n", mYuv.cols(), mYuv.rows()));
        
        switch (ScannerActivity.viewMode) {
        case ScannerActivity.VIEW_MODE_GRAY:
            Imgproc.cvtColor(mGraySubmat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
            break;
        case ScannerActivity.VIEW_MODE_RGBA:
            Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
            break;
        case ScannerActivity.VIEW_MODE_CANNY:
            Imgproc.Canny(mGraySubmat, mIntermediateMat, 80, 100);
            Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);
            break;
        case ScannerActivity.VIEW_MODE_FEATURES:
        	
        	leftSpots = findSpots(mYuv);
        	Log.d("findspot", String.format("Found %d spots", leftSpots.size()));
        	printPointToFile(leftSpots);
        	rightSpots = findCorrespondSpots(leftSpots);
        	
        	double[] point;
        	for(int i = 0; i < leftSpots.size(); i++) {
        		
        		point = triangulation(leftSpots.get(i), rightSpots.get(i));
        		
        		// render by opengl
        	}
        	
        	ScannerActivity.viewMode = ScannerActivity.VIEW_MODE_RGBA;
        	
//        	Mat thresholdImage = new Mat(getFrameHeight() + getFrameHeight() / 2, getFrameWidth(), CvType.CV_8UC1);
//        	Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
//        	Mat line = mRgba.clone();
//        	for(int k = 0; k < mYuv.channels(); k++) {
//	        	File file = new File(android.os.Environment.getExternalStorageDirectory()
//						+ "/capstone/camera" + Integer.toString(num) 
//						+ "_" + Integer.toString(k+1) + ".dat");
//	        	
//				try {
//					FileWriter filewriter = new FileWriter(file);
//					BufferedWriter  fileoutData=new BufferedWriter(filewriter);
//					for(int i = 0; i < mYuv.rows(); i++) {
//						for(int j = 0; j < mYuv.cols(); j++) {
//							fileoutData.write(String.format("%6.2f ", mYuv.get(i, j)[k]));
//						}
//						fileoutData.write("\n");
//					}
//					fileoutData.flush();
//        			fileoutData.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//        	}
        	
//            double [] p;
//            Mat mLine = Mat.zeros(mYuv.rows(), mYuv.cols(), mYuv.type());
//            Log.d("mline", mLine.toString());
//            int nStart = -1, nEnd = mYuv.cols();
//            int start, end;
//            for(int i = 0; i < 480; i++) {
//            	start = nStart;
//            	end = nEnd;
//            	while(++start < mYuv.cols() && !isTarget(mYuv.get(i, start)));
//            	while(--end > -1 && end > start && !isTarget(mYuv.get(i, end)));
//            	
//            	if(end > start) {
//            		nStart = Math.max(start - 10, 0);
//            		nEnd = Math.min(end + 10, mYuv.cols()-1);
//            	} else {
//            		nStart = -1;
//            		nEnd = mYuv.cols();
//            		continue;
//            	}
//            	
//            	Log.d("range", String.format("nStart = %d, nEnd = %d", nStart, nEnd));
//            	
//            	while(end > start) {
//            		p = mYuv.get(i, start);
//    				if(isTarget(p)) {
//    					mLine.put(i, start, p);
//    				}
//    				start++;
//            	}
//    		}
//            
//            Imgproc.cvtColor(mLine, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
            
//	        Bitmap bmp = Bitmap.createBitmap(getFrameWidth(), 
//	        		                         getFrameHeight(), 
//	        		                         Bitmap.Config.ARGB_8888);
//
//	        Utils.matToBitmap(mRgba, bmp);
//	        
//	        File imagefile = new File(android.os.Environment.getExternalStorageDirectory()
//					+ "/capstone/camera" + Integer.toString(num++) + ".jpg");
//	        try {
//				BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(imagefile));
//				bmp.compress(Bitmap.CompressFormat.JPEG, 80, os);
//				
//				os.flush();
//				os.close();
//				
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
//			Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
			
//			ScannerActivity.viewMode = ScannerActivity.VIEW_MODE_RGBA;
			
//            Imgproc.cvtColor(mRgba, thresholdImage, Imgproc.COLOR_RGB2GRAY, 4);
//            Imgproc.Canny(thresholdImage, thresholdImage, 80, 100, 3);
//            Mat lines = new Mat();
//            int threshold = 50;
//            int minLineSize = 20;
//            int lineGap = 20;
//
//            Imgproc.HoughLinesP(thresholdImage, lines, 1, Math.PI/180, threshold, minLineSize, lineGap);
//
//            for (int x = 0; x < lines.cols() && x < 1; x++) 
//            {
//                  double[] vec = lines.get(0, x);
//                  double x1 = vec[0], 
//                         y1 = vec[1],
//                         x2 = vec[2],
//                         y2 = vec[3];
//                  Point start = new Point(x1, y1);
//                  Point end = new Point(x2, y2);
//
//                  Core.line(mRgba, start, end, new Scalar(255,0,0), 3);
//
//            }
            
            //FindFeatures(mGraySubmat.getNativeObjAddr(), mRgba.getNativeObjAddr());
            break;
        }

        Bitmap bmp = Bitmap.createBitmap(getFrameWidth(), getFrameHeight(), Bitmap.Config.ARGB_8888);
		
        if (Utils.matToBitmap(mRgba, bmp))
            return bmp;

        bmp.recycle();
        return null;
    }

    @Override
    public void run() {
        super.run();

        synchronized (this) {
            // Explicitly deallocate Mats
            if (mYuv != null)
                mYuv.release();
            if (mRgba != null)
                mRgba.release();
            if (mGraySubmat != null)
                mGraySubmat.release();
            if (mIntermediateMat != null)
                mIntermediateMat.release();

            mYuv = null;
            mRgba = null;
            mGraySubmat = null;
            mIntermediateMat = null;
        }
    }

//    public native void FindFeatures(long matAddrGr, long matAddrRgba);
//
//    static {
//        System.loadLibrary("mixed_sample");
//    }
    
    
    private Boolean isTarget(double[] p) {
    	return ((int)p[0] == 255);
    }
    
    private ArrayList<Point> findSpots(Mat mat) {
    	ArrayList<Point> spots = new ArrayList<Point>();
    	
    	double[] p;
    	int nStart = -1, nEnd = mat.cols();
        int start, end;
        for(int i = 0; i < 480; i++) {
        	start = nStart;
        	end = nEnd;
        	while(++start < mat.cols() && !isTarget(mat.get(i, start)));
        	while(--end > -1 && end > start && !isTarget(mat.get(i, end)));
        	
        	if(end > start) {
        		nStart = Math.max(start - 10, 0);
        		nEnd = Math.min(end + 10, mat.cols()-1);
        	} else {
        		nStart = -1;
        		nEnd = mat.cols();
        		continue;
        	}
        	
//        	Log.d("range", String.format("nStart = %d, nEnd = %d", nStart, nEnd));
        	
        	while(end > start) {
        		p = mYuv.get(i, start);
				if(isTarget(p)) {
					spots.add(new Point(i, start));
				}
				start++;
        	}
		}
    	
    	return spots;
    }
    
    private ArrayList<Point> findCorrespondSpots(ArrayList<Point> leftSpots) {
    	ArrayList<Point> rightSpots = new ArrayList<Point>();
    	
    	// to do
    	
    	return rightSpots;
    }
    
    private double[] triangulation(Point left, Point right) {
    	double[] point = new double[3];
    	
    	return point;
    }
    
    private void printPointToFile(ArrayList<Point> spots) {
    	File file = new File(android.os.Environment.getExternalStorageDirectory()
				+ "/capstone/point_" + Integer.toString(num) + ".txt");
    	
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for(Point point : spots)
				writer.write(String.format("%3d %3d\n", (int) point.x, (int) point.y));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}