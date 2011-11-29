package Capstone.Scanner;

import org.opencv.core.Point;
import org.opencv.core.Point3;

public class Calculation {
	final static double Par         = 1000;
	final static double Kc_L[]      = {0.13297, -0.49581, 0.00288, 0.00006, 0};
	final static double Kc_R[]      = {0.13645, -0.58628, -0.00100, 0.00227, 0};
	final static double KK_L[][]    = {{2056.56113, 0, 901.05026}, 
			                           {0, 2097.44353, 545.57268}, 
			                           {0, 0, 1}};
	
	final static double invKK_L[][] = {{0.000486248614453, 0, -0.438134440477342}, 
			                           {0, 0.000476770881169, -0.260113167385250}, 
			                           {0, 0, 1}};
	
	final static double KK_R[][]    = {{2053.36249, 0, 1039.21648}, 
			                           {0, 2096.38006, 535.32910}, 
			                           {0, 0, 1}};
	
	final static double invKK_R[][] = {{0.000487006071685, 0, -0.506104735554997}, 
			                           {0, 0.000477012741669, -0.255358801685988}, 
			                           {0, 0, 1}};
	
	final static double R[][]       = {{0.999875971964085, -0.000560999925181, -0.015739312817276}, 
			                           {0.000558953767482, 0.999999834753416, -0.000134401733931}, 
			                           {0.015739385615771, 0.000125587516151, 0.999876120311018}};
	
	final static double T[]         = {-42.76721, -0.23691, 0.08279};
	
	private static void adjust(double XY[], double XY_P[],double Kc[], double KK[][])
	{
		double tempXY[] = new double[2];
		tempXY[0] = XY[0]-KK[0][2];
		tempXY[1] = XY[1]-KK[1][2];
		double r = tempXY[0]*tempXY[0]+tempXY[1]*tempXY[1];
		double a = 1+Kc[0]*r*r+Kc[1]*r*r*r*r+Kc[4]*r*r*r*r*r*r;
		double d[] = new double[2];
		d[0] = 2*Kc[2]*tempXY[0]*tempXY[1]+Kc[3]*(r*r+2*tempXY[0]*tempXY[0]);
		d[1] = Kc[2]*(r*r+2*tempXY[1]*tempXY[1])+2*Kc[3]*tempXY[0]*tempXY[1];
		XY_P[0] = a*tempXY[0]+d[0]+KK[0][2];
		XY_P[1] = a*tempXY[1]+d[1]+KK[1][2];
			
		XY_P[0] = XY[0];
		XY_P[1] = XY[1];
	}
	
	private static void product(double A[][], double B[], double C[])
	{
		int i, j;
		for (i=0;i<3;++i){
			C[i]=0;
			for (j=0;j<3;++j)
				C[i]+=A[i][j]*B[j];
		}
	}
	
	public static double[] calc(Point p){
		double XY_L[] = {p.x, p.y};
		double XY_P_L[]          = {0, 0, 1};
		double LINE_L[][]        = {{0, 0, 1}, 
				                    {0, 0, 2}};
		
		double LINE_R[][]        = {{0, 0, 0}, 
				                    {0, 0, 0}};
		
		double LINE_P_R[][]      = {{0, 0, 1}, 
				                    {0, 0, 0}};
		
		int i, j;

		adjust(XY_L, XY_P_L, Kc_L, KK_L);
		
		product(invKK_L, XY_P_L, LINE_L[0]);
	
		LINE_L[0][0] = invKK_L[0][2]+LINE_L[0][0]*(KK_L[0][0]+Par);
		LINE_L[0][1] = invKK_L[1][2]+LINE_L[0][1]*(KK_L[1][1]+Par);
		LINE_L[0][2] = Par;
		
		LINE_L[1][0] = XY_L[0];
		LINE_L[1][1] = XY_L[1];
		LINE_L[1][2] = 0;
		
		product(R, LINE_L[0], LINE_R[0]);
		product(R, LINE_L[1], LINE_R[1]);

		for (i=0;i<2;++i)
			for (j=0;j<3;++j)
				LINE_R[i][j]+=T[j];

		LINE_R[0][0] = (LINE_R[0][0] - KK_R[0][2])/(LINE_R[0][2] + KK_R[0][0]);
		LINE_R[0][1] = (LINE_R[0][1] - KK_R[1][2])/(LINE_R[0][2] + KK_R[1][1]);
		LINE_R[0][2] = 1;
		
		LINE_R[1][0] = (LINE_R[1][0] - KK_R[0][2])/(LINE_R[1][2] + KK_R[0][0]);
		LINE_R[1][1] = (LINE_R[1][1] - KK_R[1][2])/(LINE_R[1][2] + KK_R[1][1]);
		LINE_R[1][2] = 1;

		product(KK_R, LINE_R[0], LINE_P_R[0]);
		product(KK_R, LINE_R[1], LINE_P_R[1]);
		
		double rslt[] = new double[2];
		
		rslt[0] = LINE_P_R[1][1] - (LINE_P_R[1][1] - LINE_P_R[0][1])*LINE_P_R[1][0]/(LINE_P_R[1][0] - LINE_P_R[0][0]);
		rslt[1] = (LINE_P_R[1][1] - LINE_P_R[0][1])/(LINE_P_R[1][0] - LINE_P_R[0][0]);
		return rslt;
	}
	
//	public static Point findCorrespond(Point lSpot) {
//		Point rSpot = new Point();
//		
//		double[] l = {lSpot.x, lSpot.y};
//		double[] line = calc(l);
//		
//		return rSpot;
//	}
	
	// TODO 
	public static Point3 triangulation(Point left, Point right) {
    	Point3 point = new Point3();
    	
    	return point;
    }
	
//	public String toString(){
//		double rslt[] = calc(XY_L);
//		String s = "";
//		s += rslt[0];
//		s += " ";
//		s += rslt[1];
//		return s;
//	}

//	public static void main(String argv[]){
//		Calc_test test = new Calc_test();
//		test.XY_L[0] = 893;
//		test.XY_L[1] = 256;
//		System.out.println(test);
//	}
}
