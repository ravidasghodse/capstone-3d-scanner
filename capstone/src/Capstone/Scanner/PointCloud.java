package Capstone.Scanner;

import java.util.ArrayList;

public class PointCloud {
	public static int numImages;
	public static float[] vertices;
	
	//image process 2
	public static final float coefficient = 0.2f;
	public static ArrayList<Float> vertices_arr;
	public static float zavg = 0.0f;
	
	public static void addPoint(float[] point) {
		vertices_arr.add(point[0]);
		vertices_arr.add(point[0]);
		vertices_arr.add(point[0]);
	}
}
