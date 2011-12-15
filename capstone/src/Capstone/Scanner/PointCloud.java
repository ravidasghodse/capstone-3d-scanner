package Capstone.Scanner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.opencv.core.Point3;

public class PointCloud {
	public static int cur = 0;
	public static int numImages;
	public static float[] vertices;

	public static float xlook;
	public static float ylook;
	public static float zoomDist = 0.0f;
	public static float xDist = 0.0f;
	public static float yDist = 0.0f;
	public static int dump = 10;
	public static float zAvg;
	
	public static void addPoint(Point3 point) {
		vertices[cur++] = (float) point.x;
		vertices[cur++] = (float) point.y;
		vertices[cur++] = (float) point.z;
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
			for (int i = 0; i < PointCloud.cur; i += 3)
				writer.write(String.format("%.3f %.3f %.3f\n",
						PointCloud.vertices[i], PointCloud.vertices[i + 1],
						PointCloud.vertices[i + 2]));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void readModel(String filename) {
		cur = 0;
		File file = new File(
				android.os.Environment.getExternalStorageDirectory()
						+ "/capstone/" + filename);
		try {
			Scanner scan = new Scanner(file);
			while (scan.hasNext())
				vertices[cur++] = scan.nextFloat();
			scan.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
