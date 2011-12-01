package Capstone.Scanner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageProcess {
	
    private Mat mYuv;
    private Mat mRgba;

    private int num;
    
    public ImageProcess(int row, int col) {
//    	mYuv = new Mat(row, col, CvType.CV_8UC1);
//    	mRgba = new Mat();
    }
    
//	public void processFrame(byte[] data) {
	public void processFrame(Bitmap mBitmap) {
//		mYuv = Utils.bitmapToMat(mBitmap);
		mRgba = Utils.bitmapToMat(mBitmap);
//		mYuv = new Mat();
//		Imgproc.cvtColor(mRgba, mYuv, Imgproc.COLOR_RGB2YUV, 3);
//        mYuv.put(0, 0, data);

//        Log.d("mYuv", String.format("col: %d  row: %d channel: %d\n", mYuv.cols(), mYuv.rows(), mYuv.channels()));
        Log.d("mRgba", String.format("col: %d  row: %d channel: %d\n", mRgba.cols(), mRgba.rows(), mRgba.channels()));
        
//    	for(int k = 0; k < mYuv.channels(); k++) {
//        	File file = new File(android.os.Environment.getExternalStorageDirectory()
//					+ "/capstone/camera" + Integer.toString(num) 
//					+ "_" + Integer.toString(k+1) + ".dat");
//        	
//			try {
//				FileWriter filewriter = new FileWriter(file);
//				BufferedWriter  fileoutData=new BufferedWriter(filewriter);
//				for(int i = 0; i < mYuv.rows(); i+=10) {
//					for(int j = 0; j < mYuv.cols(); j+=10) {
//						fileoutData.write(String.format("%6.2f ", mYuv.get(i, j)[k]));
//					}
//					fileoutData.write("\n");
//				}
//				fileoutData.flush();
//    			fileoutData.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    	}
        
//        switch (ScannerActivity.viewMode) {
//          
//        case ScannerActivity.VIEW_MODE_RGBA:
//            Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
//            break;
//            
//        case ScannerActivity.VIEW_MODE_FINDSPOTS:
//        	
        	ArrayList<Point> lhsSpotList = findSpots(mRgba);
        	Log.d("findspot", String.format("Found %d spots", lhsSpotList.size()));
        	writePointToFile(lhsSpotList, String.format("lhspoint_%d.txt", num));
        	
        	Point rhsSpot;
        	Point3 point;
        	ArrayList<Point> rhsSpotList = new ArrayList<Point>();
        	ArrayList<Point3> pointList = new ArrayList<Point3>();
        	
        	for(Point lhsSpot : lhsSpotList) {
        		rhsSpot = findCorrespondSpots(lhsSpot);
        		
        		rhsSpotList.add(rhsSpot);
        		
//        		point = Calculation.triangulation(lhsSpot, rhsSpot);
        		point = Calculation.triangulation(rhsSpot);
        		pointList.add(point);
        		
//        		PointCloud.addPoint(point);
        		// TODO render by opengl
        	}
        	
        	writePointToFile(rhsSpotList, String.format("rhspoint_%d.txt", num));
        	writePoint3ToFile(pointList, String.format("point_%d.txt", num));
        	
        	ScannerActivity.viewMode = ScannerActivity.VIEW_MODE_RGBA;
        	
//        	break;
        	
/*//        	Mat thresholdImage = new Mat(getFrameHeight() + getFrameHeight() / 2, getFrameWidth(), CvType.CV_8UC1);
//        	Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
//        	Mat line = mRgba.clone();

        	
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
//        }

//        Bitmap bmp = Bitmap.createBitmap(getFrameWidth(), getFrameHeight(), Bitmap.Config.ARGB_8888);
//		
//        if (Utils.matToBitmap(mRgba, bmp))
//            return bmp;
//
//        bmp.recycle();
//        return null;
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
    	return ((int)p[1] == 255 && (int) p[2] == 255);
    }
    
    private ArrayList<Point> findSpots(Mat mat) {
    	ArrayList<Point> spots = new ArrayList<Point>();
    	
    	double[] p;
    	int cols = mat.cols()/2;
    	int nStart = -1, nEnd = cols;
        int start, end;
        for(int i = 0; i < mat.rows(); i+=10) {
        	start = nStart;
        	end = nEnd;
        	while(++start < cols && !isTarget(mat.get(i, start)));
        	while(--end > -1 && end > start && !isTarget(mat.get(i, end)));
        	
        	if(end > start) {
        		nStart = Math.max(start - 10, 0);
        		nEnd = Math.min(end + 10, cols);
        	} else {
        		nStart = -1;
        		nEnd = mat.cols();
        		continue;
        	}
        	
//        	Log.d("range", String.format("nStart = %d, nEnd = %d", nStart, nEnd));
        	
        	while(end > start) {
        		p = mRgba.get(i, start);
				if(isTarget(p)) {
					spots.add(new Point(start, i));
				}
				start++;
        	}
		}
    	
    	return spots;
    }
    
    private Point findCorrespondSpots(Point lhsSpots) {
    	ArrayList<Point> candidate = new ArrayList<Point>();
    	Point rhsSpot = new Point();
    	
    	// TODO
    	double[] line = Calculation.projection(lhsSpots);
    	
    	Log.d("line: ", String.format("b: %f k: %f", line[0], line[1]));
    	
    	int x, y;
    	for(int i = 0; i < mRgba.cols()/2; i++) {
    		x = i + mRgba.cols()/2;
    		y = (int) (line[1]*x + line[0]);
			if(isTarget(mRgba.get(y, x))) {
				candidate.add(new Point(x, y));
			}
    	}
    	
    	int sumX = 0, sumY = 0;
    	
    	for(Point p:candidate) {
    		sumX += p.x;
    		sumY += p.y;
    	}
    	if(candidate.size() > 0) {
	    	rhsSpot.x = sumX / candidate.size();
	    	rhsSpot.y = sumY / candidate.size();
    	}
    	
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
}
