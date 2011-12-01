package Capstone.Scanner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Point3;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageProcess {
	
    private Mat mRgba;

    private int num;
    
    public ImageProcess(int row, int col) {
//    	mYuv = new Mat(row, col, CvType.CV_8UC1);
//    	mRgba = new Mat();
    }
    
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
        
        	ArrayList<Point> lhsSpotList = findSpots(mRgba);
        	Log.d("findspot", String.format("Found %d spots", lhsSpotList.size()));
//        	writePointToFile(lhsSpotList, String.format("lhspoint_%d.txt", num));
        	
        	Point rhsSpot;
        	Point3 point;
        	ArrayList<Point> rhsSpotList = new ArrayList<Point>();
        	ArrayList<Point3> pointList = new ArrayList<Point3>();
        	
        	for(Point lhsSpot : lhsSpotList) {
        		rhsSpot = findCorrespondSpots(lhsSpot);
        		
        		if(rhsSpot.x == 0 && rhsSpot.y == 0)
        			continue;
        		
        		rhsSpotList.add(rhsSpot);
        		
//        		point = Calculation.triangulation(lhsSpot, rhsSpot);
        		point = Calculation.triangulation(rhsSpot);
        		pointList.add(point);
        		
        		PointCloud.addPoint(point);
        	}
        	
//        	writePointToFile(rhsSpotList, String.format("rhspoint_%d.txt", num));
        	writePoint3ToFile(pointList, String.format("point_%d.txt", num));
        	
        	ScannerActivity.viewMode = ScannerActivity.VIEW_MODE_RGBA;
    }

    private Boolean isTarget(double[] p) {
    	return ((int)p[1] == 255 && (int) p[2] == 255);
    }
    
    private ArrayList<Point> findSpots(Mat mat) {
    	ArrayList<Point> spots = new ArrayList<Point>();
    	
    	double[] p;
    	int cols = mat.cols()/2;
    	final int xMin = cols/8, xMax = cols - xMin;
    	final int yMin = mat.rows()/8, yMax = mat.rows() - yMin;
    	
    	int nStart = xMin - 1, nEnd = xMax;
        int start, end;
        for(int i = yMin; i < yMax; i+=5) {
        	start = nStart;
        	end = nEnd;
        	while(++start < xMax && !isTarget(mat.get(i, start)));
        	while(--end > xMin - 1 && end > start && !isTarget(mat.get(i, end)));
        	
        	if(end > start) {
        		nStart = Math.max(start - 10, xMin);
        		nEnd = Math.min(end + 10, xMax);
        	} else {
        		nStart = xMin -1;
        		nEnd = xMax;
        		continue;
        	}
        	
        	Log.d("range", String.format("nStart = %d, nEnd = %d", nStart, nEnd));
        	
        	while(end > start) {
        		p = mRgba.get(i, start);
				if(isTarget(p)) {
					spots.add(new Point(start, i));
					break;
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
    	
    	int x, y;
//    	ArrayList<Point> candidate = new ArrayList<Point>();
//    	for(int i = 0; i < mRgba.cols()/2; i++) {
//    		x = i + mRgba.cols()/2;
//    		y = (int) (line[1]*x + line[0]);
//			if(isTarget(mRgba.get(y, x))) {
//				candidate.add(new Point(x, y));
//			}
//    	}
//    	
//    	int sumX = 0, sumY = 0;
//    	
//    	for(Point p:candidate) {
//    		sumX += p.x;
//    		sumY += p.y;
//    	}
//    	if(candidate.size() > 0) {
//	    	rhsSpot.x = sumX / candidate.size();
//	    	rhsSpot.y = sumY / candidate.size();
//    	}
    	
    	for(int i = 0; i < mRgba.cols()/2; i++) {
			x = i + mRgba.cols()/2;
			y = (int) (line[1]*x + line[0]);
			
			for(int j = y-2; j <= y+2; j++) {
				if(isTarget(mRgba.get(j, x))) {
			    	rhsSpot.x = x;
			    	rhsSpot.y = y;
					break;
				}
			}
    	}
    	
    	return rhsSpot;
    }
    
    private void writePointToFile(ArrayList<Point> points, String filename) {
    	File file = new File(android.os.Environment.getExternalStorageDirectory()
				+ "/capstone/" + filename);
    	
		try {
			if(!file.exists()){
				file.createNewFile();
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
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
			if(!file.exists()){
				file.createNewFile();
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			for(Point3 point : points)
				writer.write(String.format("%.3f %.3f %.3f\n", point.x, point.y, point.z));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
