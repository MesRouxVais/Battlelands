package utils;

import java.awt.Point;

public class CollisionUtils {

	public static boolean inInsideByRotation(Point normals[],double angle, int originX, int originY,int xP, int yP) {
		
		double relaX = xP-originX;
		double relaY = yP-originY;
		
		double cos= Math.cos(-angle);
		double sin= Math.sin(-angle);
		
		int relaNX = (int)(relaX*cos-relaY*sin);
		int relaNY = (int)(relaY*cos+relaX*sin);
		
		int normalX = relaNX+originX;
		int normalY = relaNY+originY;
		
		//System.out.println("normal X = "+normalX+"  ini X = " + xP);
		
		return isInsideSimpleRectangle(normalX, normalY, normals);
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
