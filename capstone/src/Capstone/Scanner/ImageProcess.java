package Capstone.Scanner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageProcess {

	private Mat mRgba;
	private Mat mGray;
	private Mat mRes;

	private int num;

	public ImageProcess() {

	}

	public Mat readData(String filename) {
		Mat mat = null;
		try {
			ObjectInputStream outputStream = new ObjectInputStream(
					new FileInputStream(filename));
			mat = (Mat) outputStream.readObject();
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mat;
	}

	public void processFrame(Bitmap mBitmap) {
		mRgba = Utils.bitmapToMat(mBitmap);
		Log.d("mRgba", String.format("col: %d  row: %d channel: %d\n",
				mRgba.cols(), mRgba.rows(), mRgba.channels()));

		
		Point lhsSpotList[] = findSpots(mRgba, 0);
		// ArrayList<Point> estSpotList = findSpots(mRgba, 960);
//		Log.d("findspot", String.format("Found %d spots", lhsSpotList.size()));
		// writePointToFile(estSpotList, String.format("estpoint_%d.txt", num));
		
		Log.d("interpola", "left");
		writePointToFile(lhsSpotList, String.format("lhspoint_%d.txt", num));
		Calculation.interpolation(lhsSpotList);
		writePointToFile(lhsSpotList, String.format("lhspointnew_%d.txt", num));

		Point rhsSpot;
		Point3 point;
		Point rhsSpotList[] = new Point[108];
//		Point3 pointList[] = new Point3[108];
		Point3 pointList2[] = new Point3[108];

		for (int i = 0;i < 108; ++i) {
			rhsSpotList[i] = new Point();
			if (lhsSpotList[i].x != -1){
				rhsSpot = findCorrespondSpots(lhsSpotList[i]);
			}
			else {
				rhsSpot = new Point();
				rhsSpot.x = -1;
				rhsSpot.y = -1;
			}

			// if (rhsSpot.x == 0 && rhsSpot.y == 0)
			// continue;
			rhsSpotList[i].x = rhsSpot.x;
			rhsSpotList[i].y = rhsSpot.y;
		}
		
		Log.d("interpola", "right");
		Calculation.interpolation(rhsSpotList);
		
		for (int i = 0;i < 108; ++i) {
			point = null;
			if (rhsSpotList[i].x != -1)	{
			// point = Calculation.triangulation(lhsSpot, rhsSpot);
			// point = Calculation.triangulation(rhsSpot);
			// pointList.add(point);
				rhsSpotList[i].x *= 2;
				rhsSpotList[i].y *= 2;
				point = Calculation.triangulation(lhsSpotList[i], rhsSpotList[i]);
			}
			if (point == null) {
				point = new Point3();
				point.x = -1;
				point.y = -1;
				point.z = -1;
			}
			pointList2[i] = new Point3();
			pointList2[i].x = point.x;
			pointList2[i].y = point.y;
			pointList2[i].z = point.z;
			PointCloud.addPoint(point, i);
			// PointCloud.addPoint(point);
		}
		writePointToFile(rhsSpotList, String.format("rhspoint_%d.txt", num));
//		writePoint3ToFile(pointList, String.format("point_%d.txt", num));
		writePoint3ToFile(pointList2, String.format("pointnew_%d.txt", num));
		num++;
	}

//	private Boolean isTarget(double[] p) {
//		return ((int) p[1] == 255 && (int) p[2] == 255);
//	}

	private Point[] findSpots(Mat mat, int edge) {
		mGray = new Mat();
		mRes = new Mat();
		Imgproc.cvtColor(mat, mGray, Imgproc.COLOR_RGB2GRAY, 1);
		Log.i("mGray",
				String.format("col: %d row: %d", mGray.cols(), mGray.rows()));
		Imgproc.threshold(mGray, mRes, 220, 255, Imgproc.THRESH_BINARY);
		// Imgproc.findContours(image, contours, hierarchy, mode, method)
		Point spots[] = new Point[108];

//		double[] p;
		int cols = mat.cols() / 2;
		final int xMin = edge /* cols/8 */, xMax = cols + xMin;
		final int yMin = 0 /* mat.rows()/8 */, yMax = mat.rows() - yMin;

//		int nStart = xMin - 1, nEnd = xMax;
//		int start, end;

		for (int i = 0; i*5 < yMax; i++){
			spots[i] = new Point();
			spots[i].x = -1;
			spots[i].y = -1;
			for (int j = xMin; j < xMax; j++){
				if (mRes.get(i*5, j)[0] == 255) {
					spots[i].x = j;
					spots[i].y = i*5;
					break;
				}
			}
		}

		// for (int i = yMin; i < yMax; i += 5) {
		// start = nStart;
		// end = nEnd;
		// while (++start < xMax && !isTarget(mat.get(i, start)))
		// ;
		// while (--end > xMin - 1 && end > start
		// && !isTarget(mat.get(i, end)))
		// ;
		//
		// if (end > start) {
		// nStart = Math.max(start - 10, xMin);
		// nEnd = Math.min(end + 10, xMax);
		// } else {
		// nStart = xMin - 1;
		// nEnd = xMax;
		// continue;
		// }
		//
		// Log.d("range",
		// String.format("nStart = %d, nEnd = %d", nStart, nEnd));
		//
		// while (end > start) {
		// p = mRgba.get(i, start);
		// if (isTarget(p)) {
		// spots.add(new Point(start, i));
		// break;
		// }
		// start++;
		// }
		// }

		return spots;
	}

	private Point findCorrespondSpots(Point lhsSpots) {
		Point rhsSpot = new Point();

		// TODO
		lhsSpots.x *= 2;
		lhsSpots.y *= 2;
		double[] line = Calculation.projection(lhsSpots);

		// Log.d("line: ", String.format("b: %f k: %f", line[0], line[1]));

		int x, y;

		Log.i("mRes",
				String.format("col: %d row: %d", mRes.cols(), mRes.rows()));
		
		rhsSpot.x = -1;
		rhsSpot.y = -1;
		
		for (int i = 0; i < mRes.cols() / 2; i++) {
			x = i + mRes.cols() / 2;
			y = (int) (line[1] * x + line[0] / 2);
			if (mRes.get(y, x)[0] == 255) {
				rhsSpot.x = i;
				rhsSpot.y = y;
				break;
			}
		}

		return rhsSpot;
	}

	public void writePointToFile(Point[] points, String filename) {
		File file = new File(
				android.os.Environment.getExternalStorageDirectory()
						+ "/capstone/" + filename);

		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(file,
					false));
			for (Point point : points)
				writer.write(String.format("%3d %3d\n", (int) point.x,
						(int) point.y));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writePoint3ToFile(Point3[] points, String filename) {
		Log.d("out", "lalala");
		File file = new File(
				android.os.Environment.getExternalStorageDirectory()
						+ "/capstone/" + filename);

		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(file,
					false));
			for (Point3 point : points)
				writer.write(String.format("%.3f %.3f %.3f\n", point.x,
						point.y, point.z));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
