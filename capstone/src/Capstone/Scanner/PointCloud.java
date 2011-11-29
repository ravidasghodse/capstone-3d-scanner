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
	public static ArrayList<Float> vertices_arr;
	public static float zavg = 0.0f;
	
	public static void addPoint(Point3 point) {
		vertices_arr.add((float) point.x);
		vertices_arr.add((float) point.y);
		vertices_arr.add((float) point.z);
	}
}
