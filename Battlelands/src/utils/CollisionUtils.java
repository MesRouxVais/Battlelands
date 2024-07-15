package utils;

import java.awt.Point;

public class CollisionUtils {
	
	public static void main(String[] args) {
        Point[] points = new Point[4];
        points[0] = new Point(1,1);
        points[1] = new Point(4,1);
        points[2] = new Point(4,3);
        points[3] = new Point(1,3);
        System.out.println(isInsideRectangle(0,0, points));
    }
	
	static boolean verificationWithCoordinationForOrientedRectangle(int x, int y, Point[] points) {//SYMMETRY_ADJUSTMENT cause y++ when go down
		Point[] pointFinalList = new Point[4];
		final int SYMMETRY_ADJUSTMENT =11520;
		y=SYMMETRY_ADJUSTMENT-y;
		for (int i = 0; i < pointFinalList.length; i++) {
			points[i].y = SYMMETRY_ADJUSTMENT-points[i].y;
		}
		
		int record = 2147483647;
		for (int i = 0; i < pointFinalList.length; i++) {
			if(points[i].x <record) {
				record=points[i].x;
				pointFinalList[0] =points[i];
			}
		}
		
		record = -2147483648;
		for (int i = 0; i < pointFinalList.length; i++) {
			if(points[i].x >record) {
				record=points[i].x;
				pointFinalList[1] =points[i];
			}
		}
		
		record = -2147483648;
		for (int i = 0; i < pointFinalList.length; i++) {
			if(points[i].y >record) {
				record=points[i].y;
				pointFinalList[2] =points[i];
			}
		}
		
		record = 2147483647;
		for (int i = 0; i < pointFinalList.length; i++) {
			if(points[i].y <record) {
				record=points[i].y;
				pointFinalList[3] =points[i];
			}
		}
		
		
		return isInsideOrientedRectangle(x,y,pointFinalList[0],pointFinalList[1],pointFinalList[2],pointFinalList[3]);
	}
	
	static boolean isInsideOrientedRectangle(int x, int y, Point left,Point right, Point top, Point bottom) {
	    // center of rectangle
	    double centerx = (left.x + right.x)/2;
	    double centery = (left.y + right.y)/2;
	 
	    // rotation of rectangle (could be precomputed)
	    double tangt = (right.y-bottom.y) / (right.x-bottom.x);
	    double cosarctn = 1.0/Math.sqrt(1.0+tangt*tangt);
	    double sinarctn = tangt*cosarctn;
	 
	    // translation+rotation of the top/right corner of rectangle  (could be precomputed)
	    double rtx = top.x-centerx; 
	    double rty = top.y-centery;
	    double rtrx = rtx*cosarctn + rty*sinarctn;
	    double rtry = -rtx*sinarctn + rty*cosarctn;
	 
	    // translation+rotation of the point 
	    double ptx = x-centerx; 
	    double pty = y-centery;
	    double ptrx = ptx*cosarctn + pty*sinarctn;
	    double ptry = -ptx*sinarctn + pty*cosarctn;
	 
	    // test
	    if (ptrx<-rtrx || ptrx>rtrx) return false;
	    if (ptry<-rtry || ptry>rtry) return false;
	    return true;
	}

	static boolean isInsideRectangle(int x, int y, Point[] points){
		boolean sameX = false;
		boolean sameY = false;
		
		
		for (int i = 1; i < points.length; i++) {
			if(points[0].x == points[i].x) {
				sameX =true;
			}else if(points[0].y == points[i].y){
				sameY =true;
			}
		}
		if(sameY && sameX) {
			return isInsideSimpleRectangle(x,y,points);
		}else {
			return verificationWithCoordinationForOrientedRectangle(x,y,points);
		}
	}
	
	public static boolean isInsideSimpleRectangle(int x, int y, int minX, int minY, int maxX, int maxY) {
		
		if (x > maxX || x < minX) return false;
		if (y > maxY || y < minY) return false;
		return true;
	}
	
	static boolean isInsideSimpleRectangle(int x, int y, Point[] points) {
		int maxX = -2147483648;
		int maxY = -2147483648;
		int minX = 2147483647;
		int minY = 2147483647;
		
		for (int i = 0; i < points.length; i++) {
			if(points[i].x>maxX){
				maxX = points[i].x;
			}else if (points[i].x<minX){
				minX = points[i].x;
			}
			
			if(points[i].y>maxY){
				maxY = points[i].y;
			}else if (points[i].y<minY){
				minY = points[i].y;
			}
		}
		
		if (x > maxX || x < minX) return false;
		if (y > maxY || y < minY) return false;
		return true;
	}
}
