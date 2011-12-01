package Capstone.Scanner;

import java.util.ArrayList;

import org.opencv.core.Point3;

public class PointCloud {
	public static int numImages;
	public static float[] vertices;
	
	public static float xlook;
	public static float ylook;
	
	//image process 2
	public static final float coefficient = 0.2f;
	public static ArrayList<Float> vertexList;
	public static float zavg = 0.0f;
	
	public static void addPoint(Point3 point) {
		vertexList.add((float) point.x);
		vertexList.add((float) point.y);
		vertexList.add((float) point.z);
	}
}
