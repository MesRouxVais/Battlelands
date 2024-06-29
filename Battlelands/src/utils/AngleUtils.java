package utils;

public class AngleUtils {
	
	public static double getAngleBetweenTwoPoints(double y, double x, double comparedY, double comparedX) {
		
		double angle = Math.atan(Math.abs(y-comparedY)/Math.abs(x-comparedX)); //[0;90]
		
		if((y-comparedY)<0 && (x-comparedX)>0) {
			angle = Math.toRadians(180)-angle;
		}else if ((y-comparedY)>0 && (x-comparedX)<0){
			angle = Math.toRadians(360)-angle;
		}else if ((y-comparedY)>0 && (x-comparedX)>0){
			angle = Math.toRadians(180)+angle;
		}
		
		return angle;
	}
	
	public static double reverseAngle(double inputAngle) {
		double angle =  Math.toRadians(180)+inputAngle;
		if(angle<0) {
			return angle=Math.toRadians(360)-angle;
		}else if(angle>Math.toRadians(361)){
			return angle=angle-Math.toRadians(360);
		}else {
			return angle;
		}
	}
	
}
