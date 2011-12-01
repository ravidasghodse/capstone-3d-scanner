package Capstone.Scanner;

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
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceHolder;

class View extends CvViewBase {
    private Mat mYuv;
    private Mat mRgba;
    private Mat mGraySubmat;
    private Mat mIntermediateMat;

    private ArrayList<Point> lhsSpotList;
    private ArrayList<Point> rhsSpotList;
    private ArrayList<Point3> pointList;
    
    private int num;
    
    public View(Context context) {
        super(context);
        num = 0;
        rhsSpotList = new ArrayList<Point>();
        pointList = new ArrayList<Point3>();
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
//        case ScannerActivity.VIEW_MODE_GRAY:
//            Imgproc.cvtColor(mGraySubmat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
//            break;
//            
//        case ScannerActivity.VIEW_MODE_CANNY:
//            Imgproc.Canny(mGraySubmat, mIntermediateMat, 80, 100);
//            Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);
//            break;
          
        case ScannerActivity.VIEW_MODE_RGBA:
            Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
            break;
            
        case ScannerActivity.VIEW_MODE_FINDSPOTS:
        	
        	lhsSpotList = findSpots(mYuv);
        	Log.d("findspot", String.format("Found %d spots", lhsSpotList.size()));
        	writePointToFile(lhsSpotList, String.format("lhspoint_%d.txt", num));
        	
        	Point rhsSpot;
        	Point3 point;
        	for(Point lhsSpot : lhsSpotList) {
        		rhsSpot = findCorrespondSpots(lhsSpot);
        		
        		rhsSpotList.add(rhsSpot);
        		
//        		point = Calculation.triangulation(lhsSpot, rhsSpot);
        		point = Calculation.triangulation(rhsSpot);
        		pointList.add(point);
        		
        		PointCloud.addPoint(point);
        		// TODO render by opengl
        	}
        	
        	writePointToFile(rhsSpotList, String.format("rhspoint_%d.txt", num));
        	writePoint3ToFile(pointList, String.format("point_%d.txt", num));
        	
        	ScannerActivity.viewMode = ScannerActivity.VIEW_MODE_RGBA;
        	
        	break;
        	
/*//        	Mat thresholdImage = new Mat(getFrameHeight() + getFrameHeight() / 2, getFrameWidth(), CvType.CV_8UC1);
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
            
//            FindFeatures(mGraySubmat.getNativeObjAddr(), mRgba.getNativeObjAddr());
            break; */
        }

        Bitmap bmp = Bitmap.createBitmap(getFrameWidth(), getFrameHeight(), Bitmap.Config.ARGB_8888);
		
        if (Utils.matToBitmap(mRgba, bmp))
            return bmp;

        bmp.recycle();
        return null;
    }

//    @Override
//    public void run() {
//        super.run();
//
//        synchronized (this) {
//            // Explicitly deallocate Mats
//            if (mYuv != null)
//                mYuv.release();
//            if (mRgba != null)
//                mRgba.release();
//            if (mGraySubmat != null)
//                mGraySubmat.release();
//            if (mIntermediateMat != null)
//                mIntermediateMat.release();
//
//            mYuv = null;
//            mRgba = null;
//            mGraySubmat = null;
//            mIntermediateMat = null;
//        }
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
    
    private Point findCorrespondSpots(Point lhsSpots) {
    	Point rhsSpot = new Point();
    	
    	// TODO
    	double[] line = Calculation.projection(lhsSpots);
    	
    	Log.d("line: ", String.format("b: %f k: %f", line[0], line[1]));
    	
    	return rhsSpot;
    }
    
    private void writePointToFile(ArrayList<Point> points, String filename) {
    	File file = new File(android.os.Environment.getExternalStorageDirectory()
				+ "/capstone/" + filename);
    	
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for(Point point : points)
				writer.write(String.format("%3d %3d\n", (int) point.x, (int) point.y));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void writePoint3ToFile(ArrayList<Point3> points, String filename) {
    	File file = new File(android.os.Environment.getExternalStorageDirectory()
				+ "/capstone/" + filename);
    	
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for(Point3 point : points)
				writer.write(String.format("%3d %3d %3d\n", (int) point.x, (int) point.y, (int) point.z));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
    
    
//  public native void FindFeatures(long matAddrGr, long matAddrRgba);
//    
//        static {
//            System.loadLibrary("mixed_sample");
//        }
}