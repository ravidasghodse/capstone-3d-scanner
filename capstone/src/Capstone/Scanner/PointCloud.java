package Capstone.Scanner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.opencv.core.Point3;

public class PointCloud {
	public static final int POINTPERLINE = 108;
	public static final int MAXIMAGES = 100;
	
	public static float oriCameraDist = 50;
	public static int MAXMEND = 10;
	
	public static int curLine = 0;
	public static int dump = 10;
	public static int cur = 0;
	public static float zAvg;
	
	public static float xlook;
	public static float ylook;
	public static float zoomDist = 0f;
	public static float xDist = 0f;
	public static float yDist = 0f;
	public static float legalDist = 50f;

//	public static int cur = 0;
	public static float[][] vertices = new float[MAXIMAGES][POINTPERLINE*3];

	public static void addPoint(Point3 point, int i) {
		if (i == 0) curLine++;
		vertices[curLine-1][3*i] = (float) point.x;
		vertices[curLine-1][3*i+1] = (float) point.y;
		vertices[curLine-1][3*i+2] = (float) point.z;
	}

	public static void saveModel(String filename) {
		File file = new File(
				android.os.Environment.getExternalStorageDirectory()
						+ "/capstone/" + filename);

		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for (int i = 0; i < PointCloud.curLine; i++) {
				for (int j = 0;j < 108;++j) {
					writer.write(String.format("%.3f %.3f %.3f\n",
							PointCloud.vertices[i][3*j], PointCloud.vertices[i][3*j+1],
							PointCloud.vertices[i][3*j+2]));
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void readModel(String filename) {
		curLine = 0;
		int i = 0;
		File file = new File(
				android.os.Environment.getExternalStorageDirectory()
						+ "/capstone/" + filename);
		try {
			Scanner scan = new Scanner(file);
			while (scan.hasNext()){
				if (i==324) {
					i=0;
					curLine++;
				}
				vertices[curLine][i++] = scan.nextFloat();
			}
			scan.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static float calcDist(int i1, int j1, int i2, int j2) {
		float x = vertices[i1][j1*3] - vertices[i2][j2*3];
		float y = vertices[i1][j1*3+1] - vertices[i2][j2*3+1];
		float z = vertices[i1][j1*3+2] - vertices[i2][j2*3+2];
		return (float)Math.sqrt((double)(x * x + y * y + z * z));
	}
	
	public static void calcNorm(float[] normal, int i1, int j1, int i2, int j2,
			int i3, int j3) {
		float x1 = vertices[i1][j1*3];
		float y1 = vertices[i1][j1*3+1];
		float z1 = vertices[i1][j1*3+2];
		float x2 = vertices[i2][j2*3];
		float y2 = vertices[i2][j2*3+1];
		float z2 = vertices[i2][j2*3+2];
		float x3 = vertices[i3][j3*3];
		float y3 = vertices[i3][j3*3+1];
		float z3 = vertices[i3][j3*3+2];
		normal[0] = (y1-y2)*(z1-z3) - (y1-y3)*(z1-z2);
		normal[1] = (x1-x3)*(z1-z2) - (x1-x2)*(z1-z3);
		normal[2] = (x1-x2)*(y1-y3) - (x1-x3)*(y1-y2);
		if (normal[2] > 0) {
			for (int i = 0; i < 3; i++)
				normal[i] *= -1;
		}
	}
}
