package Capstone.Scanner;

import org.opencv.core.Point3;

public class PointCloud {
	public static int cur = 0;
	public static int numImages;
	public static float[] vertices;
	
	public static float xlook;
	public static float ylook;
	
	public static void addPoint(Point3 point) {
		vertices[cur++] = (float) point.x;
		vertices[cur++] = (float) point.y;
		vertices[cur++] = (float) point.z;
//		vertexList.add((float) point.x);
//		vertexList.add((float) point.y);
//		vertexList.add((float) point.z);
	}
}
