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
	
	static double LINE_LR[][]   = {{0, 0, 0}, 
                                  {0, 0, 0}};

	private static void adjust(double XY[], double Kc[], double KK[][])
	{
		double r = XY[0]*XY[0]+XY[1]*XY[1];
		double a = 1+Kc[0]*r+Kc[1]*r*r+Kc[4]*r*r*r;
		XY[0] = a*XY[0]+2*Kc[2]*XY[0]*XY[1]+Kc[3]*(r+2*XY[0]*XY[0]);
 		XY[1] = a*XY[1]+Kc[2]*(r+2*XY[1]*XY[1])+2*Kc[3]*XY[0]*XY[1];
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
	
	private static void direction(double LINE[][], double KK[][])
	{
		LINE[1][0] = LINE[0][0]*(KK[0][0]);
		LINE[1][1] = LINE[0][1]*(KK[1][1]);
		LINE[1][2] = 0;

		LINE[0][0] = LINE[0][0]*(KK[0][0]+Par);
		LINE[0][1] = LINE[0][1]*(KK[1][1]+Par);
		LINE[0][2] = Par;
		
	}
	
	public static double[] projection(Point p){
		double XY_L[] = {p.x, p.y, 1};
		double LINE_L[][]   = {{0, 0, 0}, 
                			   {0, 0, 0}};
		double LINE_R[][]   = {{0, 0, 0}, 
 			   				   {0, 0, 0}};	
		double LINE_P_R[][] = {{0, 0, 1}, 
				               {0, 0, 0}};
		
		int i, j;

		
		product(invKK_L, XY_L, LINE_L[0]);
	
		adjust(LINE_L[0], Kc_L, KK_L);

		direction(LINE_L, KK_L);
		
		product(R, LINE_L[0], LINE_LR[0]);
		product(R, LINE_L[1], LINE_LR[1]);

		for (i=0;i<2;++i)
			for (j=0;j<3;++j)
				LINE_LR[i][j]+=T[j];

		LINE_R[0][0] = LINE_LR[0][0]/(LINE_LR[0][2] + KK_R[0][0]);
		LINE_R[0][1] = LINE_LR[0][1]/(LINE_LR[0][2] + KK_R[1][1]);
		LINE_R[0][2] = 1;
		
		LINE_R[1][0] = LINE_LR[1][0]/(LINE_LR[1][2] + KK_R[0][0]);
		LINE_R[1][1] = LINE_LR[1][1]/(LINE_LR[1][2] + KK_R[1][1]);
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
	
	public static Point3 triangulation(Point right) {
//		float point[] = new float[3];
		Point3 point = new Point3();
		double XY_R[] = {right.x, right.y, 1};
		
		double LINE_R[][]   = {{0, 0, 0}, 
            				   {0, 0, 0}};
		
		product(invKK_R, XY_R, LINE_R[0]);
		
		adjust(LINE_R[0], Kc_R, KK_R);
		
		direction(LINE_R, KK_R);
		
		point.x = ((LINE_LR[1][2]-LINE_LR[0][2])*LINE_LR[0][0]/(LINE_LR[1][0]-LINE_LR[0][0])
					-(LINE_R[1][2]-LINE_R[0][2])*LINE_R[0][0]/(LINE_R[1][0]-LINE_R[0][0])-LINE_LR[0][2]+LINE_R[0][2])
					/((LINE_LR[1][2]-LINE_LR[0][2])/(LINE_LR[1][0]-LINE_LR[0][0])-(LINE_R[1][2]-LINE_R[0][2])/(LINE_R[1][0]-LINE_R[0][0]));
		
		point.y = LINE_LR[0][1]+(LINE_LR[1][1]-LINE_LR[0][1])*(point.x-LINE_LR[0][0])/(LINE_LR[1][0]-LINE_LR[0][0]);
		point.z = LINE_LR[0][2]+(LINE_LR[1][2]-LINE_LR[0][2])*(point.x-LINE_LR[0][0])/(LINE_LR[1][0]-LINE_LR[0][0]);
		
    	return point;
    }
}
